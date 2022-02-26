package com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi;

import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.pathfinding.AStar.AStar;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.pathfinding.BFS.BFS;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.pathfinding.IPathFinding;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.structures.FibonacciHeap.Fibonacci;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.structures.FibonacciHeap.Node;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.Agent;
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
    private List<Frontier> frontiers = new ArrayList<>();
    private Frontier chosenFrontier = null;
    private SecureRandom random;
    private static IPathFinding pathFindingAlgorithm = new AStar();

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
            if (frontier.getQueueNode() == null) {
                continue;
            }

            if (bestFrontier == null) {
                bestFrontier = frontier;
            } else if ((frontier.getQueueNode().getDistance() * frontier.getUnknownAreas()) < (bestFrontier.getQueueNode().getDistance() * bestFrontier.getUnknownAreas())) {
                bestFrontier = frontier;
            }
        }

        if (bestFrontier == null || bestFrontier.getQueueNode() == null) {
            return Optional.empty();
        }

        if (bestFrontier.getQueueNode().getTile().isCollision()) {
            return Optional.empty();
        }

        Angle finalPosition = bestFrontier.getQueueNode().getEntrancePosition();

        for (Angle angle : Angle.values()) {
            if (angle.equals(finalPosition)) continue;
            bestFrontier.getQueueNode().getMoves().add(angle);
        }

        chosenFrontier = bestFrontier;

        return Optional.of(bestFrontier);
    }

    private void detectFrontiers() {
        frontiers.clear();
        chosenFrontier = null;

        for (Map.Entry<Integer, HashMap<Integer, Tile>> rowEntry : knowledge.getRegion().entrySet()) {
            for (Map.Entry<Integer, Tile> colEntry : rowEntry.getValue().entrySet()) {
                // Reject tile if its a collidable object
                if (colEntry.getValue().isCollision() && !colEntry.getValue().getItems().contains(player)) {
                    continue;
                }

                // Check if it is a fully known tile

                Optional<Tile> upOpt = BoardUtils.nextPosition(knowledge, colEntry.getValue(), Angle.UP);
                Optional<Tile> rightOpt = BoardUtils.nextPosition(knowledge, colEntry.getValue(), Angle.RIGHT);
                Optional<Tile> leftOpt = BoardUtils.nextPosition(knowledge, colEntry.getValue(), Angle.LEFT);
                Optional<Tile> downOpt = BoardUtils.nextPosition(knowledge, colEntry.getValue(), Angle.DOWN);

                if (upOpt.isPresent() && rightOpt.isPresent() && leftOpt.isPresent() && downOpt.isPresent()) {
                    continue;
                }

                boolean isAdded = false;

                for (Frontier frontier : frontiers) {
                    if (frontier.add(colEntry.getValue())) {
                        if (upOpt.isEmpty()) {
                            frontier.addUnknownArea();
                        }
                        if (downOpt.isEmpty()) {
                            frontier.addUnknownArea();
                        }
                        if (leftOpt.isEmpty()) {
                            frontier.addUnknownArea();
                        }
                        if (rightOpt.isEmpty()) {
                            frontier.addUnknownArea();
                        }

                        // Find the shortest path to this tile
                        Optional<QueueNode> queueNodeOpt = pathFindingAlgorithm.execute(knowledge, player, colEntry.getValue());

                        if (queueNodeOpt.isPresent()) {
                            QueueNode queueNode = queueNodeOpt.get();

                            if (queueNode.getTile().isCollision()) continue;
                            if (frontier.getQueueNode() == null) {
                                frontier.setQueueNode(queueNode);
                            } else if (queueNode.getDistance() < frontier.getQueueNode().getDistance()) {
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

                    if (upOpt.isEmpty()) {
                        newFrontier.addUnknownArea();
                    }
                    if (downOpt.isEmpty()) {
                        newFrontier.addUnknownArea();
                    }
                    if (leftOpt.isEmpty()) {
                        newFrontier.addUnknownArea();
                    }
                    if (rightOpt.isEmpty()) {
                        newFrontier.addUnknownArea();
                    }

                    // Find the shortest path to this tile
                    Optional<QueueNode> queueNodeOpt = pathFindingAlgorithm.execute(knowledge, player, colEntry.getValue());

                    if (queueNodeOpt.isPresent()) {
                        QueueNode queueNode = queueNodeOpt.get();

                        if (queueNode.getTile().isCollision()) continue;
                        if (newFrontier.getQueueNode() == null) {
                            newFrontier.setQueueNode(queueNode);
                        } else if (queueNode.getDistance() < newFrontier.getQueueNode().getDistance()) {
                            newFrontier.setQueueNode(queueNode);
                        }
                    }
                }
            }
        }
    }

    private int computeDistanceBetween(Tile tileX, Tile tileY){
        int x = Math.abs(tileX.getX() - tileY.getX());
        int y = Math.abs(tileX.getY() - tileY.getY());
        return x + y;
    }

    public Frontier getChosenFrontier() {
        return chosenFrontier;
    }
}
