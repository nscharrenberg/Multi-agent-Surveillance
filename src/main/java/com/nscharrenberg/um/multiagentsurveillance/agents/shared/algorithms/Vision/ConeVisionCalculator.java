package com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.Vision;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;

import java.util.ArrayList;

public class ConeVisionCalculator {

    public static ArrayList<Tile> getConeVision(Tile position, int length, Action direction) {
        int px = position.getX();
        int py = position.getY();
        int s = (2*length)+1; // value for cone width
        ArrayList<Tile> observation = new ArrayList<Tile>();
        observation.add(position);

        if(direction == Action.DOWN) {
            for(int k=0; k < length; k++) {
                for(int r=0; r < s; r++) {
                    observation.add(new Tile((px+length)-k-r, (py+length)-k));
                }
                s -= 2;
            }
        }
        else if(direction == Action.RIGHT) {
            for(int k=0; k < length; k++) {
                for(int r=0; r < s; r++) {
                    observation.add(new Tile((px+length)-k, (py+length)-k-r));
                }
                s -= 2;
            }
        }
        else if(direction == Action.UP) {
            for(int k=0; k < length; k++) {
                for(int r=0; r < s; r++) {
                    observation.add(new Tile((px-length)+k+r, (py-length)+k));
                }
                s -= 2;
            }
        }
        else if(direction == Action.LEFT) {
            for(int k=0; k < length; k++) {
                for(int r=0; r < s; r++) {
                    observation.add(new Tile((px-length)+k, (py-length)+k+r));
                }
                s -= 2;
            }
        }

        return observation;
    }
}
