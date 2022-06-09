package com.nscharrenberg.um.multiagentsurveillance.agents.DQN.CNN;

public class Filter {
    private int channels;
    private double[][][] input;
    private int kernelSize = 3;
    private Kernel[] kernels;
    private double[][] bias;
    private int inputLength;
    private int outputSize;
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
        outputSize = inputLength - kernelSize + 1;

        bias = new double[outputSize][outputSize];
        for (int i = 0; i < outputSize; i++) {
            for (int j = 0; j < outputSize; j++) {
                bias[i][j] = Math.random()*k*2 - k; // random nvr = 1
            }
        }

        for (int i = 0; i < channels; i++) {
            kernels[i] = new Kernel(initWeight);
        }
    }

    public Filter() {
    }

    /**
     * @param input - gamestate matrix
     * @return the output from this filter
     */
    public double[][] calculateForwards(double[][][] input){
        assert channels == input.length;
        this.input = input;

        double[][] out = new double[outputSize][outputSize];
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

    /**
     * @param input - input channel
     * @param kernelWeights - kernel matrix weights
     * @param forward - if true the activation function is applied
     * @return Either the activated output of the cross correlation or the cross correlation product
     */
    private double[][] crossCorrelate2DValid(double[][] input, double[][] kernelWeights, boolean forward){

        int outLength = input.length - kernelWeights.length + 1;
        double[][] out = new double[outLength][outLength];

        for (int i = 0; i < outLength; i++)
            for (int j = 0; j < outLength; j++)
                out[i][j] += forward ? relu(crossCorrelate(input, kernelWeights, i, j)) : crossCorrelate(input, kernelWeights, i, j);

        return out;
    }

    private double relu(double x){
        return Math.max(0, x);
    }

    private double[][] convolution2DFull(double[][] gradient, double[][] kernel){

        int outLength = gradient.length + kernel.length - 1;
        double[][] out = new double[outLength][outLength];
        double[][] rotKernel = rot180(kernel);
        double[][] paddedGradient = padMatrix(gradient, kernel.length);

        for (int i = 0; i < outLength; i++) {
            for (int j = 0; j < outLength; j++) {
                out[i][j] = crossCorrelate(paddedGradient, rotKernel, i, j);
            }
        }

        return out;
    }

    /**
     * @param input
     * @param kernel
     * @param iOffset
     * @param jOffset
     * @return
     */
    private double crossCorrelate(double[][] input, double[][] kernel, int iOffset, int jOffset){

        double sum = 0;

        for (int i = 0; i < kernel.length; i++) {
            for (int j = 0; j < kernel.length; j++) {
                sum += kernel[i][j] * input[iOffset+i][jOffset+j];
            }
        }

        return sum;
    }

    /**
     * @param input - square matrix
     * @return - input rotated 180 degrees
     */
    private double[][] rot180(double[][] input){

        int length = input.length;
        double[][] out = new double[length][length];
        int index = length - 1;

        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++)
                out[j][i] = input[index-j][index-i];
        }

        return out;
    }

    /**
     * Method used to grow a matrix for full cross correlation. Extra indices are 0
     * @param input - matrix to be grown
     * @param smallSize - length of the matrix input is being cross correlated against
     * @return
     */
    private double[][] padMatrix(double[][] input, int smallSize){

        int size = 2 * (smallSize - 1)+ input.length;
        int diff = smallSize - 1;
        double[][] out = new double[size][size];

        for (int i = diff; i < input.length + diff; i++) {
            for (int j = diff; j < input.length + diff; j++) {
                out[i][j] = input[i-diff][j-diff];
            }
        }

        return out;
    }

    /**
     * Both main and subtract must be square and have the same length
     * @param main - matrix to be subtracted from
     * @param subtract - matrix to be scaled. Then taken from main
     * @param scale - scalar for the subtract matrix
     * @return the main matrix minus the scaled subtract matrix
     */
    public static double[][] scaleSubtract(double[][] main, double[][] subtract, double scale){
        assert main.length == subtract.length : "Unequal lengths provided";

        double[][] out = new double[main.length][main.length];
        for (int i = 0; i < main.length; i++) {
            for (int j = 0; j < main.length; j++) {
                out[i][j] = main[i][j] - (scale * subtract[i][j]);
            }
        }

        return out;
    }

    private double[][] matrixSum2D(double[][] A, double[][] B, double[][] C){
        assert A.length == B.length && A[0].length == B[0].length : "Unequal lengths provided";
        assert A.length == C.length && A[0].length == C[0].length : "Unequal lengths provided";

        double[][] out = new double[A.length][A[0].length];
        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < A[0].length; j++) {
                out[i][j] = A[i][j] + B[i][j] + C[i][j];
            }
        }

        return out;
    }

    public String[] saveFilter(int no){
        String[] out = new String[channels+3];
        out[0] = "filter" + no + ",";
        for (int i = 1; i < channels + 1; i++) {
            out[i] = "";
            out[i] += kernels[i-1].saveKernel();
        }

        out[channels + 1] = "bias" + no + ",";
        out[channels + 2] = saveBias();

        return out;
    }

    private String saveBias(){
        String out = "";

        for (int i = 0; i < outputSize; i++) {
            for (int j = 0; j < outputSize; j++) {
                out += bias[i][j] + ",";
            }
        }
        return out;
    }

    public void loadFilter(double[][] weights, double[] bias){

        for (int i = 0; i < channels; i++)
            kernels[i].loadKernel(weights[i]);

        loadBias(bias);
    }

    private void loadBias(double[] nBias){
        assert nBias.length == outputSize * outputSize : "Filter: Not enough weights to load bias";

        int ind = 0;
        for (int i = 0; i < outputSize; i++)
            for (int j = 0; j < outputSize; j++)
                bias[i][j] = nBias[ind++];
    }

    public int getChannels() {
        return channels;
    }

    public void setChannels(int channels) {
        this.channels = channels;
    }

    public double[][][] getInput() {
        return input;
    }

    public void setInput(double[][][] input) {
        this.input = input;
    }

    public int getKernelSize() {
        return kernelSize;
    }

    public void setKernelSize(int kernelSize) {
        this.kernelSize = kernelSize;
    }

    public Kernel[] getKernels() {
        return kernels;
    }

    public void setKernels(Kernel[] kernels) {
        this.kernels = kernels;
    }

    public double[][] getBias() {
        return bias;
    }

    public void setBias(double[][] bias) {
        this.bias = bias;
    }

    public int getInputLength() {
        return inputLength;
    }

    public void setInputLength(int inputLength) {
        this.inputLength = inputLength;
    }

    public int getOutputSize() {
        return outputSize;
    }

    public void setOutputSize(int outputSize) {
        this.outputSize = outputSize;
    }

    public double getLearningRate() {
        return learningRate;
    }

    public void setLearningRate(double learningRate) {
        this.learningRate = learningRate;
    }
}
