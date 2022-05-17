package com.nscharrenberg.um.multiagentsurveillance.agents.probabilistic.evader;

import com.nscharrenberg.um.multiagentsurveillance.agents.probabilistic.ProbabilisticAgent;
import com.nscharrenberg.um.multiagentsurveillance.agents.probabilistic.State;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.pathfinding.AStar.AStar;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.pathfinding.IPathFinding;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.utils.QueueNode;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.BoardNotBuildException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.InvalidTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Player;
import java.util.Optional;

public class EvaderAgent extends ProbabilisticAgent {
    private static final int MAX_FLEE_STEPS = 10;
    private static final int MAX_CAUTIOUS_STEPS = 3;
    private final IPathFinding pathFindingAlgorithm = new AStar();
    private int fleeCounter = 0;
    private int cautiousCounter = 0;

    public EvaderAgent(Player player) {
        super(player);
    }



    private Action flee() throws InvalidTileException, BoardNotBuildException {
        Optional<QueueNode> queueNodeOpt = pathFindingAlgorithm.execute(knowledge, player, closestKnownAgent);

        if (queueNodeOpt.isPresent()) {
            Action peeked = queueNodeOpt.get().getMoves().peek();

            if (peeked == Action.UP) {
                return Action.DOWN;
            } else if (peeked == Action.DOWN) {
                return Action.UP;
            } else if (peeked == Action.RIGHT) {
                return Action.LEFT;
            } else if (peeked == Action.LEFT) {
                return Action.RIGHT;
            }
        }

        return super.decide();
    }

    @Override
    public Action decide() throws InvalidTileException, BoardNotBuildException {
        checkVision(this);

        if (currentState.equals(State.FLEE) && closestKnownAgent != null) {
            if (fleeCounter < MAX_FLEE_STEPS) {
                fleeCounter++;
            } else {
                fleeCounter = 0;
                currentState = State.CAUTIOUS;
            }

            System.out.println("I am fleeing");
            return flee();
        }

        if (currentState.equals(State.CAUTIOUS)) {
            if (cautiousCounter < MAX_CAUTIOUS_STEPS) {
                cautiousCounter++;
            } else {
                cautiousCounter = 0;
                currentState = State.NORMAL;
            }

            System.out.println("I am Cautious");
            return super.decide();
        }

        return super.decide();
    }
}
