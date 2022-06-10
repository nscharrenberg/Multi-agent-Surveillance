package com.nscharrenberg.um.multiagentsurveillance.agents.DQN_inbuilt;

import com.nscharrenberg.um.multiagentsurveillance.agents.shared.Agent;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IPlayerRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;
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

public class MDP_API implements MDP<MDP_Agent, Integer, DiscreteSpace> {

    private final ArrayObservationSpace<MDP_Agent> gameArrayObservationSpace;
    private final DiscreteSpace discreteSpace = new DiscreteSpace(4);
    private final int observationSize;
    private MDP_Agent agent;

    public MDP_API(int observationSize){
        this.gameArrayObservationSpace = new ArrayObservationSpace<>(new int[]{observationSize});
        this.observationSize = observationSize;
    }

    @Override
    public ObservationSpace<MDP_Agent> getObservationSpace() {
        return gameArrayObservationSpace;
    }

    @Override
    public DiscreteSpace getActionSpace() {
        return discreteSpace;
    }

    @Override
    public MDP_Agent reset() {
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

        MDP_Agent mdp_agent = null;

        for(Agent agent : agentList){
            if(agent.getPlayer() instanceof Intruder){
                mdp_agent = (MDP_Agent) agent;
                break;
            }
        }

        if(mdp_agent == null){
            throw new RuntimeException("MDP agent is null");
        }

        if(mdp_agent.equals(agent)){
            throw new RuntimeException("MDP agent is not duplicated");
        }

        agent = mdp_agent;

        return agent;
    }

    @Override
    public void close() {

    }

    @Override
    public StepReply<MDP_Agent> step(Integer integer) {

        try {
            gameLoop(integer);
        } catch (Exception e) {
            throw new RuntimeException("Game Loop cause the Error in MDP API");
        }

        double reward = -1 / 10000.0;

        if(!agent.gameRepository().isRunning()){
            List<Intruder> listEscapeIntruders = agent.getPlayerRepository().getEscapedIntruders();

            if(!(agent.getPlayer() instanceof Intruder intruder))
                throw new RuntimeException("MDP agent is not Intruder");

            boolean escaped = false;
            for(Intruder escapeIntruder : listEscapeIntruders){
                if(escapeIntruder.equals(intruder)){
                    escaped = true;
                    break;
                }
            }

            if(escaped){
                reward = 1;
            } else {
                reward = -1;
            }
        }

        return new StepReply<>(agent, reward, isDone(), null);
    }

    @Override
    public boolean isDone() {
        return !agent.gameRepository().isRunning();
    }

    @Override
    public MDP<MDP_Agent, Integer, DiscreteSpace> newInstance() {
        return new MDP_API(observationSize);
    }

    private void gameLoop(Integer integer) throws Exception {
        IPlayerRepository playerRepository = agent.getPlayerRepository();
        playerRepository.updateSounds(playerRepository.getAgents());

        List<Agent> agentList = playerRepository.getAgents();
        for (Agent agentLoop : agentList) {
            //TODO check
            if(agent.equals(agentLoop)){
                agentLoop.execute(Action.values()[integer]);
            } else {
                Action move = agentLoop.decide();
                agentLoop.execute(move);
            }
        }
    }
}
