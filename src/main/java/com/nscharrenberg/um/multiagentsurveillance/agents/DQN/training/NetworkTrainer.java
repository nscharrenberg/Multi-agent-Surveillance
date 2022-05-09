package com.nscharrenberg.um.multiagentsurveillance.agents.DQN.training;

import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.BoardNotBuildException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.InvalidTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Player;

public class NetworkTrainer {

    private int velocity = 1;
    private double[] error;
    private double[] output;


    public NetworkTrainer(){

    }



    public double[] checkMoves(double[] output, Player player) throws InvalidTileException, BoardNotBuildException {
        error = new double[output.length];
        this.output = output;

        Action direction = player.getDirection();

        int x = player.getTile().getX();
        int y = player.getTile().getY();

        // what way is up
        switch (direction){
            case UP -> verticalCheck(x, y, true);
            case DOWN -> verticalCheck(x, y, false);
            case LEFT -> horizontalCheck(x, y, false);
            case RIGHT -> horizontalCheck(x, y, true);
        }

        return error;
    }

    private double collisionError(Tile selected){
        if (selected.isCollision())
            return -output[0];
        return  0;
    }

    private void verticalCheck(int x, int y, boolean increasing) throws InvalidTileException, BoardNotBuildException {

        // seriously what way is up lol
        int displacement;
        if (increasing)
            displacement = velocity;
        displacement = -velocity;

        Tile selected;
        selected = Factory.getMapRepository().findTileByCoordinates(x,y+velocity);
        error[0] = collisionError(selected);

        selected = Factory.getMapRepository().findTileByCoordinates(x-velocity,y);
        error[1] = collisionError(selected);

        selected = Factory.getMapRepository().findTileByCoordinates(x+velocity,y);
        error[2] = collisionError(selected);
    }

    private void horizontalCheck(int x, int y, boolean increasing) throws InvalidTileException, BoardNotBuildException {
        int displacement;
        if (increasing)
            displacement = velocity;
        displacement = -velocity;

        Tile selected;
        selected = Factory.getMapRepository().findTileByCoordinates(x,y-velocity);
        error[0] = collisionError(selected);

        selected = Factory.getMapRepository().findTileByCoordinates(x-velocity,y);
        error[1] = collisionError(selected);

        selected = Factory.getMapRepository().findTileByCoordinates(x+velocity,y);
        error[2] = collisionError(selected);
    }


}
