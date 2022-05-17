package com.nscharrenberg.um.multiagentsurveillance.agents.DQN.training;

public class EpsilonGreedy {

    private double start = 1, end = 0.1, decay = 0.001;

    public EpsilonGreedy(double start, double end, double decay){
        this.start = start;
        this.end = end;
        this.decay = decay;
    }

    public EpsilonGreedy(){}

    public double explorationRate(double currentStep){
        return end + (start - end) * Math.exp(-1 * currentStep * decay);
    }
}
