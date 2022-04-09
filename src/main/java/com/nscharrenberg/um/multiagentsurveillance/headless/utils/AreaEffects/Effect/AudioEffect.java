package com.nscharrenberg.um.multiagentsurveillance.headless.utils.AreaEffects.Effect;

public class AudioEffect implements IAudioEffect {
    protected final double RANGE;

    public AudioEffect(double range){
        this.RANGE = range;
    }

    @Override
    public boolean isEffectReachable(double distance) {
        return RANGE >= distance;
    }

    @Override
    public double computeEffectLevel(double distance){
        return Math.abs(distance/RANGE - 0.99) * 100;
    }
}
