package com.nscharrenberg.um.multiagentsurveillance.gui.dqn;

import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.DQN_Agent;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.neuralNetwork.Network;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.neuralNetwork.NetworkWriter;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.utils.NetworkUtils;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.Agent;
import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.BoardNotBuildException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.InvalidTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.ItemNotOnTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;

import java.util.List;
import java.util.Stack;

public class TrainingSimulation {
    private String NETWORK_PATH = "dqn_network_activation_layer";
    private String NETWORK_EXTENSION = ".agent";

    public TrainingSimulation() throws Exception {
        int n = 5;
//        train(n);
        testWritingNetwork();
    }

    private void testWritingNetwork() throws Exception {
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
                networkId++;
                NetworkUtils.saveNetwork(dqnAgent.getNetwork(), NETWORK_PATH + networkId + NETWORK_EXTENSION);
                networks.push(dqnAgent.getNetwork());
            }
        }
    }

    private void train(int n) {
        for (int i = 0; i < n; i++) {
            try {
                runSingleGame();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Stack<Network> networks = new Stack<>();

    private void runSingleGame() throws Exception {
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
    }
}
