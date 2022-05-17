package com.nscharrenberg.um.multiagentsurveillance.agents.SBO;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.*;

public enum TypePriority {
    SoundWave(1),
    MarkerSmell(1);

    private final double priority;

    TypePriority(double priority) {
        this.priority = priority;

    }

    public double getPriority() {
        return priority;
    }

}
