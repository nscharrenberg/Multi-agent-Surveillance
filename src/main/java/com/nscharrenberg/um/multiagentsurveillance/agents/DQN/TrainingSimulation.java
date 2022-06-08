package com.nscharrenberg.um.multiagentsurveillance.agents.DQN;

import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.DQN_Agent;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.neuralNetwork.Network;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.neuralNetwork.NetworkWriter;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.training.Experience;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.training.TrainingData;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.utils.NetworkUtils;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.Agent;
import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.BoardNotBuildException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.InvalidTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.ItemNotOnTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Player;

import java.util.List;
import java.util.Stack;

public class TrainingSimulation {
    private String NETWORK_PATH = "dqn_network_activation_layer";
    private String NETWORK_EXTENSION = ".agent";
    private Stack<Network> networks = new Stack<>();
    private DQN_Agent[] guards, intruders;
    private TrainingData[] guardsData, intrudersData;
    private final int batchSize = 256;

    public TrainingSimulation() throws Exception {
        initTraining(0,2);
    }

    private void initTraining(int numGuards, int numIntruders) throws Exception {

        guards = new DQN_Agent[numGuards];
        guardsData = new TrainingData[numGuards];
        intruders = new DQN_Agent[numIntruders];
        intrudersData = new TrainingData[numIntruders];

        for (int i = 0; i < Math.max(numGuards, numIntruders); i++) {
            if (i < numGuards){
                guards[i] = new DQN_Agent();
                guardsData[i] = new TrainingData();
            }
            if (i < numIntruders){
                intruders[i] = new DQN_Agent();
                intrudersData[i] = new TrainingData();
            }
        }

        runTraining(10);
    }

    private void runTraining(int numEpisodes) throws Exception {

        double[][][] state, nextState;
        Experience experience;
        boolean done;
        Action action;
        double reward;

        for (int episode = 1; episode <= numEpisodes ; episode++) {
            Factory.init();
            Factory.getGameRepository().startGame(guards, intruders);
            Factory.getGameRepository().setRunning(true);

            while (Factory.getGameRepository().isRunning()){
                for (int j = 0; j < intruders.length; j++) {
                    state = intruders[j].updateState();
                    action = intruders[j].selectAction(episode, state);
                    intruders[j].execute(action);
                    nextState = intruders[j].updateState();
                    reward = intruders[j].calculateReward(nextState);
                    done = Factory.getGameRepository().isRunning();                                                       // TODO: Check if final state is reached
                    experience = new Experience(state, action, reward, nextState, done);
                    intrudersData[j].push(experience);

                    // Preform training on a batch of experiences
                    if (intrudersData[j].hasBatch(batchSize))
                        intruders[j].trainAgent(intrudersData[j].randomSample(batchSize));
                    // Preform a single step of back propagation
                    // This might need to be done with a certain probability, or it might be too slow
                    // Who knows though
                    else intruders[j].trainAgent(experience);
                }

            }
        }
    }

    private boolean gameComplete(){
        return false;
    }


/*    private void testWritingNetwork() throws Exception {
        Factory.init();
        Factory.getGameRepository().startGame();

        if (!networks.isEmpty()) {
            for (Agent agent : Factory.getPlayerRepository().getAgents()) {
                if (networks.isEmpty()) {
                    break;
                }

                if (agent instanceof DQN_Agent) {
                    Network network = networks.pop();
                    ((DQN_Agent) agent).setNetwork(network);
                }
            }
        }

        Factory.getGameRepository().setRunning(true);

        int iterations = 0;

        while (iterations < 10) {
            iterations++;
            int agentId = 0;

            for (Agent agent : Factory.getPlayerRepository().getAgents()) {
                int oldX = agent.getPlayer().getTile().getX();
                int oldY = agent.getPlayer().getTile().getY();
                Action move = agent.decide();
                agent.execute(move);
                System.out.println("Agent " + agentId
                        + " going from (" + oldX + ", " + oldY + ") to move "
                        + move + " to (" + agent.getPlayer().getTile().getX() + ", "
                        + agent.getPlayer().getTile().getY() + ")");
                agentId++;
            }

            Factory.getMapRepository().checkMarkers();
        }

        int networkId = 1;
        for (Agent agent : Factory.getPlayerRepository().getAgents()) {
            if (agent instanceof DQN_Agent dqnAgent) {
                NetworkUtils.saveNetwork(dqnAgent.getNetwork(), NETWORK_PATH + networkId + NETWORK_EXTENSION);
                networks.push(dqnAgent.getNetwork());

                networkId++;
            }
        }
    }*/


/*    private void runSingleGame() throws Exception {
        Factory.init();
        Factory.getGameRepository().startGame();

        if (!networks.isEmpty()) {
            for (Agent agent : Factory.getPlayerRepository().getAgents()) {
                if (networks.isEmpty()) {
                    break;
                }

                if (agent instanceof DQN_Agent) {
                    Network network = networks.pop();
                    ((DQN_Agent) agent).setNetwork(network);
                }
            }
        }

        Factory.getGameRepository().setRunning(true);

        while (Factory.getGameRepository().isRunning()) {
            int agentId = 0;

            for (Agent agent : Factory.getPlayerRepository().getAgents()) {
                int oldX = agent.getPlayer().getTile().getX();
                int oldY = agent.getPlayer().getTile().getY();
                Action move = agent.decide();
                agent.execute(move);
                System.out.println("Agent " + agentId
                        + " going from (" + oldX + ", " + oldY + ") to move "
                        + move + " to (" + agent.getPlayer().getTile().getX() + ", "
                        + agent.getPlayer().getTile().getY() + ")");
                agentId++;
            }

            Factory.getMapRepository().checkMarkers();
        }

        for (Agent agent : Factory.getPlayerRepository().getAgents()) {
            if (agent instanceof DQN_Agent) {
                networks.push(((DQN_Agent) agent).getNetwork());
            }
        }
    }*/
}
