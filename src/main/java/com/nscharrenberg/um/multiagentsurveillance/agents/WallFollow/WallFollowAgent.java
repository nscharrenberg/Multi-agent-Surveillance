package com.nscharrenberg.um.multiagentsurveillance.agents.WallFollow;

import com.nscharrenberg.um.multiagentsurveillance.agents.shared.Agent;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.angleCalculator.ComputeDoubleAngleTiles;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.angleCalculator.LeftAngle;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.angleCalculator.RightAngle;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IGameRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IMapRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IPlayerRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.*;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.Collision.Collision;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.Item;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Player;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.BoardUtils;

import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WallFollowAgent extends Agent {

    public enum TurnType
    {
        LEFT,
        RIGHT,
        NO_TURN,
    }
    private TurnType lastTurn = TurnType.NO_TURN;
    private boolean movedForwardLast = false;
    private boolean explorationDone = false;
    private boolean noMovesDone = true;
    private boolean wallEncountered = false;
    private boolean hasLeftInitialWallFollowPos = false;
    private Tile initialWallFollowPos = null;
    private boolean initialVertexFound = false;
    private ArrayList<Tile> lastPositions = new ArrayList<>();
    private boolean isMoveFailed = false;
    private Tile currentTargetVertex = null;
    private List<Tile> currentPathToNextVertex = null;
    private ArrayList<Tile> inaccessibleCells = new ArrayList<>();
    protected WfMap wfMap;
    protected double directionHeuristicWeight = 1;
    private Tile prevAgentVertex = null;


    public WallFollowAgent(Player agent) {
        super(agent);
    }

    public WallFollowAgent(Player agent, IMapRepository mapRepository, IGameRepository gameRepository, IPlayerRepository playerRepository) {
        super(agent, mapRepository, gameRepository, playerRepository);
        wfMap = new WfMap(new WfGraph(1));
        Tile position=player.getTile();
        wfMap.add_or_adjust_Vertex(position);
        lastPositions.add((position));
        prevAgentVertex = position;
        wfMap.add_or_adjust_Vertex(position);
        player.setDirection(randDirection());
    }

    @Override
    public void execute(Action move) {
        try {
            playerRepository.move(player, move);
        } catch (CollisionException | InvalidTileException | ItemNotOnTileException | ItemAlreadyOnTileException | BoardNotBuildException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Action decide() {
        Action move = player.getDirection();
        Tile currentPosition=player.getTile();
        if (explorationDone) {
            return move;
        }
        if (!noMovesDone && !isMoveFailed) {
            updateGraphAfterSuccessfulMove();
        }
        if (!hasLeftInitialWallFollowPos && initialWallFollowPos != null &&
                !initialWallFollowPos.equals(currentPosition)) {
            hasLeftInitialWallFollowPos = true;
        }
        if (!initialVertexFound && hasLeftInitialWallFollowPos && initialWallFollowPos != null &&
                initialWallFollowPos.equals(currentPosition)) {
            initialVertexFound = true;
        }

        if (isMoveFailed) {
            move = handleFailedMove();
            currentTargetVertex = null;
            currentPathToNextVertex = null;
        } else if (wallEncountered && agentStuckInTile(currentPosition)) {
            Optional<Tile> forwardTile = BoardUtils.nextPosition(mapRepository.getBoard(),currentPosition,Action.UP);
            if(forwardTile.isPresent()) {
                if (noWallDetected(forwardTile.get())) {
                    movedForwardLast = true;
                    lastTurn = TurnType.NO_TURN;
                } else {
                    move = RightAngle.getRightAngle(move);
                    lastTurn = TurnType.RIGHT;
                    movedForwardLast = false;
                }
                wallEncountered = false;
                currentTargetVertex = null;
                currentPathToNextVertex = null;
                hasLeftInitialWallFollowPos = false;
            }else{
                isMoveFailed=true;
            }
        } else if (currentPathToNextVertex != null && !foundUnexploredWallToFollow()) {
            move = getMoveBasedOnPath();
        } else if ((currentPathToNextVertex != null || initialVertexFound) && foundUnexploredWallToFollow()) {
            currentPathToNextVertex = null;
            currentTargetVertex = null;
            move = runWallFollowAlgorithm();
            wallEncountered = true;
            initialVertexFound = false;
            hasLeftInitialWallFollowPos = false;
            initialWallFollowPos = player.getTile();
        } else if (initialVertexFound || agentInStuckMovement()) {
            move = runHeuristicsAlgorithm();
            wallEncountered = false;
            hasLeftInitialWallFollowPos = false;
        } else if (!wallEncountered) {
            Optional<Tile> forwardTile = BoardUtils.nextPosition(mapRepository.getBoard(),currentPosition,Action.UP);
            Optional<Tile> leftTile = BoardUtils.nextPosition(mapRepository.getBoard(),currentPosition,Action.LEFT);
            if(forwardTile.isPresent()&&leftTile.isPresent()) {
                if (!noWallDetected(forwardTile.get())) {
                    // TODO: check if the wall encountered is already being covered by someone else?
                    move = RightAngle.getRightAngle(move);
                    lastTurn = TurnType.RIGHT;
                    movedForwardLast = false;
                    wallEncountered = true;
                    initialWallFollowPos = player.getTile();
                } else if (!noWallDetected(leftTile.get())) {
                    wallEncountered = true;
                    initialWallFollowPos = player.getTile();
                } else {
                    movedForwardLast = true;
                    lastTurn = TurnType.NO_TURN;
                }
            }else {
                isMoveFailed=true;
            }
        } else {
            move= runWallFollowAlgorithm();
        }
        Optional<Tile> nextTile = BoardUtils.nextPosition(mapRepository.getBoard(),currentPosition,move);
        if(nextTile.isPresent()){
            if (!noWallDetected(nextTile.get())) {
                isMoveFailed=true;
            }else{
                isMoveFailed=false;
                player.setDirection(move);
            }
        }else{
            isMoveFailed=true;
        }
        noMovesDone = false;
        return move;
    }



    public Action runWallFollowAlgorithm() {

        Action move = player.getDirection();
        Tile currentPosition=player.getTile();
        Optional<Tile> forwardTile = BoardUtils.nextPosition(mapRepository.getBoard(),currentPosition,Action.UP);
        Optional<Tile> leftTile = BoardUtils.nextPosition(mapRepository.getBoard(),currentPosition,Action.LEFT);
        if(forwardTile.isPresent()&&leftTile.isPresent()) {
            if (lastTurn == TurnType.LEFT && noWallDetected(forwardTile.get())) {
                movedForwardLast = true;
                lastTurn = TurnType.NO_TURN;
            } else if (noWallDetected(leftTile.get()) && wallEncountered) {
                move = LeftAngle.getLeftAngle(move);
                lastTurn = TurnType.LEFT;
                movedForwardLast = false;
            } else if (noWallDetected(forwardTile.get())) {
                movedForwardLast = true;
                lastTurn = TurnType.NO_TURN;
                wfMap.markWallAsCovered(currentPosition);
            }
        }else {
            move = RightAngle.getRightAngle(move);
            lastTurn = TurnType.RIGHT;
            movedForwardLast = false;
        }
        return move;
    }

    public Action runHeuristicsAlgorithm() {
        Action newDirection = player.getDirection();
        ArrayList<Tile> unexploredVertices = wfMap.getVerticesWithUnexploredNeighbours();
        if (unexploredVertices.size() == 0) {
            explorationDone = true;
        } else {
            double bestScore = 0;
            Tile bestScoreVertex = null;
            for (Tile vertex : unexploredVertices) {
                if (!inaccessibleCells.contains(vertex)) {
                    double score = getVertexScore(vertex);
                    if (bestScore == 0 || score < bestScore)
                    {
                        bestScore = score;
                        bestScoreVertex = vertex;
                    }
                }
            }
            if (bestScoreVertex != null) {
                currentTargetVertex = bestScoreVertex;
                currentPathToNextVertex = DijkstraShortestPath.findPathBetween(wfMap.G,
                        player.getTile(), currentTargetVertex).getVertexList();
                return getMoveBasedOnPath();
            } else {
                inaccessibleCells.clear();
            }
        }
        return newDirection;
    }

    public double getVertexScore(Tile vertex)
    {
        double score;
        double shortestPathLength;
        GraphPath dijkstrasPath = DijkstraShortestPath.findPathBetween(wfMap.G, player.getTile(), vertex);
        if (dijkstrasPath != null) {
            shortestPathLength = dijkstrasPath.getVertexList().size();
        }
        else {
            return 100000;
        }
        int neighboursOnUnexploredFrontier = 0;
        List<Tile> neighbours = Graphs.neighborListOf(wfMap.G,vertex);
        for (Tile neighbour : neighbours) {
            if (noWall(neighbour) && wfMap.G.edgesOf(vertex).size() < 4) {
                neighboursOnUnexploredFrontier++;
            }
        }

        score = shortestPathLength;
        score = score / getDirectionScore(vertex);
        if (neighboursOnUnexploredFrontier != 0) {
            score = score / neighboursOnUnexploredFrontier;
        }

        return score;
    }

    public boolean noWall(Tile tile) {

        boolean flag = true;
        for (Item items : tile.getItems()) {
            if (items instanceof Collision) {
                flag = false;
                break;
            }
        }
        return flag;
    }


    public double getDirectionScore(Tile targetVector) {
        double angle = ComputeDoubleAngleTiles.computeAngle(targetVector, player.getTile());
        double agentAngle =getAngle(player.getDirection().getxIncrement(),player.getDirection().getyIncrement());
        if (agentAngle == 0) {
            if (angle >= 315 || angle <= 45) {
                return 3 * directionHeuristicWeight;
            }
            else if (angle >= 225 || angle <= 135) {
                return 2;
            }
            else {
                return 1;
            }
        } else if (angle >= agentAngle-45 && angle <= agentAngle+45) {
            return 3 * directionHeuristicWeight;
        } else if (angle >= agentAngle-135 && angle <= agentAngle+135) {
            return 2;
        } else {
            return 1;
        }

    }

    public double getAngle(double x,double y) {
        // + 90 to set the angle to north, negate angle to make it clockwise
        // Rounded to 1 decimal place
        double angle = (-Math.toDegrees(Math.atan2(y,x)) + 360.0 + 90.0) % 360.0;
        double roundedAngle = (double) Math.round(angle * 10) / 10;

        if(roundedAngle == 360.0)
        {
            roundedAngle = 0.0;
        }
        return roundedAngle;
    }



    public boolean noWallDetected(Tile tile) {
        Optional<Tile> nextTileOpt = mapRepository.getBoard().getByCoordinates(tile.getX(), tile.getY());
        boolean nextBlocked = true;
        if (nextTileOpt.isPresent()) {
            Tile nextTile = nextTileOpt.get();

            for (Item items : nextTile.getItems()) {
                if (items instanceof Collision) {
                    nextBlocked = false;
                    break;
                }
            }
        }
        return nextBlocked;
    }

    public boolean agentStuckInTile(Tile currentPos) {
        if (lastPositions.size() < 8) {
            return false;
        }
        for (Tile tile : lastPositions) {
            if (tile != currentPos) {
                return false;
            }
        }
        return true;
    }

    public Action handleFailedMove() {
        Action newDirection = player.getDirection();
        if (movedForwardLast) {
            if (currentTargetVertex != null) {
                inaccessibleCells.add(currentTargetVertex);
            }
            newDirection = RightAngle.getRightAngle(newDirection);
            lastTurn = TurnType.RIGHT;
            movedForwardLast = false;
            wallEncountered = false;
            initialVertexFound = false;
        } else{
            if(lastTurn.equals(TurnType.RIGHT)){
                newDirection = RightAngle.getRightAngle(newDirection);
            }else if(lastTurn.equals(TurnType.LEFT)){
                newDirection = LeftAngle.getLeftAngle(newDirection);
            }
        }
        return newDirection;
    }

    private boolean foundUnexploredWallToFollow()
    {
        Optional<Tile> forwardTile = BoardUtils.nextPosition(mapRepository.getBoard(),player.getTile(),Action.UP);
        if(forwardTile.isPresent()) {
            return !noWallDetected(forwardTile.get());
        }else{
            return false;
        }

    }


    public Action getMoveBasedOnPath() {
        if(currentPathToNextVertex!=null&&currentPathToNextVertex.size()>0) {
            Tile nextVertex = currentPathToNextVertex.get(0);
            if (nextVertex.equals(player.getTile())) {
                currentPathToNextVertex.remove(nextVertex);
                nextVertex = currentPathToNextVertex.get(0);
            }
            Action nextDir = getNeighbourDir(player.getTile(), nextVertex);
            Action direction = player.getDirection();
            if (nextDir != null && nextDir.equals(direction)) {
                currentPathToNextVertex.remove(nextVertex);
                if (currentPathToNextVertex.isEmpty()) {
                    currentPathToNextVertex = null;
                    inaccessibleCells.clear();
                }
                lastTurn = TurnType.NO_TURN;
                movedForwardLast = true;
                return direction;
            } else {
                if (LeftAngle.getLeftAngle(direction).equals(nextDir)) {
                    lastTurn = TurnType.LEFT;
                    movedForwardLast = false;
                    return LeftAngle.getLeftAngle(direction);
                } else if (RightAngle.getRightAngle(direction).equals(nextDir)) {
                    lastTurn = TurnType.RIGHT;
                    movedForwardLast = false;
                    return RightAngle.getRightAngle(direction);
                } else {
                    lastTurn = TurnType.LEFT;
                    movedForwardLast = false;
                    return LeftAngle.getLeftAngle(direction);
                }
            }
        }else{
            return randDirection();
        }
    }

    public Action getNeighbourDir(Tile agentCell, Tile neighbour) {

        if (agentCell.getX() == neighbour.getX() && neighbour.getY() < agentCell.getY()) {
            return Action.UP;  // north of agent
        } else if (agentCell.getX() == neighbour.getX() && neighbour.getY() > agentCell.getY()) {
            return Action.DOWN;  // south of agent
        } else if (agentCell.getY() == neighbour.getY() && neighbour.getX() < agentCell.getX()) {
            return Action.LEFT;  // west of agent
        } else if (agentCell.getY() == neighbour.getY() && neighbour.getX() > agentCell.getX()) {
            return Action.RIGHT;  // east of agent
        }
        return null;
    }

    public boolean agentInStuckMovement() {
        ArrayList<Tile> diffVertices = new ArrayList<>();
        if (lastPositions.size() >= 24) {
            for (int i=0; i<lastPositions.size(); i++) {
                if (lastPositions.get(i) != null) {
                    if(!diffVertices.contains(lastPositions.get(i))) {
                        diffVertices.add(lastPositions.get(i));
                    }
                    if(i == lastPositions.size() - 1 && diffVertices.size() <=8) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void updateGraphAfterSuccessfulMove() {
        updateLastPositions(player.getTile());
//        if (prevAgentVertex != null) {
//            wfMap.G.leaveVertex(prevAgentVertex);
//        }
        wfMap.add_or_adjust_Vertex(player.getTile());
    }

    public void updateLastPositions(Tile currentAgentCell) {
        if (lastPositions.size() >= 24) {
            lastPositions.remove(0);
        }
        if (lastPositions.size() > 0)
        {
            prevAgentVertex = lastPositions.get(lastPositions.size()-1);
        }
        lastPositions.add(currentAgentCell);
    }

    private Action randDirection() {
        double r = Math.random();
        if(r < 0.25)
            return Action.DOWN;
        else if(r < 0.5)
            return Action.RIGHT;
        else if(r < 0.75)
            return Action.LEFT;
        else
            return  Action.UP;
    }
}
