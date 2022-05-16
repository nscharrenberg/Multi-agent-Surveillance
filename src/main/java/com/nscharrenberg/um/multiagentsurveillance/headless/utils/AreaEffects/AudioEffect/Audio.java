package com.nscharrenberg.um.multiagentsurveillance.headless.utils.AreaEffects.AudioEffect;


import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;

public class Audio {

    private double effectLevel;

    private Action ActionDirection;

    public Audio(double effectLevel, Action ActionDirection) {
        this.effectLevel = effectLevel;
        this.ActionDirection = ActionDirection;
    }

    public void setEffectLevel(double effectLevel) {
        this.effectLevel = effectLevel;
    }

    public void setActionDirection(Action ActionDirection) {
        this.ActionDirection = ActionDirection;
    }

    public double effectLevel() {
        return effectLevel;
    }


    public Action actionDirection() {
        return ActionDirection;
    }
}
