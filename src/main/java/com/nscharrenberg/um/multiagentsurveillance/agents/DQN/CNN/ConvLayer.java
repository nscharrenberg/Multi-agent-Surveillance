package com.nscharrenberg.um.multiagentsurveillance.agents.DQN.CNN;

import static com.nscharrenberg.um.multiagentsurveillance.agents.DQN.DQN_Util.matrixSum3D;

public class ConvLayer {

    private int inputLength;
    private int numFilters;
    private int channels;
    private int kernelSize = 3;
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

    public ConvLayer() {
    }

    public double[][][] forward(double[][][] input) {
        this.length = inputLength - kernelSize + 1;
        double[][][] out = new double[numFilters][length][length];

        for (int i = 0; i < numFilters; i++)
            out[i] = filters[i].calculateForwards(input);

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

    public int getInputLength() {
        return inputLength;
    }

    public void setInputLength(int inputLength) {
        this.inputLength = inputLength;
    }

    public int getNumFilters() {
        return numFilters;
    }

    public void setNumFilters(int numFilters) {
        this.numFilters = numFilters;
    }

    public int getChannels() {
        return channels;
    }

    public void setChannels(int channels) {
        this.channels = channels;
    }

    public int getKernelSize() {
        return kernelSize;
    }

    public void setKernelSize(int kernelSize) {
        this.kernelSize = kernelSize;
    }

    public Filter[] getFilters() {
        return filters;
    }

    public void setFilters(Filter[] filters) {
        this.filters = filters;
    }

    public double[][][] getOutput() {
        return output;
    }

    public void setOutput(double[][][] output) {
        this.output = output;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}
