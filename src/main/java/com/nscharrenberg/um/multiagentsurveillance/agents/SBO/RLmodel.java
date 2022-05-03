package com.nscharrenberg.um.multiagentsurveillance.agents.SBO;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle;

import java.util.ArrayList;

public class RLmodel {
    double scaler = 0;
    double baseline = 0;
    Angle redirect = null;
    ArrayList<Parameter> inputs;


    public RLmodel() {
        inputs = new ArrayList<Parameter>();
    }

    public void AssessParameter(Parameter input) {
        double val = 0;
        // TODO: Calc priority value
        val = input.type.getPriority() * scaler - input.strength;

        if(val >= baseline) {
            // Set new angle
            // redirect = input.direction;

            baseline = val;
        }

    }

    public void update() {

    }

}
