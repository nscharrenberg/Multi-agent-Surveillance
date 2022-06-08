package com.nscharrenberg.um.multiagentsurveillance.agents.DQN.utils.properties;

public enum DQNDenseLayerProperties {
    NEURONS("neurons"),
    NUM_INPUTS("numInputs"),
    NUM_OUTPUTS("numOutputs"),
    INPUTS("inputs"),
    OUTPUTS("outputs");

    final String key;

    DQNDenseLayerProperties(String key) {
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
