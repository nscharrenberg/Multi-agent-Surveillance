package com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.DQN_Agent;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.GameMode;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.GameState;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Player;

public interface IGameRepository {
    void startGame();

    void stopGame();

    void setupAgents(Class<? extends Player> playerClass);

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

    void startGame(DQN_Agent[] guards, DQN_Agent[] intruders);

    void importMap();

    Action getTargetGameAngle(Player player);

    double getTargetRealAngle(Player player);

    GameState getGameState();

    void setGameState(GameState gameState);

    IMapRepository getMapRepository();

    void setMapRepository(IMapRepository mapRepository);

    IPlayerRepository getPlayerRepository();

    void setPlayerRepository(IPlayerRepository playerRepository);

    double getDistanceSoundSprinting();

    void setDistanceSoundSprinting(double distanceSoundSprinting);

    double getDistanceSoundWalking();

    void setDistanceSoundWalking(double distanceSoundWalking);

    double getDistanceSoundRotating();

    void setDistanceSoundRotating(double distanceSoundRotating);

    double getDistanceSoundWaiting();

    void setDistanceSoundWaiting(double distanceSoundWaiting);

    double getDistanceSoundYelling();

    void setDistanceSoundYelling(double distanceSoundYelling);

    double getDistanceViewing();

    void setDistanceViewing(double distanceViewing);
}
