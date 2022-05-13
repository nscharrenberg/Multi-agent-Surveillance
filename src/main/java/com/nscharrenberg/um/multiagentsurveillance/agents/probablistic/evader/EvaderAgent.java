package com.nscharrenberg.um.multiagentsurveillance.agents.probablistic.evader;

import com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi.YamauchiAgent;
import com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi.comparator.guard.IWeightComparatorGuard;
import com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi.comparator.guard.MinDistanceUnknownAreaComparator;
import com.nscharrenberg.um.multiagentsurveillance.agents.probablistic.ClosestKnownAgent;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.distanceCalculator.CalculateDistance;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.distanceCalculator.ManhattanDistance;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.pathfinding.AStar.AStar;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.pathfinding.IPathFinding;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.utils.QueueNode;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle.Angle;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Player;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EvaderAgent extends YamauchiAgent {
    private static final int MAX_FLEE_STEPS = 10;
    private static final int MAX_CAUTIOUS_STEPS = 3;
    private SecureRandom random;
    private final IPathFinding pathFindingAlgorithm = new AStar();
    private EvaderState currentState = EvaderState.NORMAL;
    private Tile closestKnownGuard = null;
    private int fleeCounter = 0;
    private int cautiousCounter = 0;

    public EvaderAgent(Player player) {
        super(player);

        try {
            this.random = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private void checkVision() {
        if (player.getVision() == null) return;

        for (Map.Entry<Integer, HashMap<Integer, Tile>> rowEntry : player.getVision().getRegion().entrySet()) {
            for (Map.Entry<Integer, Tile> colEntry : rowEntry.getValue().entrySet()) {
                Tile tile = colEntry.getValue();

                if (tile.hasGuard()) {
                    currentState = EvaderState.FLEE;

                    if (ClosestKnownAgent.isClosestKnownAgent(closestKnownGuard, player, tile)) {
                        closestKnownGuard = tile;
                    }
                }
            }
        }
    }

    private Angle flee() {
        Optional<QueueNode> queueNodeOpt = pathFindingAlgorithm.execute(knowledge, player, closestKnownGuard);

        if (queueNodeOpt.isPresent()) {
            Angle peeked = queueNodeOpt.get().getMoves().peek();

            if (peeked == Angle.UP) {
                return Angle.DOWN;
            } else if (peeked == Angle.DOWN) {
                return Angle.UP;
            } else if (peeked == Angle.RIGHT) {
                return Angle.LEFT;
            } else if (peeked == Angle.LEFT) {
                return Angle.RIGHT;
            }
        }

        return super.decide();
    }

    @Override
    public Angle decide() {
        checkVision();

        if (currentState.equals(EvaderState.FLEE) && closestKnownGuard != null) {
            if (fleeCounter < MAX_FLEE_STEPS) {
                fleeCounter++;
            } else {
                fleeCounter = 0;
                currentState = EvaderState.CAUTIOUS;
            }

            System.out.println("I am fleeing");
            return flee();
        }

        if (currentState.equals(EvaderState.CAUTIOUS)) {
            if (cautiousCounter < MAX_CAUTIOUS_STEPS) {
                cautiousCounter++;
            } else {
                cautiousCounter = 0;
                currentState = EvaderState.NORMAL;
            }

            System.out.println("I am Cautious");
            return super.decide();
        }

        return super.decide();
    }
}
