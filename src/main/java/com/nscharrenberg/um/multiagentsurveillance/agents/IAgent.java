package com.nscharrenberg.um.multiagentsurveillance.agents;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle;

public interface IAgent {
    void execute();
    void execute(Angle move);
    Angle decide();
}
