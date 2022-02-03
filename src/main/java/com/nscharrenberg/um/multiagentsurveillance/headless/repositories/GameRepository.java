package com.nscharrenberg.um.multiagentsurveillance.headless.repositories;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Area;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.GameMode;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Tile;

public class GameRepository {
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
    private Area<Tile> intruderSpawnArea;
    private Area<Tile> guardSpawnArea;

    // Temp variable, will probably not be needed in the near future.
    private Area<Tile> targetArea;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public int getGuardCount() {
        return guardCount;
    }

    public void setGuardCount(int guardCount) {
        this.guardCount = guardCount;
    }

    public int getIntruderCount() {
        return intruderCount;
    }

    public void setIntruderCount(int intruderCount) {
        this.intruderCount = intruderCount;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public double getScaling() {
        return scaling;
    }

    public void setScaling(double scaling) {
        this.scaling = scaling;
    }

    public double getBaseSpeedIntruders() {
        return baseSpeedIntruders;
    }

    public void setBaseSpeedIntruders(double baseSpeedIntruders) {
        this.baseSpeedIntruders = baseSpeedIntruders;
    }

    public double getSpringSpeedIntruders() {
        return springSpeedIntruders;
    }

    public void setSpringSpeedIntruders(double springSpeedIntruders) {
        this.springSpeedIntruders = springSpeedIntruders;
    }

    public double getBaseSpeedGuards() {
        return baseSpeedGuards;
    }

    public void setBaseSpeedGuards(double baseSpeedGuards) {
        this.baseSpeedGuards = baseSpeedGuards;
    }

    public double getTimeStep() {
        return timeStep;
    }

    public void setTimeStep(double timeStep) {
        this.timeStep = timeStep;
    }

    public Area<Tile> getIntruderSpawnArea() {
        return intruderSpawnArea;
    }

    public void setIntruderSpawnArea(Area<Tile> intruderSpawnArea) {
        this.intruderSpawnArea = intruderSpawnArea;
    }

    public Area<Tile> getGuardSpawnArea() {
        return guardSpawnArea;
    }

    public void setGuardSpawnArea(Area<Tile> guardSpawnArea) {
        this.guardSpawnArea = guardSpawnArea;
    }

    public Area<Tile> getTargetArea() {
        return targetArea;
    }

    public void setTargetArea(Area<Tile> targetArea) {
        this.targetArea = targetArea;
    }
}
