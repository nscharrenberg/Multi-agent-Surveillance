package com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi;

import com.nscharrenberg.um.multiagentsurveillance.agents.shared.Agent;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IGameRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IMapRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IPlayerRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.CollisionException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.InvalidTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.ItemAlreadyOnTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.ItemNotOnTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.*;
import com.rits.cloning.Cloner;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;

public class YamauchiAgent extends Agent {
    private List<Frontier> frontiers = new ArrayList<>();
    private Frontier chosenFrontier = null;
    private SecureRandom random;

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
            if (bestFrontier == null) {
                bestFrontier = frontier;
            }

            if (frontier.getQueueNode() != null && frontier.getQueueNode().getDistance() < bestFrontier.getQueueNode().getDistance() && frontier.getUnknownAreas() > bestFrontier.getUnknownAreas()) {
                bestFrontier = frontier;
            }
        }

        if (bestFrontier.getQueueNode() != null) {
            Angle finalPosition = bestFrontier.getQueueNode().getEntrancePosition();

            for (Angle angle : Angle.values()) {
                if (angle.equals(finalPosition)) continue;

                bestFrontier.getQueueNode().getMoves().add(angle);
            }
        }

        chosenFrontier = bestFrontier;

        return Optional.of(bestFrontier);
    }

    private void detectFrontiers() {
        // Clear up all previously found frontiers
        frontiers.clear();
        chosenFrontier = null;

        // Classify each cell by comparing its occupancy probability to the initial (prior) probability assigned to all cells
        // Any open cell adjacent to an unknown cell is labeled a frontier edge cell.

        for (Map.Entry<Integer, HashMap<Integer, Tile>> rowEntry : knowledge.getRegion().entrySet()) {
            for (Map.Entry<Integer, Tile> colEntry : rowEntry.getValue().entrySet()) {
                if (colEntry.getValue().isCollision()) {
                    continue;
                }

                Optional<Tile> upOpt = nextPosition(colEntry.getValue(), Angle.UP);
                Optional<Tile> rightOpt = nextPosition(colEntry.getValue(), Angle.RIGHT);
                Optional<Tile> leftOpt = nextPosition(colEntry.getValue(), Angle.LEFT);
                Optional<Tile> downOpt = nextPosition(colEntry.getValue(), Angle.DOWN);

                if (upOpt.isPresent() && rightOpt.isPresent() && leftOpt.isPresent() && downOpt.isPresent()) {
                    continue;
                }

                if (colEntry.getValue().isCollision() && !colEntry.getValue().getItems().contains(player)) {
                    continue;
                }

                boolean addedTofrontier = false;
                // At least 1 unknown adjacent cel
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

                        Optional<QueueNode> queueNodeOpt = BFS(colEntry.getValue());

                        if (queueNodeOpt.isPresent()) {
                            QueueNode queueNode = queueNodeOpt.get();
                            if (queueNode.getTile().isCollision()) continue;
                            frontier.setQueueNode(queueNode);
                        }

                        addedTofrontier = true;
                        break;
                    }
                }

                if (!addedTofrontier) {
                    if (colEntry.getValue().isCollision()) {
                        continue;
                    }

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

                    Optional<QueueNode> queueNodeOpt = BFS(colEntry.getValue());

                    if (queueNodeOpt.isPresent()) {
                        QueueNode queueNode = queueNodeOpt.get();
                        if (queueNode.getTile().isCollision()) continue;
                        newFrontier.setQueueNode(queueNode);
                    }
                }
            }
        }
    }

    public Optional<QueueNode> BFS(Tile target) {
        // Unable to find any path to target or agent already on target
        if (target.isCollision() || player.getTile().equals(target) || knowledge.isEmpty()) {
            return Optional.empty();
        }

        HashMap<Integer, HashMap<Integer, Boolean>> visited = new HashMap<>();

        // Set all cells in knowledge to not visited
        for (Map.Entry<Integer, HashMap<Integer, Tile>> rowEntry : knowledge.getRegion().entrySet()) {
            if (!visited.containsKey(rowEntry.getKey())) {
                visited.put(rowEntry.getKey(), new HashMap<>());
            }

            for (Map.Entry<Integer, Tile> colEntry : rowEntry.getValue().entrySet()) {
                visited.get(rowEntry.getKey()).put(colEntry.getKey(), Boolean.FALSE);
            }
        }

        if (visited.isEmpty()) {
            return Optional.empty();
        }

        // Set current tile to visited
        if (!visited.containsKey(player.getTile().getX())) {
            visited.put(player.getTile().getX(), new HashMap<>());
        }
        visited.get(player.getTile().getX()).put(player.getTile().getY(), Boolean.TRUE);

        Queue<QueueNode> queue = new LinkedList<>();

        QueueNode s = new QueueNode(player.getTile(), 0, player.getDirection());
        queue.add(s);

        Cloner cloner = new Cloner();

        while (!queue.isEmpty()) {
            QueueNode currentNode = queue.peek();

            if (currentNode.getTile().equals(target)) {
                return Optional.of(currentNode);
            }

            queue.remove();

            for (Angle angle : Angle.values()) {
                Optional<Tile> nextTileOpt = nextPosition(currentNode.getTile(), angle);

                if (nextTileOpt.isPresent() && !nextTileOpt.get().isCollision() && visited.get(nextTileOpt.get().getX()).get(nextTileOpt.get().getY()).equals(Boolean.FALSE)) {
                    visited.get(nextTileOpt.get().getX()).put(nextTileOpt.get().getY(), Boolean.TRUE);

                    QueueNode adj = new QueueNode(nextTileOpt.get(), currentNode.getDistance()+1, angle);

                    adj.setMoves(cloner.deepClone(currentNode.getMoves()));
                    // If player is not looking at this direction, then it takes 2 timesteps to get to this tile
                    if (!currentNode.entrancePosition.equals(angle)) {
                        adj.getMoves().add(angle);
                        adj.setDistance(adj.getDistance() + 1);
                    }
                    adj.getMoves().add(angle);
                    queue.add(adj);
                }
            }
        }

        return Optional.empty();
    }

    private Optional<Tile> nextPosition(Tile tile, Angle direction) {
        int nextX = tile.getX() + direction.getxIncrement();
        int nextY = tile.getY() + direction.getyIncrement();

        Optional<Tile> currentTileOpt = knowledge.getByCoordinates(tile.getX(), tile.getY());

        // Current tile doesn't exist in knowledge --> Shouldn't happen
        if (currentTileOpt.isEmpty()) {
            return Optional.empty();
        }

        Optional<Tile> nextTileOpt = knowledge.getByCoordinates(nextX, nextY);

        if (nextTileOpt.isEmpty()) {
            return Optional.empty();
        }

        return nextTileOpt;
    }

    public Frontier getChosenFrontier() {
        return chosenFrontier;
    }
}
