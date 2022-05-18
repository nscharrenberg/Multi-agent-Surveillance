package com.nscharrenberg.um.multiagentsurveillance.agents.probabilistic.pursuer;

import com.nscharrenberg.um.multiagentsurveillance.agents.probabilistic.ProbabilisticAgent;
import com.nscharrenberg.um.multiagentsurveillance.agents.probabilistic.State;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.pathfinding.AStar.AStar;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.pathfinding.IPathFinding;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.utils.QueueNode;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IGameRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IMapRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IPlayerRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.BoardNotBuildException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.InvalidTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Guard;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Player;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

public class PursuerAgent extends ProbabilisticAgent {
    private static final int MAX_SEARCH_STEPS = 3;
    private final IPathFinding pathFindingAlgorithm = new AStar();
    private int searchCounter = 0;
    private Queue<Action> huntingSteps = new LinkedList<>();

    public PursuerAgent(Player player) {
        super(player);
    }

    public PursuerAgent(Player player, IMapRepository mapRepository, IGameRepository gameRepository, IPlayerRepository playerRepository) {
        super(player, mapRepository, gameRepository, playerRepository);
    }

    private Action hunt(boolean foundSomeone, boolean heardSomeone) throws InvalidTileException, BoardNotBuildException {
        if(foundSomeone) {
            Optional<QueueNode> queueNodeOpt = pathFindingAlgorithm.execute(knowledge, player, closestKnownAgent);
            if (queueNodeOpt.isPresent()) {
                huntingSteps = queueNodeOpt.get().getMoves();

                return huntingSteps.poll();
            }
        } else if(heardSomeone && !ignoreSounds) {

            for (int i = 0; i < 3; i++) {
                huntingSteps.add(closestSound.actionDirection());
            }

            return huntingSteps.poll();
        }

        return super.decide();
    }

    @Override
    public Action decide() throws InvalidTileException, BoardNotBuildException {
        boolean foundSomeone = checkVision(this);
        boolean heardSomeone = checkSounds(this);

        if (currentState.equals(State.HUNT) && (foundSomeone || heardSomeone)) {

            if (!huntingSteps.isEmpty()) {
                return huntingSteps.poll();
            }

            return hunt(foundSomeone, heardSomeone);
        }

        if (currentState.equals(State.SEARCH)) {
            if (searchCounter < MAX_SEARCH_STEPS) {
                searchCounter++;
            } else {
                searchCounter = 0;
                currentState = State.NORMAL;
            }

            return super.decide();
        }

        return super.decide();
    }
}
