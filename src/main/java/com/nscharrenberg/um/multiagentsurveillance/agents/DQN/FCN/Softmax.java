package com.nscharrenberg.um.multiagentsurveillance.agents.DQN.FCN;

import java.util.Arrays;

public class Softmax {


    public static double[] softmax(double[] neuronValues) {
        double total = Arrays.stream(neuronValues).map(Math::exp).sum();
        double[] out = new double[neuronValues.length];

        for (int i = 0; i < out.length; i++) {
            out[i] = Math.exp(neuronValues[i]) / total;
        }

        return out;
    }




}
