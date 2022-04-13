package com.nscharrenberg.um.multiagentsurveillance.headless.utils.AreaEffects.AudioEffect;

import com.nscharrenberg.um.multiagentsurveillance.agents.shared.Agent;
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

        Angle soundAngle = computeSoundAngle(tileX.getX(), tileX.getY(), tileY.getX(), tileY.getY());

        double effectLevel = computeEffectLevel(distance);

        return new Audio(effectLevel, soundAngle);
    }

    @Override
    public double computeEffectLevel(double distance) {
        return 100 - (distance/RANGE * 100);
    }

    @Override
    public Angle computeSoundAngle(int x1, int y1, int x2, int y2) {
        int x = x2 - x1;
        int y = y2 - y1;

        int absX = Math.abs(x);
        int absY = Math.abs(y);

        if(x == 0 && y == 0)
            throw new RuntimeException("Sound Angle Error");


        if(x == 0){

            if(y > 0) return Angle.DOWN;
             else return Angle.UP;

        } else if(y == 0){

            if(x > 0) return Angle.RIGHT;
             else return Angle.LEFT;

        } else if(x > 0){

            if(y > 0) {
                if (absX > absY) return Angle.RIGHT;
                else return Angle.DOWN;
            } else {
                if(absX > absY) return Angle.RIGHT;
                else return Angle.UP;
            }

        } else {

            if(y > 0) {
                if (absX > absY) return Angle.LEFT;
                else return Angle.DOWN;
            } else {
                if(absX > absY) return Angle.LEFT;
                else return Angle.UP;
            }

        }
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
