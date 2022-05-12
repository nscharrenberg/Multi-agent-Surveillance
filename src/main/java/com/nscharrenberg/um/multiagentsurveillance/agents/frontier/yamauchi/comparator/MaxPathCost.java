package com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi.comparator;

import com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi.Frontier;

public class MaxPathCost implements IWeightComparator{
    @Override
    public Frontier compare(Frontier frontier, Frontier bestFrontier) {

        int f = frontier.getFrontier().size();
        int bf = bestFrontier.getFrontier().size();
        return f > bf? frontier:bestFrontier;
    }
}
