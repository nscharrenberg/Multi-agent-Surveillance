package com.nscharrenberg.um.multiagentsurveillance.headless.utils.AreaEffects.Effect;

public abstract class CEffect implements Effect{

    protected int effectLevel = 0;

    protected final int RANGE;

    public CEffect(int range){
        this.RANGE = range;
    }

    @Override
    public boolean isEffectReachable(int distance) {
        return false;
    }

    @Override
    public Effect computeEffectLevel(int distance) {
        return null;
    }

    @Override
    public int getEffectsLevel() {
        return effectLevel;
    }
}
