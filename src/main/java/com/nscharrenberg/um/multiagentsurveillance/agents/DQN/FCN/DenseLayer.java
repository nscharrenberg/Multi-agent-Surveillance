package com.nscharrenberg.um.multiagentsurveillance.agents.DQN.FCN;

public class DenseLayer {

    private Neuron[] neurons;
    private int numOutputs, numNeurons;
    private double[] bias, inputs, outputs;
    private double learningRate;

    public DenseLayer(int numNeurons, int numOutputs, double learningRate){
        this.numOutputs = numOutputs;
        this.numNeurons = numNeurons;
        this.learningRate = learningRate;
        this.bias = new double[numOutputs];
        initNeurons(learningRate);
    }

    public DenseLayer() {
    }

    private void initNeurons(double learningRate){
        neurons = new Neuron[numNeurons];
        for (int i = 0; i < numNeurons; i++) {
            neurons[i] = new Neuron(numOutputs, learningRate);
        }
    }
    
    public double[] forward (double[] inputs) {
        this.inputs = inputs;
        double[] out = new double[numOutputs];

        for (int i = 0; i < numOutputs; i++){
            for (int j = 0; j < numNeurons; j++) {
                out[i] += neurons[j].forward(inputs[j], i);
            }
            out[i] += bias[i];
        }

        this.outputs = out;
        return out;
    }

    public double[] backward (double[] dEdY) {

        double[][] dEdW = transpose(dEdW(dEdY, inputs));

        for (int i = 0; i < numNeurons; i++) {
            neurons[i].backward(dEdW[i]);
        }

        for (int i = 0; i < numOutputs; i++) {
            bias[i] -= learningRate * dEdY[i];
        }


        double[] dEdX = dEdX(dEdY, getLayerWeights());

        return dEdX;
    }

    private double[] dEdX(double[] dEdY, double[][] layerWeights){
        double[] dEdX = new double[numNeurons];

        for (int i = 0; i < numNeurons; i++) {
            for (int j = 0; j < numOutputs; j++) {
                dEdX[i] += dEdY[j] * layerWeights[i][j];
            }
        }

        return dEdX;
    }

    private double[][] dEdW(double[] dEdY, double[] inputs){
        double[][] gradient = new double[numOutputs][numNeurons];

        for (int i = 0; i < numOutputs; i++) {
            for (int j = 0; j < numNeurons; j++) {
                gradient[i][j] = dEdY[i] * inputs[j];
            }
        }


        return gradient;
    }

    private double[][] transpose(double[][] input){
        double[][] output = new double[input[0].length][input.length];

        for (int i = 0; i < input[0].length; i++) {
            for (int j = 0; j < input.length; j++) {
                output[i][j] = input[j][i];
            }
        }

        return output;
    }



    public double[][] getLayerWeights(){
        double[][] out = new double[numNeurons][numOutputs];

        for (int i = 0; i < numNeurons; i++)
            out[i] = neurons[i].getWeights();

        return out;
    }

    public String[] saveDenseLayer(){
/*        double[][] lW = getLayerWeights();
        String[] out = new String[numNeurons];

        for (int i = 0; i < numNeurons; i++) {
            out[i] = "";
            for (int j = 0; j < numInputs; j++)
                out[i] += lW[i][j]+ ",";

            out[i] += neurons[i].getBias() + ",";
        }*/

        return null;
    }

    public Neuron[] getNeurons() {
        return neurons;
    }

    public void setNeurons(Neuron[] neurons) {
        this.neurons = neurons;
    }


    // TODO: UPDATE

    public int getNumInputs() {
        return 0;
    }

    // TODO: UPDATE
    public void setNumInputs(int numInputs) {

    }

    public int getNumNeurons() {
        return numNeurons;
    }

    public void setNumNeurons(int numNeurons) {
        this.numNeurons = numNeurons;
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
