package com.nscharrenberg.um.multiagentsurveillance.agents.DQN.utils.properties;

public enum DQNConvLayerProperties {

    FILTERS("filters");

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
