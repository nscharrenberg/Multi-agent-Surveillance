package com.nscharrenberg.um.multiagentsurveillance.headless.utils;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Area;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Tile;

import java.util.ArrayList;

// probably have players inherit this class later
public class CharacterVision {
    private int length;
    private Angle direction;

    public CharacterVision(int length, Angle direction) {
        this.length = length;
        this.direction = direction;
    }

    // TODO: convert it from arraylist to area later on probably, waiting on NOAH's update on area
    public ArrayList<Tile> getRawVision(Tile position) {
        int px = position.getX();
        int py = position.getY();
        int s = (2*length)+1; // value for cone width
        ArrayList<Tile> observation = new ArrayList<Tile>();
        observation.add(position);

        // TODO: handle out of bounds tiles
        if(this.direction == Angle.DOWN) {
            for(int k=0; k < length; k++) {
                for(int r=0; r < s; r++) {
                    observation.add(new Tile((px+length)-k-r, (py+length)-k, null));
                }
                s -= 2;
            }
        }
        else if(this.direction == Angle.RIGHT) {
            for(int k=0; k < length; k++) {
                for(int r=0; r < s; r++) {
                    observation.add(new Tile((px+length)-k, (py+length)-k-r, null));
                }
                s -= 2;
            }
        }
        else if(this.direction == Angle.UP) {
            for(int k=0; k < length; k++) {
                for(int r=0; r < s; r++) {
                    observation.add(new Tile((px-length)+k+r, (py-length)+k, null));
                }
                s -= 2;
            }
        }
        else if(this.direction == Angle.LEFT) {
            for(int k=0; k < length; k++) {
                for(int r=0; r < s; r++) {
                    observation.add(new Tile((px-length)+k, (py-length)+k+r, null));
                }
                s -= 2;
            }
        } else {
            // Invalid direction
        }

        return observation;

    }

    // Ignore for now
//    // TODO: properly rework method to check for item collision
//    public ArrayList<Tile> getRealVision(ArrayList<Tile> rawvision) {
//        ArrayList<Tile> finalvision = new ArrayList<>();
//
//        for (Tile t : rawvision) {
//            // Trim out of bounds tiles
//            if(t.getX() < 0 || t.getY() < 0 || t.getX() > 48 || t.getY() > 24) {
//                //got to check map values
//            }
//            if(t.getItems().size() == 0) {
//                // dummy method, will rework to proper item recognition later
//                finalvision.add(t);
//            }
//        }
//
//        return finalvision;
//    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public Angle getDirection() {
        return direction;
    }

    public void setDirection(Angle direction) {
        this.direction = direction;
    }
}
