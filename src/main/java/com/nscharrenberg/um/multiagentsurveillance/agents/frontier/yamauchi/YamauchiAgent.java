package com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi;

import com.nscharrenberg.um.multiagentsurveillance.agents.shared.Agent;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IGameRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IMapRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IPlayerRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.*;
import com.rits.cloning.Cloner;

import java.util.*;

public class YamauchiAgent extends Agent {
    private List<Frontier> frontiers = new ArrayList<>();
    private int frontierRadius = 3;

    public YamauchiAgent(Player player) {
        super(player);
    }

    public YamauchiAgent(Player player, IMapRepository mapRepository, IGameRepository gameRepository, IPlayerRepository playerRepository) {
        super(player, mapRepository, gameRepository, playerRepository);
    }

    public YamauchiAgent(Player player, Area<Tile> knowledge, Queue<Angle> plannedMoves, IMapRepository mapRepository, IGameRepository gameRepository, IPlayerRepository playerRepository) {
        super(player, knowledge, plannedMoves, mapRepository, gameRepository, playerRepository);
    }

    @Override
    public void execute(Angle angle) {

    }

    @Override
    public Angle decide() {
        return null;
    }

    private void detectFrontiers() {
        // Clear up all previously found frontiers
        frontiers.clear();

        // Classify each cell by comparing its occupancy probability to the initial (prior) probability assigned to all cells
        // Any open cell adjacent to an unknown cell is labeled a frontier edge cell.

        for (Map.Entry<Integer, HashMap<Integer, Tile>> rowEntry : knowledge.getRegion().entrySet()) {
            for (Map.Entry<Integer, Tile> colEntry : rowEntry.getValue().entrySet()) {
                Optional<Tile> upOpt = nextPosition(colEntry.getValue(), Angle.UP);
                Optional<Tile> rightOpt = nextPosition(colEntry.getValue(), Angle.RIGHT);
                Optional<Tile> leftOpt = nextPosition(colEntry.getValue(), Angle.LEFT);
                Optional<Tile> downOpt = nextPosition(colEntry.getValue(), Angle.DOWN);

                if (upOpt.isPresent() && rightOpt.isPresent() && leftOpt.isPresent() && downOpt.isPresent()) {
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

                        addedTofrontier = true;
                        break;
                    }
                }

                if (!addedTofrontier) {
                    Frontier newFrontier = new Frontier(colEntry.getValue());
                    frontiers.add(new Frontier());

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
        visited.get(player.getTile().getX()).put(player.getTile().getY(), Boolean.TRUE);

        Queue<QueueNode> queue = new LinkedList<>();

        QueueNode s = new QueueNode(player.getTile(), 0);
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

                    QueueNode adj = new QueueNode(nextTileOpt.get(), currentNode.getDistance()+1);
                    adj.setTiles(cloner.deepClone(currentNode.getTiles()));
                    adj.getTiles().add(adj.getTile());
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
}
