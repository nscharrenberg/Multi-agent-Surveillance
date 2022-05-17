package com.nscharrenberg.um.multiagentsurveillance.agents.SBO;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle.*;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.*;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.*;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Marker;
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
        this.type = typeConverter(ms.getType());
        this.owner = ms.getPlayer();
    }

    private TypePriority typeConverter(Marker.MarkerType mt) {
        if(mt == Marker.MarkerType.DEAD_END) {
            return TypePriority.MarkerDeadEnd;
        } else if (mt == Marker.MarkerType.GUARD_SPOTTED) {
            return TypePriority.MarkerGuard;
        } else if (mt == Marker.MarkerType.INTRUDER_SPOTTED) {
            return TypePriority.MarkerIntruder;
        } else if (mt == Marker.MarkerType.TARGET) {
            return TypePriority.MarkerTarget;
        } else if (mt == Marker.MarkerType.TELEPORTER) {
            return TypePriority.MarkerTeleporter;
        } else if (mt == Marker.MarkerType.SHADED) {
            return TypePriority.MarkerShaded;
        } else {
            return null;
        }
    }

}
