package com.nscharrenberg.um.multiagentsurveillance.agents.probabilistic.pursuer;

import com.nscharrenberg.um.multiagentsurveillance.agents.probabilistic.ProbabilisticAgent;
import com.nscharrenberg.um.multiagentsurveillance.agents.probabilistic.State;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.pathfinding.AStar.AStar;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.pathfinding.IPathFinding;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.utils.QueueNode;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle.Angle;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Player;
import java.util.Optional;

public class PursuerAgent extends ProbabilisticAgent {
    private static final int MAX_SEARCH_STEPS = 3;
    private final IPathFinding pathFindingAlgorithm = new AStar();
    private int searchCounter = 0;
    private QueueNode huntingSteps = null;

    public PursuerAgent(Player player) {
        super(player);
    }


    private Angle hunt() {
        Optional<QueueNode> queueNodeOpt = pathFindingAlgorithm.execute(knowledge, player, closestKnownAgent);

        if (queueNodeOpt.isPresent()) {
            huntingSteps = queueNodeOpt.get();

            return huntingSteps.getMoves().poll();
        }

        return super.decide();
    }

    @Override
    public Angle decide() {
        checkVision(this);

        if (currentState.equals(State.HUNT) && closestKnownAgent != null && huntingSteps != null) {
            System.out.println("I am hunting");
            Angle h = hunt();

            if (huntingSteps.getMoves().isEmpty()) {
                huntingSteps = null;
            }
        }

        if (currentState.equals(State.SEARCH)) {
            if (searchCounter < MAX_SEARCH_STEPS) {
                searchCounter++;
            } else {
                searchCounter = 0;
                currentState = State.NORMAL;
            }

            System.out.println("I am searching");
            return super.decide();
        }

        return super.decide();
    }
}
