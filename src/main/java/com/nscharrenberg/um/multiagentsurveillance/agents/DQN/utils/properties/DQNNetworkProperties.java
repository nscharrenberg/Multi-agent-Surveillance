package com.nscharrenberg.um.multiagentsurveillance.agents.DQN.utils.properties;

public enum DQNNetworkProperties {
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
