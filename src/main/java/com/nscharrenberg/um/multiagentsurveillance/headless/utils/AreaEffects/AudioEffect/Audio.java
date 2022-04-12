package com.nscharrenberg.um.multiagentsurveillance.headless.utils.AreaEffects.AudioEffect;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle.Angle;

public record Audio(double effectLevel, Angle angleDirection) {

    @Override
    public double effectLevel() {
        return effectLevel;
    }

    @Override
    public Angle angleDirection() {
        return angleDirection;
    }
}
