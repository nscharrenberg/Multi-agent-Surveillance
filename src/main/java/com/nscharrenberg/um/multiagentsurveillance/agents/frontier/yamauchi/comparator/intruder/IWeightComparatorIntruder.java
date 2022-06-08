package com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi.comparator.intruder;

import com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi.Frontier;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;

/**
 * Comparator for intruders
 */
public interface IWeightComparatorIntruder {

        /**
         * Compare two frontiers
         * @param frontier Frontier
         * @param bestFrontier Best Frontier
         * @param target Position of the target
         * @return the best frontier from both
         */
        Frontier compare(Frontier frontier, Frontier bestFrontier, Tile target);

}
