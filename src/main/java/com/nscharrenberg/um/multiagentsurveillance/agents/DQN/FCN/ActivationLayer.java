package com.nscharrenberg.um.multiagentsurveillance.agents.DQN.FCN;

public class ActivationLayer {

    private int numInputs;
    private double[] inputs;
    private double[] outputs;

    public ActivationLayer(int numInputs){
        this.numInputs = numInputs;
    }

    public ActivationLayer() {
        this.numInputs = 0;
    }

    public double[] forward (double[] inputs){
        this.inputs = inputs;
        double[] outputs = new double[inputs.length];

        for (int i = 0; i < inputs.length; i++)
            outputs[i] = Math.max(0, inputs[i]);

        this.outputs = outputs;
        return outputs;
    }

    public double[] backward (double[] dEdY){
        double[] dEdYz = new double[numInputs];

        for (int i = 0; i < dEdY.length; i++)
            dEdYz[i] = dEdY[i] * dZ(outputs[i]);

        return dEdYz;
    }

    private double dZ(double output){
        if (output > 0)
            return 1;
        return 0;
    }

    public int getNumInputs() {
        return numInputs;
    }

    public void setNumInputs(int numInputs) {
        this.numInputs = numInputs;
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
