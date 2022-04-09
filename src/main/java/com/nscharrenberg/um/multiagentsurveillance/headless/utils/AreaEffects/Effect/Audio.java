package com.nscharrenberg.um.multiagentsurveillance.headless.utils.AreaEffects.Effect;

public record Audio(double effectLevel) {

    @Override
    public double effectLevel() {
        return effectLevel;
    }
}
