package com.nscharrenberg.um.multiagentsurveillance.agents.SBO;

import com.nscharrenberg.um.multiagentsurveillance.agents.shared.Agent;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.pathfinding.BFS.BFS;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IGameRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IMapRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IPlayerRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.CollisionException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.InvalidTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.ItemAlreadyOnTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.ItemNotOnTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.*;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.CharacterVision;

import java.util.*;

public class SBOAgent extends Agent {
    private final Stack<Tile> scanned = new Stack<>();
    private Tile tparea = new Tile();
    private Tile currentgoal = new Tile();

    public SBOAgent(Player agent) {
        super(agent);
    }

    public SBOAgent(Player agent, IMapRepository mapRepository, IGameRepository gameRepository, IPlayerRepository playerRepository) {
        super(agent, mapRepository, gameRepository, playerRepository);
    }

    @Override
    public void execute() {
        execute(decide());
    }

    @Override
    public void execute(Angle move) {
        try {
            playerRepository.move(player, move);
        } catch (CollisionException | InvalidTileException | ItemNotOnTileException | ItemAlreadyOnTileException e) {
            gameRepository.setRunning(false);
            System.out.println(e.getMessage());
        }
    }

    @Override
    public Angle decide() {

        if (!plannedMoves.isEmpty() && knowledge.getByCoordinates(currentgoal.getX(), currentgoal.getY()).isEmpty()) {
            return plannedMoves.poll();
        }

        currentgoal = this.player.getTile();
        gatherV2();

        while(!scanned.isEmpty()) {
            Tile top = scanned.peek();
            if(this.knowledge.getByCoordinates(top.getX(), top.getY()).isPresent()) {
                scanned.pop();
            } else {
                currentgoal = scanned.peek();
                break;
            }
        }

        // Go to teleporter if no tile is available anymore
        if(scanned.isEmpty()) {
            currentgoal = tparea;
        }

        System.out.println("Players Tile: " + player.getTile().getX() +"  "+ player.getTile().getY());
        System.out.println("Current goal Tile: " + currentgoal.getX() +"  "+ currentgoal.getY());
        System.out.println("Stack size: " + scanned.size());

        // Turn goal tile into Queue angle
        BFS bfs = new BFS();
        if(bfs.execute(mapRepository.getBoard(), this.player, currentgoal).isPresent()) {
            plannedMoves = bfs.execute(mapRepository.getBoard(), this.player, currentgoal).get().getMoves();
        } else if(knowledge.getByCoordinates(currentgoal.getX(), currentgoal.getY()).isEmpty()) {
            for (Tile agt: getAdjacent(currentgoal)) {
                if(knowledge.getByCoordinates(agt.getX(),agt.getY()).isPresent()) {
                    if(bfs.execute(mapRepository.getBoard(), this.player, agt).isPresent()) {
                        plannedMoves = bfs.execute(mapRepository.getBoard(), this.player, agt).get().getMoves();
                        break;
                    }
                }
            }
        } else {
            System.out.println("Invalid goal tile!");
        }

        return plannedMoves.poll();
    }


    // TODO: Optimize the order in which tiles are added
    private void gatherV2() {
        if(this.player.getVision() != null) {
            for (Map.Entry<Integer, HashMap<Integer, Tile>> rowEntry : this.player.getVision().getRegion().entrySet()) {
                for (Map.Entry<Integer, Tile> colEntry : rowEntry.getValue().entrySet()) {
                    // Add tp knowledge
                    for (Item im:colEntry.getValue().getItems()) {
                        if(im instanceof Teleporter) {
                            tparea = colEntry.getValue();
                            break;
                        }
                    }
                    // Add vision adjacency to stack
                    Tile vt = colEntry.getValue();
                    if(vt != null) {
                        if(unobstructedTile(mapRepository.getBoard(), vt)) {
                            for (Tile at:getAdjacent(vt)) {
                                if(this.knowledge.getByCoordinates(at.getX(), at.getY()).isEmpty()) {
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
