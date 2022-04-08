package com.nscharrenberg.um.multiagentsurveillance.headless.utils.AreaEffects.Effect;

public interface Effect {

    boolean isEffectReachable(int distance);

    Effect computeEffectLevel(int distance);

    int getEffectsLevel();
}
