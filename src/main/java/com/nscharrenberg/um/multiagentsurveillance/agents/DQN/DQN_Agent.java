package com.nscharrenberg.um.multiagentsurveillance.agents.DQN;

import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.neuralNetwork.Network;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.training.EpsilonGreedy;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.training.Experience;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.training.TrainingData;
import com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi.YamauchiAgent;
import com.nscharrenberg.um.multiagentsurveillance.agents.probabilistic.evader.EvaderAgent;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.Agent;
import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IGameRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IMapRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IPlayerRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.*;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.Collision.Wall;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.Item;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.TileArea;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Guard;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Intruder;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Player;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.AreaEffects.AudioEffect.Sound;

/*
    Note:
    1. The state might need to store the current direction of the agent for training
        a. Add this to the state matrix in the player index
        b. Create a state object
    2.


*/

import java.util.*;

public class DQN_Agent extends Agent {

    private EpsilonGreedy strategy;
    private final int rewardScalar = 1;
    private final int channels = 3;
    private final int length = 13;
    private final int xOffset = 6;
    private final int yOffset = 6;
    private Network policyNetwork, targetNetwork;
    private final Random random = new Random();
    private final double gamma = 0.999;
    private TrainingData trainingData;
    private double minTargetDistance = Double.POSITIVE_INFINITY;

    // TODO: Decide on exploration policy
    private Agent explorationAgent = new EvaderAgent(player);

    public DQN_Agent(){
        super(null, Factory.getMapRepository(), Factory.getGameRepository(), Factory.getPlayerRepository());
        initAgent();
    }

    public DQN_Agent(Player player) {
        super(player, Factory.getMapRepository(), Factory.getGameRepository(), Factory.getPlayerRepository());
        initAgent();
    }

    public DQN_Agent(IMapRepository mapRepository, IGameRepository gameRepository, IPlayerRepository playerRepository){
        super(null, mapRepository, gameRepository, playerRepository);
        initAgent();
    }

    public DQN_Agent(Player player, IMapRepository mapRepository, IGameRepository gameRepository, IPlayerRepository playerRepository) {
        super(player, mapRepository, gameRepository, playerRepository);
        initAgent();
    }

    public void setPlayer(Player player){
        this.player = player;
        this.initRepositories(mapRepository, gameRepository, playerRepository);
        explorationAgent.setPlayer(player);
        explorationAgent.initRepositories(mapRepository, gameRepository, playerRepository);
    }

    private void initAgent(){
        policyNetwork = new Network();
        policyNetwork.initLayers(channels, length);
        targetNetwork = policyNetwork.clone();
        strategy = new EpsilonGreedy();
    }

    public Action selectAction(int episodeNum, double[][][] state) throws Exception {

        Action action;

        int i = 0;
        // Exploration V Exploitation
        if (strategy.explorationRate(episodeNum) > random.nextDouble()) {
        //if (0.5 > random.nextDouble()){
            action = randomAction();
//            System.out.println(strategy.explorationRate(episodeNum));
            // This is a terrible fix :(
            while (action.equals(player.getDirection()) && collisionForward()) {
                action = explorationAgent.decide();
                //System.out.println("Evasion stuck");
                action.setPrediction(actionIndex(action));
                if (i++ > 15)
                    action = predictAction(state);
            }
        }
        else
            action = predictAction(state);



        return action;
    }

    private Action randomAction() throws Exception {
        switch (random.nextInt(4)){
            case 0 -> {
                if (!collisionForward())
                    return player.getDirection();
            }
            case 1 -> {
                return turnLeft(player.getDirection());
            }
            case 2 -> {
                return turnRight(player.getDirection());
            }
        }
        return random.nextDouble() > 0.5 ? turnRight(player.getDirection()) : turnLeft(player.getDirection());
    }

    private int actionIndex(Action action) throws Exception {
        Action direction = player.getDirection();

        if (action.equals(direction))
            return 0;
        if (action.equals(turnRight(direction)))
            return 1;
        if (action.equals(turnLeft(direction)))
            return 2;

        System.out.println("Illegal action 180");
        return -1;
    }

    private Action predictAction(double[][][] state) throws Exception {

        double[] qValues = policyNetwork.forwardPropagate(state);

        while (argmax(qValues) == 0 && collisionForward()){
            trainAgentCollision(qValues);
            qValues = policyNetwork.forwardPropagate(state);
            System.out.println("DQN stuck");
        }

        System.out.println("QValues = [ " + qValues[0] + " " + qValues[1]  + " " + qValues[2]  + " ]");
        Action currentDirection = player.getDirection();
        Action action;

        // TODO: Add marker handling

        int prediction = argmax(qValues);

        switch (prediction){
            case 0 -> action = currentDirection;
            case 1 -> action = turnRight(currentDirection);
            case 2 -> action = turnLeft(currentDirection);
            default -> throw new Exception("Invalid Move Selected");
        }

        action.setPrediction(prediction);

        return action;
    }

    public void trainAgentCollision(double[] predictedQV){

        double[] targetQV = predictedQV.clone();
        targetQV[0] = targetQV[0] - 0.1;

        policyNetwork.backwardPropagate(targetQV, predictedQV);
    }

    public void trainAgent(Experience experience) throws Exception {

        double[] predictedQV, targetQV;

        predictedQV = policyNetwork.forwardPropagate(experience.state);
                                                // This shouldn't be .clone
        targetQV = targetQValues(experience.nextState, predictedQV.clone(), experience.action, experience.reward);
        policyNetwork.backwardPropagate(targetQV, predictedQV);
    }

    public void trainAgent(TrainingData batch) throws Exception {

        ArrayList<double[][][]> states = batch.states, nextStates = batch.nextStates;
        ArrayList<Double> rewards = batch.rewards;
        ArrayList<Action> actions = batch.actions;

        double[] predictedQV, targetQV, loss = new double[0];

        for (int i = 0; i < states.size(); i++) {
            predictedQV = policyNetwork.forwardPropagate(states.get(i));
            targetQV = targetQValues(nextStates.get(i), predictedQV.clone(), actions.get(i), rewards.get(i));
            policyNetwork.backwardPropagate(targetQV, predictedQV);
        }

    }

    private double[] targetQValues(double[][][] state, double[] qValues, Action action, double reward) throws Exception {

        double[] temp = targetNetwork.forwardPropagate(state);

        if (action.getPrediction()<0)
            throw new Exception("Illegal Action");

        //System.out.println(action.getPrediction() + " Reward " + reward);
        qValues[action.getPrediction()] = temp[action.getPrediction()] * gamma + reward;

        //qValues[argmax(temp)] += argmax(temp) == action.getPrediction() ? -0.1 : 0;
        //System.out.println("Target QValues = [ " + qValues[0] + " " + qValues[1]  + " " + qValues[2]  + " ]");

        return qValues;
    }

    public double calculateReward(double[][][] previousState, double[][][] state, Action action) throws Exception {

        double distanceReward = .5;

        // Delta Sound Proximity & Delta Vision Proximity
        double dSP, dVP;

        // Positive if sound intensity from all sides and behind is decreasing
        dSP = soundProximity(previousState) - soundProximity(state);

        // Positive if visible distance from all guards is decreasing
        dVP = visionProximity(previousState) - visionProximity(state);


        Intruder intruder = (Intruder) this.player;
        double reward = 0;

        if (player.getDirection().equals(intruder.getTargetAngle()))
            reward = 0.005;

        double targetDistance = intruder.getDistanceToTarget();

        if (targetDistance < minTargetDistance){
            System.out.println("Getting closer");
            minTargetDistance = targetDistance;
            reward += distanceReward;
        }

        TileArea targetArea = mapRepository.getTargetArea();

        if (targetArea.within(player.getTile().getX(), player.getTile().getY()))
            reward += 10;

        state[0][xOffset][yOffset] = actionIndex(action);

        return rewardScalar * (dSP + dVP ) + reward;
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

    public double[][][] updateState(){

        double[][][] state = new double[channels][length][length];
        List<Sound> SoundList = player.getSoundEffects();
        List<Item> items;

        if (player == null)
            System.out.println("Null");

        if (player.getTile() == null) {
            System.out.println("test");
        }

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
        for (Sound sound : SoundList) {
            dx = sound.actionDirection().getxIncrement();
            dy = sound.actionDirection().getyIncrement();

            state[2][xOffset + dx][yOffset + dy] += sound.effectLevel();
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
                return mapRepository.findTileByCoordinates(x,y - velocity).isCollision();
            }
            case DOWN -> {
                return mapRepository.findTileByCoordinates(x,y + velocity).isCollision();
            }
            case LEFT -> {
                return mapRepository.findTileByCoordinates(x - velocity, y).isCollision();
            }
            case RIGHT -> {
                return mapRepository.findTileByCoordinates(x + velocity, y).isCollision();
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

    private int predictMove() throws InvalidTileException, BoardNotBuildException {

        double[] qValues = policyNetwork.forwardPropagate(updateState());

        // TODO: check if moves collide with wall or other guard/intruder

        int start = 0;

        if (collisionForward())
            start = 1;

        return argmax(qValues, start);
    }

    private int argmax(double[] input){
        return argmax(input, 0);
    }

    private int argmax(double[] input, int start){
        int maxInd = start;
        for (int i = start; i < input.length; i++) maxInd = input[i] > input[maxInd] ?  i : maxInd;
        return maxInd;
    }

    public IGameRepository getGameRepository() {
        return gameRepository;
    }

    public void setGameRepository(IGameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Override
    public IMapRepository getMapRepository() {
        return mapRepository;
    }

    public void setMapRepository(IMapRepository mapRepository) {
        this.mapRepository = mapRepository;
    }

    @Override
    public IPlayerRepository getPlayerRepository() {
        return playerRepository;
    }

    public void setPlayerRepository(IPlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public TrainingData getTrainingData() {
        return trainingData;
    }

    public void setTrainingData(TrainingData trainingData) {
        this.trainingData = trainingData;
    }

    public Agent getExplorationAgent() {
        return explorationAgent;
    }

    public void setExplorationAgent(Agent explorationAgent) {
        this.explorationAgent = explorationAgent;
    }
}
