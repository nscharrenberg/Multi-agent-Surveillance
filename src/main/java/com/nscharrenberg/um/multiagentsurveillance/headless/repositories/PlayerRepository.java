package com.nscharrenberg.um.multiagentsurveillance.headless.repositories;

import com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi.YamauchiAgent;
import com.nscharrenberg.um.multiagentsurveillance.agents.random.RandomAgent;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.Agent;
import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IGameRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IMapRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IPlayerRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.CollisionException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.InvalidTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.ItemAlreadyOnTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.ItemNotOnTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.*;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.BoardUtils;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.CharacterVision;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.StopWatch;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;

public class PlayerRepository implements IPlayerRepository {
    private IMapRepository mapRepository;
    private IGameRepository gameRepository;

    private SecureRandom random;
    private List<Intruder> intruders;
    private List<Guard> guards;

    private TileArea completeKnowledgeProgress;
    private StopWatch stopWatch;

    private List<Agent> agents;

    private static final Class<? extends Agent> agentType = YamauchiAgent.class;

    private float explorationPercentage = 0;

    public PlayerRepository(IMapRepository mapRepository, IGameRepository gameRepository) {
        this.mapRepository = mapRepository;
        this.gameRepository = gameRepository;

        this.intruders = new ArrayList<>();
        this.guards = new ArrayList<>();
        this.agents = new ArrayList<>();
        this.completeKnowledgeProgress = new TileArea();
        this.stopWatch = new StopWatch();

        try {
            this.random = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error while generating Random Class");
        }
    }

    @Override
    public void calculateInaccessibleTiles() {
        for (Map.Entry<Integer, HashMap<Integer, Tile>> rowEntry : mapRepository.getBoard().getRegion().entrySet()) {
            for (Map.Entry<Integer, Tile> colEntry : rowEntry.getValue().entrySet()) {
                HashMap<AdvancedAngle, Tile> neighbours = BoardUtils.getNeighbours(mapRepository.getBoard(), colEntry.getValue());

                boolean isInaccessible = true;
                for (Map.Entry<AdvancedAngle, Tile> neighbour : neighbours.entrySet()) {
                    if (neighbour.getValue() == null) {
                        continue;
                    }

                    if (!neighbour.getValue().isCollision() && !neighbour.getValue().isTeleport()) {
                        isInaccessible = false;
                        break;
                    }
                }

                if (!isInaccessible) {
                    continue;
                }

                completeKnowledgeProgress.add(colEntry.getValue());
            }
        }
    }

    @Override
    public float calculateExplorationPercentage() {
        for (Agent agent : agents) {
            completeKnowledgeProgress = (TileArea) completeKnowledgeProgress.merge(agent.getKnowledge());
        }

        float totalTileCount = mapRepository.getBoard().height() * mapRepository.getBoard().width();
        float discoveredAreaTileCount = 0;

        // TODO: Could probably do with some optimization
        for (Map.Entry<Integer, HashMap<Integer, Tile>> rowEntry : completeKnowledgeProgress.getRegion().entrySet()) {
            for (Map.Entry<Integer, Tile> colEntry : rowEntry.getValue().entrySet()) {
                discoveredAreaTileCount += 1;
            }
        }

        // no tiles = 100% (division by 0 not possible)
        if (totalTileCount <= 0) {
            explorationPercentage = 100;
            gameRepository.setRunning(false);
            return 100;
        }

        float percentage = (discoveredAreaTileCount / totalTileCount) * 100;
        explorationPercentage = percentage;

        // TODO: Remove this when UI elements are present
        System.out.println("Explored: " + explorationPercentage + "%");

        try {
            stopWatch.saveOrIgnoreSplit(explorationPercentage);
        } catch (Exception e) {
            System.out.println("Stopwatch Error: " + e.getMessage());
        }

        return percentage;
    }

    public PlayerRepository() {
        this.mapRepository = Factory.getMapRepository();
        this.gameRepository = Factory.getGameRepository();

        this.intruders = new ArrayList<>();
        this.guards = new ArrayList<>();
        this.agents = new ArrayList<>();
        this.completeKnowledgeProgress = new TileArea();
        this.stopWatch = new StopWatch();

        try {
            this.random = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error while generating Random Class");
        }
    }

    @Override
    public void spawn(Class<?> playerInstance) {
        if (playerInstance.equals(Intruder.class)) {
            spawnIntruder();
            return;
        }

        spawnGuard();
    }

    private void spawnGuard() {
        TileArea guardSpawnArea = mapRepository.getGuardSpawnArea();
        spawn(Guard.class, guardSpawnArea);
    }

    private void spawnIntruder() {
        TileArea guardSpawnArea = mapRepository.getIntruderSpawnArea();
        spawn(Intruder.class, guardSpawnArea);
    }

    @Override
    public void spawn(Class<? extends Player> playerClass, TileArea playerSpawnArea) {
        HashMap<Integer, HashMap<Integer, Tile>> spawnArea = playerSpawnArea.getRegion();

        boolean tileAssigned = false;
        Map.Entry<Map.Entry<Integer, Integer>, Map.Entry<Integer, Integer>> bounds = playerSpawnArea.bounds();

        while (!tileAssigned) {
            int rowIndex = random.nextInt(bounds.getKey().getKey(), bounds.getKey().getValue()+1);
            HashMap<Integer, Tile> row = spawnArea.get(rowIndex);

            int colIndex = random.nextInt(bounds.getValue().getKey(), bounds.getValue().getValue()+1);
            Tile tile = row.get(colIndex);

            boolean invalid = false;

            for (Item item : tile.getItems()) {
                if (item instanceof Collision) {
                    invalid = true;
                    break;
                }
            }

            if (!invalid) {
                try {

                    Agent agent = null;
                    if (playerClass.equals(Intruder.class)) {
                        Intruder intruder = new Intruder(tile, Angle.UP);
                        tile.add(intruder);
                        intruders.add(intruder);
                        agent = spawnAgent(intruder, agentType);
                    } else {
                        Guard guard = new Guard(tile, Angle.UP);
                        tile.add(guard);
                        guards.add(guard);
                        agent = spawnAgent(guard, agentType);
                    }

                    agent.addKnowledge(tile);
                    tileAssigned = true;
                } catch (ItemAlreadyOnTileException e) {
                    System.out.println("Player Already on tile - this shouldn't happen");
                }
            }
        }
    }

    public Agent spawnAgent(Player player, Class<? extends Agent> agentClass) {
        Agent agent = null;

        if (agentClass.equals(RandomAgent.class)) {
            agent = new RandomAgent(player);
        } else if (agentClass.equals(YamauchiAgent.class)) {
            agent = new YamauchiAgent(player);
        }

        if (agent == null) {
            return null;
        }

        this.agents.add(agent);
        player.setAgent(agent);
        agent.addKnowledge(player.getTile());
        return agent;
    }

    @Override
    public void move(Player player, Angle direction) throws CollisionException, InvalidTileException, ItemNotOnTileException, ItemAlreadyOnTileException {
        Angle currentDirection = player.getDirection();

        // Rotate the player when it's not facing the same direction as it wants to go to.
        if (!currentDirection.equals(direction)) {
            player.setDirection(direction);

            if (player.getAgent() != null) {
                CharacterVision characterVision = new CharacterVision(6, player.getDirection());
                List<Tile> vision = characterVision.getVision(mapRepository.getBoard(), player.getTile());
                player.getAgent().addKnowledge(vision);

                List<Tile> vision2 = characterVision.getVision(mapRepository.getBoard(), player.getTile());
                player.getAgent().addKnowledge(vision2);
                player.setVision(new TileArea(vision2));

                calculateExplorationPercentage();
            }

            return;
        }

        Tile currentPosition = player.getTile();

        int nextX = player.getTile().getX() + direction.getxIncrement();
        int nextY = player.getTile().getY() + direction.getyIncrement();
        Optional<Tile> nextPositionOpt = mapRepository.getBoardAsArea().getByCoordinates(nextX, nextY);

        if (nextPositionOpt.isEmpty()) {
            throw new InvalidTileException(nextX, nextY);
        }

        Tile nextPosition = nextPositionOpt.get();

        Optional<Item> collisionFound = nextPosition.getItems().stream().filter(item -> item instanceof Collision).findFirst();

        if (collisionFound.isPresent()) {
            throw new CollisionException();
        }

        Optional<Item> teleporterFoundOpt = nextPosition.getItems().stream().filter(item -> item instanceof Teleporter).findFirst();

        // remove player from tile
        currentPosition.remove(player);

        // Teleport to destination instead of the source tile
        if (teleporterFoundOpt.isPresent()) {
            Teleporter teleporter = (Teleporter) teleporterFoundOpt.get();

            teleporter.getTile().add(player);
            player.setTile(teleporter.getTile());
            player.setDirection(teleporter.getDirection());

            if (player.getAgent() != null) {
                CharacterVision characterVision = new CharacterVision(6, player.getDirection());
                List<Tile> vision = characterVision.getVision(mapRepository.getBoard(), nextPosition);
                player.getAgent().addKnowledge(vision);

                List<Tile> vision2 = characterVision.getVision(mapRepository.getBoard(), player.getTile());
                player.getAgent().addKnowledge(vision2);
                player.setVision(new TileArea(vision2));

                calculateExplorationPercentage();
            }

            return;
        }

        // add player to tile
        nextPosition.add(player);
        player.setTile(nextPosition);

        if (player.getAgent() != null) {
            CharacterVision characterVision = new CharacterVision(6, player.getDirection());
            List<Tile> vision = characterVision.getVision(mapRepository.getBoard(), player.getTile());

            player.getAgent().addKnowledge(vision);
            player.setVision(new TileArea(vision));
            calculateExplorationPercentage();
        }
    }

    @Override
    public boolean isLegalMove(Player player, Angle direction) {
        Angle currentDirection = player.getDirection();

        // Rotate the player when it's not facing the same direction as it wants to go to.
        if (!currentDirection.equals(direction)) {
            return true;
        }

        int nextX = player.getTile().getX() + direction.getxIncrement();
        int nextY = player.getTile().getY() + direction.getyIncrement();

        Optional<Tile> nextPositionOpt = mapRepository.getBoardAsArea().getByCoordinates(nextX, nextY);

        if (nextPositionOpt.isEmpty()) {
            return false;
        }

        Tile nextPosition = nextPositionOpt.get();

        Optional<Item> collisionFound = nextPosition.getItems().stream().filter(item -> item instanceof Collision).findFirst();

        return collisionFound.isEmpty();
    }

    @Override
    public List<Intruder> getIntruders() {
        return intruders;
    }

    @Override
    public void setIntruders(List<Intruder> intruders) {
        this.intruders = intruders;
    }

    @Override
    public void setGuards(List<Guard> guards) {
        this.guards = guards;
    }

    @Override
    public List<Guard> getGuards() {
        return this.guards;
    }

    @Override
    public IMapRepository getMapRepository() {
        return mapRepository;
    }

    @Override
    public IGameRepository getGameRepository() {
        return gameRepository;
    }

    @Override
    public void setMapRepository(IMapRepository mapRepository) {
        this.mapRepository = mapRepository;
    }

    @Override
    public void setGameRepository(IGameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Override
    public List<Agent> getAgents() {
        return agents;
    }

    @Override
    public void setAgents(List<Agent> agents) {
        this.agents = agents;
    }

    @Override
    public float getExplorationPercentage() {
        return explorationPercentage;
    }

    @Override
    public void setExplorationPercentage(float explorationPercentage) {
        this.explorationPercentage = explorationPercentage;
    }

    @Override
    public StopWatch getStopWatch() {
        return stopWatch;
    }

    @Override
    public void setStopWatch(StopWatch stopWatch) {
        this.stopWatch = stopWatch;
    }
}
