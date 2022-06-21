package com.nscharrenberg.um.multiagentsurveillance.agents.DQN.training;

public class EpsilonGreedy {

    private double start = 1.0, end = 0.0, decay = 0.0015;

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
