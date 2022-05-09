package com.nscharrenberg.um.multiagentsurveillance.headless.utils.AreaEffects.AudioEffect;

import com.nscharrenberg.um.multiagentsurveillance.agents.shared.Agent;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;
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

        Action soundAction = computeSoundAction(tileX.getX(), tileX.getY(), tileY.getX(), tileY.getY());

        double effectLevel = computeEffectLevel(distance);

        return new Audio(effectLevel, soundAction);
    }

    @Override
    public double computeEffectLevel(double distance) {
        return 100 - (distance/RANGE * 100);
    }

    @Override
    public Action computeSoundAction(int x1, int y1, int x2, int y2) {
        int x = x2 - x1;
        int y = y2 - y1;

        int absX = Math.abs(x);
        int absY = Math.abs(y);

        if(x == 0 && y == 0)
            throw new RuntimeException("Sound Action Error");


        if(x == 0){

            if(y > 0) return Action.DOWN;
             else return Action.UP;

        } else if(y == 0){

            if(x > 0) return Action.RIGHT;
             else return Action.LEFT;

        } else if(x > 0){

            if(y > 0) {
                if (absX > absY) return Action.RIGHT;
                else return Action.DOWN;
            } else {
                if(absX > absY) return Action.RIGHT;
                else return Action.UP;
            }

        } else {

            if(y > 0) {
                if (absX > absY) return Action.LEFT;
                else return Action.DOWN;
            } else {
                if(absX > absY) return Action.LEFT;
                else return Action.UP;
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
