package com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle.Angle;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.GameMode;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Player;

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

    Angle getTargetGameAngle(Player player);

    double getTargetRealAngle(Player player);
}
