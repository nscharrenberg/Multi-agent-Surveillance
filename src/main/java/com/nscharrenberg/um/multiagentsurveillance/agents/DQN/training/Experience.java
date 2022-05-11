package com.nscharrenberg.um.multiagentsurveillance.agents.DQN.training;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;

import java.util.Random;

public class Experience {

    private double[][][] state;
    private Action action;
    private double reward;
    private double[][][] nextState;


    public Experience(double[][][] state, Action action, double reward, double[][][] nextState){
        this.state = state;
        this.action = action;
        this.nextState = nextState;
        this.reward = reward;
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
