package com.nscharrenberg.um.multiagentsurveillance.headless.utils;

import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle.Angle;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.TileArea;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.files.MapImporter;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class TargetDirection {

    public TargetDirection(){}

    public static Angle computeTargetDirection(int x1, int y1) {

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

        int x2 = targetCentre.getX();
        int y2 = targetCentre.getY();

        int x = x2 - x1;
        int y = y2 - y1;

        int absX = Math.abs(x);
        int absY = Math.abs(y);

        if(x == 0 && y == 0)
            throw new RuntimeException("Target position Error");


        if(x == 0){

            if(y > 0) return Angle.DOWN;
            else return Angle.UP;

        } else if(y == 0){

            if(x > 0) return Angle.RIGHT;
            else return Angle.LEFT;

        } else if(x > 0){

            if(y > 0) {
                if (absX > absY) return Angle.RIGHT;
                else return Angle.DOWN;
            } else {
                if(absX > absY) return Angle.RIGHT;
                else return Angle.UP;
            }

        } else {

            if(y > 0) {
                if (absX > absY) return Angle.LEFT;
                else return Angle.DOWN;
            } else {
                if(absX > absY) return Angle.LEFT;
                else return Angle.UP;
            }

        }
    }
    public static Angle computeTargetDirectionTesting(int x1, int y1) {

        File file = new File("src/test/resources/maps/testmap6.txt");

        if (!file.exists()) {
            throw new RuntimeException("Resource not found");
        }

        String path = file.getAbsolutePath();

        MapImporter importer = new MapImporter();

        try {
            importer.load(path);

        } catch (IOException e) {
            throw new RuntimeException("Importer failed");
        }

        return computeTargetDirection(x1, y1);

    }
}