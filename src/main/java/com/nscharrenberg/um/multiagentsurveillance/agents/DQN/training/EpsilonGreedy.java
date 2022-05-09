package com.nscharrenberg.um.multiagentsurveillance.agents.DQN.training;

public class EpsilonGreedy {

    private double start;
    private double end;
    private double decay;

    public EpsilonGreedy(double start, double end, double decay){
        this.start = start;
        this.end = end;
        this.decay = decay;
    }

    private double explorationRate(double currentStep){
        return end + (start - end) * Math.exp(-1 * currentStep * decay);
    }
}
