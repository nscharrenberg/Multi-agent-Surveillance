package com.nscharrenberg.um.multiagentsurveillance.agents.DQN.utils.properties;

public enum DQNKernelProperties {
    SIZE("size"),
    WEIGHTS("weights");

    final String key;

    DQNKernelProperties(String key) {
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
