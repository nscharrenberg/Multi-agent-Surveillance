package com.nscharrenberg.um.multiagentsurveillance.headless.utils.AreaEffects;

import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.ItemAlreadyOnTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle.*;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.SoundWave;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.*;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.*;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.abs;

public class MarkerRange {
    private final int range;

    public MarkerRange(int range) {
        this.range = range;
    }

//    public void setMarker(TileArea board, Tile position) throws ItemAlreadyOnTileException {
//        //Get all surrounding tiles within the range
//        TileArea surrounding = new TileArea(board.subset(position.getX() - range, position.getY() - range,
//                position.getX() + range, position.getY() + range));
//
//        // Update the Tile with a SoundWave item
//        for (Map.Entry<Integer, HashMap<Integer, Tile>> rowEntry : surrounding.getRegion().entrySet()) {
//            for (Map.Entry<Integer, Tile> colEntry : rowEntry.getValue().entrySet()) {
//                Tile st = colEntry.getValue();
//                if (st != null) {
//                    st.add(new SoundWave(st, getStrength(st, position), getDirection(st, position), null));
//                }
//            }
//        }
//    }

    // t1 = current tile, t2 = centre position
    public AdvancedAngle getDirection(Tile t1, Tile t2) {
        if (t1.getX() == t2.getX()) {
            if (t1.getY() < t2.getY())
                return AdvancedAngle.DOWN;
            else
                return AdvancedAngle.UP;
        } else if(t1.getY() == t2.getY()) {
            if (t1.getX() < t2.getX())
                return AdvancedAngle.RIGHT;
            else
                return AdvancedAngle.LEFT;
        } else if (t1.getX() > t2.getX()) {
            if (t1.getY() < t2.getY())
                return AdvancedAngle.BOTTOM_LEFT;
            else
                return AdvancedAngle.TOP_LEFT;
        } else {
            if (t1.getY() < t2.getY())
                return AdvancedAngle.BOTTOM_RIGHT;
            else
                return AdvancedAngle.TOP_RIGHT;
        }
    }

    public int getStrength(Tile current, Tile target) {
        return Math.max(abs(current.getX()-target.getX()), abs(current.getY()-target.getY()));
    }

}
