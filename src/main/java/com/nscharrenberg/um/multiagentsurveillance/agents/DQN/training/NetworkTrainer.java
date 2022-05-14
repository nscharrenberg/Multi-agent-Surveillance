package com.nscharrenberg.um.multiagentsurveillance.agents.DQN.training;

import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.DQN_Agent;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Area;

import java.util.ArrayList;

public class NetworkTrainer {

    private int velocity = 1;
    private final int batchSize = 256;
    private final double gamma = 0.999;
    private final double epsStart = 1;
    private final double epsEnd = 0.1;
    private final double epsDecay = 0.001;
    private final int targetUpdate = 10;
    private final int memorySize = 10000;
    private final double lr = 0.001;
    private final int numEpisodes = 1000;
    private TrainingData trainingData;
    private DQN_Agent agent;


    /*
        This class could do many, many things. Some ideas:
        1. This class runs the games and has all the agents here
        2. Another class runs the games and
            a. This class manages all the agents
            b. This class manages one agent
    */


    public NetworkTrainer(DQN_Agent agent){
        trainingData = new TrainingData(memorySize);
        this.agent = agent;
    }

    public void runTraining() throws Exception {
        Action action;
        double reward;
        double[][][] state = agent.getState(), nextState;
        boolean done;


        for (int ts = 0; ts < 1; ts++) {
            action = agent.decide();
            reward = agent.preformMove(action);
            nextState = agent.getState();
            done = false; // UPDATE

            //trainingData.push(new Experience(state,action,reward,nextState,done));
            state = nextState;

            // TODO: Add short and long term training methods
            // TODO: Agent requires a game finished check

            if (trainingData.hasBatch(batchSize))
                batchTrain();
        }
    }

    private void batchTrain(){
        TrainingData samples = trainingData.randomSample(batchSize);
        ArrayList<double[]> qValues = samples.qValues;
        //ArrayList<double[]> nextQValues =
    }



}
