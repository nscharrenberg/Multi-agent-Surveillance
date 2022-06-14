package com.nscharrenberg.um.multiagentsurveillance.agents.DQN.neuralNetwork;

import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.CNN.ConvLayer;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.DQN_Params;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.FCN.ActivationLayer;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.FCN.DenseLayer;

import java.util.ArrayList;

import static com.nscharrenberg.um.multiagentsurveillance.agents.DQN.neuralNetwork.NetworkWriter.readNetwork;
import static com.nscharrenberg.um.multiagentsurveillance.agents.DQN.neuralNetwork.NetworkWriter.writeNetwork;

public class Network {


    private ConvLayer[] convLayers;
    private DenseLayer[] denseLayers;
    private ActivationLayer activationLayer;
    private final double learningRate = DQN_Params.learningRate.valueDbl;
    private final int       c1Filters = DQN_Params.c1Filters.valueInt,
                            c2Filters = DQN_Params.c2Filters.valueInt,
                            c3Filters = DQN_Params.c3Filters.valueInt;
    private int conv3Length;

    private double[] networkOutput;

    public Network(){}


    public void initLayers(int channels, int inputLength){

        int input2Length = outDim(inputLength);
        int input3Length = outDim(input2Length);

        convLayers = new ConvLayer[]{
                new ConvLayer(channels, inputLength, c1Filters, 7, learningRate),
                new ConvLayer(c1Filters, input2Length, c2Filters, 6, learningRate),
                new ConvLayer(c2Filters, input3Length, c3Filters, 6, learningRate)
        };

        conv3Length = outDim(input3Length);
        int numNeurons = conv3Length * conv3Length * c3Filters;


        denseLayers = new DenseLayer[]{
            new DenseLayer(numNeurons, (numNeurons /= 4), learningRate),
            new DenseLayer(numNeurons, DQN_Params.outputLength.valueInt, learningRate)
        };

        activationLayer = new ActivationLayer(numNeurons);
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


    public void backwardPropagate(double[] target, double[] predicted){

        double[][][] inputGradient;

        double[] dEdY = dEdY(target, predicted);

        dEdY = denseLayers[1].backward(dEdY);
        dEdY = activationLayer.backward(dEdY);
        dEdY = denseLayers[0].backward(dEdY);

        inputGradient = unFlatten3D(dEdY, c3Filters, conv3Length);

        inputGradient = convLayers[2].backward(inputGradient);
        inputGradient = convLayers[1].backward(inputGradient);
        convLayers[0].backward(inputGradient);
    }

    private double[] dEdY(double[] target, double[] predicted){
        assert target.length == predicted.length : "dEdY: Vectors length differs";

        double[] dEdY = new double[target.length];

        for (int i = 0; i < dEdY.length; i++)
            dEdY[i] = (predicted[i] - target[i]);

        return dEdY;
    }


    private double[][][] unFlatten3D(double[] input, int channels, int length){
        assert input.length == length * length * channels : "Unflatten: size error";

        double[][][] out = new double[channels][length][length];
        int ind = 0;

        for (int i = 0; i < channels; i++) {
            for (int j = 0; j < length; j++) {
                for (int k = 0; k < length; k++) {
                    out[i][j][k] = input[ind++];
                }
            }
        }
        return out;
    }

    private double[] flatten3D(double[][][] input){
        assert input[0].length == input[0][0].length : "Unequal lengths provided";

        double[] out = new double[input.length * input[0].length * input[0].length];

        int ind = 0;
        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input[0].length; j++) {
                for (int k = 0; k < input[0].length; k++) {
                    out[ind++] = input[i][j][k];
                }
            }
        }

        return out;
    }


    public void saveNetwork(int networkNum){

        String name = Integer.toString(networkNum);

        ArrayList<String[][]> cLayers = new ArrayList<>();
        cLayers.add(convLayers[0].saveLayer());
        cLayers.add(convLayers[1].saveLayer());
        cLayers.add(convLayers[2].saveLayer());

        ArrayList<String[]> dLayers = new ArrayList<>();
        dLayers.add(denseLayers[0].saveDenseLayer());
        dLayers.add(denseLayers[1].saveDenseLayer());

        writeNetwork(cLayers, dLayers, name);
    }

    public void loadNetwork(int networkNum) throws Exception {
        readNetwork(networkNum, this);
    }

    private Network(ConvLayer[] convLayers, DenseLayer[] denseLayers, ActivationLayer activationLayer) {
        this.convLayers = convLayers;
        this.denseLayers = denseLayers;
        this.activationLayer = activationLayer;
    }

    public int getFilterNumber(int layer){
        return convLayers[layer].getNumFilters();
    }

    public int getKernelSize(int layer){
        return convLayers[layer].getKernelSize();
    }

    public int getChannels(int layer){
        return convLayers[layer].getChannels();
    }

    public void loadConvLayer(int layer, double[][][] weights, double[][] bias){
        convLayers[layer].loadLayer(weights, bias);
    }

    public void loadDenseLayer(int layer, double[][] weights, double[] bias){
        denseLayers[layer].loadLayer(weights, bias);
    }

    public Network clone(){
        return new Network(convLayers.clone(), denseLayers.clone(), activationLayer);
    }

    private int outDim(int length){
        return length - DQN_Params.kernelSize.valueInt + 1;
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

}
