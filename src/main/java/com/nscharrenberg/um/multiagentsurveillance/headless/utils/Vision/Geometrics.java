package com.nscharrenberg.um.multiagentsurveillance.headless.utils.Vision;

import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;

import java.util.ArrayList;

public class Geometrics {

    public Geometrics() {

    }

    public ArrayList<Tile> getIntersectingTiles(Tile position, Tile endpoint) {
        ArrayList<Tile> linetiles = new ArrayList<>();
        double x0 = position.getX();
        double y0 = position.getY();
        double x1 = endpoint.getX();
        double y1 = endpoint.getY();

        /*
        Y formula:  y = (a * (x-x0)) + y0
        X formula:  x = (y-y0) / a + x0
        */
        try {
            double a = (y1-y0)/(x1-x0);
            double imin = Math.min(x0,x1);
            double imax = Math.max(x0,x1);
            double vmin = Math.min(y0,y1);
            double vmax = Math.max(y0,y1);

            // Algorithm depends on slope
            if(Math.abs(a) <= 1) {
                for (double i = imin; i <= imax; i++) {
                    int yt = (int)Math.round((a * (i-x0)) + y0);
                    linetiles.add(new Tile((int)i, yt));
                    //System.out.println("xt: " + i + " yt: " + yt);
                }
            } else {
                for (double i = vmin; i <= vmax; i++) {
                    int xt = (int)Math.round(((i-y0)/a)+x0);
                    linetiles.add(new Tile(xt, (int)i));
                    //System.out.println("a: " + a + " xt: " + xt + " yt: " + i);
                }
            }
        } catch(Exception exc) {
            // divide by 0 error
        }

        // Remove start and end tiles
        linetiles.removeIf(tc -> (tc.getX() == position.getX() && tc.getY() == position.getY()));
        linetiles.removeIf(tc -> (tc.getX() == endpoint.getX() && tc.getY() == endpoint.getY()));

        return linetiles;
    }

    // Used for agent audio
    public ArrayList<Tile> getSurroundingTiles() {

        return null;
    }

}
