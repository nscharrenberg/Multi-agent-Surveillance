package com.nscharrenberg.um.multiagentsurveillance.agents.ReinforcementLearningAgent;

import com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi.Frontier;
import com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi.comparator.guard.IWeightComparatorGuard;
import com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi.comparator.guard.MinDistanceUnknownAreaComparator;
import com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi.comparator.intruder.CloseToTarget;
import com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi.comparator.intruder.IWeightComparatorIntruder;
import com.nscharrenberg.um.multiagentsurveillance.agents.probabilistic.State;
import com.nscharrenberg.um.multiagentsurveillance.agents.probabilistic.pursuer.PursuerAgent;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.Agent;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.distanceCalculator.CalculateDistance;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.distanceCalculator.ManhattanDistance;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.pathfinding.AStar.AStar;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.pathfinding.BFS.BFS;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.pathfinding.IPathFinding;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.utils.QueueNode;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IGameRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IMapRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IPlayerRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.*;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.Collision.Collision;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.Item;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.MarkerSmell;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.SoundWave;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Area;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Guard;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Intruder;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Player;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.BoardUtils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;

public class RLAgent extends Agent {
    private final List<Frontier> frontiers = new ArrayList<>();
    private Frontier chosenFrontier = null;
    private SecureRandom random;
    private RLmodel rlmodel = new RLmodel();
    private Tile goal = this.player.getTile();
    private Tile previous = this.player.getTile();
    private final int[][] visited = new int[mapRepository.getBoard().width()][mapRepository.getBoard().height()];
    private final IPathFinding pathFindingAlgorithm = new AStar();
    private final IWeightComparatorGuard weightDetectorGuard = new MinDistanceUnknownAreaComparator();
    private final IWeightComparatorIntruder weightDetectorIntruder = new CloseToTarget();
    private final CalculateDistance calculateDistance = new ManhattanDistance();
    private final static boolean pathNotForAll = true;

    public RLAgent(Player player) {
        super(player);
        try {
            this.random = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public RLAgent(Player player, IMapRepository mapRepository, IGameRepository gameRepository, IPlayerRepository playerRepository) {
        super(player, mapRepository, gameRepository, playerRepository);
        try {
            this.random = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public RLAgent(Player player, Area<Tile> knowledge, Queue<Action> plannedMoves, IMapRepository mapRepository, IGameRepository gameRepository, IPlayerRepository playerRepository) {
        super(player, knowledge, plannedMoves, mapRepository, gameRepository, playerRepository);
        try {
            this.random = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void execute(Action action) {
        try {
            playerRepository.move(player, action);
        } catch (CollisionException | InvalidTileException | ItemNotOnTileException | ItemAlreadyOnTileException | BoardNotBuildException e) {
            e.printStackTrace();
//            System.out.println(e.getMessage());

            // If any of the above errors is thrown we can't continue with our planned moves, and need to recalculate our frontiers
            plannedMoves.clear();
            frontiers.clear();

            detect();
        }
    }

    @Override
    public Action decide() throws InvalidTileException, BoardNotBuildException{

        // Store visited tiles
        if(!previous.equals(player.getTile())) {
            visited[player.getTile().getX()][player.getTile().getY()] += 1;
        }

        // Update current tile to previous tile
        previous = player.getTile();

        // just a check
        // System.out.println(Arrays.deepToString(visited));

        // Parameter assessment
        rlmodel.reset();
        if(player.getTile().getItems().size() >= 2) {
            for (Item it: player.getTile().getItems()) {
                if(it instanceof SoundWave) {
                    rlmodel.parameterEvaluation(new Parameter((SoundWave) it), this.player, 0);
                } else if(it instanceof MarkerSmell) {
                    rlmodel.parameterEvaluation(new Parameter((MarkerSmell) it), this.player, 0);
                }
            }

            if(rlmodel.getRedirect().size() != 0) {
                plannedMoves = rlmodel.getRedirect();
            }
        }

        // Check the agents vision
        //ScanVision();

        // Continue
        if (!plannedMoves.isEmpty() && visited[player.getTile().getX()][player.getTile().getY()] <= 3) {
            return plannedMoves.poll();
        }

        frontiers.clear();

        Optional<Frontier> chosenFrontierOpt = pickBestFrontier();

        // Current path, select random move
        if (chosenFrontierOpt.isEmpty() || chosenFrontierOpt.get().getQueueNode() == null) {
            return selectRandom();
        }

        Frontier chosenFrontier = chosenFrontierOpt.get();
        goal = chosenFrontier.getQueueNode().getTile();

        plannedMoves = chosenFrontier.getQueueNode().getMoves();

        return plannedMoves.poll();

    }

    // Get random move
    private Action selectRandom() {
        int value = this.random.nextInt(100);

        Action move = player.getDirection();

        Optional<Tile> nextTileOpt = knowledge.getByCoordinates(player.getTile().getX() + move.getxIncrement(),
                player.getTile().getY() + player.getDirection().getyIncrement());

        boolean nextBlocked = false;
        if (nextTileOpt.isPresent()) {
            for (Item items : nextTileOpt.get().getItems()) {
                if (items instanceof Collision) {
                    nextBlocked = true;
                    break;
                }
            }
        }

        if (value <= 30 || nextBlocked) {
            int pick = this.random.nextInt(4);
            move = Action.values()[pick];
        }

        return move;
    }

    // Vision assessment
    private void ScanVision() {
        for (Map.Entry<Integer, HashMap<Integer, Tile>> rowEntry : this.player.getVision().getRegion().entrySet()) {
            for (Map.Entry<Integer, Tile> colEntry : rowEntry.getValue().entrySet()) {
                Tile seen = colEntry.getValue();
                if (seen.hasIntruder()) {
                    Optional<QueueNode> queueNodeOpt = pathFindingAlgorithm.execute(this.knowledge, this.player, seen);
                    queueNodeOpt.ifPresent(queueNode -> plannedMoves = queueNode.getMoves());
                    System.out.println("Players Tile: " + player.getTile().getX() + "  " + player.getTile().getY());
                    System.out.println(seen.getX() + " - " + seen.getY());
                    System.out.println(plannedMoves);
                }
            }
        }
    }

    private Optional<Frontier> pickBestFrontier() {
        if (frontiers.isEmpty() && plannedMoves.isEmpty()) {
            detect();
        }

        if (frontiers.isEmpty()) {
            return Optional.empty();
        }

        Frontier bestFrontier = null;

        for (Frontier frontier : frontiers) {
            if (frontier.getQueueNode() == null || frontier.getUnknownAreas() == 0) {
                continue;
            }

            if (bestFrontier == null)
                bestFrontier = frontier;

            if(player instanceof Guard)
                bestFrontier = weightDetectorGuard.compare(frontier, bestFrontier);
            else if (player instanceof Intruder)
                bestFrontier = weightDetectorIntruder.compare(frontier, bestFrontier, ((Intruder)player).getTarget());

        }

        if (bestFrontier == null || bestFrontier.getQueueNode() == null) {
            return Optional.empty();
        }

        if (bestFrontier.getQueueNode().getTile().isCollision()) {
            return Optional.empty();
        }


        if(pathNotForAll) findTheBestPath(bestFrontier);

        chosenFrontier = bestFrontier;

        return Optional.of(bestFrontier);
    }

    private void findTheBestPath(Frontier frontier){

        Optional<QueueNode> queueNodeOpt = pathFindingAlgorithm.execute(knowledge, player, frontier.getTarget());

        if (queueNodeOpt.isPresent()){
            QueueNode queueNode = queueNodeOpt.get();
            frontier.setQueueNode(queueNode);
        }
    }

    private void detect(){
        if(detectFrontierByRegion())
            detectFrontiers();
    }

    private void detectFrontiers() {
        frontiers.clear();
        chosenFrontier = null;
        Tile possibleTeleport = null;

        for (Map.Entry<Integer, HashMap<Integer, Tile>> rowEntry : knowledge.getRegion().entrySet()) {
            for (Map.Entry<Integer, Tile> colEntry : rowEntry.getValue().entrySet()) {
                // Reject tile if its a collidable object
                if (colEntry.getValue().isCollision() && !colEntry.getValue().getItems().contains(player)) {
                    continue;
                }
                // Reject if all tile is surrounded by collision objects or teleports
                if (BoardUtils.isSurrounded(knowledge, colEntry.getValue())) {
                    continue;
                }

                if (colEntry.getValue().isTeleport()){
                    possibleTeleport = colEntry.getValue();
                    continue;
                }
                // Check if it is a fully known tile
                List<Optional<Tile>> neighbours = getAllNeighbours(colEntry, rowEntry);


                if (isFullyKnown(neighbours)) {
                    continue;
                }

                if (addTileToFrontier(colEntry, neighbours)) {
                    createNewFrontier(colEntry, neighbours);
                }
            }
        }

        // If No frontiers are found but teleporter is in knowledge, go to teleporter.
        if (frontiers.isEmpty() && possibleTeleport != null) {
            Frontier newFrontier = new Frontier(possibleTeleport);
            newFrontier.setUnknownAreas(1);
            frontiers.add(newFrontier);

            // Find the shortest path to this tile
            Optional<QueueNode> queueNodeOpt = pathFindingAlgorithm.execute(knowledge, player, possibleTeleport);

            if (queueNodeOpt.isPresent()) {
                QueueNode queueNode = queueNodeOpt.get();

                if (queueNode.getTile().isCollision()) return;
                if (newFrontier.getQueueNode() == null || (queueNode.getDistance() < newFrontier.getQueueNode().getDistance())) {
                    newFrontier.setQueueNode(queueNode);
                }
            }

            chosenFrontier = newFrontier;
        }
    }

    private boolean detectFrontierByRegion(){
        int x = player.getTile().getX();
        int y = player.getTile().getY();
        int RANGE = 20;
        HashMap<Integer, HashMap<Integer, Tile>> region = knowledge.subset(x - RANGE, y - RANGE, x + RANGE, y + RANGE);

        frontiers.clear();
        chosenFrontier = null;

        for (Map.Entry<Integer, HashMap<Integer, Tile>> rowEntry : region.entrySet()) {
            for (Map.Entry<Integer, Tile> colEntry : rowEntry.getValue().entrySet()) {
                // Reject tile if its a collidable object
                if (colEntry.getValue().isCollision() && !colEntry.getValue().getItems().contains(player)) {
                    continue;
                }

                // Reject if all tile is surrounded by collision objects or teleports
                if (BoardUtils.isSurrounded(knowledge, colEntry.getValue())) {
                    continue;
                }

                if (colEntry.getValue().isTeleport()){
                    continue;
                }

                // Check if it is a fully known tile

                List<Optional<Tile>> neighbours = getAllNeighbours(colEntry, rowEntry);


                if (isFullyKnown(neighbours)) {
                    continue;
                }

                if (addTileToFrontier(colEntry, neighbours)) {
                    createNewFrontier(colEntry, neighbours);
                }
            }
        }

        return frontiers.isEmpty();
    }

    private boolean addTileToFrontier(Map.Entry<Integer, Tile> colEntry, List<Optional<Tile>> neighbours){
        for (Frontier frontier : frontiers) {
            if (frontier.add(colEntry.getValue())) {
                addUnknownArea(frontier, neighbours);

                if(pathNotForAll) {
                    int distance = (int) calculateDistance.compute(player.getTile(), colEntry.getValue());
                    if (frontier.getDistance() > distance) {
                        frontier.setTarget(colEntry.getValue());
                        frontier.setDistance(distance);
                    }
                } else {
                    // Find the shortest path to this tile
                    Optional<QueueNode> queueNodeOpt = pathFindingAlgorithm.execute(knowledge, player, colEntry.getValue());

                    if (queueNodeOpt.isPresent()) {
                        QueueNode queueNode = queueNodeOpt.get();

                        if (queueNode.getTile().isCollision()) return false;
                        if (frontier.getQueueNode() == null || (queueNode.getDistance() < frontier.getQueueNode().getDistance())) {
                            frontier.setQueueNode(queueNode);
                        }
                    }
                }
                return false;
            }
        }
        return true;
    }

    private void createNewFrontier(Map.Entry<Integer, Tile> colEntry, List<Optional<Tile>> neighbours){
        Frontier newFrontier = new Frontier(colEntry.getValue());
        frontiers.add(newFrontier);

        addUnknownArea(newFrontier, neighbours);

        // Find the shortest path to this tile
        Optional<QueueNode> queueNodeOpt = pathFindingAlgorithm.execute(knowledge, player, colEntry.getValue());

        if (queueNodeOpt.isPresent()) {
            QueueNode queueNode = queueNodeOpt.get();

            if (queueNode.getTile().isCollision()) return;
            if (newFrontier.getQueueNode() == null || (queueNode.getDistance() < newFrontier.getQueueNode().getDistance())) {
                newFrontier.setQueueNode(queueNode);
            }
        }
    }

    private boolean isFullyKnown(List<Optional<Tile>> neighbours){
        return neighbours.get(0).isPresent() && neighbours.get(1).isPresent() && neighbours.get(2).isPresent() &&
                neighbours.get(3).isPresent();
    }

    private List<Optional<Tile>> getAllNeighbours(Map.Entry<Integer, Tile> colEntry, Map.Entry<Integer, HashMap<Integer, Tile>> rowEntry){
        Optional<Tile> upOpt = BoardUtils.nextPosition(knowledge, colEntry.getValue(), Action.UP);
        Optional<Tile> rightOpt = BoardUtils.nextPosition(knowledge, colEntry.getValue(), Action.RIGHT);
        Optional<Tile> leftOpt = BoardUtils.nextPosition(knowledge, colEntry.getValue(), Action.LEFT);
        Optional<Tile> downOpt = BoardUtils.nextPosition(knowledge, colEntry.getValue(), Action.DOWN);
        Optional<Tile> upLeftOpt = knowledge.getByCoordinates(colEntry.getKey() + Action.LEFT.getxIncrement(), rowEntry.getKey() + Action.UP.getyIncrement());
        Optional<Tile> upRightOpt = knowledge.getByCoordinates(colEntry.getKey() + Action.RIGHT.getxIncrement(), rowEntry.getKey() + Action.UP.getyIncrement());
        Optional<Tile> bottomLeftOpt = knowledge.getByCoordinates(colEntry.getKey() + Action.LEFT.getxIncrement(), rowEntry.getKey() + Action.DOWN.getyIncrement());
        Optional<Tile> bottomRightOpt = knowledge.getByCoordinates(colEntry.getKey() + Action.RIGHT.getxIncrement(), rowEntry.getKey() + Action.DOWN.getyIncrement());

        return Arrays.asList(upOpt, rightOpt, leftOpt, downOpt, upLeftOpt, upRightOpt, bottomLeftOpt, bottomRightOpt);
    }

    @Override
    public IPathFinding getPathFindingAlgorithm(){
        return pathFindingAlgorithm;
    }

    private void addUnknownArea(Frontier frontier, List<Optional<Tile>> neighbours) {
        for(Optional<Tile> opt : neighbours){
            if (opt.isEmpty()) frontier.addUnknownArea();
        }
    }

    public Frontier getChosenFrontier() {
        return chosenFrontier;
    }
}
