package com.nscharrenberg.um.multiagentsurveillance.headless.repositories;

import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.angleCalculator.AngleTilesCalculator;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.angleCalculator.ComputeDoubleAngleTiles;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.DQN_Agent;
import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IGameRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IMapRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IPlayerRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.GameMode;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.GameState;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Guard;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Intruder;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Player;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.files.Importer;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.files.MapImporter;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.files.TiledMapImporter;

import java.io.File;
import java.io.IOException;

public class GameRepository implements IGameRepository {
    //private static String MAP_PATH = "src/test/resources/maps/maze3.json";
    private static String MAP_PATH = "src/test/resources/maps/deadEndMaze.json";
    //private static String MAP_PATH = "src/test/resources/RLtrainingMaps/trainingExampleMap.txt";
    //private static String MAP_PATH = "src/test/resources/RLtrainingMaps/ChasingTestMap.txt";
    //private static String MAP_PATH = "src/test/resources/maps/rust.txt";
    private IMapRepository mapRepository;
    private IPlayerRepository playerRepository;

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
    private GameState gameState = GameState.NO_RESULT;

    public GameRepository() {
        this.mapRepository = Factory.getMapRepository();
        this.playerRepository = Factory.getPlayerRepository();
    }

    public GameRepository(IMapRepository mapRepository, IPlayerRepository playerRepository) {
        this.mapRepository = mapRepository;
        this.playerRepository = playerRepository;
    }

    public void startGame(DQN_Agent[] guards, DQN_Agent[] intruders) {
        importMap();

        if (gameMode == null) {
            setGameMode(GameMode.EXPLORATION);
        }

        setupAgents(guards, intruders);

//        playerRepository.getStopWatch().start();
    }

    @Override
    public void startGame() {
        importMap();

        if (gameMode == null) {
            setGameMode(GameMode.EXPLORATION);
        }

        setupAgents();

        playerRepository.getStopWatch().start();
    }

    @Override
    public void stopGame() {
        try {
            playerRepository.getStopWatch().stop();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void importMap() {
        File file = new File(MAP_PATH);
        String path = file.getAbsolutePath();
        Importer importer = new TiledMapImporter(this, mapRepository, playerRepository);

        setRunning(true);

        try {
            importer.load(path);
            playerRepository.calculateInaccessibleTiles();
        } catch (IOException e) {
            e.printStackTrace();
//            Factory.getGameRepository().setRunning(false);
        }
    }

    private void setupAgents(DQN_Agent[] guards, DQN_Agent[] intruders) {
        for (int i = 0; i < guards.length; i++) {
            Factory.getPlayerRepository().spawn(Guard.class, guards[i]);
        }

        if (getGameMode().equals(GameMode.GUARD_INTRUDER_ALL) || getGameMode().equals(GameMode.GUARD_INTRUDER_ONE)) {
            for (int i = 0; i < intruders.length; i++) {
                playerRepository.spawn(Intruder.class, intruders[i]);
            }
        }
    }

    @Override
    public Action getTargetGameAngle(Player player){
        if(player instanceof Intruder) {
            return AngleTilesCalculator.computeAngle(mapRepository.getTargetCenter(), player.getTile());
        }

        return null;
    }

    @Override
    public double getTargetRealAngle(Player player){
        if(player instanceof Intruder) {
            return ComputeDoubleAngleTiles.computeAngle(mapRepository.getTargetCenter(), player.getTile());
        }

        return 0.0;
    }

    @Override
    public void setupAgents(Class<? extends Player> playerClass) {
        if (playerClass.equals(Guard.class)) {
            for (int i = 0; i < getGuardCount(); i++) {
                playerRepository.spawn(Guard.class);
            }

            return;
        }

        for (int i = 0; i < getIntruderCount(); i++) {
            playerRepository.spawn(Intruder.class);
        }
    }

    @Override
    public void setupAgents() {
        setupAgents(Guard.class);

        if (getGameMode().equals(GameMode.GUARD_INTRUDER_ALL) || getGameMode().equals(GameMode.GUARD_INTRUDER_ONE)) {
            setupAgents(Intruder.class);
        }
    }

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

    @Override
    public GameState getGameState() {
        return gameState;
    }

    @Override
    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    @Override
    public IMapRepository getMapRepository() {
        return mapRepository;
    }

    @Override
    public void setMapRepository(IMapRepository mapRepository) {
        this.mapRepository = mapRepository;
    }

    @Override
    public IPlayerRepository getPlayerRepository() {
        return playerRepository;
    }

    @Override
    public void setPlayerRepository(IPlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }
}
