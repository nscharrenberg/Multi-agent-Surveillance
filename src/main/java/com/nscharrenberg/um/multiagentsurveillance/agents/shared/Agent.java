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
    protected Queue<Angle> plannedMoves;

    protected final IMapRepository mapRepository;
    protected final IGameRepository gameRepository;
    protected final IPlayerRepository playerRepository;

    public Agent(Player player) {
        this.player = player;
        this.knowledge = new TileArea();
        this.plannedMoves = new PriorityQueue<>();

        this.mapRepository = Factory.getMapRepository();
        this.playerRepository = Factory.getPlayerRepository();
        this.gameRepository = Factory.getGameRepository();
    }

    public Agent(Player player, IMapRepository mapRepository, IGameRepository gameRepository, IPlayerRepository playerRepository) {
        this.player = player;
        this.knowledge = new TileArea();
        this.plannedMoves = new PriorityQueue<>();

        this.mapRepository = mapRepository;
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
    }

    public Agent(Player player, Area<Tile> knowledge, Queue<Angle> plannedMoves, IMapRepository mapRepository, IGameRepository gameRepository, IPlayerRepository playerRepository) {
        this.player = player;
        this.knowledge = knowledge;
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

    public Queue<Angle> getPlannedMoves() {
        return plannedMoves;
    }

    public void setPlannedMoves(Queue<Angle> plannedMoves) {
        this.plannedMoves = plannedMoves;
    }

    public void addKnowledge(Tile tile) {
        knowledge.add(tile, false);
    }

    public void addMove(Angle angle) {
        plannedMoves.add(angle);
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

    public Angle move() {
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

    public abstract void execute(Angle angle);
    public abstract Angle decide();

    public void execute() {
        execute(decide());
    }
}
