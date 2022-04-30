package com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.pathfinding.BFS;

import com.nscharrenberg.um.multiagentsurveillance.agents.shared.utils.QueueNode;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.pathfinding.IPathFinding;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.*;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.BoardUtils;
import com.rits.cloning.Cloner;

import java.util.*;

public class BFS implements IPathFinding {
    @Override
    public Optional<QueueNode> execute(Area<Tile> board, Player player, Tile target) {
        if (target.isCollision() || player.getTile().equals(target) || board.isEmpty()) {
            return Optional.empty();
        }

        HashMap<Integer, HashMap<Integer, Boolean>> visited = new HashMap<>();

        // Set all cells in knowledge to not visited
        for (Map.Entry<Integer, HashMap<Integer, Tile>> rowEntry : board.getRegion().entrySet()) {
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

        // Current tile always explored
        if (!visited.containsKey(player.getTile().getX())) {
            visited.put(player.getTile().getX(), new HashMap<>());
        }
        visited.get(player.getTile().getX()).put(player.getTile().getY(), Boolean.TRUE);

        Queue<QueueNode> queue = new LinkedList<>();

        QueueNode s = new QueueNode(player.getTile(), player.getDirection());
        queue.add(s);

        Cloner cloner = new Cloner();

        while (!queue.isEmpty()) {
            QueueNode currentNode = queue.poll();

            if (currentNode.getTile().equals(target)) {
                return  Optional.of(currentNode);
            }

            for (Action action : Action.values()) {
                Optional<Tile> nextTileOpt = BoardUtils.nextPosition(board, currentNode.getTile(), action);

                if (nextTileOpt.isPresent() && !nextTileOpt.get().isCollision() && visited.get(nextTileOpt.get().getX()).get(nextTileOpt.get().getY()).equals(Boolean.FALSE)) {
                    if (!target.isTeleport() && nextTileOpt.get().isTeleport()) {
                        continue;
                    }

                    visited.get(nextTileOpt.get().getX()).put(nextTileOpt.get().getY(), Boolean.TRUE);

                    QueueNode adj = new QueueNode(nextTileOpt.get(), action, cloner.deepClone(currentNode.getMoves()), 0);

                    // If player is not looking at this direction, then it takes 2 timesteps to get to this tile
                    if (!currentNode.getEntrancePosition().equals(action)) {
                        adj.getMoves().add(action);
                    }

                    adj.getMoves().add(action);
                    queue.add(adj);
                }
            }
        }


        return Optional.empty();
    }
}
