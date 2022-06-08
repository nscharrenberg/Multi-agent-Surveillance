package com.nscharrenberg.um.multiagentsurveillance.headless.utils.AreaEffects.AudioEffect;

import com.nscharrenberg.um.multiagentsurveillance.agents.shared.Agent;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;

public interface ISoundEffect {

    boolean isEffectReachable(double distance);

    Sound getSoundEffect(Agent x, Agent y, double distance);

    double computeEffectLevel(double distance);

    void setRange(double range);

    double getRange();
}
