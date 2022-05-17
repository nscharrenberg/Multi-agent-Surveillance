package com.nscharrenberg.um.multiagentsurveillance.headless.utils;

import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle.AdvancedAngle;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.TileArea;

import java.util.List;

public class TargetDirection {

    public TargetDirection(){}
    
    public static AdvancedAngle computeTargetDirectionInAdvancedAngle(int x, int y) {

        TileArea targetArea = Factory.getMapRepository().getTargetArea();
        List<Tile> bounds = targetArea.getBounds();

        if (bounds.isEmpty()) {
            throw new RuntimeException("no target position provided for the intruder");
        }

        // Top Left
        int topLeftX = bounds.get(0).getX();
        int topLeftY = bounds.get(0).getY();

        // Bottom Right
        int bottomRightX = bounds.get(3).getX();
        int bottomRightY = bounds.get(3).getY();

        Tile targetCentre = new Tile((topLeftX + bottomRightX)/2, (topLeftY + bottomRightY)/2);

        Tile intruderPosition = new Tile(x, y);

        if (intruderPosition.getX() == targetCentre.getX())
        {
            if (intruderPosition.getY() < targetCentre.getY())
                return AdvancedAngle.DOWN;
            else
                return AdvancedAngle.UP;
        }
        else if(intruderPosition.getY() == targetCentre.getY())
        {
            if (intruderPosition.getX() < targetCentre.getX())
                return AdvancedAngle.RIGHT;
            else
                return AdvancedAngle.LEFT;
        }
        else if (intruderPosition.getX() > targetCentre.getX())
        {
            if (intruderPosition.getY() < targetCentre.getY())
                return AdvancedAngle.BOTTOM_LEFT;
            else
                return AdvancedAngle.TOP_LEFT;
        }
        else 
            {
            if (intruderPosition.getY() < targetCentre.getY())
            {
                return AdvancedAngle.BOTTOM_RIGHT;
            }
            else
                {
                return AdvancedAngle.TOP_RIGHT;
                }
        }
    }

}
