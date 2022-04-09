package com.nscharrenberg.um.multiagentsurveillance.headless.utils.AreaEffects.Effect;

public interface IAudioEffect {

    boolean isEffectReachable(double distance);

    double computeEffectLevel(double distance);
}
