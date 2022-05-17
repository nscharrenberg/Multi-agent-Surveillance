package com.nscharrenberg.um.multiagentsurveillance.agents.SBO;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.*;

public enum TypePriority {
    SoundWave(1, SoundWave.class),
    Marker(1, Marker.class);

    TypePriority(double priority, Class<?> instance) {
        this.priority = priority;
        this.instance = instance;

    }

    private final double priority;
    private Class<?> instance;

    public double getPriority() {
        return priority;
    }

    public Class<?> getInstance() {
        return instance;
    }

}
