package com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi.comparator.guard;

import com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi.Frontier;

public class MinDistanceUnknownAreaComparator implements IWeightComparatorGuard {
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
