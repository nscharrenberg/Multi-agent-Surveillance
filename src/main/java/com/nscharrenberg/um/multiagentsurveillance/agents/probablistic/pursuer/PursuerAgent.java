package com.nscharrenberg.um.multiagentsurveillance.agents.probablistic.pursuer;

import com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi.YamauchiAgent;
import com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi.comparator.IWeightComparator;
import com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi.comparator.MinDistanceUnknownAreaComparator;
import com.nscharrenberg.um.multiagentsurveillance.agents.probablistic.evader.EvaderState;
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

public class PursuerAgent extends YamauchiAgent {
    private static int MAX_SEARCH_STEPS = 3;
    private SecureRandom random;
    private final IPathFinding pathFindingAlgorithm = new AStar();
    private final IWeightComparator weightDetector = new MinDistanceUnknownAreaComparator();
    private final CalculateDistance calculateDistance = new ManhattanDistance();
    private PursuerState currentState = PursuerState.NORMAL;
    private Tile closestKnownIntruder = null;
    private int searchCounter = 0;
    private QueueNode huntingSteps = null;

    public PursuerAgent(Player player) {
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

                if (tile.hasIntruder()) {
                    currentState = PursuerState.HUNT;

                    if (closestKnownIntruder == null
                            || Math.abs(player.getTile().getX() - tile.getX()) < Math.abs(player.getTile().getX() - closestKnownIntruder.getX())
                            || Math.abs(player.getTile().getX() - tile.getX()) < Math.abs(player.getTile().getY() - closestKnownIntruder.getY())
                            || Math.abs(player.getTile().getY() - tile.getY()) < Math.abs(player.getTile().getX() - closestKnownIntruder.getX())
                            || Math.abs(player.getTile().getY() - tile.getY()) < Math.abs(player.getTile().getY() - closestKnownIntruder.getY())) {
                        closestKnownIntruder = tile;
                    }
                }
            }
        }
    }

    private Angle hunt() {
        Optional<QueueNode> queueNodeOpt = pathFindingAlgorithm.execute(knowledge, player, closestKnownIntruder);

        if (queueNodeOpt.isPresent()) {
            huntingSteps = queueNodeOpt.get();

            return huntingSteps.getMoves().poll();
        }

        return super.decide();
    }

    @Override
    public Angle decide() {
        checkVision();

        if (currentState.equals(PursuerState.HUNT) && closestKnownIntruder != null && huntingSteps != null) {
            System.out.println("I am hunting");
            Angle h = hunt();

            if (huntingSteps.getMoves().isEmpty()) {
                huntingSteps = null;
            }
        }

        if (currentState.equals(PursuerState.SEARCH)) {
            if (searchCounter < MAX_SEARCH_STEPS) {
                searchCounter++;
            } else {
                searchCounter = 0;
                currentState = PursuerState.NORMAL;
            }

            System.out.println("I am searching");
            return super.decide();
        }

        return super.decide();
    }
}
