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
    private final TileArea visited = new TileArea();

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

        int SBOversion = 1;
        Tile goal = this.player.getTile();
        try {
            //System.out.println("knowledgesize: " + this.knowledge.getRegion().entrySet().size());
            //System.out.println("vissize: " + this.player.getVision().getRegion().entrySet().size());
        } catch (Exception exc) {
            System.out.println("Shits empty bruh");
            System.out.println(exc.getMessage());
        }

        // V1:
        if(SBOversion == 1) {
            visited.add(this.player.getTile());
            gather();

            while(!scanned.isEmpty()) {
                Tile top = scanned.peek();
                if(this.visited.getByCoordinates(top.getX(), top.getY()).isPresent()) {
                    scanned.pop();
                } else {
                    goal = scanned.peek();
                    break;
                }
            }
        }

        // V2:
        if(SBOversion == 2) {
            gatherV2();

            while(!scanned.isEmpty()) {
                Tile top = scanned.peek();
                if(this.knowledge.getByCoordinates(top.getX(), top.getY()).isPresent()) {
                    scanned.pop();
                } else {
                    goal = scanned.peek();
                    break;
                }
            }

            // If goal tile is outside the knowledge, get the adjacent tile thats inside the knowledge
//            if(knowledge.getByCoordinates(goal.getX(), goal.getY()).isEmpty()) {
//                for (Tile at: getAdjacent(goal)) {
//                    if(knowledge.getByCoordinates(at.getX(),at.getY()).isPresent())
//                        goal = at;
//                }
//            }
        }

        // TODO: If stack is empty, search for teleporter
        //System.out.println("Current goal Tile: " + goal.getX() +"  "+ goal.getY());
        //System.out.println("Stack size: " + scanned.size());


//        for (Tile st:scanned){
//            System.out.println("Stack tile: " + st.getX() +"  "+ st.getY());
//        }

        // Turn goal tile into Queue angle
        BFS bfs = new BFS();
        plannedMoves = bfs.execute(mapRepository.getBoard(), this.player, goal).get().getMoves();

        return plannedMoves.poll();
    }

    private void gather() {
        Tile current = player.getTile();
        for (Tile t : getAdjacent(current)) {
            if(this.visited.getByCoordinates(t.getX(),t.getY()).isEmpty()) {
                if(unobstructedTile(this.mapRepository.getBoard(), t)) {
                    scanned.push(t);
                }
            }
        }
    }

    private void gatherV2() {
        if(this.player.getVision() != null) {
            for (Map.Entry<Integer, HashMap<Integer, Tile>> intHashMapEntry : this.player.getVision().getRegion().entrySet()) {
                Tile vt = intHashMapEntry.getValue().get(intHashMapEntry.getKey());
                if(vt != null) {
                    if(this.knowledge.getByCoordinates(vt.getX(), vt.getY()).isEmpty()) {
                        if(unobstructedTile(mapRepository.getBoard(), vt))
                            scanned.addAll(getAdjacent(vt));
                    }
                }
            }
        } else {
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
