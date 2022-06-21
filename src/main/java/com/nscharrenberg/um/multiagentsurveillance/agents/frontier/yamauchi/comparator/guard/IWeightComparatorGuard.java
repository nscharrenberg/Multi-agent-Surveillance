package com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi.comparator.guard;

import com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi.Frontier;

/**
 * Comparator for guards
 */
public interface IWeightComparatorGuard {

    /**
     * Compare two frontiers
     * @param frontier Frontier
     * @param bestFrontier Best Frontier
     * @return the best frontier from both
     */
    Frontier compare(Frontier frontier, Frontier bestFrontier);
}
