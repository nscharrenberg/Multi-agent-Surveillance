package com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi.comparator;

import com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi.Frontier;

public class MaxPathCost implements IWeightComparator{
    @Override
    public Frontier compare(Frontier frontier, Frontier bestFrontier) {

        int f = frontier.getQueueNode().getPathCost() / frontier.getQueueNode().getDistance();
        int bf = bestFrontier.getQueueNode().getPathCost() / bestFrontier.getQueueNode().getDistance();
        return f > bf? frontier:bestFrontier;
    }
}
