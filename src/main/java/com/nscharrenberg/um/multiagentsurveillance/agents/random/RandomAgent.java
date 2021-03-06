package com.nscharrenberg.um.multiagentsurveillance.agents.random;

import com.nscharrenberg.um.multiagentsurveillance.agents.shared.Agent;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IGameRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IMapRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IPlayerRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.*;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.Collision.Collision;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.Item;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Player;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.RandomUtil;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Optional;

public class RandomAgent extends Agent {
    private SecureRandom random;

    public RandomAgent(Player agent) {
        super(agent);

        try {
            this.random = RandomUtil.seeded();
        } catch (NoSuchAlgorithmException e) {
//            gameRepository.setRunning(false);
        }
    }

    public RandomAgent(Player agent, IMapRepository mapRepository, IGameRepository gameRepository, IPlayerRepository playerRepository) {
        super(agent, mapRepository, gameRepository, playerRepository);

        try {
            this.random = RandomUtil.seeded();
        } catch (NoSuchAlgorithmException e) {
//            gameRepository.setRunning(false);
        }
    }

    @Override
    public void execute(Action move) {
        try {
            playerRepository.move(player, move);
        } catch (CollisionException | InvalidTileException | ItemNotOnTileException | ItemAlreadyOnTileException | BoardNotBuildException e) {
            e.printStackTrace();
			//System.out.println(e.getMessage());
        }
    }

    @Override
    public Action decide() {
        int value = this.random.nextInt(100);

        Action move = player.getDirection();

        Optional<Tile> nextTileOpt = knowledge.getByCoordinates(player.getTile().getX() + move.getxIncrement(), player.getTile().getY() + player.getDirection().getyIncrement());

        boolean nextBlocked = false;
        if (nextTileOpt.isPresent()) {
            Tile nextTile = nextTileOpt.get();

            for (Item items : nextTile.getItems()) {
                if (items instanceof Collision) {
                    nextBlocked = true;
                    break;
                }
            }
        }

        if (value <= 30 || nextBlocked) {
            int pick = this.random.nextInt(4);
            move = Action.values()[pick];
        }

        return move;
    }
}
