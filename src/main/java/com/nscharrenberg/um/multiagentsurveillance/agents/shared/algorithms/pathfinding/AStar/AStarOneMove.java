package com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.pathfinding.AStar;

import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.distanceCalculator.CalculateDistance;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.distanceCalculator.ManhattanDistance;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.pathfinding.IPathFinding;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.structures.FibonacciHeap.Fibonacci;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.structures.FibonacciHeap.Node;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.utils.QueueNode;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.utils.TreeNode;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Area;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Player;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.BoardUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;

public class AStarOneMove implements IPathFinding {

    private final CalculateDistance calculateDistance = new ManhattanDistance();

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
            for (Action action : Action.values()) {
                Optional<Tile> nextTileOpt = BoardUtils.nextPosition(board, tree.getTile(), action);


                if (nextTileOpt.isPresent() && !nextTileOpt.get().isCollision() && visited.get(nextTileOpt.get().getX()).get(nextTileOpt.get().getY()).equals(Boolean.FALSE)) {
                    if (!target.isTeleport() && nextTileOpt.get().isTeleport()) {
                        continue;
                    }

                    visited.get(nextTileOpt.get().getX()).put(nextTileOpt.get().getY(), Boolean.TRUE);

                    int distance = (int) calculateDistance.compute(nextTileOpt.get(), target);

                    TreeNode childNode = new TreeNode(nextTileOpt.get(), action, tree);

                    heap.insert(new Node(distance, childNode));                }
            }

            Node currentNode = heap.extractMin();

            if (currentNode == null){
                return Optional.empty();
            }

            tree = currentNode.getTreeNode();
        }



        LinkedList<Action> sequenceMoves = new LinkedList<>();

        TreeNode lastMove = tree;

        int pathCost = 0;

        while (tree.getParent() != null) {
            sequenceMoves.addFirst(tree.getEntrancePosition());
            tree = tree.getParent();
        }


        if (sequenceMoves.isEmpty()) {
            return Optional.empty();
        }

        QueueNode queueNode = new QueueNode(target, lastMove.getEntrancePosition(), sequenceMoves, pathCost);

        return Optional.of(queueNode);
    }
}
