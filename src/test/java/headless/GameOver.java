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
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Player;


import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class GameOver {

    private static String MAP_PATH = "src/test/resources/maps/testmap.txt";
    private IMapRepository mapRepository;
    private IPlayerRepository playerRepository;

    private ManhattanDistance manhattanDistance;

    private GameMode gameMode;

    enum gameOver{
        WIN,
        LOSE
    }

    public GameOver(){
        this.mapRepository = Factory.getMapRepository();
        this.playerRepository = Factory.getPlayerRepository();
    }

    // Check whether the gameMode is Guard vs Intruder
    private boolean checkGameMode(){
        return gameMode.getName().equals("Guard vs Intruder") && gameMode.getId() == 1;
    }

    // Check if all the intruders are gone
    private gameOver findIntruder(TileArea board, Tile intruderPos)
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
    private gameOver checkTargetArea(TileArea targetArea, Intruder intruder, Tile target)
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
        }
    }

}