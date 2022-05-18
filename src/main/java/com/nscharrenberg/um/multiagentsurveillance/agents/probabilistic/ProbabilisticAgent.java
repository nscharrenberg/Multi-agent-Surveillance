package com.nscharrenberg.um.multiagentsurveillance.agents.probabilistic;

import com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi.YamauchiAgent;
import com.nscharrenberg.um.multiagentsurveillance.agents.probabilistic.evader.EvaderAgent;
import com.nscharrenberg.um.multiagentsurveillance.agents.probabilistic.pursuer.PursuerAgent;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IGameRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IMapRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IPlayerRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Player;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.AreaEffects.AudioEffect.Sound;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ProbabilisticAgent extends YamauchiAgent {

    public State currentState = State.NORMAL;
    public Tile closestKnownAgent;
    public Sound closestSound;
    public boolean ignoreSounds = false;

    public ProbabilisticAgent(Player player) {
        super(player);
    }

    public ProbabilisticAgent(Player player, IMapRepository mapRepository, IGameRepository gameRepository, IPlayerRepository playerRepository) {
        super(player, mapRepository, gameRepository, playerRepository);
    }

    public boolean checkSounds(ProbabilisticAgent agent){
        closestSound = null;
        if(player.getSoundEffects().isEmpty())
            return false;

        if (agent instanceof EvaderAgent) {
            currentState = State.FLEE;
        } else if(agent instanceof PursuerAgent){
            currentState = State.HUNT;
        }

        List<Sound> listSounds = player.getSoundEffects();
        closestSound = new Sound(Double.MAX_VALUE, Action.DOWN);

        for(Sound sound : listSounds){
            if(sound.effectLevel() < closestSound.effectLevel()){
                closestSound = sound;
            }
        }
        return true;
    }


    public boolean checkVision(ProbabilisticAgent agent) {
        if (player.getVision() == null)
            return false;

        boolean flag = false;
        for (Map.Entry<Integer, HashMap<Integer, Tile>> rowEntry : player.getVision().getRegion().entrySet()) {
            for (Map.Entry<Integer, Tile> colEntry : rowEntry.getValue().entrySet()) {
                Tile tile = colEntry.getValue();

                if (tile.hasGuard()) {
                    if(agent instanceof EvaderAgent) {
                        changeState(State.FLEE, tile);
                        flag = true;
                    } else {
                        ignoreSounds = true;
                    }
                } else if(tile.hasIntruder()){
                    if(agent instanceof PursuerAgent) {
                        changeState(State.HUNT, tile);
                        flag = true;
                    } else {
                        ignoreSounds = true;
                    }
                }
            }
        }
        return flag;
    }

    private void changeState(State state, Tile tile){
        currentState = state;

        if (ClosestKnownAgent.isClosestKnownAgent(closestKnownAgent, player, tile)) {
            closestKnownAgent =  tile;
        }
    }
}
