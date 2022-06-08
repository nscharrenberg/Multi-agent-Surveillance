package com.nscharrenberg.um.multiagentsurveillance.agents.DQN.FCN;

import java.util.Random;
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

    public Neuron() {
    }

    public double[] getWeights() { return weights; }
    public double getBias() { return 0; }

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
        Random random = new Random();

        for (int i = 0; i < weights.length; i++)
            weights[i] = ThreadLocalRandom.current().nextDouble(-k, k);
    }

    public void setWeights(double[] weights) {
        this.weights = weights;
    }

    // TODO: UPDATE
    public void setBias(double bias) {

    }
    // TODO: UPDATE
    public int getNumInputs() {
        return 0;
    }

    // TODO: UPDATE
    public void setNumInputs(int numInputs) {
    }

    public double getLearningRate() {
        return learningRate;
    }

    public void setLearningRate(double learningRate) {
        this.learningRate = learningRate;
    }
}
