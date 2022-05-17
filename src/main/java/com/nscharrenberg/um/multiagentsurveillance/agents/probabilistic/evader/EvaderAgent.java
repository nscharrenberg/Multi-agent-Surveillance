package com.nscharrenberg.um.multiagentsurveillance.agents.probabilistic.evader;

import com.nscharrenberg.um.multiagentsurveillance.agents.probabilistic.ProbabilisticAgent;
import com.nscharrenberg.um.multiagentsurveillance.agents.probabilistic.State;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.angleCalculator.OppositeAngle;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.angleCalculator.RightAngle;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.pathfinding.AStar.AStar;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.pathfinding.IPathFinding;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.utils.QueueNode;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.BoardNotBuildException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.InvalidTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
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



    private Action flee(boolean foundSomeone, boolean heardSomeone) throws InvalidTileException, BoardNotBuildException {
        if(foundSomeone) {
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
        } else if(heardSomeone && !ignoreSounds){
            Action action = OppositeAngle.getOppositeAngle(closestSound.actionDirection());
            Tile position = getPlayer().getTile();
            Optional<Tile> optTile = getKnowledge().getByCoordinates(position.getX() + action.getxIncrement(), position.getY() + action.getyIncrement());
            if(optTile.isPresent()){
                Tile tile = optTile.get();
                if(tile.isCollision())
                    return RightAngle.getRightAngle(action);
            }
            return action;
        }

        return super.decide();
    }

    @Override
    public Action decide() throws InvalidTileException, BoardNotBuildException {
        boolean foundSomeone = checkVision(this);
        boolean heardSomeone = checkSounds(this);

        if (currentState.equals(State.FLEE) && (foundSomeone || heardSomeone)) {
            if (fleeCounter < MAX_FLEE_STEPS) {
                fleeCounter++;
            } else {
                fleeCounter = 0;
                currentState = State.CAUTIOUS;
            }

            return flee(foundSomeone, heardSomeone);
        }

        if (currentState.equals(State.CAUTIOUS)) {
            if (cautiousCounter < MAX_CAUTIOUS_STEPS) {
                cautiousCounter++;
            } else {
                cautiousCounter = 0;
                currentState = State.NORMAL;
            }

            return super.decide();
        }

        if (mapRepository.getTargetArea().within(player.getTile().getX(), player.getTile().getY())) {
            return Action.PLACE_MARKER_TARGET;
        }

        return super.decide();
    }
}
