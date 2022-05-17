package com.nscharrenberg.um.multiagentsurveillance.agents.SBO;

import com.nscharrenberg.um.multiagentsurveillance.agents.shared.Agent;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.pathfinding.BFS.BFS;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IGameRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IMapRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IPlayerRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.*;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.Collision.Wall;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.Item;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.TileArea;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.MarkerSmell;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Player;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.SoundWave;

import java.util.*;

public class SBOAgent extends Agent {
    private final Stack<Tile> scanned = new Stack<>();
    private final TileArea visited = new TileArea();
    private final RLmodel agentmodel = new RLmodel();
    Tile goal = this.player.getTile();

    public SBOAgent(Player agent) {
        super(agent);
    }

    public SBOAgent(Player agent, IMapRepository mapRepository, IGameRepository gameRepository, IPlayerRepository playerRepository) {
        super(agent, mapRepository, gameRepository, playerRepository);
    }

    @Override
    public void execute() throws InvalidTileException, BoardNotBuildException {
        execute(decide());
    }

    @Override
    public void execute(Action move) {
        try {
            playerRepository.move(player, move);
        } catch (CollisionException | InvalidTileException | ItemNotOnTileException | ItemAlreadyOnTileException | BoardNotBuildException e) {
            gameRepository.setRunning(false);
			e.printStackTrace();
            //System.out.println(e.getMessage());
        }
    }

    @Override
    public Action decide() throws InvalidTileException, BoardNotBuildException {

        Action markerChecked = player.getAgent().markerCheck();
        if (markerChecked != null) {
            return markerChecked;
        }

        for (Item it: player.getTile().getItems()) {

            if(it instanceof SoundWave) {
                if(agentmodel.AssessParameter(new Parameter((SoundWave) it), this.player))
                    plannedMoves = agentmodel.getRedirect();
            } else if(it instanceof MarkerSmell) {
                if(agentmodel.AssessParameter(new Parameter((MarkerSmell) it), this.player))
                    plannedMoves = agentmodel.getRedirect();
            }
        }

        // Continue queue
        // && knowledge.getByCoordinates(goal.getX(), goal.getY()).isEmpty()
        if (!plannedMoves.isEmpty()) {
            return plannedMoves.poll();
        }

        gatherV2();

        while (!scanned.isEmpty()) {
            Tile top = scanned.peek();
            if (this.knowledge.getByCoordinates(top.getX(), top.getY()).isPresent()) {
                scanned.pop();
            } else {
                goal = scanned.peek();
                break;
            }
        }

        // TODO: If stack is empty, search for teleporter
        System.out.println("Players Tile: " + player.getTile().getX() + "  " + player.getTile().getY());
        System.out.println("Current goal Tile: " + goal.getX() + "  " + goal.getY());
        System.out.println("Stack size: " + scanned.size());

        // Turn goal tile into Queue angle
        BFS bfs = new BFS();
        if (bfs.execute(mapRepository.getBoard(), this.player, goal).isPresent()) {
            plannedMoves = bfs.execute(mapRepository.getBoard(), this.player, goal).get().getMoves();
        } else if (knowledge.getByCoordinates(goal.getX(), goal.getY()).isEmpty()) {
            for (Tile agt : getAdjacent(goal)) {
                if (knowledge.getByCoordinates(agt.getX(), agt.getY()).isPresent()) {
                    if (bfs.execute(mapRepository.getBoard(), this.player, agt).isPresent()) {
                        plannedMoves = bfs.execute(mapRepository.getBoard(), this.player, agt).get().getMoves();
                        break;
                    }
                }
            }
        } else {
            System.out.println("invalid goal?");
        }

        return plannedMoves.poll();
    }


    private void gatherV2() {
            if (this.player.getVision() != null) {
                for (Map.Entry<Integer, HashMap<Integer, Tile>> rowEntry : this.player.getVision().getRegion().entrySet()) {
                    for (Map.Entry<Integer, Tile> colEntry : rowEntry.getValue().entrySet()) {
                        Tile vt = colEntry.getValue();
                        if (vt != null) {
                            if (unobstructedTile(mapRepository.getBoard(), vt)) {
                                for (Tile at : getAdjacent(vt)) {
                                    if (this.knowledge.getByCoordinates(at.getX(), at.getY()).isEmpty()) {
                                        scanned.add(at);
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                System.out.println("Vision not updated! ");
                scanned.addAll(getAdjacent(this.player.getTile()));
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

    private boolean unobstructedTile(TileArea board, Tile t) {
        if(board.getByCoordinates(t.getX(), t.getY()).isPresent()) {
            if (board.getByCoordinates(t.getX(), t.getY()).get().getItems().size() != 0) {
                for (Item im : board.getByCoordinates(t.getX(), t.getY()).get().getItems()) {
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
