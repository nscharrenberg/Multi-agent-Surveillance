package com.nscharrenberg.um.multiagentsurveillance.agents.DQN.training;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;

import java.util.Random;

public class Experience {

    public double[][][] state;
    public Action action;
    public double reward;
    public double[][][] nextState;
    public boolean done;
    public double[] qValues;


    public Experience(double[][][] state, Action action, double reward, double[][][] nextState, boolean done, double[] qValues){
        this.state = state;
        this.action = action;
        this.nextState = nextState;
        this.reward = reward;
        this.done = done;
        this.qValues = qValues;
    }

    public double[][][] getState() {
        return state;
    }

    public Action getAction() {
        return action;
    }


    public double getReward() {
        return reward;
    }
}
