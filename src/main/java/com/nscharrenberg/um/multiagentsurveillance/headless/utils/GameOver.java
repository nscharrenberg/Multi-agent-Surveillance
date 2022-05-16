package com.nscharrenberg.um.multiagentsurveillance.headless.utils;

import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.BoardNotBuildException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.InvalidTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.ItemNotOnTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.GameMode;
import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IMapRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IPlayerRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.Item;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.distanceCalculator.ManhattanDistance;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Area;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.TileArea;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Guard;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Intruder;


public class GameOver {

    private IMapRepository mapRepository;
    private IPlayerRepository playerRepository;

    private ManhattanDistance manhattanDistance;

    private static GameMode gameMode;

    private static int caught = 0;
    private static int escaped = 0;

    public enum gameOver
    {
        WIN,
        LOSE
    }

    public GameOver()
    {
        this.mapRepository = Factory.getMapRepository();
        this.playerRepository = Factory.getPlayerRepository();
    }

    // Check whether the gameMode is Guard vs Intruder
    public static boolean checkGameMode(){
        return gameMode.getName().equals("Guard vs Intruder") || gameMode.getId() == 1;
    }

    // Check if all the intruders are gone
    public static gameOver findIntruder(TileArea board, Tile intruderPos)
    {
        if(board.getByCoordinates(intruderPos.getX(), intruderPos.getY()).isPresent())
        {
            if (board.getByCoordinates(intruderPos.getX(), intruderPos.getY()).get().getItems().size() != 0)
            {
                for (Item im : board.getByCoordinates(intruderPos.getX(), intruderPos.getY()).get().getItems())
                {
                    if (im instanceof Intruder)
                    {
                        return gameOver.LOSE;
                    }
                }
            }
        }
        return gameOver.WIN;
    }

    // Check if the intruders are in the target area
    public static gameOver checkTargetArea(TileArea targetArea, Intruder intruder, Tile target)
    {
        if (intruder.getTile().equals(target) && !targetArea.isEmpty())
        {
            return gameOver.LOSE;
        }
        return gameOver.WIN;
    }

    // if the intruder is no more than 0.5 meter away and in sight.
    public void capture(Intruder intruder, Guard guard) throws BoardNotBuildException, InvalidTileException, ItemNotOnTileException
    {
        double dist = manhattanDistance.compute(intruder.getTile(), guard.getTile());
        if(dist <= 0.5)
        {
            mapRepository.findTileByCoordinates(intruder.getTile().getX(), intruder.getTile().getY()).remove(intruder);
            caught+=1;
        }
        else
            {
                escaped+=1;
            }
    }


    public static int getIntruderNumber()
    {
        return 1;
    }

    public static int getCaughtNumber()
    {
        return caught;
    }

    public static int getEscapeNumber()
    {
        return escaped;
    }

}
