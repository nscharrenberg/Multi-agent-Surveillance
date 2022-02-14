package com.nscharrenberg.um.multiagentsurveillance.headless.utils;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.*;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

// probably have players inherit this class later
public class CharacterVision{
    private int length;
    private Angle direction;
    private Geometrics gm;

    public CharacterVision(int length, Angle direction) {
        this.length = length;
        this.direction = direction;
        gm = new Geometrics();
    }

    public ArrayList<Tile> getRawVision(Tile position) {
        int px = position.getX();
        int py = position.getY();
        int s = (2*length)+1; // value for cone width
        ArrayList<Tile> observation = new ArrayList<Tile>();
        observation.add(position);

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
        }

        return observation;
    }


    private ArrayList<Tile> getRealVision(TileArea board, ArrayList<Tile> rawvision, Tile position) {
        ArrayList<Tile> finalvision = new ArrayList<>();
        boolean validtile = true;

        // Remove out of bound tiles first (maybe redundant if we use Optional)
        rawvision.removeIf(tc -> (tc.getX() < 0 || tc.getY() < 0));
        rawvision.removeIf(tc -> (tc.getX() > board.width() || tc.getY() > board.height()));

        // Check remaining tiles for items
        for (Tile t : rawvision) {
            if (unobstructedTile(board, t)) {
                for (Tile it : gm.getIntersectingTiles(position, t)) {
                    if(!unobstructedTile(board,it)) {
                        validtile = false;
                        break;
                    }
                }
            }

            if(validtile)
                finalvision.add(t);
        }

        return finalvision;
    }

    // TODO: Confirm if return parameter is what we want
    public ArrayList<Tile> getVision(TileArea board, Tile position) {
        return getRealVision(board, getRawVision(position), position);
    }

    private boolean unobstructedTile(TileArea board, Tile t) {
        if(board.getByCoordinates(t.getX(), t.getY()).isPresent()) {
            if (board.getByCoordinates(t.getX(), t.getY()).get().getItems().size() != 0) {
                for (Item im : t.getItems()) {
                    if (im instanceof Wall) {   // Might have to add other checks to this later
                        return false;
                    }
                }
            }
        } else {
            return false;
        }

        return true;
    }

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
