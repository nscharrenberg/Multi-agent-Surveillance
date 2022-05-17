package com.nscharrenberg.um.multiagentsurveillance.agents.SBO;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.*;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Marker;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.MarkerSmell;

public enum TypePriority {
    SoundWave(3),
    MarkerDeadEnd(-5),
    MarkerTarget(1),
    MarkerGuard(-4),
    MarkerIntruder(4),
    MarkerTeleporter(1),
    MarkerShaded(1);

    private final double priority;

    TypePriority(double priority) {
        this.priority = priority;
    }

    public double getPriority() {
        return priority;
    }

}
