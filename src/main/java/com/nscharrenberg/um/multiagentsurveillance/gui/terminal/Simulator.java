package com.nscharrenberg.um.multiagentsurveillance.gui.terminal;

import com.nscharrenberg.um.multiagentsurveillance.agents.shared.Agent;
import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.BoardNotBuildException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.InvalidTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.ItemNotOnTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;

public class Simulator {

    public Simulator() throws InvalidTileException, BoardNotBuildException, ItemNotOnTileException {
        Factory.init();
        Factory.getGameRepository().startGame();
        gameLoop();
    }

    private void gameLoop() throws InvalidTileException, BoardNotBuildException, ItemNotOnTileException {
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
        }

        System.out.println("100% Achieved");
    }
}
