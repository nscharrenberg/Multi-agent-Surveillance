package com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.Vision;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.Collision.Wall;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.Item;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.ShadowTile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.TileArea;

import java.util.Optional;

public class UnobstructedTile {

    private static boolean checkDifBetweenPlayerAndShadowTile(Tile tile, Tile playerTile, Action direction){

        int bound = 2;
        int yDif = Math.abs(tile.getY() - playerTile.getY());
        int xDif = Math.abs(tile.getX() - playerTile.getX());

        if(direction == Action.UP || direction == Action.DOWN){

            return bound >= yDif;

        }  else if(direction == Action.LEFT || direction == Action.RIGHT){

            return bound >= xDif;

        }

        return false;
    }

    public static boolean isUnobstructedTile(TileArea board, Tile t, Tile playerTile, Action direction) {
        Optional<Tile> optTile = board.getByCoordinates(t.getX(), t.getY());
        if(optTile.isPresent()) {
            Tile tile = optTile.get();

            if(tile instanceof ShadowTile){
                if(!checkDifBetweenPlayerAndShadowTile(tile, playerTile, direction))
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

}
