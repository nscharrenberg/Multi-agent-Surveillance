package com.nscharrenberg.um.multiagentsurveillance.headless.utils.AreaEffects.AudioEffect;

import com.nscharrenberg.um.multiagentsurveillance.agents.shared.Agent;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.angleCalculator.AngleTilesCalculator;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle.Angle;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;

public class AudioEffect implements IAudioEffect {
    protected double RANGE;

    public AudioEffect(double range){
        this.RANGE = range;
    }

    @Override
    public boolean isEffectReachable(double distance) {
        return RANGE >= distance;
    }

    @Override
    public Audio getAudioEffect(Agent x, Agent y, double distance) {

        Tile tileX = x.getPlayer().getTile();
        Tile tileY = y.getPlayer().getTile();

        Angle soundAngle = AngleTilesCalculator.computeAngle(tileX, tileY);

        double effectLevel = computeEffectLevel(distance);

        return new Audio(effectLevel, soundAngle);
    }

    @Override
    public double computeEffectLevel(double distance) {
        return 100 - (distance/RANGE * 100);
    }

    @Override
    public void setRange(double range) {
        this.RANGE = range;
    }

    @Override
    public double getRange() {
        return RANGE;
    }
}
