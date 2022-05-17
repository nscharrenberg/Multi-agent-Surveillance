package com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi.comparator.guard;

import com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi.Frontier;

/**
 * Compare frontiers by the number unknown tiles on their path
 */
public class MaxPathCost implements IWeightComparatorGuard {
    @Override
    public Frontier compare(Frontier frontier, Frontier bestFrontier) {

        int f = frontier.getFrontier().size();
        int bf = bestFrontier.getFrontier().size();
        return f > bf? frontier:bestFrontier;
    }
}
