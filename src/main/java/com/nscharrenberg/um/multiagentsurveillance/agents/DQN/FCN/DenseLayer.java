package com.nscharrenberg.um.multiagentsurveillance.agents.DQN.FCN;

public class DenseLayer {

    private Neuron[] neurons;
    private int numInputs, numOutputs;
    private double[] inputs;
    private double[] outputs;

    public DenseLayer(int numInputs, int numOutputs, double learningRate){
        this.numInputs = numInputs;
        this.numOutputs = numOutputs;
        initNeurons(learningRate);
    }

    public DenseLayer() {
    }

    private void initNeurons(double learningRate){
        neurons = new Neuron[numOutputs];
        for (int i = 0; i < numOutputs; i++) {
            neurons[i] = new Neuron(numInputs, learningRate);
        }
    }
    
    public double[] forward (double[] inputs) {
        this.inputs = inputs;
        double[] out = new double[numOutputs];

        for (int i = 0; i < numOutputs; i++)
            out[i] = neurons[i].calculateForward(inputs);

        this.outputs = out;
        return out;
    }

    public double[] backward (double[] dEdY) {
        double[] dEdX = dEdX(dEdY, getLayerWeights());

        for (int i = 0; i < neurons.length; i++)
           neurons[i].calculateBackward(dEdY[i]);

        return dEdX;
    }

    private double[] dEdX(double[] dEdY, double[][] layerWeights){
        double[] dEdX = new double[layerWeights[0].length];

        for (int i = 0; i < dEdX.length; i++) {
            for (int j = 0; j < dEdY.length; j++) {
                dEdX[i] += dEdY[j] + layerWeights[j][i];
            }
        }

        return dEdX;
    }


    public double[][] getLayerWeights(){
        double[][] out = new double[numOutputs][numInputs];

        for (int i = 0; i < numOutputs; i++)
            out[i] = neurons[i].getWeights();

        return out;
    }

    public String[] saveDenseLayer(){
        double[][] lW = getLayerWeights();
        String[] out = new String[numOutputs];

        for (int i = 0; i < numOutputs; i++) {
            out[i] = "";
            for (int j = 0; j < numInputs; j++)
                out[i] += lW[i][j]+ ",";

            out[i] += neurons[i].getBias() + ",";
        }

        return out;
    }

    public Neuron[] getNeurons() {
        return neurons;
    }

    public void setNeurons(Neuron[] neurons) {
        this.neurons = neurons;
    }

    public int getNumInputs() {
        return numInputs;
    }

    public void setNumInputs(int numInputs) {
        this.numInputs = numInputs;
    }

    public int getNumOutputs() {
        return numOutputs;
    }

    public void setNumOutputs(int numOutputs) {
        this.numOutputs = numOutputs;
    }

    public double[] getInputs() {
        return inputs;
    }

    public void setInputs(double[] inputs) {
        this.inputs = inputs;
    }

    public double[] getOutputs() {
        return outputs;
    }

    public void setOutputs(double[] outputs) {
        this.outputs = outputs;
    }
}
