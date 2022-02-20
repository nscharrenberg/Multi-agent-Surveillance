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
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Area;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Tile;

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
    private Area<Tile> observation;

    public SBOAgent(Player agent) {
        this.mapRepository = Factory.getMapRepository();
        this.playerRepository = Factory.getPlayerRepository();
        this.gameRepository = Factory.getGameRepository();
        this.agent = agent;

        // Not sure if we want to take this approach yet
        this.observation = agent.getObservation();

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
        int pick = 0;
        return Angle.values()[pick];
    }

    private void gather() {
        Tile current = agent.getTile();
        for (Tile t : getAdjacent(current)) {
            if(observation.getByCoordinates(t.getX(),t.getY()).isEmpty()) {

            }

        }


    }

    private List<Tile> getAdjacent(Tile pos) {
        List<Tile> adj = new ArrayList<Tile>();
        adj.add(new Tile(pos.getX(), pos.getY()-1)); // UP
        adj.add(new Tile(pos.getX()+1, pos.getY())); // RIGHT
        adj.add(new Tile(pos.getX(), pos.getY()+1)); // DOWN
        adj.add(new Tile(pos.getX()-1, pos.getY())); // LEFT
        return adj;
    }


}
