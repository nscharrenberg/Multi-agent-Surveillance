package com.nscharrenberg.um.multiagentsurveillance.agents.DQN;

public enum DQN_Params {
    xOffset(6),
    yOffset(6),
    inputLength(13),
    inputChannels(3),
    targetUpdate(10),
    gamma(0.69),
    learningRate(0.001),
    kernelSize(3),
    outputLength(4),
    c1Filters(8),
    c2Filters(16),
    c3Filters(8),
    version("V1.0"),
    maxMoves(2000),
    batchSize(256);

    DQN_Params(int valueInt) {
        this.valueInt = valueInt;
    }

    DQN_Params(double valueDbl) {
        this.valueDbl = valueDbl;
    }

    DQN_Params(String valueStr) { this.valueStr = valueStr; }

    public int valueInt;
    public double valueDbl;
    public String valueStr;
}
