package com.nscharrenberg.um.multiagentsurveillance.agents.DQN.neuralNetwork;

import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.CNN.ConvLayer;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.FCN.ActivationLayer;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.FCN.DenseLayer;

import java.util.ArrayList;

import static com.nscharrenberg.um.multiagentsurveillance.agents.DQN.DQN_Util.flatten3D;
import static com.nscharrenberg.um.multiagentsurveillance.agents.DQN.DQN_Util.unFlatten3D;
import static com.nscharrenberg.um.multiagentsurveillance.agents.DQN.neuralNetwork.NetworkWriter.writeNetwork;

public class Network {

    private ConvLayer[] convLayers;
    private DenseLayer[] denseLayers;
    private ActivationLayer activationLayer;
    private int kernelSize = 3;
    private double learningRate;

    private double[] networkOutput;

    public Network(double learningRate){
        this.learningRate = learningRate;
    }

    public void initLayers(int channels, int inputLength){

        int c1Filters = 32, c2Filters = 128, c3Filters = 32;
        int input2Length = outDim(inputLength);
        int input3Length = outDim(input2Length);

        convLayers = new ConvLayer[]{
                new ConvLayer(channels, inputLength, c1Filters, 7, learningRate),
                new ConvLayer(c1Filters, input2Length, c2Filters, 6, learningRate),
                new ConvLayer(c2Filters, input3Length, c3Filters, 6, learningRate),
        };

        int numDenseIn = outDim(input3Length) * outDim(input3Length) * c3Filters;
        int numFinalOut = 3; // TODO: decide on dis

        denseLayers = new DenseLayer[]{
            denseLayers[0] = new DenseLayer(numDenseIn, numDenseIn, learningRate),
            denseLayers[1] = new DenseLayer(numDenseIn, numFinalOut, learningRate),
        };

        activationLayer = new ActivationLayer(numDenseIn);
    }

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

/*    public void backwardPropagate(double[] validated){

        double[][][] inputGradient;

        double[] dEdY = msePrime(numFinalOut, networkOutput, validated);

        dEdY = denseLayers[1].backward(dEdY);
        dEdY = activationLayer.backward(dEdY);
        dEdY = denseLayers[0].backward(dEdY);

        inputGradient = unFlatten3D(dEdY, 32, 2);

        inputGradient = convLayers[2].backward(inputGradient);
        inputGradient = convLayers[1].backward(inputGradient);
        convLayers[0].backward(inputGradient);
    }*/

    private double[] msePrime(double n, double[] yP, double[] yA){

        double[] dEdY = new double[yP.length];

        for (int i = 0; i < dEdY.length; i++) {
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

}
