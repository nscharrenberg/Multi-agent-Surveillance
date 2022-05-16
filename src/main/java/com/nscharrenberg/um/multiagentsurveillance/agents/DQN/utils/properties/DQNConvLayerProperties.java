package com.nscharrenberg.um.multiagentsurveillance.agents.DQN.utils.properties;

public enum DQNConvLayerProperties {
    INPUT_LENGTH("inputLength"),
    NUM_FILTERS("numFilters"),
    CHANNELS("channels"),
    KERNEL_SIZE("kernelSize"),
    FILTERS("filtes"),
    OUTPUT("output");

    final String key;

    DQNConvLayerProperties(String key) {
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
