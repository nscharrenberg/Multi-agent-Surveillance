package com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi.comparator;

import com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi.Frontier;

public class MaxPathCost implements IWeightComparator{
    @Override
    public Frontier compare(Frontier frontier, Frontier bestFrontier) {

        if (frontier.getQueueNode().getDistance() < bestFrontier.getQueueNode().getDistance()) {
            return frontier;
        } else if(frontier.getQueueNode().getDistance() == bestFrontier.getQueueNode().getDistance()){
            if(frontier.getQueueNode().getPathCost() > bestFrontier.getQueueNode().getPathCost())
                return frontier;
        }

        return bestFrontier;
    }
}
