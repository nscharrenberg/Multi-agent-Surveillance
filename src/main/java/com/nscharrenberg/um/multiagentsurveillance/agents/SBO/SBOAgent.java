package com.nscharrenberg.um.multiagentsurveillance.agents.SBO;

import com.nscharrenberg.um.multiagentsurveillance.agents.shared.Agent;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.pathfinding.AStar.AStar;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.pathfinding.BFS.BFS;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.pathfinding.IPathFinding;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IGameRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IMapRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IPlayerRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.*;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.Collision.Collision;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.Collision.Wall;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.Item;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.Marker;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.MarkerSmell;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.SoundWave;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.TileArea;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Player;

import java.util.*;

public class SBOAgent extends Agent {
    private final Stack<Tile> scanned = new Stack<>();
    private final TileArea visited = new TileArea();
    private final RLmodel agentmodel = new RLmodel();
    private final IPathFinding PFA = new AStar();
    Tile goal = this.player.getTile();
    private Random random = new Random();

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

//        Action markerChecked = player.getAgent().markerCheck();
//        if (markerChecked != null) {
//            return markerChecked;
//        }

        for (Item it: player.getTile().getItems()) {
            if(it instanceof SoundWave) {
                if(agentmodel.parameterEvaluation(new Parameter((SoundWave) it), this.player))
                    plannedMoves = agentmodel.getRedirect();
            } else if(it instanceof MarkerSmell) {
                if(agentmodel.parameterEvaluation(new Parameter((MarkerSmell) it), this.player))
                    plannedMoves = agentmodel.getRedirect();
            }
        }

        // Continue queue
        if (!plannedMoves.isEmpty() && knowledge.getByCoordinates(goal.getX(), goal.getY()).isEmpty()) {
            return plannedMoves.poll();
        }

        // Add vision tiles to the stack
        gatherV2();

        // Iterate the stack for the next tile
        while (!scanned.isEmpty()) {
            Tile top = scanned.peek();
            if (this.knowledge.getByCoordinates(top.getX(), top.getY()).isPresent()) {
                scanned.pop();
            } else {
                goal = scanned.peek();
                break;
            }
        }

        if (PFA.execute(mapRepository.getBoard(), this.player, goal).isPresent()) {
            plannedMoves = PFA.execute(mapRepository.getBoard(), this.player, goal).get().getMoves();
        } else if (knowledge.getByCoordinates(goal.getX(), goal.getY()).isEmpty()) {
            for (Tile agt : getAdjacent(goal)) {
                if (knowledge.getByCoordinates(agt.getX(), agt.getY()).isPresent()) {
                    if (PFA.execute(mapRepository.getBoard(), this.player, agt).isPresent()) {
                        plannedMoves = PFA.execute(mapRepository.getBoard(), this.player, agt).get().getMoves();
                        break;
                    }
                }
            }
        } else {
            System.out.println("invalid goal?");
        }

        System.out.println("Players Tile: " + player.getTile().getX() + "  " + player.getTile().getY());
        System.out.println("Current goal Tile: " + goal.getX() + "  " + goal.getY());
        System.out.println("Stack size: " + scanned.size());

        // Stop agent for testing
        if(scanned.isEmpty() && plannedMoves.isEmpty())
            gameRepository.setRunning(false);

        // Switches to random agent if no more tiles in the stack
        if(scanned.isEmpty() || plannedMoves.isEmpty()) {
            Optional<Tile> nextTileOpt = knowledge.getByCoordinates(player.getTile().getX() + player.getDirection().getxIncrement(),
                    player.getTile().getY() + player.getDirection().getyIncrement());

            boolean nextBlocked = false;
            if (nextTileOpt.isPresent()) {
                for (Item items : nextTileOpt.get().getItems()) {
                    if (items instanceof Collision) {
                        nextBlocked = true;
                        break;
                    }
                }
            }
            int pick = this.random.nextInt(4);
            while (nextBlocked && Action.values()[pick] == player.getDirection()) {
                pick = this.random.nextInt(4);
            }
            return Action.values()[pick];
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
                                if(mapRepository.getBoard().getByCoordinates(at.getX(), at.getY()).isPresent()) {
                                    if (this.knowledge.getByCoordinates(at.getX(), at.getY()).isEmpty()) {
                                        if(unobstructedTile(mapRepository.getBoard(), at))
                                            scanned.add(at);
                                    }
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
                    if (im instanceof Wall) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

}
