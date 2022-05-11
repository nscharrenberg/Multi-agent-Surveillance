package com.nscharrenberg.um.multiagentsurveillance.agents.DQN.FCN;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Neuron {

    private double[] weights;
    private double bias;
    private final int numInputs;
    private final double learningRate;

    public Neuron(int numInputs, double learningRate) {
        this.numInputs = numInputs;
        this.learningRate = learningRate;
        weights = new double[numInputs];
        bias = 0;
        initWeights();
    }

    public double[] getWeights() { return weights; }
    public double getBias() { return bias; }

    public double calculateForward(double[] input){
        double out = 0;

        for (int i = 0; i < input.length; i++) {
            out += input[i] * weights[i];
        }
        out += bias;


        return out;
    }

    public void calculateBackward(double dEdY){
        double[] dEdW = dEdW(dEdY);

        for (int i = 0; i < weights.length; i++) {
            weights[i] -= learningRate * dEdW[i];
        }

        bias -= learningRate * dEdY;
    }


    private double[] dEdW(double dEdY){
        double[] dEdW = new double[weights.length];

        for (int i = 0; i < dEdW.length; i++) {
            dEdW[i] = dEdY * weights[i];
        }

        return dEdW;
    }

    /**
     * Method uniformly initials weights based on the number of inputs
     */
    private void initWeights(){
        double k = 1 / Math.sqrt(numInputs);
        Random random = new Random();

        for (int i = 0; i < weights.length; i++)
            weights[i] = ThreadLocalRandom.current().nextDouble(-k, k);
    }

}
