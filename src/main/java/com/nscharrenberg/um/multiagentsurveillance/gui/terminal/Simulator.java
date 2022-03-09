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
        importMap();
        spawn();
        gameLoop();
    }

    private void importMap() {
        File file = new File("src/test/resources/maps/testmap2.txt");
        String path = file.getAbsolutePath();
        MapImporter importer = new MapImporter();

        Factory.getGameRepository().setRunning(true);

        try {
            importer.load(path);
        } catch (IOException e) {
//            Factory.getGameRepository().setRunning(false);
        }
    }

    private void spawn() {
        for (int i = 0; i <= Factory.getGameRepository().getGuardCount(); i++) {
            Factory.getPlayerRepository().spawn(Guard.class);
        }
    }

    private void gameLoop() {
        Factory.getGameRepository().setRunning(true);
        while (Factory.getGameRepository().isRunning()) {
            int agentId = 0;
            for (Agent agent : Factory.getPlayerRepository().getAgents()) {
                StringBuilder sb = new StringBuilder();
                sb.append("Agent ");
                sb.append(agentId);
                sb.append(" going from (");
                sb.append(agent.getPlayer().getTile().getX());
                sb.append(", ");
                sb.append(agent.getPlayer().getTile().getY());
                sb.append(") to move ");

                Angle move = agent.decide();
                sb.append(move);
                sb.append(" to ");
                agent.execute(move);

                sb.append(agent.getPlayer().getTile().getX());
                sb.append(", ");
                sb.append(agent.getPlayer().getTile().getY());
                sb.append(")");

                System.out.println(sb.toString());
                agentId++;
            }

            if (Factory.getPlayerRepository().getExplorationPercentage() >= 100) {
                System.out.println("Out of Iterations - Game Over");
                break;
            }
        }
    }

}
