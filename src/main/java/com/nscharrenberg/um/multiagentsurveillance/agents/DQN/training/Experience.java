package com.nscharrenberg.um.multiagentsurveillance.agents.DQN.training;

public class Experience {

    private double[][][] state;
    private int action;
    private double[][][] nextState;
    private double reward;

    private Experience(double[][][] state, int action, double[][][] nextState, double reward){
        this.state = state;
        this.action = action;
        this.nextState = nextState;
        this.reward = reward;
    }
}
