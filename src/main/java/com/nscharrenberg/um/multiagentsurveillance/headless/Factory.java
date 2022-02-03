package com.nscharrenberg.um.multiagentsurveillance.headless;

import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IGameRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IMapRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IPlayerRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.repositories.GameRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.repositories.MapRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.repositories.PlayerRepository;

public class Factory {
    private static IGameRepository gameRepository;
    private static IMapRepository mapRepository;
    private static IPlayerRepository playerRepository;

    public Factory() {
        reset();
    }

    public static IGameRepository getGameRepository() {
        return gameRepository;
    }

    public static IMapRepository getMapRepository() {
        return mapRepository;
    }

    public static IPlayerRepository getPlayerRepository() {
        return playerRepository;
    }

    public static void reset() {
        gameRepository = new GameRepository();
        mapRepository = new MapRepository();
        playerRepository = new PlayerRepository();
    }
}
