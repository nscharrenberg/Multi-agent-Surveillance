package com.nscharrenberg.um.multiagentsurveillance.agents.SBO;

import com.nscharrenberg.um.multiagentsurveillance.agents.IAgent;
import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IGameRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IMapRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IPlayerRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.CollisionException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.InvalidTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.ItemAlreadyOnTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.ItemNotOnTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;


public class SBOAgent implements IAgent {
    private final IMapRepository mapRepository;
    private final IGameRepository gameRepository;
    private final IPlayerRepository playerRepository;

    private final Player agent;

    private Stack<Tile> scanned;
    private Area<Tile> visited;

    public SBOAgent(Player agent) {
        this.mapRepository = Factory.getMapRepository();
        this.playerRepository = Factory.getPlayerRepository();
        this.gameRepository = Factory.getGameRepository();
        this.agent = agent;

        visited = new TileArea();

    }

    public SBOAgent(Player agent, IMapRepository mapRepository, IGameRepository gameRepository, IPlayerRepository playerRepository) {
        this.mapRepository = mapRepository;
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.agent = agent;
    }

    @Override
    public void execute() {
        execute(decide());
    }

    @Override
    public void execute(Angle move) {
        try {
            playerRepository.move(agent, move);
        } catch (CollisionException | InvalidTileException | ItemNotOnTileException | ItemAlreadyOnTileException e) {
            gameRepository.setRunning(false);
            System.out.println(e.getMessage());
        }
    }

    @Override
    public Angle decide() {
        // Update Stack
        gather();

        // Select first valid top Tile from Stack
        Tile goal = this.agent.getTile();
        while(!scanned.isEmpty()) {
            Tile top = scanned.peek();
            if(visited.getByCoordinates(top.getX(), top.getY()).isPresent()) {
                scanned.pop();
            } else {
                goal = scanned.peek();
            }
        }

        // TODO: Calculate angle for specified Tile
        // This is actually a lot harder than it looks,
        // because we cant run path finding algorithms on unspecified map areas
        // and the closest discovered tile might not result in a solution....

        int pick = 0;
        return Angle.values()[pick];
    }

    private void gather() {
        Tile current = agent.getTile();
        for (Tile t : getAdjacent(current)) {
            if(visited.getByCoordinates(t.getX(),t.getY()).isEmpty()) {
                if(unobstructedTile(this.mapRepository.getBoard(), t)) {
                    scanned.push(t);
                    // TODO: Some way of adding tiles to the visited tile area
                    // visited.add(t)
                }
            }
        }

        // More advanced gathering based on vision
        // TODO: implement

    }

    private List<Tile> getAdjacent(Tile pos) {
        List<Tile> adj = new ArrayList<Tile>();
        adj.add(new Tile(pos.getX(), pos.getY()-1)); // UP
        adj.add(new Tile(pos.getX()+1, pos.getY())); // RIGHT
        adj.add(new Tile(pos.getX(), pos.getY()+1)); // DOWN
        adj.add(new Tile(pos.getX()-1, pos.getY())); // LEFT
        return adj;
    }

    private boolean unobstructedTile(TileArea board, Tile t) {
        if(board.getByCoordinates(t.getX(), t.getY()).isPresent()) {
            if (board.getByCoordinates(t.getX(), t.getY()).get().getItems().size() != 0) {
                for (Item im : t.getItems()) {
                    if (im instanceof Wall) {   // Might have to add other checks to this later
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

}
