package com.nscharrenberg.um.multiagentsurveillance.agents.DQN.utils.properties;

public enum DQNNeuronProperties {
    WEIGHTS("weights");

    final String key;

    DQNNeuronProperties(String key) {
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
