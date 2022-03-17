package com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi.comparator;

import com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi.Frontier;
import com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi.comparator.IWeightComparator;

public class MinDistanceUnknownAreaComparator implements IWeightComparator {
    @Override
    public Frontier compare(Frontier frontier, Frontier bestFrontier) {

        if (frontier.getQueueNode().getDistance() < bestFrontier.getQueueNode().getDistance()) {
            return frontier;
        } else if(frontier.getQueueNode().getDistance() == bestFrontier.getQueueNode().getDistance()){
            if(frontier.getUnknownAreas() > bestFrontier.getUnknownAreas())
                return frontier;
        }

        return bestFrontier;
    }
}
