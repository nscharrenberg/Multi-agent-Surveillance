package com.nscharrenberg.um.multiagentsurveillance.agents.SBO;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle.*;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Player;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.AngleConverter;


import java.util.ArrayList;

public class RLmodel {
    double prioscaler = 0;
    double strengthbias = 1;
    double baseline = 0;
    Angle redirect = null;
    ArrayList<Parameter> inputs;


    public RLmodel() {
        inputs = new ArrayList<Parameter>();
    }

    public boolean AssessParameter(Parameter input, Player player) {
        // Skip its own inputs
        if(input.owner == null || input.owner == player) {
            return false;
        }

        double val = 0;
        // TODO: Calc priority value || will be different for intruders
        val = (input.type.getPriority() * prioscaler) - (strengthbias * input.strength);

        if(val >= baseline) {
            // Set new angle
            // TODO: convert advanced angle to normal angle for the agent
            //redirect = input.direction;

            baseline = val;
            return true;
        }

        return false;

    }

    public void update() {

    }


    public double getBaseline() {
        return baseline;
    }

    public void setBaseline(double baseline) {
        this.baseline = baseline;
    }

    public Angle getRedirect() {
        return redirect;
    }

    public void setRedirect(Angle redirect) {
        this.redirect = redirect;
    }


}
