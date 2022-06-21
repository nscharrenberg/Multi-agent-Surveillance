package com.nscharrenberg.um.multiagentsurveillance.agents.DQN.FCN;

import java.util.concurrent.ThreadLocalRandom;

public class Neuron {

    private double[] weights;
    private int numOutputs;
    private double learningRate;

    public Neuron(int numOutputs, double learningRate) {
        this.numOutputs = numOutputs;
        this.learningRate = learningRate;
        weights = new double[numOutputs];
        initWeights();
    }

    public Neuron(double[] weights, int numOutputs, double learningRate){
        this.weights = weights;
        this.numOutputs = numOutputs;
        this.learningRate = learningRate;
    }

    public double[] getWeights() { return weights; }

    public double forward(double input, int index){
        return input * weights[index];
    }

    public void backward(double[] dEdW){

        // TODO: Decide if updates are + | -
        for (int i = 0; i < weights.length; i++) {
            weights[i] -= learningRate * dEdW[i];
        }

    }

    /**
     * Method uniformly initials weights based on the number of outputs
     */
    private void initWeights(){
        double k = 1 / Math.sqrt(numOutputs);

        for (int i = 0; i < weights.length; i++)
            weights[i] = ThreadLocalRandom.current().nextDouble(-k, k);
    }

    public void loadWeights(double[] weights) {
        assert this.weights.length == weights.length : "Neuron: error loading weights";

        this.weights = weights;
    }

    public Neuron clone(){
        return new Neuron(this.weights.clone(), numOutputs, learningRate);
    }
}
