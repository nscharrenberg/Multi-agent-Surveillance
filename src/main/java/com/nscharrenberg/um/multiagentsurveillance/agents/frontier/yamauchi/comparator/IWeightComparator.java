package com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi.comparator;

import com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi.Frontier;

public interface IWeightComparator {
    Frontier compare(Frontier frontier, Frontier bestFrontier);
}
