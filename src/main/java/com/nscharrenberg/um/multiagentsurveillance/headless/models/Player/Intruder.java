package com.nscharrenberg.um.multiagentsurveillance.headless.models.Player;

import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle.AdvancedAngle;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle.Angle;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.TileArea;

import java.util.List;
import java.util.Objects;

public class Intruder extends Player {
    private double sprintSpeed;
    private boolean isSprinting = false;
    private Angle targetDirection;

    public Intruder(Tile position, Angle direction) {
        // TODO: Read speed from Configuration
        super(position, direction, 10);

        // TODO: Read sprint speed from Configuration
        this.sprintSpeed = 20;

        targetDirection = computeTargetDirection(position.getX(), position.getY());
    }

    public double getSprintSpeed() {
        return sprintSpeed;
    }

    public void setSprintSpeed(double sprintSpeed) {
        this.sprintSpeed = sprintSpeed;
    }

    public boolean isSprinting() {
        return isSprinting;
    }

    public void setSprinting(boolean sprinting) {
        isSprinting = sprinting;
    }

    @Override
    public double getSpeed() {
        if (isSprinting) {
            return sprintSpeed;
        }

        return super.getSpeed();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Intruder intruder = (Intruder) o;
        return Double.compare(intruder.sprintSpeed, sprintSpeed) == 0 && isSprinting == intruder.isSprinting;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), sprintSpeed, isSprinting);
    }

    public Angle computeTargetDirection(int x1, int y1) {
        
        File file = new File("src/test/resources/maps/exam.txt");

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


}
