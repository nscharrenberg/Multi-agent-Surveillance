package com.nscharrenberg.um.multiagentsurveillance.headless.utils;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.TileArea;

import java.util.ArrayList;
import java.util.Optional;

public class Geometrics {

    public Geometrics() {

    }

    public ArrayList<Tile> getIntersectingTiles(TileArea board, Tile position, Tile endpoint) {
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
                    Tile t = findTile(board, (int) i, yt);

                    if (t == null) continue;

                    linetiles.add(t);
                    //System.out.println("xt: " + i + " yt: " + yt);
                }
            } else {
                for (double i = vmin; i <= vmax; i++) {
                    int xt = (int)Math.round(((i-y0)/a)+x0);
                    Tile t = findTile(board, xt, (int)i);
                    if (t == null) continue;
                    linetiles.add(t);
                    //System.out.println("a: " + a + " xt: " + xt + " yt: " + i);
                }
            }
        } catch(Exception exc) {
            // divide by 0 error
        }

        return linetiles;
    }

    private Tile findTile(TileArea board, int x, int y) {
        Optional<Tile> tileOpt = board.getByCoordinates(x, y);

        if (tileOpt.isEmpty()) {
            return null;
        }

        return tileOpt.get();
    }

}
