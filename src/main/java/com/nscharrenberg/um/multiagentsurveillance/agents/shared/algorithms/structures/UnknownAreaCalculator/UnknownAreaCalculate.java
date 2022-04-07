package com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.structures.UnknownAreaCalculator;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle.AdvancedAngle;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Area;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.BoardUtils;

import java.util.HashMap;
import java.util.Map;

public class UnknownAreaCalculate {

    public int calculateUnknownArea(Area<Tile> board, Tile tile){

        HashMap<AdvancedAngle, Tile> neighbours = BoardUtils.getNeighbours(board, tile);

        int point = 0;
        for (Map.Entry<AdvancedAngle, Tile> neighbour : neighbours.entrySet()) {
            if (neighbour.getValue() == null)
                point++;
        }

        return point;
    }
}
