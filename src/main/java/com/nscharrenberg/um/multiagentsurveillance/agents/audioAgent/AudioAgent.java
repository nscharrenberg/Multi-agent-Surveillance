package com.nscharrenberg.um.multiagentsurveillance.agents.audioAgent;

import com.nscharrenberg.um.multiagentsurveillance.agents.shared.Agent;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.CollisionException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.InvalidTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.ItemAlreadyOnTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.ItemNotOnTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle.Angle;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Player;


public class AudioAgent extends Agent {

    public AudioAgent(Player player) {
        super(player);

    }

    @Override
    public void execute(Angle angle) {
        try {
            playerRepository.move(getPlayer(), angle);
        } catch (CollisionException | InvalidTileException | ItemNotOnTileException | ItemAlreadyOnTileException e) {

            throw new RuntimeException("Error");
        }
    }

    @Override
    public Angle decide() {
        return null;
    }
}
