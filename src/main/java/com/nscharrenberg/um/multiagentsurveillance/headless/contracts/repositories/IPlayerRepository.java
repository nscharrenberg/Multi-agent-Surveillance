package com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories;

import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.DQN_Agent;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.Agent;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.*;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.TileArea;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Guard;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Intruder;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Player;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.StopWatch;

import java.util.List;

public interface IPlayerRepository {

    void calculateInaccessibleTiles();

    float calculateExplorationPercentage();

    /**
     * Spawn a player to their corresponding spawn area
     * @param playerInstance - the player instance to be spawned
     */
    void spawn(Class<?> playerInstance);

    boolean spawn(Class<? extends Player> playerClass, Tile tile);

    void spawn(Class<?> playerInstance, DQN_Agent agent);

    void spawn(Class<? extends Player> playerClass, TileArea playerSpawnArea);

    /**
     * Rotate or Move a player to a new tile
     * @param player - the player to move
     * @param direction - the direction to move to
     * @throws CollisionException - Thrown when the player is about to move into an inactive Tile
     * @throws InvalidTileException - Thrown when the player is trying to move to an invalid tile
     * @throws ItemNotOnTileException - Thrown when the player is not on the tile (Should not happen)
     * @throws ItemAlreadyOnTileException - Thrown when the player is already on the tile its trying to move to (should not happen)
     */
    void move(Player player, Action direction) throws CollisionException, InvalidTileException, ItemNotOnTileException, ItemAlreadyOnTileException, BoardNotBuildException;

    void basicMove(Player player, Action direction) throws CollisionException, InvalidTileException, ItemNotOnTileException, ItemAlreadyOnTileException, BoardNotBuildException;
    /**
     * Validates whether the move the player wants to make is a valid move
     * Same logic as the `move` function but not actually making the move and instead giving a valid/invalid indication
     * @param player - the player to move
     * @param direction - the direction to move to
     * @return whether it is a valid move or not
     */
    boolean isLegalMove(Player player, Action direction);

    void updateSounds(List<Agent> agentList);

    List<Intruder> getIntruders();

    void setIntruders(List<Intruder> intruders);

    void setGuards(List<Guard> guards);

    List<Guard> getGuards();

    IMapRepository getMapRepository();

    IGameRepository getGameRepository();

    void setMapRepository(IMapRepository mapRepository);

    void setGameRepository(IGameRepository gameRepository);

    List<Agent> getAgents();

    void setAgents(List<Agent> agents);

    float calculateAgentExplorationRate(Agent agent);

    float getExplorationPercentage();

    void setExplorationPercentage(float explorationPercentage);

    TileArea getCompleteKnowledgeProgress();

    StopWatch getStopWatch();

    void setStopWatch(StopWatch stopWatch);

    List<Intruder> getCaughtIntruders();

    List<Intruder> getEscapedIntruders();
}
