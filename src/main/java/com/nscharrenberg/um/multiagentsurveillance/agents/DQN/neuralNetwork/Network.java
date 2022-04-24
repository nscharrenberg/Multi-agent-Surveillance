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
    private double learningRate;

    int CLO;
    private int numDenseIn;
    private int numFinalOut;
    private double[] networkOutput;

    public Network(double learningRate){
        this.learningRate = learningRate;
    }

    private void initLayers(){
        convLayers = new ConvLayer[]{
                new ConvLayer(6, 8, 32, 7, learningRate),
                new ConvLayer(32, 6, 128, 6, learningRate),
                new ConvLayer(128, 4, 32, 6, learningRate),
        };
        denseLayers = new DenseLayer[]{
            denseLayers[0] = new DenseLayer(numDenseIn, numDenseIn, learningRate),
            denseLayers[1] = new DenseLayer(numDenseIn, numFinalOut, learningRate),
        };

        activationLayer = new ActivationLayer(CLO);
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

    public void backwardPropagate(double[] validated){

        double[][][] inputGradient;
        if (validated[0] > 1000)
            System.out.println("big");

        double[] dEdY = msePrime(numFinalOut, networkOutput, validated);

        dEdY = denseLayers[1].backward(dEdY);
        dEdY = activationLayer.backward(dEdY);
        dEdY = denseLayers[0].backward(dEdY);

        inputGradient = unFlatten3D(dEdY, 32, 2);

        inputGradient = convLayers[2].backward(inputGradient);
        inputGradient = convLayers[1].backward(inputGradient);
        convLayers[0].backward(inputGradient);
    }

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
}
