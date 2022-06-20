package com.nscharrenberg.um.multiagentsurveillance.agents.DQN.CNN;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.RandomUtil;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static com.nscharrenberg.um.multiagentsurveillance.agents.DQN.CNN.Filter.scaleSubtract;


public class Kernel {
    private int size = 3;
    private double[][] weights;
    private transient SecureRandom random;

    public Kernel(double initWeight){
        try {
            this.random = RandomUtil.seeded();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        double scale = Math.pow(10, -initWeight);
        weights = new double[size][size];

        for (int i=0; i<size; i++)
            for (int j=0; j<size; j++)
                weights[i][j] = scale * (random.nextInt(3) - 1);


    }

    public Kernel() {
        try {
            this.random = RandomUtil.seeded();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
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
}
