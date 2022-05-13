package com.nscharrenberg.um.multiagentsurveillance.agents.SBO;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle.*;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.*;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.*;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.SoundWave;



public class Parameter {
    AdvancedAngle direction;
    TypePriority type;
    int strength;

    public Parameter(Item par) {

    }

    public Parameter(SoundWave sw) {
        this.direction = sw.getDirection();
        this.strength = sw.getStrength();
        this.type = TypePriority.SoundWave;

    }


}
