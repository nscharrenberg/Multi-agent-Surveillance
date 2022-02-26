package com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi.detectors;

import com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi.Frontier;
import com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi.IWeightDetector;

public class SimpleWeightDetector implements IWeightDetector {
    @Override
    public double compute(Frontier frontier) {
        if (frontier.getQueueNode() == null) {
            return frontier.getUnknownAreas();
        }

        return frontier.getUnknownAreas() * frontier.getQueueNode().getDistance();
    }
}
