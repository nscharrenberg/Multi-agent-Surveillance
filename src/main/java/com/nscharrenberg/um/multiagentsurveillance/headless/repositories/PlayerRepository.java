package com.nscharrenberg.um.multiagentsurveillance.headless.repositories;

import com.nscharrenberg.um.multiagentsurveillance.agents.SBO.SBOAgent;
import com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi.YamauchiAgent;
import com.nscharrenberg.um.multiagentsurveillance.agents.random.RandomAgent;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.Agent;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.distanceCalculator.ManhattanDistance;
import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IGameRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IMapRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IPlayerRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.*;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle.AdvancedAngle;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.GameMode;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.Collision.Collision;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.Item;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.Teleporter;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.ShadowTile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.TileArea;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Marker;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Guard;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Intruder;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Player;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.AreaEffects.DistanceEffects;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.BoardUtils;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.StopWatch;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.Vision.CharacterVision;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;

import static com.nscharrenberg.um.multiagentsurveillance.headless.utils.AreaEffects.AudioEffectHelper.*;

public class PlayerRepository implements IPlayerRepository {
    private IMapRepository mapRepository;
    private IGameRepository gameRepository;

    private SecureRandom random;
    private List<Intruder> intruders;
    private List<Guard> guards;

    private TileArea completeKnowledgeProgress;
    private StopWatch stopWatch;

    private static double TOLERANCE_RATE = 0.01;

    private List<Agent> agents;

    private static final Class<? extends Agent> guardType = YamauchiAgent.class;
    private static final Class<? extends Agent> intruderType = YamauchiAgent.class;

    private float explorationPercentage = 0;

    private double captureRange = 2.0;
    private int timeStepsToEscape = 3;

    private HashMap<String, Integer> intrudersAboutToEscape;
    private List<Intruder> caughtIntruders;
    private List<Intruder> escapedIntruders;

    public PlayerRepository(IMapRepository mapRepository, IGameRepository gameRepository) {
        this.mapRepository = mapRepository;
        this.gameRepository = gameRepository;

        this.intruders = new ArrayList<>();
        this.guards = new ArrayList<>();
        this.agents = new ArrayList<>();
        this.completeKnowledgeProgress = new TileArea();
        this.stopWatch = new StopWatch();

        this.intrudersAboutToEscape = new HashMap<>();
        this.caughtIntruders = new ArrayList<>();
        this.escapedIntruders = new ArrayList<>();

        try {
            this.random = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error while generating Random Class");
        }
    }

    public PlayerRepository() {
        this.mapRepository = Factory.getMapRepository();
        this.gameRepository = Factory.getGameRepository();

        this.intruders = new ArrayList<>();
        this.guards = new ArrayList<>();
        this.agents = new ArrayList<>();
        this.completeKnowledgeProgress = new TileArea();
        this.stopWatch = new StopWatch();

        this.intrudersAboutToEscape = new HashMap<>();
        this.caughtIntruders = new ArrayList<>();
        this.escapedIntruders = new ArrayList<>();

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
    public float calculateAgentExplorationRate(Agent agent){
        float totalTileCount = mapRepository.getBoard().height() * mapRepository.getBoard().width();
        float discoveredAreaTileCount = agent.getKnowledge().size();

        return (discoveredAreaTileCount / totalTileCount) * 100;
    }

    @Override
    public float calculateExplorationPercentage() {
        float totalTileCount = mapRepository.getBoard().height() * mapRepository.getBoard().width();
        float discoveredAreaTileCount = completeKnowledgeProgress.size();


        // no tiles = 100% (division by 0 not possible)
        if (totalTileCount <= 0 || explorationPercentage >= (100 - TOLERANCE_RATE)) {
            explorationPercentage = 100;
            if (!getGameRepository().getGameMode().equals(GameMode.EXPLORATION)) {
                //end game
                Factory.getGameRepository().setRunning(false);
            }

            return 100;
        }

        float percentage = (discoveredAreaTileCount / totalTileCount) * 100;
        explorationPercentage = percentage;

        return percentage;
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
                        Intruder intruder = new Intruder(tile, Action.UP);
                        tile.add(intruder);
                        intruders.add(intruder);
                        agent = spawnAgent(intruder, intruderType);
                    } else {
                        Guard guard = new Guard(tile, Action.UP);
                        tile.add(guard);
                        guards.add(guard);
                        agent = spawnAgent(guard, guardType);
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
        } else if (agentClass.equals(SBOAgent.class)) {
            agent = new SBOAgent(player);
        }

        if (agent == null) {
            return null;
        }

        this.agents.add(agent);
        player.setAgent(agent);
        agent.addKnowledge(player.getTile());

        int visionLength = 6;

        if(player.getTile() instanceof ShadowTile)
            visionLength /= 2;

        CharacterVision characterVision = new CharacterVision(visionLength, player.getDirection(), player);
        List<Tile> vision = characterVision.getVision(mapRepository.getBoard(), player.getTile());
        player.getAgent().addKnowledge(vision);
        completeKnowledgeProgress.add(vision);
        player.setVision(new TileArea(vision));

        return agent;
    }



    @Override
    public void move(Player player, Action direction) throws CollisionException, InvalidTileException, ItemNotOnTileException, ItemAlreadyOnTileException, BoardNotBuildException {
        if (player instanceof Guard guard) {
            capture(guard);
        } else if (player instanceof Intruder intruder) {
            escape(intruder);
        }

        Action currentDirection = player.getDirection();
        Tile currentTilePlayer = player.getTile();
        int visionLength = 6;

        if(currentTilePlayer instanceof ShadowTile)
            visionLength /= 2;

        // Rotate the player when it's not facing the same direction as it wants to go to.
        if (!currentDirection.equals(direction) && !direction.equals(Action.PLACE_MARKER_DEADEND) && !direction.equals(Action.PLACE_MARKER_TARGET) && !direction.equals(Action.PLACE_MARKER_GUARDSPOTTED) && !direction.equals(Action.PLACE_MARKER_INTRUDERSPOTTED) && !direction.equals(Action.PLACE_MARKER_TELEPORTER) && !direction.equals(Action.PLACE_MARKER_SHADED)) {
            player.setDirection(direction);

            if (player.getAgent() != null) {
                //Vision
                CharacterVision characterVision = new CharacterVision(visionLength, player.getDirection(), player);
                List<Tile> vision = characterVision.getVision(mapRepository.getBoard(), player.getTile());

                //Add knowledge to the player
                player.getAgent().addKnowledge(vision);

                //Add tiles to the progress
                completeKnowledgeProgress.add(vision);

                //Add vision to the player
                player.setVision(new TileArea(vision));

                //Set the represented sound range
                player.setRepresentedSoundRange(ROTATE);
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

        if ((nextPosition.getX() == currentPosition.getX()) && (nextPosition.getY() == currentPosition.getY())) {
            if (direction == Action.PLACE_MARKER_DEADEND) {
                Factory.getMapRepository().addMarker(Marker.MarkerType.DEAD_END, currentPosition.getX(), currentPosition.getY(), player);
                player.getAgent().decrementDeadEndMarkers();
                return;
            }
            else if (direction == Action.PLACE_MARKER_GUARDSPOTTED) {
                Factory.getMapRepository().addMarker(Marker.MarkerType.GUARD_SPOTTED, currentPosition.getX(), currentPosition.getY(), player);
                player.getAgent().decrementGuardSpottedMarkers();
                return;
            }
            else if (direction == Action.PLACE_MARKER_INTRUDERSPOTTED) {
                Factory.getMapRepository().addMarker(Marker.MarkerType.INTRUDER_SPOTTED, currentPosition.getX(), currentPosition.getY(), player);
                player.getAgent().decrementIntruderSpottedMarkers();
                return;
            }
            else if (direction == Action.PLACE_MARKER_SHADED) {
                Factory.getMapRepository().addMarker(Marker.MarkerType.SHADED, currentPosition.getX(), currentPosition.getY(), player);
                player.getAgent().decrementShadedMarkers();
                return;
            }
            else if (direction == Action.PLACE_MARKER_TARGET) {
                Factory.getMapRepository().addMarker(Marker.MarkerType.TARGET, currentPosition.getX(), currentPosition.getY(), player);
                player.getAgent().decrementTargetMarkers();
                return;
            }
            else if (direction == Action.PLACE_MARKER_TELEPORTER) {
                Factory.getMapRepository().addMarker(Marker.MarkerType.TELEPORTER, currentPosition.getX(), currentPosition.getY(), player);
                player.getAgent().decrementTeleporterMarkers();
                return;
            }

        }

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
                //Vision
                CharacterVision characterVision = new CharacterVision(visionLength, player.getDirection(), player);
                List<Tile> vision = characterVision.getVision(mapRepository.getBoard(), nextPosition);

                //Add tiles to the progress
                completeKnowledgeProgress.add(vision);

                //Add knowledge to the player
                player.getAgent().addKnowledge(vision);

                //Vision
                List<Tile> vision2 = characterVision.getVision(mapRepository.getBoard(), player.getTile());

                //Add knowledge to the player
                player.getAgent().addKnowledge(vision2);

                //Add tiles to the progress
                completeKnowledgeProgress.add(vision2);

                //Add vision to the player
                player.setVision(new TileArea(vision2));

                //Set the represented sound range
                player.setRepresentedSoundRange(WAIT);
            }

            return;
        }

        // add player to tile
        nextPosition.add(player);
        player.setTile(nextPosition);

        if (player.getAgent() != null) {
            //Vision
            CharacterVision characterVision = new CharacterVision(visionLength, player.getDirection(), player);
            List<Tile> vision = characterVision.getVision(mapRepository.getBoard(), player.getTile());

            //Add knowledge to the player
            player.getAgent().addKnowledge(vision);

            //Add tiles to the progress
            completeKnowledgeProgress.add(vision);

            //Add vision to the player
            player.setVision(new TileArea(vision));

            //Set the represented sound range
            player.setRepresentedSoundRange(WALK);

        }
    }

    /**
     * Check if the given guard is capturing an intruder
     * @param guard - the given guard
     */
    private void capture(Guard guard) {
        ManhattanDistance manhattanDistance = new ManhattanDistance();

        for (Intruder intruder : intruders) {
            double distance = manhattanDistance.compute(guard.getTile(), intruder.getTile());

            if (distance <= captureRange) {
                System.out.println("Intruder " + intruder.getId() + " has been Caught");

                caughtIntruders.add(intruder);

                intruders.remove(intruder);
                agents.remove(intruder.getAgent());
                try {
                    intruder.getTile().remove(intruder);
                } catch (ItemNotOnTileException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Check if the given intruder is in the process of escaping
     * @param intruder - the given intruder
     */
    private void escape(Intruder intruder) {
        if (mapRepository.getTargetArea().within(intruder.getTile().getX(), intruder.getTile().getY())) {
            if (intrudersAboutToEscape.containsKey(intruder.getId())) {
                int count = intrudersAboutToEscape.get(intruder.getId());

                // Intruder escaped & is removed from the board.
                if (count > timeStepsToEscape) {
                    System.out.println("Intruder " + intruder.getId() + " has escaped");

                    escapedIntruders.add(intruder);

                    intrudersAboutToEscape.remove(intruder.getId());
                    intruders.remove(intruder);
                    agents.remove(intruder.getAgent());

                    try {
                        intruder.getTile().remove(intruder);
                    } catch (ItemNotOnTileException e) {
                        e.printStackTrace();
                    }
                    return;
                }

                int newCount = count + 1;

                System.out.println("Intruder " + intruder.getId() + " is trying to escape (" + newCount + ")");

                // Intruder is about to escape, just a few more timesteps to go!
                intrudersAboutToEscape.put(intruder.getId(), newCount);

                return;
            }

            System.out.println("Intruder " + intruder.getId() + " entered the target zone");
            intrudersAboutToEscape.put(intruder.getId(), 0);
        } else if (intrudersAboutToEscape.containsKey(intruder.getId())) {
            System.out.println("Intruder " + intruder.getId() + " has left the target zone");
            // The intruder left the targetZone
            intrudersAboutToEscape.remove(intruder.getId());
        }
    }

    @Override
    public boolean isLegalMove(Player player, Action direction) {
        Action currentDirection = player.getDirection();

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
    public void updateSounds(List<Agent> agentList) {
        for(Agent agent : agentList){
            DistanceEffects.areaEffects(agent, agentList);
        }
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
    public TileArea getCompleteKnowledgeProgress() {
        return completeKnowledgeProgress;
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
