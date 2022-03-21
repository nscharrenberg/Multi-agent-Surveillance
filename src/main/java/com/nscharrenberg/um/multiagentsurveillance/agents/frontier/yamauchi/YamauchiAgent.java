package com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi;

import com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi.comparator.IWeightComparator;
import com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi.comparator.MinDistanceUnknownAreaComparator;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.Agent;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.pathfinding.AStar.AStar;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.pathfinding.IPathFinding;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.utils.QueueNode;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IGameRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IMapRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IPlayerRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.CollisionException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.InvalidTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.ItemAlreadyOnTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.ItemNotOnTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.*;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.BoardUtils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;

public class YamauchiAgent extends Agent {
    private final List<Frontier> frontiers = new ArrayList<>();
    private Frontier chosenFrontier = null;
    private SecureRandom random;
    private final IPathFinding pathFindingAlgorithm = new AStar();
    private final IWeightComparator weightDetector = new MinDistanceUnknownAreaComparator();

    private int consecutiveNoFrontier = 0;
    private static final int MAX_CONSECUTIVE_NO_FRONTIER_COUNT = 3;

    public YamauchiAgent(Player player) {
        super(player);
        try {
            this.random = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public YamauchiAgent(Player player, IMapRepository mapRepository, IGameRepository gameRepository, IPlayerRepository playerRepository) {
        super(player, mapRepository, gameRepository, playerRepository);
        try {
            this.random = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public YamauchiAgent(Player player, Area<Tile> knowledge, Queue<Angle> plannedMoves, IMapRepository mapRepository, IGameRepository gameRepository, IPlayerRepository playerRepository) {
        super(player, knowledge, plannedMoves, mapRepository, gameRepository, playerRepository);
        try {
            this.random = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void execute(Angle angle) {
        try {
            playerRepository.move(player, angle);
        } catch (CollisionException | InvalidTileException | ItemNotOnTileException e) {
            System.out.println(e.getMessage());

            // If any of the above errors is thrown we can't continue with our planned moves, and need to recalculate our frontiers
            plannedMoves.clear();
            frontiers.clear();
            detectFrontiers();
        } catch (ItemAlreadyOnTileException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Angle decide() {
        // If moves are alread planned just continue deciding them.
        if (!plannedMoves.isEmpty()) {
            return plannedMoves.poll();
        }

        frontiers.clear();

        Optional<Frontier> chosenFrontierOpt = pickBestFrontier();

        // No Frontier found, just do a random move for now
        if (chosenFrontierOpt.isEmpty() || chosenFrontierOpt.get().getQueueNode() == null) {
            consecutiveNoFrontier++;
            int value = this.random.nextInt(100);

            Angle move = player.getDirection();

            Optional<Tile> nextTileOpt = knowledge.getByCoordinates(player.getTile().getX() + move.getxIncrement(), player.getTile().getY() + player.getDirection().getyIncrement());

            boolean nextBlocked = false;
            if (nextTileOpt.isPresent()) {
                Tile nextTile = nextTileOpt.get();

                for (Item items : nextTile.getItems()) {
                    if (items instanceof Collision) {
                        nextBlocked = true;
                        break;
                    }
                }
            }

            if (value <= 30 || nextBlocked) {
                int pick = this.random.nextInt(Angle.values().length);
                move = Angle.values()[pick];
            }

            return move;
        }

        consecutiveNoFrontier = 0;

        Frontier chosenFrontier = chosenFrontierOpt.get();

        plannedMoves = chosenFrontier.getQueueNode().getMoves();

        return plannedMoves.poll();
    }

    private Optional<Frontier> pickBestFrontier() {
        if (frontiers.isEmpty() && plannedMoves.isEmpty()) {
            detectFrontiers();
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



            bestFrontier = weightDetector.compare(frontier, bestFrontier);

        }

        if (bestFrontier == null || bestFrontier.getQueueNode() == null) {
            return Optional.empty();
        }

        if (bestFrontier.getQueueNode().getTile().isCollision()) {
            return Optional.empty();
        }

        Angle finalPosition = bestFrontier.getQueueNode().getEntrancePosition();

//        for (Angle angle : Angle.values()) {
//            if (angle.equals(finalPosition)) continue;
//            bestFrontier.getQueueNode().getMoves().add(angle);
//        }

        chosenFrontier = bestFrontier;

        return Optional.of(bestFrontier);
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

                // Reject if all tile is surrounded by collision objects or teleporters
                HashMap<AdvancedAngle, Tile> neighbours = BoardUtils.getNeighbours(knowledge, colEntry.getValue());

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

                if (isInaccessible) {
                    continue;
                }

                if (colEntry.getValue().isTeleport() && consecutiveNoFrontier >= MAX_CONSECUTIVE_NO_FRONTIER_COUNT && (possibleTeleport == null
                        || (Math.abs(player.getTile().getX() - colEntry.getValue().getX()) < Math.abs(player.getTile().getX() - possibleTeleport.getX()) && Math.abs(player.getTile().getX() - colEntry.getValue().getY()) < Math.abs(player.getTile().getX() - possibleTeleport.getY()))
                        || (Math.abs(player.getTile().getX() - colEntry.getValue().getX()) < Math.abs(player.getTile().getX() - possibleTeleport.getX()))
                        || (Math.abs(player.getTile().getX() - colEntry.getValue().getY()) < Math.abs(player.getTile().getX() - possibleTeleport.getY())))) {
                    possibleTeleport = colEntry.getValue();
                }

                // Check if it is a fully known tile

                Optional<Tile> upOpt = BoardUtils.nextPosition(knowledge, colEntry.getValue(), Angle.UP);
                Optional<Tile> rightOpt = BoardUtils.nextPosition(knowledge, colEntry.getValue(), Angle.RIGHT);
                Optional<Tile> leftOpt = BoardUtils.nextPosition(knowledge, colEntry.getValue(), Angle.LEFT);
                Optional<Tile> downOpt = BoardUtils.nextPosition(knowledge, colEntry.getValue(), Angle.DOWN);
                Optional<Tile> upLeftOpt = knowledge.getByCoordinates(colEntry.getKey() + Angle.LEFT.getxIncrement(), rowEntry.getKey() + Angle.UP.getyIncrement());
                Optional<Tile> upRightOpt = knowledge.getByCoordinates(colEntry.getKey() + Angle.RIGHT.getxIncrement(), rowEntry.getKey() + Angle.UP.getyIncrement());
                Optional<Tile> bottomLeftOpt = knowledge.getByCoordinates(colEntry.getKey() + Angle.LEFT.getxIncrement(), rowEntry.getKey() + Angle.DOWN.getyIncrement());
                Optional<Tile> bottomRightOpt = knowledge.getByCoordinates(colEntry.getKey() + Angle.RIGHT.getxIncrement(), rowEntry.getKey() + Angle.DOWN.getyIncrement());

                if (upOpt.isPresent() && rightOpt.isPresent() && leftOpt.isPresent()
                        && downOpt.isPresent()) {
                    continue;
                }

                boolean isAdded = false;

                for (Frontier frontier : frontiers) {
                    if (frontier.add(colEntry.getValue())) {
                        addUnknownArea(frontier,upOpt);
                        addUnknownArea(frontier, downOpt);
                        addUnknownArea(frontier, leftOpt);
                        addUnknownArea(frontier, rightOpt);
                        addUnknownArea(frontier, upRightOpt);
                        addUnknownArea(frontier, upLeftOpt);
                        addUnknownArea(frontier, bottomLeftOpt);
                        addUnknownArea(frontier, bottomRightOpt);

                        // Find the shortest path to this tile
                        Optional<QueueNode> queueNodeOpt = pathFindingAlgorithm.execute(knowledge, player, colEntry.getValue());

                        if (queueNodeOpt.isPresent()) {
                            QueueNode queueNode = queueNodeOpt.get();

                            if (queueNode.getTile().isCollision()) continue;
                            if (frontier.getQueueNode() == null || (queueNode.getDistance() < frontier.getQueueNode().getDistance())) {
                                frontier.setQueueNode(queueNode);
                            }
                        }

                        isAdded = true;
                        break;
                    }
                }

                if (!isAdded) {
                    Frontier newFrontier = new Frontier(colEntry.getValue());
                    frontiers.add(newFrontier);

                    addUnknownArea(newFrontier, upOpt);
                    addUnknownArea(newFrontier, downOpt);
                    addUnknownArea(newFrontier, leftOpt);
                    addUnknownArea(newFrontier, rightOpt);
                    addUnknownArea(newFrontier, upRightOpt);
                    addUnknownArea(newFrontier, upLeftOpt);
                    addUnknownArea(newFrontier, bottomLeftOpt);
                    addUnknownArea(newFrontier, bottomRightOpt);

                    // Find the shortest path to this tile
                    Optional<QueueNode> queueNodeOpt = pathFindingAlgorithm.execute(knowledge, player, colEntry.getValue());

                    if (queueNodeOpt.isPresent()) {
                        QueueNode queueNode = queueNodeOpt.get();

                        if (queueNode.getTile().isCollision()) continue;
                        if (newFrontier.getQueueNode() == null || (queueNode.getDistance() < newFrontier.getQueueNode().getDistance())) {
                            newFrontier.setQueueNode(queueNode);
                        }
                    }
                }
            }
        }

        // If No frontiers are found but teleporter is in knowledge, go to teleporter.
        if (chosenFrontier == null && possibleTeleport != null && consecutiveNoFrontier >= MAX_CONSECUTIVE_NO_FRONTIER_COUNT) {
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

    private void addUnknownArea(Frontier frontier, Optional<Tile> opt) {
        if (opt.isEmpty()) frontier.addUnknownArea();
    }

    public Frontier getChosenFrontier() {
        return chosenFrontier;
    }
}
