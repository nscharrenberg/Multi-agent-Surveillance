package com.nscharrenberg.um.multiagentsurveillance.agents.SBO;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.AdvancedAngle;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.ItemType;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.SoundWave;

public class Parameter {
    AdvancedAngle direction;
    int strength;
    int type;

    public Parameter() {

    }

    public Parameter(SoundWave sw) {
        this.direction = sw.getDirection();
        this.strength = sw.getStrength();
        this.type = ItemType.SOUNDWAVE.getOrder();

    }


}
