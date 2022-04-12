package com.nscharrenberg.um.multiagentsurveillance.headless.utils.AreaEffects.AudioEffect;

import com.nscharrenberg.um.multiagentsurveillance.agents.shared.Agent;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle.Angle;

public interface IAudioEffect {

    boolean isEffectReachable(double distance);

    Audio getAudioEffect(Agent x, Agent y, double distance);

    double computeEffectLevel(double distance);

    Angle computeSoundAngle(int x1, int y1, int x2, int y2);

    void setRange(double range);

    double getRange();
}
