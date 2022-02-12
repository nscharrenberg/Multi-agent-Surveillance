package com.nscharrenberg.um.multiagentsurveillance.headless.utils;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Tile;

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
                    linetiles.add(new Tile((int)i, yt, null));
                    //System.out.println("xt: " + i + " yt: " + yt);
                }
            } else {
                for (double i = vmin; i <= vmax; i++) {
                    int xt = (int)Math.round(((i-y0)/a)+x0);
                    linetiles.add(new Tile(xt, (int)i, null));
                    //System.out.println("a: " + a + " xt: " + xt + " yt: " + i);
                }
            }
        } catch(Exception exc) {
            // divide by 0 error
        }

        return linetiles;
    }

}
