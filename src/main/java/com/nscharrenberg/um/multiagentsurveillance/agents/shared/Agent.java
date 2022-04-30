package com.nscharrenberg.um.multiagentsurveillance.agents.shared;

import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.pathfinding.IPathFinding;
import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IGameRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IMapRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IPlayerRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.*;

import java.util.*;

public abstract class Agent {
    protected final Player player;
    protected Area<Tile> knowledge;
    protected Area<Tile> vision;
    protected Queue<Action> plannedMoves;

    protected final IMapRepository mapRepository;
    protected final IGameRepository gameRepository;
    protected final IPlayerRepository playerRepository;

    protected int deadEndMarkers;
    protected int targetMarkers;
    protected int teleporterMarkers;
    protected int intruderSpottedMarkers;
    protected int guardSpottedMarkers;
    protected int shadedMarkers;

    public Agent(Player player) {
        this.player = player;
        if (player instanceof Guard) {
            deadEndMarkers = 5;
            targetMarkers = 5;
            teleporterMarkers = 5;
            intruderSpottedMarkers = 5;
            shadedMarkers = 5;
        }

        else if (player instanceof Intruder) {
            deadEndMarkers = 5;
            targetMarkers = 5;
            teleporterMarkers = 5;
            guardSpottedMarkers = 5;
            shadedMarkers = 5;
        }

        this.knowledge = new TileArea();
        this.vision = new TileArea();
        this.plannedMoves = new PriorityQueue<>();

        this.mapRepository = Factory.getMapRepository();
        this.playerRepository = Factory.getPlayerRepository();
        this.gameRepository = Factory.getGameRepository();
    }

    public Agent(Player player, IMapRepository mapRepository, IGameRepository gameRepository, IPlayerRepository playerRepository) {
        this.player = player;
        if (player instanceof Guard) {
            deadEndMarkers = 5;
            targetMarkers = 5;
            teleporterMarkers = 5;
            intruderSpottedMarkers = 5;
            shadedMarkers = 5;
        }

        else if (player instanceof Intruder) {
            deadEndMarkers = 5;
            targetMarkers = 5;
            teleporterMarkers = 5;
            guardSpottedMarkers = 5;
            shadedMarkers = 5;
        }

        this.knowledge = new TileArea();
        this.vision = new TileArea();
        this.plannedMoves = new PriorityQueue<>();

        this.mapRepository = mapRepository;
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
    }

    public Agent(Player player, Area<Tile> knowledge, Queue<Action> plannedMoves, IMapRepository mapRepository, IGameRepository gameRepository, IPlayerRepository playerRepository) {
        this.player = player;
        if (player instanceof Guard) {
            deadEndMarkers = 5;
            targetMarkers = 5;
            teleporterMarkers = 5;
            intruderSpottedMarkers = 5;
            shadedMarkers = 5;
        }

        else if (player instanceof Intruder) {
            deadEndMarkers = 5;
            targetMarkers = 5;
            teleporterMarkers = 5;
            guardSpottedMarkers = 5;
            shadedMarkers = 5;
        }

        this.knowledge = knowledge;
        this.vision = new TileArea();
        this.plannedMoves = plannedMoves;

        this.mapRepository = mapRepository;
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
    }

    public Area<Tile> getKnowledge() {
        return knowledge;
    }

    public void setKnowledge(Area<Tile> knowledge) {
        this.knowledge = knowledge;
    }

    public Queue<Action> getPlannedMoves() {
        return plannedMoves;
    }

    public void setPlannedMoves(Queue<Action> plannedMoves) {
        this.plannedMoves = plannedMoves;
    }

    public void addKnowledge(Tile tile) {
        knowledge.add(tile, false);
    }

    public void addMove(Action action) {
        plannedMoves.add(action);
    }

    public void addKnowledge(HashMap<Integer, HashMap<Integer, Tile>> tiles) {
        for (Map.Entry<Integer, HashMap<Integer, Tile>> rowEntry : tiles.entrySet()) {
            for (Map.Entry<Integer, Tile> colEntry : rowEntry.getValue().entrySet()) {
                knowledge.add(colEntry.getValue(), false);
            }
        }
    }

    public void addKnowledge(List<Tile> tiles) {
        for (Tile tile : tiles) {
            knowledge.add(tile, false);
        }
    }

    public void addKnowledge(Tile... tiles) {
        for (Tile tile : tiles) {
            knowledge.add(tile, false);
        }
    }

    public Action move() {
        return plannedMoves.poll();
    }

    public IMapRepository getMapRepository() {
        return mapRepository;
    }

    public IGameRepository getGameRepository() {
        return gameRepository;
    }

    public IPlayerRepository getPlayerRepository() {
        return playerRepository;
    }

    public Player getPlayer() {
        return player;
    }

    public IPathFinding getPathFindingAlgorithm(){
        return null;
    }

    public abstract void execute(Action action);

    public abstract Action decide();

    public void execute() {
        execute(decide());
    }

    public Action placeMarker(){
        //TODO: Here loop through the current vision and check when to place a marker
        Action move;
        HashMap<Integer, HashMap<Integer, Tile>> vision = player.getVision().getRegion();

        List<Item> currentTileItems= new ArrayList<Item>();

        for (Map.Entry<Integer, HashMap<Integer, Tile>> rowEntry : vision.entrySet()) {
            for (Map.Entry<Integer, Tile> colEntry : rowEntry.getValue().entrySet()) {
                for (Item item : colEntry.getValue().getItems()) {
                    if (player instanceof Guard) {
                        if (item instanceof Intruder) {

                        }
                    }
                    else if (player instanceof Intruder) {

                    }
                }
            }

        }


        //TODO: A different check for the dead-end (something with looping through knowledge).
        return null;
    }

    public int getDeadEndMarkers() {
        return deadEndMarkers;
    }

    public void decrementDeadEndMarkers() {
        deadEndMarkers--;
    }

    public int getTargetMarkers() {
        return targetMarkers;
    }

    public void decrementTargetMarkers() {
        targetMarkers--;
    }

    public int getTeleporterMarkers() {
        return teleporterMarkers;
    }

    public void decrementTeleporterMarkers() {
        teleporterMarkers--;
    }

    public int getIntruderSpottedMarkers() {
        return intruderSpottedMarkers;
    }

    public void decrementIntruderSpottedMarkers() {
        intruderSpottedMarkers--;
    }

    public int getGuardSpottedMarkers() {
        return guardSpottedMarkers;
    }

    public void decrementGuardSpottedMarkers() {
        guardSpottedMarkers--;
    }

    public int getShadedMarkers() {
        return shadedMarkers;
    }

    public void decrementShadedMarkers() {
        shadedMarkers--;
    }

}
