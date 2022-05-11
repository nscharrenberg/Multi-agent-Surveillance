package com.nscharrenberg.um.multiagentsurveillance.gui.terminal;

import com.nscharrenberg.um.multiagentsurveillance.agents.shared.Agent;
import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.BoardNotBuildException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.InvalidTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.ItemNotOnTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;

public class Simulator {

    public Simulator() throws Exception {
        Factory.init();
        Factory.getGameRepository().startGame();
        gameLoop();
    }

    private void gameLoop() throws Exception {
        Factory.getGameRepository().setRunning(true);
        while (Factory.getGameRepository().isRunning()) {
            int agentId = 0;
            for (Agent agent : Factory.getPlayerRepository().getAgents()) {
                int oldX = agent.getPlayer().getTile().getX();
                int oldY = agent.getPlayer().getTile().getY();
                Action move = agent.decide();
                agent.execute(move);
                System.out.println("Agent " + agentId
                        + " going from (" + oldX + ", " + oldY + ") to move "
                        + move + " to (" + agent.getPlayer().getTile().getX() + ", "
                        + agent.getPlayer().getTile().getY() + ")");
                agentId++;
            }

            Factory.getMapRepository().checkMarkers();
        }

        System.out.println("100% Achieved");
    }

    /** What to change:
     *  1. Ask Noah and Tjardo about different markers on the same tile. Since the guards cannot understand the
     *      markers of the intruders and vice versa, so it shouldn't be a problem for putting them on the same tile
     *      However, we will need to check if the same player type can place 2 markers on the same tile (do we allow
     *      this or not?). The only problem would be the visibility of markers in the GUI.
     */
}
