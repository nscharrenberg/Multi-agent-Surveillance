package com.nscharrenberg.um.multiagentsurveillance.agents.random;

import com.nscharrenberg.um.multiagentsurveillance.agents.IAgent;
import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IGameRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IMapRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IPlayerRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.CollisionException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.InvalidTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.ItemAlreadyOnTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.ItemNotOnTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class RandomAgent implements IAgent {
    private final IMapRepository mapRepository;
    private final IGameRepository gameRepository;
    private final IPlayerRepository playerRepository;

    private final Player agent;

    private SecureRandom random;

    public RandomAgent(Player agent) {
        this.agent = agent;

        this.mapRepository = Factory.getMapRepository();
        this.playerRepository = Factory.getPlayerRepository();
        this.gameRepository = Factory.getGameRepository();

        try {
            this.random = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error while generating Random Class");
        }
    }

    public RandomAgent(Player agent, IMapRepository mapRepository, IGameRepository gameRepository, IPlayerRepository playerRepository) {
        this.mapRepository = mapRepository;
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.agent = agent;

        try {
            this.random = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error while generating Random Class");
        }
    }

    @Override
    public void execute() {
        execute(decide());
    }

    @Override
    public void execute(Angle move) {
        try {
            playerRepository.move(agent, move);
        } catch (CollisionException | InvalidTileException | ItemNotOnTileException | ItemAlreadyOnTileException e) {
            e.printStackTrace();
        }
    }


    @Override
    public Angle decide() {
        int pick = this.random.nextInt(Angle.values().length);
        return Angle.values()[pick];
    }
}
