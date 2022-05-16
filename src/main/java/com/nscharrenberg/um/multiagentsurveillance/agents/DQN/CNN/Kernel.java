package com.nscharrenberg.um.multiagentsurveillance.agents.DQN.CNN;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static com.nscharrenberg.um.multiagentsurveillance.agents.DQN.DQN_Util.scaleSubtract;

public class Kernel {
    private int size = 3;
    private double[][] weights;
    private transient Random random = ThreadLocalRandom.current();

    public Kernel(double initWeight){
        double scale = Math.pow(10, -initWeight);
        weights = new double[size][size];

        for (int i=0; i<size; i++)
            for (int j=0; j<size; j++)
                weights[i][j] = scale * (random.nextInt(3) - 1);


    }

    public Kernel() {

    }

    public double[][] getWeights(){
        return weights;
    }

    /**
     * @param kernelGradient - derivative of the error with respect to the kernel
     * @param learningRate - learning rate of the network
     */
    public void updateWeights(double[][] kernelGradient, double learningRate){
        weights = scaleSubtract(weights, kernelGradient, learningRate);
    }

    public String saveKernel(){
        String line = "";

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                line += weights[i][j] + ",";
            }
        }

        return line;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setWeights(double[][] weights) {
        this.weights = weights;
    }

    public Random getRandom() {
        return random;
    }

    public void setRandom(Random random) {
        this.random = random;
    }
}
