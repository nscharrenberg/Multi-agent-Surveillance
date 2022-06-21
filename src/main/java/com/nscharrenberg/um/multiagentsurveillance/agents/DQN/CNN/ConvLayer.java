package com.nscharrenberg.um.multiagentsurveillance.agents.DQN.CNN;

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
        this.length = inputLength - kernelSize + 1;
        filters = new Filter[numFilters];

        for (int i = 0; i < numFilters; i++) {
            filters[i] = new Filter(channels, inputLength, initWeight, learningRate);
        }
    }

    public ConvLayer(Filter[] filters, int channels, int inputLength, int numFilters){
        this.inputLength = inputLength;
        this.numFilters = numFilters;
        this.channels = channels;
        this.length = inputLength - kernelSize + 1;
        this.filters = filters;
    }


    public double[][][] forward(double[][][] input) {

        double[][][] out = new double[numFilters][length][length];

        for (int i = 0; i < numFilters; i++)
            out[i] = filters[i].calculateForwards(input);

        this.output = out;
        return out;
    }

    public double[][][] backward(double[][][] outputGradient){

        outputGradient = reluPrime(outputGradient);

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

    private double[][][] matrixSum3D(double[][][] A, double[][][] B){
        assert A.length == B.length && A[0].length == B[0].length && A[0][0].length == B[0][0].length : "Unequal lengths provided";

        int channels = A.length;
        int length = A[0].length;

        double[][][] out = new double[channels][length][length];

        for (int i = 0; i < channels; i++) {
            for (int j = 0; j < length; j++) {
                for (int k = 0; k < length; k++) {
                    out[i][j][k] = A[i][j][k] + B[i][j][k];
                }
            }
        }

        return out;
    }


    public String[][] saveLayer(){
        String[][] out = new String[numFilters][channels + 2];

        for (int i = 0; i < numFilters; i++) {
            out[i] = filters[i].saveFilter(i);
        }
        return out;
    }

    public void loadLayer(double[][][] weights, double[][] bias){
        for (int i = 0; i < numFilters; i++)
            filters[i].loadFilter(weights[i], bias[i]);
    }


    public ConvLayer clone(){
        Filter[] filtersClone = new Filter[filters.length];

        for (int i = 0; i < filters.length; i++) {
            filtersClone[i] = filters[i].clone();
        }

        return new ConvLayer(filtersClone, channels, inputLength, numFilters);
    }


    public int getKernelSize(){
        return kernelSize * kernelSize;
    }

    public int getNumFilters() {
        return numFilters;
    }

    public int getChannels() {
        return channels;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}
