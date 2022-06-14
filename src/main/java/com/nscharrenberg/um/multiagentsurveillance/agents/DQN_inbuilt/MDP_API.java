package com.nscharrenberg.um.multiagentsurveillance.agents.DQN_inbuilt;

import com.nscharrenberg.um.multiagentsurveillance.agents.shared.Agent;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.distanceCalculator.CalculateDistance;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.distanceCalculator.ManhattanDistance;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.pathfinding.AStar.AStar;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.pathfinding.IPathFinding;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.utils.QueueNode;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IPlayerRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Intruder;
import com.nscharrenberg.um.multiagentsurveillance.headless.repositories.GameRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.repositories.MapRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.repositories.PlayerRepository;
import org.deeplearning4j.gym.StepReply;
import org.deeplearning4j.rl4j.mdp.MDP;
import org.deeplearning4j.rl4j.space.ArrayObservationSpace;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import org.deeplearning4j.rl4j.space.ObservationSpace;

import java.util.List;
import java.util.Optional;

public class MDP_API implements MDP<DeepQN_Agent, Integer, DiscreteSpace> {

    private final ArrayObservationSpace<DeepQN_Agent> gameArrayObservationSpace;
    private final DiscreteSpace discreteSpace = new DiscreteSpace(4);
    private final int observationSize;
    private DeepQN_Agent agent;
    private final CalculateDistance distance = new ManhattanDistance();

    private final IPathFinding pathFinding = new AStar();

    private double maxBoardDistance;
    private double minTargetDistance;
    private boolean flag = false;
    private int point = 0;
    private int samePosition = 0;

    public MDP_API(int observationSize){
        this.gameArrayObservationSpace = new ArrayObservationSpace<>(new int[]{observationSize});
        this.observationSize = observationSize;
    }

    @Override
    public ObservationSpace<DeepQN_Agent> getObservationSpace() {
        return gameArrayObservationSpace;
    }

    @Override
    public DiscreteSpace getActionSpace() {
        return discreteSpace;
    }

    @Override
    public DeepQN_Agent reset() {
        GameRepository gameRepositoryDup = new GameRepository();
        MapRepository mapRepositoryDup = new MapRepository();
        PlayerRepository playerRepositoryDup = new PlayerRepository();

        gameRepositoryDup.setMapRepository(mapRepositoryDup);
        gameRepositoryDup.setPlayerRepository(playerRepositoryDup);

        mapRepositoryDup.setGameRepository(gameRepositoryDup);
        mapRepositoryDup.setPlayerRepository(playerRepositoryDup);

        playerRepositoryDup.setGameRepository(gameRepositoryDup);
        playerRepositoryDup.setMapRepository(mapRepositoryDup);

        gameRepositoryDup.startGame();

        List<Agent> agentList = playerRepositoryDup.getAgents();

        DeepQN_Agent deepQN_agent = null;

        for(Agent agent : agentList){
            if(agent.getPlayer() instanceof Intruder){
                deepQN_agent = (DeepQN_Agent) agent;
                break;
            }
        }

        if(deepQN_agent == null){
            throw new RuntimeException("MDP agent is null");
        }

        if(deepQN_agent.equals(this.agent)){
            throw new RuntimeException("MDP agent is not duplicated");
        }

        this.agent = deepQN_agent;

        this.maxBoardDistance = distance.compute(new Tile(gameRepositoryDup.getWidth(), gameRepositoryDup.getHeight()), new Tile(0, 0));

        this.minTargetDistance = distance.compute(agent.getMapRepository().getTargetCenter(), agent.getPlayer().getTile());

        return this.agent;
    }

    @Override
    public void close() {

    }

    @Override
    public StepReply<DeepQN_Agent> step(Integer integer) {



        if(!(agent.getPlayer() instanceof Intruder intruder))
            throw new RuntimeException("MDP agent is not Intruder");


        Tile tileBefore = intruder.getTile();

//        double distanceTargetBefore = 0.0;
//
//        if(tileBefore != null) {
//            Optional<QueueNode> pathBefore =  pathFinding.execute(agent.getMapRepository().getBoard(), agent.getPlayer(), agent.getMapRepository().getTargetCenter());
//            if(pathBefore.isPresent())
//                distanceTargetBefore = pathBefore.get().getDistance();
//        }

        if(point % 500 == 0){
            if(tileBefore != null) {
                System.out.println(minTargetDistance);
            }
        }


        try {
            gameLoop(integer);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Tile tileAfter = intruder.getTile();
        double distanceTargetAfter = 0.0;

        if(tileAfter != null) {
            Optional<QueueNode> pathAfter =  pathFinding.execute(agent.getMapRepository().getBoard(), agent.getPlayer(), agent.getMapRepository().getTargetCenter());

            if(pathAfter.isPresent())
                distanceTargetAfter = pathAfter.get().getDistance();
        }

        double reward = 0;

//        if(distanceTargetAfter != 0.0 && distanceTargetBefore != 0.0) {
//            if (distanceTargetBefore > distanceTargetAfter) {
//                reward += 0.5;
//            } else if (distanceTargetBefore < distanceTargetAfter) {
//                reward -= 0.5;
//            }
//        }

        if(distanceTargetAfter < minTargetDistance){
            minTargetDistance = distanceTargetAfter;
            reward += 0.5;
        } else {
            reward -= 0.5;
        }

        if(tileBefore != null && tileAfter != null) {
//            if (tileBefore.getX() != tileAfter.getX() || tileBefore.getY() != tileAfter.getY()) {
//                samePosition = 0;
//            }else if (tileBefore.getX() == tileAfter.getX() && tileBefore.getY() == tileAfter.getY()) {
//                samePosition++;
//
//                if(samePosition >= 5){
//                    agent.gameRepository().setRunning(false);
//                    samePosition = 0;
//                }
//            }


            Action direction = Action.values()[integer];
            int nextX = intruder.getTile().getX() + direction.getxIncrement();
            int nextY = intruder.getTile().getY() + direction.getyIncrement();
            Optional<Tile> nextPositionOpt = agent.getMapRepository().getBoardAsArea().getByCoordinates(nextX, nextY);

            if (nextPositionOpt.isPresent()) {
                Tile nextPosition = nextPositionOpt.get();
                if(nextPosition.isCollision()) {
                    reward -= 0.5;
//                    agent.gameRepository().setRunning(false);
                } else if(nextPosition.hasGuard())
                    reward -= 3;
            }

            if (agent.getMapRepository().getTargetArea().within(tileAfter.getX(), tileAfter.getY())) {
                reward += 3;
                flag = true;
            } else {
                if(flag){
                    reward -= 1; //Good was 1
                    flag = false;
                }
            }

        }


        if(!agent.gameRepository().isRunning()){
            List<Intruder> listEscapeIntruders = agent.getPlayerRepository().getEscapedIntruders();

            boolean escaped = false;
            for(Intruder escapeIntruder : listEscapeIntruders){
                if(escapeIntruder.equals(intruder)){
                    escaped = true;
                    break;
                }
            }

            if(escaped){
                reward = 100;
            } else {
                reward = -100;
            }
        }

        point++;

        return new StepReply<>(agent, reward, isDone(), null);
    }

    @Override
    public boolean isDone() {
        return !agent.gameRepository().isRunning();
    }

    @Override
    public MDP<DeepQN_Agent, Integer, DiscreteSpace> newInstance() {
        return new MDP_API(observationSize);
    }

    private void gameLoop(Integer integer) throws Exception {
        IPlayerRepository playerRepository = agent.getPlayerRepository();
        playerRepository.updateSounds(playerRepository.getAgents());

        List<Agent> agentList = playerRepository.getAgents();

        playerRepository.updateSounds(agentList);
        if(agentList.size() != 0) {
            for (Agent agentLoop : agentList) {
                //TODO check
                if (agent.equals(agentLoop)) {
                    agentLoop.execute(Action.values()[integer]);
                } else {
                    Action move = agentLoop.decide();
                    agentLoop.execute(move);
                }
            }
        }
    }
}
