package com.nscharrenberg.um.multiagentsurveillance.headless.utils;

import com.nscharrenberg.um.multiagentsurveillance.agents.shared.Agent;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.BoardNotBuildException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.InvalidTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.ItemNotOnTileException;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.distanceCalculator.CalculateDistance;
import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IMapRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IPlayerRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle.Angle;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.Item;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.distanceCalculator.ManhattanDistance;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.TileArea;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Guard;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Intruder;

import java.util.*;


public class GameOver {

    private IMapRepository mapRepository;
    private IPlayerRepository playerRepository;

    private ManhattanDistance manhattanDistance;
    private CalculateDistance calculateDistance;

    private static boolean secondGameMode;

    private static int caught = 0;
    private static int escaped = 0;

    public enum gameState
    {
        INTRUDER_WIN,
        GUARD_WIN,
        GAME_IN_PROCESS
    }

    public GameOver()
    {
        this.mapRepository = Factory.getMapRepository();
        this.playerRepository = Factory.getPlayerRepository();
    }

    // Check whether the gameMode is Guard vs Intruder
    public static boolean checkGameMode()
    {
        if (Factory.getGameRepository().getGameMode().getName().equals("Guard vs Intruder") || Factory.getGameRepository().getGameMode().getId() == 1)
        {
            secondGameMode = true;
        }
        return secondGameMode;
    }

    // Check if all the intruders are gone
    public static gameState findIntruder() throws NoSuchElementException
    {
        TileArea board = Factory.getMapRepository().getBoard();

        Intruder intruder = Factory.getPlayerRepository().getIntruders().get(0);

        if(secondGameMode)
        {
            if(board.getByCoordinates(intruder.getTile().getX() ,intruder.getTile().getY()).isPresent())
            {
                if (board.getByCoordinates(intruder.getTile().getX(), intruder.getTile().getY()).get().getItems().size() != 0)
                {
                   return gameState.GAME_IN_PROCESS;
                }
            }
            for (Map.Entry<Integer, HashMap<Integer, Tile>> rowEntry : board.getRegion().entrySet())
            {
                for (Map.Entry<Integer, Tile> colEntry : rowEntry.getValue().entrySet())
                {
                    for (Item im : board.getByCoordinates(rowEntry.getKey(), colEntry.getKey()).get().getItems())
                    {
                        if (im instanceof Intruder)
                        {
                            return gameState.GAME_IN_PROCESS;
                        }
                    }
                }
            }
        }
        return gameState.GUARD_WIN;
    }

    // Check if the intruders are in the target area
    public static gameState checkTargetArea()
    {
        Intruder intruder = Factory.getPlayerRepository().getIntruders().get(0);

        TileArea targetArea = Factory.getMapRepository().getTargetArea();

        Tile target = null;

        if (targetArea != null)
        {
            for (Map.Entry<Integer, HashMap<Integer, Tile>> rowEntry : targetArea.getRegion().entrySet())
            {
                for (Map.Entry<Integer, Tile> colEntry : rowEntry.getValue().entrySet())
                {
                    target = colEntry.getValue();
                }
            }
        }

        if (secondGameMode)
        {
            while (Factory.getGameRepository().isRunning())
            {
                int startMarker = 0;

                while (intruder.getTile().equals(target) && !targetArea.isEmpty())
                {
                    for (Agent agent : Factory.getPlayerRepository().getAgents())
                    {
                        Angle move = agent.decide();

                        agent.execute(move);

                        startMarker++;
                    }
                }
                if (startMarker>=3)
                {
                    return gameState.INTRUDER_WIN;
                }
            }
        }
        return gameState.GAME_IN_PROCESS;
    }

    // if the intruder is no more than 0.5 meter away and in sight.
    public gameState capture() throws BoardNotBuildException, InvalidTileException, ItemNotOnTileException
    {
        Intruder intruder = Factory.getPlayerRepository().getIntruders().get(0);
        Guard guard = Factory.getPlayerRepository().getGuards().get(0);

        int distance = (int) calculateDistance.compute(intruder.getTile(), guard.getTile());

        if(distance <= 0.5)
        {
            mapRepository.findTileByCoordinates(intruder.getTile().getX(), intruder.getTile().getY()).remove(intruder);
            caught+=1;
            return gameState.GUARD_WIN;
        }
        else
            {
                escaped+=1;
            }
        return gameState.GAME_IN_PROCESS;
    }

    public static boolean getGameMode()
    {
        return secondGameMode;
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
