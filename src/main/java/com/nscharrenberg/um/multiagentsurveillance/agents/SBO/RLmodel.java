package com.nscharrenberg.um.multiagentsurveillance.agents.SBO;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle.*;


import java.util.ArrayList;

public class RLmodel {
    double prioscaler = 0;
    double strengthbias = 1;
    double baseline = 0;
    Action redirect = null;
    ArrayList<Parameter> inputs;


    public RLmodel() {
        inputs = new ArrayList<Parameter>();
    }

    public boolean AssessParameter(Parameter input) {
        double val = 0;
        // TODO: Calc priority value || will be different for intruders
        val = (input.type.getPriority() * prioscaler) - (strengthbias * input.strength);

        if(val >= baseline) {
            // Set new angle
            // redirect = input.direction;

            baseline = val;
            return true;
        }

        return false;

    }

    public void update() {

    }

}
