package com.nscharrenberg.um.multiagentsurveillance.agents.AudioAgent;

import com.nscharrenberg.um.multiagentsurveillance.agents.shared.Agent;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.*;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Player;


public class AudioAgent extends Agent {

    public AudioAgent(Player player) {
        super(player);

    }

    @Override
    public void execute(Action Action) {
        try {
            playerRepository.move(getPlayer(), Action);
        } catch (CollisionException | InvalidTileException | ItemNotOnTileException | ItemAlreadyOnTileException | BoardNotBuildException e) {
            e.getCause();
        }
    }

    @Override
    public Action decide() {
        return null;
    }
}
