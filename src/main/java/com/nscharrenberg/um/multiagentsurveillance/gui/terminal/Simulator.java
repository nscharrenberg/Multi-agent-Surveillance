package com.nscharrenberg.um.multiagentsurveillance.gui.terminal;

import com.nscharrenberg.um.multiagentsurveillance.agents.shared.Agent;
import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Guard;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.files.MapImporter;

import java.io.File;
import java.io.IOException;

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
                Angle move = agent.decide();
                if (move != Angle.STOP) {
                    agent.execute(move);
                }
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
