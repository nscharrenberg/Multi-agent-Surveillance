package com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi.comparator.guard;

import com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi.Frontier;

public interface IWeightComparatorGuard {
    Frontier compare(Frontier frontier, Frontier bestFrontier);
}
