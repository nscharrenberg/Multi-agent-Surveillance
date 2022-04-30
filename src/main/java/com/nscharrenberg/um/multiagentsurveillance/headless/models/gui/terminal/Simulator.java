package com.nscharrenberg.um.multiagentsurveillance.headless.models.gui.terminal;

import com.nscharrenberg.um.multiagentsurveillance.agents.shared.Agent;
import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;

public class Simulator {

    public Simulator() {
        Factory.init();
        Factory.getGameRepository().startGame();
        gameLoop();
    }

    private void gameLoop() {
        Factory.getGameRepository().setRunning(true);
        while (Factory.getGameRepository().isRunning()) {
            int agentId = 0;
            for (Agent agent : Factory.getPlayerRepository().getAgents()) {
                int oldX = agent.getPlayer().getTile().getX();
                int oldY = agent.getPlayer().getTile().getY();
                Action move = agent.decide();
                // TODO: We need to somehow store our placed markers with their initial duration (which is going to be
                // TODO: a constant) and the coordinates of the placement and then each game loop run over the placed markers to check if the duration left
                // TODO: equals 0 (and decrement the durations). Then it is removed (by removing it from the list of items of the corresponding tile) and a method in the GUI is called which removes it from the board visually.
                agent.execute(move);,
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
