package com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi;

import com.nscharrenberg.um.multiagentsurveillance.agents.shared.Agent;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IGameRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IMapRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IPlayerRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.*;

import java.util.*;

public class YamauchiAgent extends Agent {
    public YamauchiAgent(Player player) {
        super(player);
    }

    public YamauchiAgent(Player player, IMapRepository mapRepository, IGameRepository gameRepository, IPlayerRepository playerRepository) {
        super(player, mapRepository, gameRepository, playerRepository);
    }

    public YamauchiAgent(Player player, Area<Tile> knowledge, Queue<Angle> plannedMoves, IMapRepository mapRepository, IGameRepository gameRepository, IPlayerRepository playerRepository) {
        super(player, knowledge, plannedMoves, mapRepository, gameRepository, playerRepository);
    }

    @Override
    public void execute(Angle angle) {

    }

    @Override
    public Angle decide() {
        return null;
    }

    private void detectFrontiers() {
        // Classify each cell by comparing its occupancy probability to the initial (prior) probability assigned to all cells
        // Any open cell adjacent to an unknown cell is labeled a frontier edge cell.

        List<TileArea> frontiers = new ArrayList<>();

        for (Map.Entry<Integer, HashMap<Integer, Tile>> rowEntry : knowledge.getRegion().entrySet()) {
            for (Map.Entry<Integer, Tile> colEntry : rowEntry.getValue().entrySet()) {
                Optional<Tile> upOpt = nextPosition(colEntry.getValue(), Angle.UP);
                Optional<Tile> rightOpt = nextPosition(colEntry.getValue(), Angle.RIGHT);
                Optional<Tile> leftOpt = nextPosition(colEntry.getValue(), Angle.LEFT);
                Optional<Tile> downOpt = nextPosition(colEntry.getValue(), Angle.DOWN);

                if (upOpt.isPresent() && rightOpt.isPresent() && leftOpt.isPresent() && downOpt.isPresent()) {
                    continue;
                }

                // At least 1 unknown adjacent cell

            }
        }

    }

    private Optional<Tile> nextPosition(Tile tile, Angle direction) {
        int nextX = tile.getX() + direction.getxIncrement();
        int nextY = tile.getY() + direction.getyIncrement();

        Optional<Tile> currentTileOpt = knowledge.getByCoordinates(tile.getX(), tile.getY());

        // Current tile doesn't exist in knowledge --> Shouldn't happen
        if (currentTileOpt.isEmpty()) {
            return Optional.empty();
        }

        Optional<Tile> nextTileOpt = knowledge.getByCoordinates(nextX, nextY);

        if (nextTileOpt.isEmpty()) {
            return Optional.empty();
        }

        return nextTileOpt;
    }
}
