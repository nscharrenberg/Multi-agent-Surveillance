package com.nscharrenberg.um.multiagentsurveillance.headless.utils.targetPositionCalculator;

import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.intersectionCalculator.IntersectionPoint;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.intersectionCalculator.Point;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;

public class TargetPositionCalculator {

    private Point firstPosition;
    private Point imitatedFirstPoint;

    private Point secondPosition;
    private Point imitatedSecondPoint;

    private int moveCount = 0;


    public Tile calculate(double targetAngle, Tile position){
        if(moveCount == 0){
            firstMove(targetAngle, position);
            return null;
        } else if(moveCount == 1){
            secondMove(targetAngle, position);
            return IntersectionPoint.calculateIntersectionPoint(firstPosition, imitatedFirstPoint,
                    secondPosition, imitatedSecondPoint);
        } else {
            throw new RuntimeException("Error in the target position calculation, should be calculated step before");
        }
    }

    private void firstMove(double firstTargetAngle, Tile firstPosition){
        this.firstPosition = new Point(firstPosition.getX(), firstPosition.getY());
        double x = (Math.round(firstPosition.getX() + (10000 * Math.cos(firstTargetAngle))));
        double y = (Math.round(firstPosition.getY() + (10000 * Math.sin(firstTargetAngle))));
        this.imitatedFirstPoint = new Point(x, y);
        this.moveCount++;
    }

    private void secondMove(double secondTargetAngle, Tile secondPosition){
        this.secondPosition = new Point(secondPosition.getX(), secondPosition.getY());
        double x = (secondPosition.getX() + (10000 * Math.cos(secondTargetAngle)));
        double y = (secondPosition.getY() + (10000 * Math.sin(secondTargetAngle)));
        this.imitatedSecondPoint = new Point(x, y);
        this.moveCount++;
    }
}
