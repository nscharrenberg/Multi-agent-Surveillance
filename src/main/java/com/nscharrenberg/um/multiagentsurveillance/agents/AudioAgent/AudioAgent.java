package com.nscharrenberg.um.multiagentsurveillance.agents.AudioAgent;

import com.nscharrenberg.um.multiagentsurveillance.agents.shared.Agent;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IGameRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IMapRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.*;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Intruder;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Player;
import com.nscharrenberg.um.multiagentsurveillance.headless.repositories.PlayerRepository;


public class AudioAgent extends Agent {

    public AudioAgent(Player player, IMapRepository mapRepository, IGameRepository gameRepository, PlayerRepository playerRepository) {
        super(player, mapRepository, gameRepository, playerRepository);

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
        if(player instanceof Intruder intruder) {
            if(intruder.getTargetAngle() != null)
                return intruder.getTargetAngle();
        }
        return Action.UP;
    }
}
