package com.nscharrenberg.um.multiagentsurveillance.agents.DQN;

import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.neuralNetwork.Network;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.Agent;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.*;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.Collision.Wall;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.Item;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Guard;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Intruder;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Player;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.AreaEffects.AudioEffect.Audio;

import java.util.List;

public class DQN_Agent extends Agent {

    private double[][][] state, previousState;
    private int rewardScalar = 5;
    private int channels = 3;
    private int length = 13;
    private int xOffset = 6;
    private int yOffset = 6;
    private double lr = 0.90; // TBD
    private Network network;

    public DQN_Agent(Player player) {
        super(player);

        network = new Network(lr);
        network.initLayers(channels, length);
    }


    public double preformMove(Action angle){

        execute(angle);
        previousState = state.clone();
        updateState();

        return calculateReward();
    }

    @Override
    public void execute(Action angle) {

        try {
            playerRepository.move(player, angle);
        } catch (CollisionException | InvalidTileException | ItemNotOnTileException | ItemAlreadyOnTileException | BoardNotBuildException e) {
            e.printStackTrace();
        }

        updateState();
    }

    @Override
    public Action decide() throws Exception {

        int move = predictMove();
        Action currentDirection = player.getDirection();
        Action action = currentDirection;

        // TODO: Add option to drop makers

        switch (move){
            case 0 -> action = currentDirection;
            case 1 -> action = turnRight(currentDirection);
            case 2 -> action = turnLeft(currentDirection);
            default -> dropMaker(player);
        }

        return action;
    }

    private int predictMove(){

        double[] qValues = network.forwardPropagate(updateState());

        // TODO: Save qValues for error calculations (Maybe ArrayList)
        // TODO: check if moves collide with wall or other guard/intruder

        int maxInd = 0;


        for (int i = 0; i < qValues.length; i++) {
            maxInd = qValues[i] > qValues[maxInd] ?  i : maxInd;
        }

        return maxInd;
    }

    private void dropMaker(Player player){

    }

    private Action turnRight(Action playerDirection) throws Exception{
        if (playerDirection.equals(Action.UP))
            return Action.RIGHT;
        if (playerDirection.equals(Action.DOWN))
            return Action.LEFT;
        if (playerDirection.equals(Action.LEFT))
            return Action.UP;
        if (playerDirection.equals(Action.RIGHT))
            return Action.DOWN;
        else
            throw new Exception("Player has no direction");
    }

    private Action turnLeft(Action playerDirection) throws Exception{
        if (playerDirection.equals(Action.UP))
            return Action.LEFT;
        if (playerDirection.equals(Action.DOWN))
            return Action.RIGHT;
        if (playerDirection.equals(Action.LEFT))
            return Action.DOWN;
        if (playerDirection.equals(Action.RIGHT))
            return Action.UP;
        else
            throw new Exception("Player has no direction");
    }

    private double calculateReward(){

        double reward = 0;
        double dx,dy;

        // TODO: Add reward function for intruders
        // TODO: Improve reward function to encompass sound

        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                if (state[0][i][j] == -1){
                    dx = i - xOffset;
                    dy = j - yOffset;
                    reward = (1 / Math.sqrt(dx*dx + dy*dy)) * rewardScalar;
                }
            }
        }

        return reward;
    }

    public double[][][] updateState(){

        state = new double[channels][length][length];
        List<Tile> visionList = (List<Tile>) vision;
        List<Audio> audioList = player.getAudioEffects();
        List<Item> items;
        int xP = player.getTile().getX();
        int yP = player.getTile().getY();
        int VIX, VIY;

        for (Tile tile : visionList) {
            // Get the vision index for x and y position in state tensor
            VIX = (tile.getX() - xP) + xOffset;
            VIY = (tile.getY() - yP) + yOffset;

            items = player.getTile().getItems();

            for (Item item : items) {
                if (item instanceof Guard)
                    state[0][VIX][VIY] = 1;
                if (item instanceof Intruder)
                    state[0][VIX][VIY] = -1;
                if (item instanceof Wall)
                    state[1][VIX][VIY] = 1;
            }
        }

        int dx, dy;
        for (Audio sound : audioList) {
            dx = sound.ActionDirection().getxIncrement();
            dy = sound.ActionDirection().getyIncrement();

            state[2][xP + dx][yP + dy] += sound.effectLevel();
        }

        return state;
    }

    public double[][][] getState() {
        return state.clone();
    }

}
