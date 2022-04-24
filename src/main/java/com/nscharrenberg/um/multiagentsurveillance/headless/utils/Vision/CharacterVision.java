package com.nscharrenberg.um.multiagentsurveillance.headless.utils.Vision;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle.Angle;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.Item;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.Collision.Wall;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.ShadowTile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.TileArea;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.Geometrics;

import java.util.ArrayList;
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

    public ArrayList<Tile> getVision(TileArea board, Tile position) {
        //return getBasicVision(board, position);
        return getRealVision(board, getConeVision(position), position);
    }

    // Basic method for line vision + adjacent tiles
    private ArrayList<Tile> getBasicVision(TileArea board, Tile position) {
        ArrayList<Tile> vision = new ArrayList<Tile>();
        int px = position.getX();
        int py = position.getY();

        // Add left and right tiles
        if (this.direction == Angle.UP || this.direction == Angle.DOWN) {
            vision.add(new Tile(px+1,py));
            vision.add(new Tile(px-1,py));
        } else if(this.direction == Angle.RIGHT || this.direction == Angle.LEFT) {
            vision.add(new Tile(px,py+1));
            vision.add(new Tile(px,py-1));
        }

        // Add tiles in vision line
        Tile current;
        switch(direction) {
            case UP:
                for(int i = 0; i < this.length; i++) {
                    current = new Tile(px,py-i);
                    vision.add(current);
                    if(unobstructedTile(board, current))
                        break;
                }
                break;
            case DOWN:
                for(int i = 0; i < this.length; i++) {
                    current = new Tile(px,py+i);
                    vision.add(current);
                    if(unobstructedTile(board, current))
                        break;
                }
                break;
            case RIGHT:
                for(int i = 0; i < this.length; i++) {
                    current = new Tile(px+i, py);
                    vision.add(current);
                    if(unobstructedTile(board, current))
                        break;
                }
                break;
            case LEFT:
                for(int i = 0; i < this.length; i++) {
                    current = new Tile(px-i, py);
                    vision.add(current);
                    if(unobstructedTile(board, current))
                        break;
                }
                break;
        }

        return vision;
    }

    // -------------- Cone Vision Methods --------------
    private ArrayList<Tile> getConeVision(Tile position) {
        int px = position.getX();
        int py = position.getY();
        int s = (2*length)+1; // value for cone width
        ArrayList<Tile> observation = new ArrayList<Tile>();
        observation.add(position);

        if(this.direction == Angle.DOWN) {
            for(int k=0; k < length; k++) {
                for(int r=0; r < s; r++) {
                    observation.add(new Tile((px+length)-k-r, (py+length)-k));
                }
                s -= 2;
            }
        }
        else if(this.direction == Angle.RIGHT) {
            for(int k=0; k < length; k++) {
                for(int r=0; r < s; r++) {
                    observation.add(new Tile((px+length)-k, (py+length)-k-r));
                }
                s -= 2;
            }
        }
        else if(this.direction == Angle.UP) {
            for(int k=0; k < length; k++) {
                for(int r=0; r < s; r++) {
                    observation.add(new Tile((px-length)+k+r, (py-length)+k));
                }
                s -= 2;
            }
        }
        else if(this.direction == Angle.LEFT) {
            for(int k=0; k < length; k++) {
                for(int r=0; r < s; r++) {
                    observation.add(new Tile((px-length)+k, (py-length)+k+r));
                }
                s -= 2;
            }
        }

        return observation;
    }

    // Method for vision collision (only needed for cone vision)
    private ArrayList<Tile> getRealVision(TileArea board, ArrayList<Tile> rawvision, Tile position) {
        ArrayList<Tile> finalvision = new ArrayList<>();
        boolean validtile = true;

        // Remove out of bound tiles first
        rawvision.removeIf(tc -> (tc.getX() < 0 || tc.getY() < 0));
        rawvision.removeIf(tc -> (tc.getX() > board.width() || tc.getY() > board.height()));

        int xBound = 0;
        int yBound = 0;
        boolean flag = true;

        // Check remaining tiles for items
        for (Tile t : rawvision) {
            for (Tile it : gm.getIntersectingTiles(position, t)) {
                if (unobstructedTile(board, it)) {
                    validtile = false;
                    break;
                }
            }

            if(validtile) {
                Optional<Tile> tileAddOpt = board.getByCoordinates(t.getX(), t.getY());

                if (tileAddOpt.isEmpty()) {
                    continue;
                }

                if(tileAddOpt.get() instanceof ShadowTile){
                    Tile tileAdd = tileAddOpt.get();
                    if(flag){
                        flag = false;
                        xBound = tileAdd.getX();
                        yBound = tileAdd.getY();
                    }

                    if(!checkShadowTile(position, xBound, yBound))
                        continue;

                }

                finalvision.add(tileAddOpt.get());
            } else {
                validtile = true;
            }
        }

        return finalvision;
    }

    private boolean checkShadowTile(Tile tile, int xBound, int yBound){

        int shadowBound = 1;

        if(this.direction == Angle.UP){

            return tile.getY() >= yBound - shadowBound;

        } else if(this.direction == Angle.DOWN){

            return tile.getY() <= yBound + shadowBound;

        } else if(this.direction == Angle.LEFT){

            return tile.getX() >= xBound - shadowBound;

        } else if(this.direction == Angle.RIGHT){

            return tile.getX() <= xBound + shadowBound;

        }

        return false;
    }


    private boolean unobstructedTile(TileArea board, Tile t) {
        Optional<Tile> optTile = board.getByCoordinates(t.getX(), t.getY());
        if(optTile.isPresent()) {
            Tile tile = optTile.get();
            if (tile.getItems().size() != 0) {
                for (Item im : tile.getItems()) {
                    if (im instanceof Wall) {   // Might have to add other checks to this later
                        return true;
                    }
                }
            }
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
