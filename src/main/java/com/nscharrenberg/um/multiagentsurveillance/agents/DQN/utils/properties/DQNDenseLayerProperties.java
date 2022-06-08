package com.nscharrenberg.um.multiagentsurveillance.agents.DQN.utils.properties;

public enum DQNDenseLayerProperties {
    NEURONS("neurons"),
    BIAS("bias");

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
