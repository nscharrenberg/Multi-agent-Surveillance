package com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi.comparator.intruder;

import com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi.Frontier;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle.Angle;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;

public interface IWeightComparatorIntruder {

        Frontier compare(Frontier frontier, Frontier bestFrontier, Tile target);

}
