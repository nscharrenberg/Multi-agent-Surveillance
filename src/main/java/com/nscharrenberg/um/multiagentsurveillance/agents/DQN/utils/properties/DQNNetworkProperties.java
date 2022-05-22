package com.nscharrenberg.um.multiagentsurveillance.agents.DQN.utils.properties;

public enum DQNNetworkProperties {
    LEARNING_RATE("learningRate"),
    KERNEL_SIZE("kernelSize"),
    OUTPUT_LENGTH("outputLength"),
    C1_FILTERS("c1Filters"),
    C2_FILTERS("c2Filters"),
    C3_FILTERS("c3Filters"),
    CONV3_LENGTH("conv3Length"),
    ACTIVATION_LAYER("activationLayer"),
    DENSE_LAYER("denseLayers"),
    CONV_LAYER("convLayers");

    final String key;

    DQNNetworkProperties(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return key;
    }
}
