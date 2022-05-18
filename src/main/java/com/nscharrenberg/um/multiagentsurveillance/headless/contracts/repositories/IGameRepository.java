package com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.GameMode;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.GameState;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Player;

public interface IGameRepository {
    void startGame();

    void stopGame();

    void setupAgents();

    String getName();

    void setName(String name);

    GameMode getGameMode();

    void setGameMode(GameMode gameMode);

    int getGuardCount();

    void setGuardCount(int guardCount);

    int getIntruderCount();

    void setIntruderCount(int intruderCount);

    int getWidth();

    void setWidth(int width);

    int getHeight();

    void setHeight(int height);

    double getScaling();

    void setScaling(double scaling);

    double getBaseSpeedIntruders();

    void setBaseSpeedIntruders(double baseSpeedIntruders);

    double getSpringSpeedIntruders();

    void setSpringSpeedIntruders(double springSpeedIntruders);

    double getBaseSpeedGuards();

    void setBaseSpeedGuards(double baseSpeedGuards);

    double getTimeStep();

    void setTimeStep(double timeStep);

    boolean isRunning();

    void setRunning(boolean running);

    Action getTargetGameAngle(Player player);

    double getTargetRealAngle(Player player);

    GameState getGameState();

    void setGameState(GameState gameState);

    IMapRepository getMapRepository();

    void setMapRepository(IMapRepository mapRepository);

    IPlayerRepository getPlayerRepository();

    void setPlayerRepository(IPlayerRepository playerRepository);
}
