package com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.pathfinding.AStar;

import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.pathfinding.IPathFinding;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.structures.FibonacciHeap.Fibonacci;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.structures.FibonacciHeap.Node;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.utils.QueueNode;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.utils.TreeNode;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Area;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.BoardUtils;

import java.util.*;

public class AStar implements IPathFinding {
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

        Fibonacci heap = new Fibonacci();

        TreeNode tree = new TreeNode(player.getTile(), player.getDirection(), null);

        board.add(player.getTile());

        while (!tree.getTile().equals(target)) {
            for (Angle angle : Angle.values()) {
                Optional<Tile> nextTileOpt = BoardUtils.nextPosition(board, tree.getTile(), angle);


                if (nextTileOpt.isPresent() && !nextTileOpt.get().isCollision() && !isVisited(tree, nextTileOpt.get()) && visited.get(nextTileOpt.get().getX()).get(nextTileOpt.get().getY()).equals(Boolean.FALSE)) {
                    visited.get(nextTileOpt.get().getX()).put(nextTileOpt.get().getY(), Boolean.TRUE);

                    if(!nextTileOpt.get().equals(target) && nextTileOpt.get().isTeleport())
                        continue;

                    int unknownTiles = 0;
//                    for (Angle angleForNextTile : Angle.values()) {
//                        Optional<Tile> knownTile = BoardUtils.nextPosition(board, nextTileOpt.get(), angleForNextTile);
//                        if(knownTile.isEmpty())
//                            unknownTiles++;
//
//                    }
                    int distance = computeDistance(nextTileOpt.get(), target) - unknownTiles;

                    TreeNode childNode = new TreeNode(nextTileOpt.get(), angle, tree);


                    if (!tree.getEntrancePosition().equals(childNode.getEntrancePosition())) {
                        TreeNode additionalChildNode = new TreeNode(nextTileOpt.get(), angle, childNode);

                        if(nextTileOpt.get().equals(target)){
                            tree = additionalChildNode;
                            break;
                        }

                        heap.insert(new Node(distance+1, additionalChildNode));
                    } else {
                        heap.insert(new Node(distance, childNode));
                    }


                }
            }

            Node currentNode = heap.extractMin();

            if (currentNode == null) break;

            tree = currentNode.getTreeNode();
        }

        LinkedList<Angle> sequenceMoves = new LinkedList<>();

        TreeNode lastMove = tree;

        if(tree.getParent() == null)
            sequenceMoves.addFirst(tree.getEntrancePosition());

        while (tree.getParent() != null) {
            sequenceMoves.addFirst(tree.getEntrancePosition());
            tree = tree.getParent();
        }

        Queue<Angle> queue = new LinkedList<>(sequenceMoves);

        if (sequenceMoves.isEmpty()) {
            return Optional.empty();
        }

        QueueNode queueNode = new QueueNode(lastMove.getTile(), tree.getEntrancePosition(), queue);

        return Optional.of(queueNode);
    }

    private boolean isVisited(TreeNode tree, Tile tile){
        if(tree.getParent() == null || tree.getParent().getParent() == null) {
            return false;
        } else {
            return tree.getParent().getTile().equals(tile) || tree.getParent().getParent().getTile().equals(tile);
        }
    }

    private int computeDistance(Tile tileX, Tile tileY){
        int x = Math.abs(tileX.getX() - tileY.getX());
        int y = Math.abs(tileX.getY() - tileY.getY());
        return x + y;
    }
}
