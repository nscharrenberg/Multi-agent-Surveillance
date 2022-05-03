package com.nscharrenberg.um.multiagentsurveillance.agents.SBO;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.AdvancedAngle;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.ItemType;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.SoundWave;


public class Parameter {
    AdvancedAngle direction;
    TypePriority type;
    int strength;

    public Parameter() {

    }

    public Parameter(SoundWave sw) {
        this.direction = sw.getDirection();
        this.strength = sw.getStrength();
        this.type = TypePriority.SoundWave;

    }


}
