package com.nscharrenberg.um.multiagentsurveillance.agents.SBO;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle.*;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.*;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.*;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.MarkerSmell;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Player;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.SoundWave;



public class Parameter {
    AdvancedAngle direction;
    TypePriority type;
    int strength;
    Player owner;

    public Parameter(Item it) {

    }

    public Parameter(SoundWave sw) {
        this.direction = sw.getDirection();
        this.strength = sw.getStrength();
        this.type = TypePriority.SoundWave;
        this.owner = sw.getOwner();
    }

    public Parameter(MarkerSmell ms) {
        this.direction = ms.getDirection();
        this.strength = ms.getStrength();
        this.type = TypePriority.MarkerSmell;
        this.owner = ms.getOwner();
    }

}
