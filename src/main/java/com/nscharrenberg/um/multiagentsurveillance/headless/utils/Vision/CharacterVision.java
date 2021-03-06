package com.nscharrenberg.um.multiagentsurveillance.headless.utils.Vision;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.Collision.Wall;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.Item;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.ShadowTile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.TileArea;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Guard;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Player;

import java.util.ArrayList;
import java.util.Optional;

// probably have players inherit this class later
public class CharacterVision{
    private int length;
    private Action direction;
    private Geometrics gm;
    private Player player;

    public CharacterVision(int length, Action direction, Player player) {
        this.length = length;
        this.direction = direction;
        this.gm = new Geometrics();
        this.player = player;
    }

    public ArrayList<Tile> getVision(TileArea board, Tile position) {
        //return getBasicVision(board, position);
        return getRealVision(board, getConeVision(position), position);
    }

    // Basic method for line vision + adjacent tiles
    public ArrayList<Tile> getBasicVision(TileArea board, Tile position) {
        ArrayList<Tile> vision = new ArrayList<Tile>();
        int px = position.getX();
        int py = position.getY();

        // Add left and right tiles
        if (this.direction == Action.UP || this.direction == Action.DOWN) {
            vision.add(new Tile(px+1,py));
            vision.add(new Tile(px-1,py));
        } else if(this.direction == Action.RIGHT || this.direction == Action.LEFT) {
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

        if(this.direction == Action.DOWN) {
            for(int k=0; k < length; k++) {
                for(int r=0; r < s; r++) {
                    observation.add(new Tile((px+length)-k-r, (py+length)-k));
                }
                s -= 2;
            }
        }
        else if(this.direction == Action.RIGHT) {
            for(int k=0; k < length; k++) {
                for(int r=0; r < s; r++) {
                    observation.add(new Tile((px+length)-k, (py+length)-k-r));
                }
                s -= 2;
            }
        }
        else if(this.direction == Action.UP) {
            for(int k=0; k < length; k++) {
                for(int r=0; r < s; r++) {
                    observation.add(new Tile((px-length)+k+r, (py-length)+k));
                }
                s -= 2;
            }
        }
        else if(this.direction == Action.LEFT) {
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

        //Guards
        boolean isHunting = false;

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
                Tile tile = tileAddOpt.get();
                finalvision.add(tile);

                if(player instanceof Guard && !isHunting){
                    if(tile.hasIntruder()){
                        isHunting = true;
                    }
                }


            } else {
                validtile = true;
            }
        }

        if(player instanceof Guard guard)
            guard.setHunting(isHunting);


        return finalvision;
    }

    private boolean checkDifBetweenPlayerAndShadowTile(Tile tile, Tile playerTile){

        int bound = 2;
        int yDif = Math.abs(tile.getY() - playerTile.getY());
        int xDif = Math.abs(tile.getX() - playerTile.getX());

        if(this.direction == Action.UP || this.direction == Action.DOWN){

            return bound >= yDif;

        }  else if(this.direction == Action.LEFT || this.direction == Action.RIGHT){

            return bound >= xDif;

        }

        return false;
    }

    private boolean unobstructedTile(TileArea board, Tile t) {
        Optional<Tile> optTile = board.getByCoordinates(t.getX(), t.getY());
        if(optTile.isPresent()) {
            Tile tile = optTile.get();

            if(tile instanceof ShadowTile){
                if(!checkDifBetweenPlayerAndShadowTile(tile, player.getTile()))
                    return true;
            }

            if (tile.getItems().size() != 0) {
                for (Item im : tile.getItems()) {
                    if (im instanceof Wall) { // Cant update to collision cause players inherit collision
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

    public Action getDirection() {
        return direction;
    }

    public void setDirection(Action direction) {
        this.direction = direction;
    }
}
