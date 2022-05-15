package com.nscharrenberg.um.multiagentsurveillance.agents.DQN.neuralNetwork;

import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.CNN.ConvLayer;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.FCN.ActivationLayer;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.FCN.DenseLayer;

import java.util.ArrayList;

import static com.nscharrenberg.um.multiagentsurveillance.agents.DQN.DQN_Util.flatten3D;
import static com.nscharrenberg.um.multiagentsurveillance.agents.DQN.DQN_Util.unFlatten3D;
import static com.nscharrenberg.um.multiagentsurveillance.agents.DQN.neuralNetwork.NetworkWriter.writeNetwork;

public class Network {

    // TODO: create a read network method to load saved weights

    private ConvLayer[] convLayers;
    private DenseLayer[] denseLayers;
    private ActivationLayer activationLayer;
    private int kernelSize = 3;
    private double learningRate;
    private int outputLength = 3; // TODO: Add markers to decisions
    private int c1Filters = 32, c2Filters = 128, c3Filters = 32;
    private int conv3Length;

    private double[] networkOutput;

    public Network(double learningRate){
        this.learningRate = learningRate;
    }

    public Network() {
        this.learningRate = 0;
    }

    public void initLayers(int channels, int inputLength){


        int input2Length = outDim(inputLength);
        int input3Length = outDim(input2Length);

        convLayers = new ConvLayer[]{
                new ConvLayer(channels, inputLength, c1Filters, 7, learningRate),
                new ConvLayer(c1Filters, input2Length, c2Filters, 6, learningRate),
                new ConvLayer(c2Filters, input3Length, c3Filters, 6, learningRate)
        };

        conv3Length = outDim(input3Length);
        int numDenseIn = conv3Length * conv3Length * c3Filters;


        denseLayers = new DenseLayer[]{
            new DenseLayer(numDenseIn, numDenseIn, learningRate),
            new DenseLayer(numDenseIn, outputLength, learningRate)
        };

        activationLayer = new ActivationLayer(numDenseIn);
    }


    /**
     * Function controls forward propagation through the network
     * @param state - tensor of the current state of the agent
     * @return - networks prediction value vector
     */
    public double[] forwardPropagate(double[][][] state){

        double[][][] convOut = state.clone();

        for (ConvLayer conv : convLayers)
            convOut = conv.forward(convOut);

        networkOutput = flatten3D(convOut);

        networkOutput = denseLayers[0].forward(networkOutput);
        networkOutput = activationLayer.forward(networkOutput);
        networkOutput = denseLayers[1].forward(networkOutput);


        return networkOutput;
    }

    // TODO: Implement and validate backward propagation

    public void backwardPropagate(double[] validated){

        double[][][] inputGradient;

        double[] dEdY = msePrime(outputLength, networkOutput, validated);

        dEdY = denseLayers[1].backward(dEdY);
        dEdY = activationLayer.backward(dEdY);
        dEdY = denseLayers[0].backward(dEdY);

        inputGradient = unFlatten3D(dEdY, c3Filters, conv3Length);

        inputGradient = convLayers[2].backward(inputGradient);
        inputGradient = convLayers[1].backward(inputGradient);
        convLayers[0].backward(inputGradient);
    }

    private double[] msePrime(int n, double[] yP, double[] yA){

        double[] dEdY = new double[n];

        for (int i = 0; i < n; i++) {
            dEdY[i] = (2 / n) * (yP[i] - yA[i]);
        }

        return dEdY;
    }

    public void saveNetwork(int networkNum, String team){

        String name = team + networkNum;

        ArrayList<String[][]> cLayers = new ArrayList<>();
        cLayers.add(convLayers[0].saveLayer());
        cLayers.add(convLayers[1].saveLayer());
        cLayers.add(convLayers[2].saveLayer());

        ArrayList<String[]> dLayers = new ArrayList<>();
        dLayers.add(denseLayers[0].saveDenseLayer());
        dLayers.add(denseLayers[1].saveDenseLayer());

        writeNetwork(cLayers, dLayers, name);
    }

    private int outDim(int length){
        return length - kernelSize + 1;
    }

    public ConvLayer[] getConvLayers() {
        return convLayers;
    }

    public void setConvLayers(ConvLayer[] convLayers) {
        this.convLayers = convLayers;
    }

    public DenseLayer[] getDenseLayers() {
        return denseLayers;
    }

    public void setDenseLayers(DenseLayer[] denseLayers) {
        this.denseLayers = denseLayers;
    }

    public ActivationLayer getActivationLayer() {
        return activationLayer;
    }

    public void setActivationLayer(ActivationLayer activationLayer) {
        this.activationLayer = activationLayer;
    }

    public int getKernelSize() {
        return kernelSize;
    }

    public double getLearningRate() {
        return learningRate;
    }

    public int getOutputLength() {
        return outputLength;
    }

    public int getC1Filters() {
        return c1Filters;
    }

    public int getC2Filters() {
        return c2Filters;
    }

    public int getC3Filters() {
        return c3Filters;
    }

    public int getConv3Length() {
        return conv3Length;
    }

    public void setConv3Length(int conv3Length) {
        this.conv3Length = conv3Length;
    }

    public double[] getNetworkOutput() {
        return networkOutput;
    }

    public void setNetworkOutput(double[] networkOutput) {
        this.networkOutput = networkOutput;
    }

    public void setLearningRate(double learningRate) {
        this.learningRate = learningRate;
    }

    public void setKernelSize(int kernelSize) {
        this.kernelSize = kernelSize;
    }

    public void setOutputLength(int outputLength) {
        this.outputLength = outputLength;
    }

    public void setC1Filters(int c1Filters) {
        this.c1Filters = c1Filters;
    }

    public void setC2Filters(int c2Filters) {
        this.c2Filters = c2Filters;
    }

    public void setC3Filters(int c3Filters) {
        this.c3Filters = c3Filters;
    }
}
