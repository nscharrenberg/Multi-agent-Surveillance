package com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories;

import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.DQN_Agent;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.GameMode;

public interface IGameRepository {
    void startGame();

    void stopGame();

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
}
