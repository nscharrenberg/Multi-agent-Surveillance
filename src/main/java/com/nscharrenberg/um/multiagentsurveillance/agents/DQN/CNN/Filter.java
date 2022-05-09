package com.nscharrenberg.um.multiagentsurveillance.agents.DQN.CNN;


import static com.nscharrenberg.um.multiagentsurveillance.agents.DQN.DQN_Util.*;

public class Filter {
    private final int channels;
    private double[][][] input;
    private final int kernelSize = 3;
    private Kernel[] kernels;
    private double[][] bias;
    private final int inputLength;
    private final int size;
    private double learningRate;

    /**
     * @param channels - number of channels in the input
     * @param initWeight - scalar to initials weights of kernel
     * @param learningRate - learning rate of the network
     */
    public Filter(int channels, int inputLength, double initWeight, double learningRate){
        this.channels = channels;
        this.kernels = new Kernel[channels];
        this.inputLength = inputLength;
        this.learningRate = learningRate;

        double k = Math.sqrt(1.0 / (channels*kernelSize*kernelSize));
        size = inputLength - kernelSize + 1;

        bias = new double[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                bias[i][j] = Math.random()*k*2 - k; // random nvr = 1
            }
        }

        for (int i = 0; i < channels; i++) {
            kernels[i] = new Kernel(initWeight);
        }
    }

    /**
     * @param input - gamestate matrix
     * @return the output from this filter
     */
    public double[][] calculateForwards(double[][][] input){
        assert channels == input.length;
        this.input = input;

        double[][] out = new double[size][size];
        double[][] temp;

        for (int k = 0; k < input.length; k++) {
            temp = crossCorrelate2DValid(input[k], kernels[k].getWeights(), true);
            out = matrixSum2D(out, temp, bias);
        }

        return out;
    }

    /**
     * @param outputGradient - input gradient from the previous layer
     * @return input gradient for this filter
     */
    public double[][][] calculateBackwards(double[][] outputGradient){
        assert input != null;

        double[][][] inputGradient = new double[channels][inputLength][inputLength];

        for (int i = 0; i < kernels.length; i++) {
            inputGradient[i] = convolution2DFull(outputGradient, kernels[i].getWeights());
            kernels[i].updateWeights(crossCorrelate2DValid(input[i], outputGradient, false), learningRate);
        }

        bias = scaleSubtract(bias, outputGradient, learningRate);

        return inputGradient;
    }

    public String[] saveFilter(int no){
        String[] out = new String[channels+3];
        out[0] = "filter" + no;
        for (int i = 1; i <  channels + 1; i++) {
            out[i] = "";
            out[i] += kernels[i-1].saveKernel();
        }

        out[channels + 1] = "bias";
        out[channels + 2] = saveBias();

        return out;
    }

    public String saveBias(){
        String out = "";

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                out += bias[i][j] + ",";
            }
        }
        return out;
    }

}
