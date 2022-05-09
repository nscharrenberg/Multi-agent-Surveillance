package com.nscharrenberg.um.multiagentsurveillance.agents.DQN;

import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.neuralNetwork.Network;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.Agent;
import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.CollisionException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.InvalidTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.ItemAlreadyOnTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.ItemNotOnTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle.Angle;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.Collision.Wall;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.Item;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Guard;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Intruder;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Player;
import com.nscharrenberg.um.multiagentsurveillance.headless.repositories.MapRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.AreaEffects.AudioEffect.Audio;

import java.util.List;

public class DQN_Agent extends Agent {

    double[][][] state, previousState;
    int rewardScalar = 5;
    int channels = 3;
    int length = 13;
    int xOffset = 6;
    int yOffset = 6;
    private double lr = 0.90; // TBD

    boolean isGuard = true;

    private Network network;

    public DQN_Agent(Player player) {
        super(player);

        network = new Network(lr);
        network.initLayers(channels, length);
    }

    public double preformMove(Angle angle){
        execute(angle);
        previousState = state.clone();
        updateState();



        return calculateReward();
    }

    @Override
    public void execute(Angle angle) {
        try {
            playerRepository.move(player, angle);
        } catch (CollisionException | InvalidTileException | ItemNotOnTileException e) {
            System.out.println(e.getMessage());
        } catch (ItemAlreadyOnTileException e) {
            e.printStackTrace();
        }
        updateState();
    }

    @Override
    public Angle decide() throws Exception {
        int move = calculateMove();
        Angle currentDirection = player.getDirection();
        Angle nextDirection = currentDirection;
        switch (move){
            case 0 -> nextDirection = currentDirection;
            case 1 -> nextDirection = turnRight(currentDirection);
            case 2 -> nextDirection = turnLeft(currentDirection);
            default -> dropMaker(player);
        }

        return nextDirection;
    }

    private void dropMaker(Player player){

    }

    private Angle turnRight(Angle playerDirection) throws Exception{
        if (playerDirection.equals(Angle.UP))
            return Angle.RIGHT;
        if (playerDirection.equals(Angle.DOWN))
            return Angle.LEFT;
        if (playerDirection.equals(Angle.LEFT))
            return Angle.UP;
        if (playerDirection.equals(Angle.RIGHT))
            return Angle.DOWN;
        else
            throw new Exception("Player has no direction");
    }

    private Angle turnLeft(Angle playerDirection) throws Exception{
        if (playerDirection.equals(Angle.UP))
            return Angle.LEFT;
        if (playerDirection.equals(Angle.DOWN))
            return Angle.RIGHT;
        if (playerDirection.equals(Angle.LEFT))
            return Angle.DOWN;
        if (playerDirection.equals(Angle.RIGHT))
            return Angle.UP;
        else
            throw new Exception("Player has no direction");
    }

    private double calculateReward(){

        double reward = 0;
        double dx,dy;

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

    private int calculateMove(){


        return 0;
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
            dx = sound.angleDirection().getxIncrement();
            dy = sound.angleDirection().getyIncrement();

            state[2][xP + dx][yP + dy] = sound.effectLevel();
        }

        return state;
    }

    public double[][][] getState() {
        return state.clone();
    }
}
