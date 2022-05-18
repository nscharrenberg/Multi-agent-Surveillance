package com.nscharrenberg.um.multiagentsurveillance.agents.DQN;

import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.neuralNetwork.Network;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.training.EpsilonGreedy;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.training.Experience;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.training.TrainingData;
import com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi.YamauchiAgent;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.Agent;
import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.*;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.Collision.Wall;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.Item;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Guard;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Intruder;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Player;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.AreaEffects.AudioEffect.Audio;

import java.util.*;

public class DQN_Agent extends Agent {

    private EpsilonGreedy strategy;
    private final int rewardScalar = 5;
    private final int channels = 3;
    private final int length = 13;
    private final int xOffset = 6;
    private final int yOffset = 6;
    private Network policyNetwork, targetNetwork;
    private final Random random = new Random();
    private final double gamma = 0.999;

    // TODO: Decide on exploration policy
    private Agent explorationAgent = new YamauchiAgent(player);

    public DQN_Agent(){
        super(null);
        initAgent();
    }

    public DQN_Agent(Player player) {
        super(player);
        initAgent();
    }

    public void setPlayer(Player player){
        this.player = player;
        explorationAgent.setPlayer(player);
    }

    private void initAgent(){
        policyNetwork = new Network();
        policyNetwork.initLayers(channels, length);
        targetNetwork = policyNetwork.clone();
        strategy = new EpsilonGreedy();
    }

    public Action selectAction(int episodeNum, double[][][] state) throws Exception {

        Action action;

        // Exploration V Exploitation
        if (strategy.explorationRate(episodeNum) > random.nextDouble())
            action = explorationAgent.decide();
        else
            action = predictAction(state);


        return action;
    }

    private Action predictAction(double[][][] state) throws Exception {

        double[] qValues = policyNetwork.forwardPropagate(state);

        Action currentDirection = player.getDirection();
        Action action;

        // TODO: Add marker handling

        switch (optAction(qValues)){
            case 0 -> action = currentDirection;
            case 1 -> action = turnRight(currentDirection);
            case 2 -> action = turnLeft(currentDirection);
            default -> throw new Exception("Invalid Move Selected");
        }

        return action;
    }

    private int optAction(double[] qValues) throws Exception {
        int start = 0;

        // TODO: Train if agent selects moving into a wall. Not sure about other collisions

        if (collisionForward())
            start = 1;

        return argmax(qValues, start);
    }

    public void trainAgent(Experience experience){

        double[] predictedQV, targetQV;

        predictedQV = policyNetwork.forwardPropagate(experience.state);
        targetQV = targetQValues(experience.nextState, predictedQV.clone(), experience.reward);
        policyNetwork.backwardPropagate(targetQV, predictedQV);
    }

    public void trainAgent(TrainingData batch){

        ArrayList<double[][][]> states = batch.states, nextStates = batch.nextStates;
        ArrayList<Double> rewards = batch.rewards;
        double[] predictedQV, targetQV, loss = new double[0];


        for (int i = 0; i < states.size(); i++) {
            predictedQV = policyNetwork.forwardPropagate(states.get(i));
            targetQV = targetQValues(nextStates.get(i), predictedQV.clone(), rewards.get(i));
            policyNetwork.backwardPropagate(targetQV, predictedQV);
        }

    }

    private double[] targetQValues(double[][][] state, double[] qValues, double reward){

        double[] temp = targetNetwork.forwardPropagate(state);
        qValues[argmax(temp)] = temp[argmax(temp)] * gamma + reward;

        return qValues;
    }

    private int predictMove() throws InvalidTileException, BoardNotBuildException {

        double[] qValues = policyNetwork.forwardPropagate(updateState());

        // TODO: check if moves collide with wall or other guard/intruder

        int start = 0;

        if (collisionForward())
            start = 1;

        return argmax(qValues, start);
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

    public double calculateReward(double[][][] previousState, double[][][] state){

        // Delta Sound Proximity & Delta Vision Proximity
        double dSP = 0, dVP = 0;

        // Positive if sound intensity from all sides and behind is decreasing
        dSP = soundProximity(previousState) - soundProximity(state);

        // Positive if visible distance from all guards is decreasing
        dVP = visionProximity(previousState) - visionProximity(state);

        // TODO: Add reward moving towards target area
        // TODO: Add reward for being in target area



        return rewardScalar * (dSP + dVP);
    }

    private double soundProximity(double[][][] state){

        int x,y;
        double proximity = 0;
        Action direction = player.getDirection();

        Action[] checklist = {Action.UP, Action.DOWN, Action.LEFT, Action.RIGHT};

        for (Action check : checklist) {
            if (check.equals(direction))
                continue;

            x = check.getxIncrement() + xOffset;
            y = check.getyIncrement() + yOffset;

            proximity += state[2][x][y];
        }

        return proximity;
    }

    private double visionProximity(double[][][] state){
        int dx, dy;
        double proximity = 0;

        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                if (state[0][i][j] == 1){
                    dx = i - xOffset;
                    dy = j - yOffset;
                    proximity += Math.sqrt(dx*dx + dy*dy);
                }
            }
        }

        return proximity;
    }

/*    public double OLD_calculateReward(){
        double reward = 0;
        double dx,dy;

        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                if (previousState[0][i][j] == 1){
                    dx = i - xOffset;
                    dy = j - yOffset;
                    reward = (1 / Math.sqrt(dx*dx + dy*dy)) * rewardScalar;
                }
            }
        }
    }*/

    public double[][][] updateState(){

        double[][][] state = new double[channels][length][length];
        List<Audio> audioList = player.getAudioEffects();
        List<Item> items;
        int xP = player.getTile().getX();
        int yP = player.getTile().getY();
        int VIX, VIY;

        for (Map.Entry<Integer, HashMap<Integer, Tile>> rowEntry : vision.getRegion().entrySet()) {
            for (Map.Entry<Integer, Tile> colEntry : rowEntry.getValue().entrySet()) {

                // Get the vision index for x and y position in state tensor
                VIX = (colEntry.getValue().getX() - xP) + xOffset;
                VIY = (colEntry.getValue().getY() - yP) + yOffset;

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
        }

        int dx, dy;
        for (Audio sound : audioList) {
            dx = sound.actionDirection().getxIncrement();
            dy = sound.actionDirection().getyIncrement();

            state[2][xP + dx][yP + dy] += sound.effectLevel();
        }

        return state;
    }

    public boolean collisionForward() throws InvalidTileException, BoardNotBuildException {

        Action direction = player.getDirection();

        int x = player.getTile().getX();
        int y = player.getTile().getY();

        int velocity = 1;
        switch (direction){
            case UP -> {
                return Factory.getMapRepository().findTileByCoordinates(x,y - velocity).isCollision();
            }
            case DOWN -> {
                return Factory.getMapRepository().findTileByCoordinates(x,y + velocity).isCollision();
            }
            case LEFT -> {
                return Factory.getMapRepository().findTileByCoordinates(x - velocity, y).isCollision();
            }
            case RIGHT -> {
                return Factory.getMapRepository().findTileByCoordinates(x + velocity, y).isCollision();
            }
        }

        return false;
    }

    @Override
    public void execute(Action action) {
        try {
            playerRepository.move(player, action);
        } catch (CollisionException | InvalidTileException | ItemNotOnTileException | BoardNotBuildException | ItemAlreadyOnTileException e) {
            e.printStackTrace();
        }
    }

    // TODO: Track move number
    @Override
    public Action decide() throws Exception {

        int move = predictMove();

        Action currentDirection = player.getDirection();
        Action action;

        // TODO: Add option to drop makers

        switch (move){
            case 0 -> action = currentDirection;
            case 1 -> action = turnRight(currentDirection);
            case 2 -> action = turnLeft(currentDirection);
            default -> throw new Exception("Invalid Move Selected");
        }


        return action;
    }

    private int argmax(double[] input){
        return argmax(input, 0);
    }

    private int argmax(double[] input, int start){
        int maxInd = start;
        for (int i = start; i < input.length; i++) maxInd = input[i] > input[maxInd] ?  i : maxInd;
        return maxInd;
    }

}
