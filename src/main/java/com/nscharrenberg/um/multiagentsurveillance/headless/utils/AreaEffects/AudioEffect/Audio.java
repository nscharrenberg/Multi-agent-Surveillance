package com.nscharrenberg.um.multiagentsurveillance.headless.utils.AreaEffects.AudioEffect;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle.Angle;

public class Audio {

    private double effectLevel;

    private Angle angleDirection;

    public Audio(double effectLevel, Angle angleDirection) {
        this.effectLevel = effectLevel;
        this.angleDirection = angleDirection;
    }

    public void setEffectLevel(double effectLevel) {
        this.effectLevel = effectLevel;
    }

    public void setAngleDirection(Angle angleDirection) {
        this.angleDirection = angleDirection;
    }

    public double effectLevel() {
        return effectLevel;
    }


    public Angle angleDirection() {
        return angleDirection;
    }
}
