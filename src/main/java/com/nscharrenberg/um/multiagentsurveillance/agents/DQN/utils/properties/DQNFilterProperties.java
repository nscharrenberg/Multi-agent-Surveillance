package com.nscharrenberg.um.multiagentsurveillance.agents.DQN.utils.properties;

public enum DQNFilterProperties {
    CHANNELS("channels"),
    INPUT("input"),
    KERNEL_SIZE("kernelSize"),
    KERNELS("kernels"),
    BIAS("bias"),
    INPUT_LENGTH("inputLength"),
    SIZE("size"),
    LEARNING_RATE("learningRate");

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
