package com.nscharrenberg.um.multiagentsurveillance.agents.DQN.CNN;

import static com.nscharrenberg.um.multiagentsurveillance.agents.DQN.DQN_Util.matrixSum3D;

public class ConvLayer {

    private int inputLength;
    private int numFilters;
    private int channels;
    private final int kernelSize = 3;
    private Filter[] filters;
    private double[][][] output;

    private int length;

    public ConvLayer(int channels, int inputLength, int numFilters, double initWeight, double learningRate){
        this.inputLength = inputLength;
        this.numFilters = numFilters;
        this.channels = channels;
        filters = new Filter[numFilters];

        for (int i = 0; i < numFilters; i++) {
            filters[i] = new Filter(channels, inputLength, initWeight, learningRate);
        }
    }

    public double[][][] forward(double[][][] input) {
        this.length = inputLength - kernelSize + 1;
        double[][][] out = new double[numFilters][length][length];
        double[][] layer;

        for (int i = 0; i < numFilters; i++) {
             layer = filters[i].calculateForwards(input);
            for (int j = 0; j < length; j++) {
                for (int k = 0; k < length; k++) {
                    out[i][j][k] = layer[j][k];
                }
            }
        }

        this.output = out;
        return out;
    }

    public double[][][] backward(double[][][] outputGradient){

        //outputGradient = reluPrime(outputGradient);

        double[][][] inputGradient = new double[channels][inputLength][inputLength];

        for (int i = 0; i < numFilters; i++)
            inputGradient = matrixSum3D(inputGradient, filters[i].calculateBackwards(outputGradient[i]));

        return inputGradient;
    }

    private double[][][] reluPrime(double[][][] gradient){

        double[][][] dYdE = gradient.clone();

        for (int i = 0; i < numFilters; i++) {
            for (int j = 0; j < length; j++) {
                for (int k = 0; k < length; k++) {
                    if (output[i][j][k] <= 0)
                        dYdE[i][j][k] = 0;
                }
            }
        }

        return dYdE;
    }

    public String[][] saveLayer(){
        String[][] out = new String[numFilters][channels + 2];

        for (int i = 0; i < numFilters; i++) {
            out[i] = filters[i].saveFilter(i);
        }
        return out;
    }

}
