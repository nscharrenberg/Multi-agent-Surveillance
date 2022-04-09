package com.nscharrenberg.um.multiagentsurveillance.headless.utils.recorder.json;

public record Coordinates(float x, float y) {

    @Override
    public float x() {
        return x;
    }

    @Override
    public float y() {
        return y;
    }
}
