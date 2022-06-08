package com.nscharrenberg.um.multiagentsurveillance.agents.DQN.utils.properties;

public enum DQNActivationLayerProperties {
    NUM_INPUTS("numInputs"),
    INPUTS("inputs"),
    OUTPUTS("outputs");

    final String key;

    DQNActivationLayerProperties(String key) {
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
