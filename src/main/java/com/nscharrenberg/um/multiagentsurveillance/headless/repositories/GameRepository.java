package com.nscharrenberg.um.multiagentsurveillance.headless.repositories;

import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IGameRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.GameMode;

public class GameRepository implements IGameRepository {
    private String name;
    private GameMode gameMode;
    private int guardCount;
    private int intruderCount;
    private int width;
    private int height;
    private double scaling;
    private double baseSpeedIntruders;
    private double springSpeedIntruders;
    private double baseSpeedGuards;
    private double timeStep;
    private boolean isRunning = false;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public GameMode getGameMode() {
        return gameMode;
    }

    @Override
    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    @Override
    public int getGuardCount() {
        return guardCount;
    }

    @Override
    public void setGuardCount(int guardCount) {
        this.guardCount = guardCount;
    }

    @Override
    public int getIntruderCount() {
        return intruderCount;
    }

    @Override
    public void setIntruderCount(int intruderCount) {
        this.intruderCount = intruderCount;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public double getScaling() {
        return scaling;
    }

    @Override
    public void setScaling(double scaling) {
        this.scaling = scaling;
    }

    @Override
    public double getBaseSpeedIntruders() {
        return baseSpeedIntruders;
    }

    @Override
    public void setBaseSpeedIntruders(double baseSpeedIntruders) {
        this.baseSpeedIntruders = baseSpeedIntruders;
    }

    @Override
    public double getSpringSpeedIntruders() {
        return springSpeedIntruders;
    }

    @Override
    public void setSpringSpeedIntruders(double springSpeedIntruders) {
        this.springSpeedIntruders = springSpeedIntruders;
    }

    @Override
    public double getBaseSpeedGuards() {
        return baseSpeedGuards;
    }

    @Override
    public void setBaseSpeedGuards(double baseSpeedGuards) {
        this.baseSpeedGuards = baseSpeedGuards;
    }

    @Override
    public double getTimeStep() {
        return timeStep;
    }

    @Override
    public void setTimeStep(double timeStep) {
        this.timeStep = timeStep;
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public void setRunning(boolean running) {
        isRunning = running;
    }
}
