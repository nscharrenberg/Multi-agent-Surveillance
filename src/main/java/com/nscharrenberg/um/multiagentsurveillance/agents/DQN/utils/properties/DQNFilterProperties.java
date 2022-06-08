package com.nscharrenberg.um.multiagentsurveillance.agents.DQN.utils.properties;

public enum DQNFilterProperties {
    KERNELS("kernels"),
    BIAS("bias");

    final String key;

    DQNFilterProperties(String key) {
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
