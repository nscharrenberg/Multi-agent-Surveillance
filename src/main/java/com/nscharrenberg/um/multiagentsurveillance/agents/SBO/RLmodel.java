package com.nscharrenberg.um.multiagentsurveillance.agents.SBO;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle.*;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Player;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.AngleConverter;


import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;

public class RLmodel {
    double prioscaler = 1;
    double strengthbias = 1;
    double baseline = 0;
    Queue<Action> redirect = new PriorityQueue<>();
    ArrayList<Parameter> inputs;


    public RLmodel() {
        inputs = new ArrayList<Parameter>();
    }


    /*
         TODO: Calc priority value || will be different for intruders
            Integrate positive and negative effects
            Handle walls if new angle is blocked
            setup baseline parameter

    */
    public boolean parameterEvaluation(Parameter input, Player player) {
        // Skip its own inputs
        if(input.owner == player) {
            return false;
        }

        double val = 0;
        val = (input.type.getPriority() * prioscaler) - (strengthbias * input.strength);

        if(val > baseline) {
            // Set new angle (might need to duplicate in order to actually perform move)
            redirect.addAll(AngleConverter.split(input.direction));
            System.out.println("Updated path: " + input.direction);

            // Set new baseline (so new inputs dont overwrite existing higher priority ones)
            baseline = val;
            return true;
        }

        if(val < 0) {
            // Return inverse angle
            redirect.addAll(AngleConverter.split(AngleConverter.AngleInverter(input.direction)));
            System.out.println("Updated path: " + redirect);
            return true;

        }

        return false;

    }

    public double getBaseline() {
        return baseline;
    }

    public void setBaseline(double baseline) {
        this.baseline = baseline;
    }

    public Queue<Action> getRedirect() {
        return redirect;
    }

}
