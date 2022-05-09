package com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi.comparator;

import com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi.Frontier;
import com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi.comparator.IWeightComparator;

public class MinDistanceUnknownAreaComparator implements IWeightComparator {
    @Override
    public Frontier compare(Frontier frontier, Frontier bestFrontier) {

        if (frontier.getDistance() < bestFrontier.getDistance()) {
            return frontier;
        } else if(frontier.getDistance() == bestFrontier.getDistance()){
            if(frontier.getUnknownAreas() > bestFrontier.getUnknownAreas())
                return frontier;
        }

        return bestFrontier;
    }
}
