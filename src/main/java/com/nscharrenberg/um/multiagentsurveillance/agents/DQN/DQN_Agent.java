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

import java.util.*;

import static com.nscharrenberg.um.multiagentsurveillance.agents.DQN.DQN_Agent_Util.*;

public class DQN_Agent extends Agent {

    private final int channels = DQN_Params.inputChannels.valueInt;
    private final int length = DQN_Params.inputLength.valueInt;
    private final int xOffset = DQN_Params.xOffset.valueInt;
    private final int yOffset = DQN_Params.yOffset.valueInt;
    private final int targetUpdate = DQN_Params.targetUpdate.valueInt;
    private final double gamma = DQN_Params.gamma.valueDbl;
    private final Random random = new Random();
    private final Agent explorationAgent = new EvaderAgent(player);
    private EpsilonGreedy strategy;
    private Network policyNetwork, targetNetwork;
    private TrainingData trainingData;
    private double minTargetDistance = Double.POSITIVE_INFINITY;
    private int updateCount = 0;

    public void newGame(int networkNum, int episode){
        policyNetwork.saveNetwork(networkNum, episode);
        updateCount = 0;
        minTargetDistance = Double.POSITIVE_INFINITY;
    }

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

        // Exploration V Exploitation
        if (strategy.explorationRate(episodeNum) > random.nextDouble()) {
            action = explorationAgent.decide();

            // This is a terrible fix :(
            while (action.equals(player.getDirection()) && collisionForward()) {
                action = explorationAgent.decide();
                //System.out.println("Exploration agent colliding");
            }

            return action;
        }

        return predictAction(state);
    }


    private Action predictAction(double[][][] state) throws Exception {

        double[] qValues = policyNetwork.forwardPropagate(state);

        int prediction = argmax(qValues);

        if (actionPrediction(prediction).equals(player.getDirection()) && collisionForward()){
            System.out.println("DQN trying to collide");
            trainAgentCollision(qValues);
            return predictAction(state);
        }

        System.out.println("QValues = [ " + qValues[0] + " " + qValues[1]  + " " + qValues[2]  + " " + qValues[3] + " ]");

        return actionPrediction(prediction);
    }

    public void trainAgentCollision(double[] predictedQV) throws Exception {

        if (markerAction())
            return;

        double[] targetQV = predictedQV.clone();
        targetQV[predictionAction(player.getDirection())] -= 0.1;

        policyNetwork.backwardPropagate(targetQV, predictedQV);
    }

    public boolean markerAction(){
        Action[] legalActions = {Action.UP, Action.DOWN, Action.LEFT, Action.RIGHT};
        for (Action action : legalActions)
             if (action.equals(player.getDirection())) return false;

        return true;
    }

    public void endTrain(double[][][] state, Action action) throws Exception {

        double reward;
        double[][][] nextState = state.clone();

        reward = calculateEndReward(escaped((Intruder) this.player));

        Experience experience = new Experience(state, action, reward, nextState, true);
        trainingData.push(experience);

        trainAgentEnd(experience);
    }

    public void trainAgentEnd(Experience experience) throws Exception {

        double[] predictedQV, targetQV;

        int prediction = predictionAction(experience.action);

        predictedQV = policyNetwork.forwardPropagate(experience.state);
        targetQV = predictedQV.clone();
        targetQV[prediction] = targetQV[argmax(targetQV)] * gamma +  experience.getReward();
        policyNetwork.backwardPropagate(targetQV, predictedQV);

        targetNetwork = policyNetwork.clone();
    }


    public void trainAgent(Experience experience) throws Exception {

        if (markerAction())
            return;

        double[] predictedQV, targetQV;

        predictedQV = policyNetwork.forwardPropagate(experience.state);
        targetQV = targetQValues(experience.nextState, predictedQV, experience.action, experience.reward);
        policyNetwork.backwardPropagate(targetQV, predictedQV);

        if (updateCount++ == targetUpdate) {
            targetNetwork = policyNetwork.clone();
            updateCount = 0;
        }
    }

    public void trainAgent(TrainingData batch) throws Exception {

        ArrayList<Experience> experiences = batch.experiences;

        for (Experience experience : experiences)
            trainAgent(experience);
    }

    private double[] targetQValues(double[][][] state, double[] predictedQV, Action action, double reward) throws Exception {

        double[] out = predictedQV.clone();
        double[] target = targetNetwork.forwardPropagate(state);

        out[predictionAction(action)] = (target[argmax(target)] * gamma) + reward;

        return out;
    }

    public double calculateEndReward(boolean escaped) {
        int reward = 3;
        return escaped ?  reward : -reward;
    }


    public double calculateReward(double[][][] state, Action action) throws Exception {

        // Delta Sound Proximity & Delta Vision Proximity
        double dSP, dVP;

        dSP = -soundProximity(state) / 100;

        dVP = -visionProximity(state) / 10;

        Intruder intruder = (Intruder) this.player;
        double reward = 0;

        if (player.getDirection().equals(intruder.getTargetAngle()))
            reward = 0.01;

        double targetDistance = intruder.getDistanceToTarget();

        if (targetDistance < minTargetDistance){
            minTargetDistance = targetDistance;
            reward += 0.1;
        }

        TileArea targetArea = mapRepository.getTargetArea();

        if (targetArea.within(player.getTile().getX(), player.getTile().getY()))
            reward += 1;

        if (action.equals(player.getDirection()));
            reward += 0.01;

        return dSP + dVP + reward;
    }


    public double[][][] updateState(){

        double[][][] state = new double[channels][length][length];
        List<Sound> SoundList = player.getSoundEffects();
        TileArea targetArea = mapRepository.getTargetArea();
        Intruder intruder = (Intruder) this.player;
        Action direction = player.getDirection();
        List<Item> items;

        int     xP = player.getTile().getX(),
                yP = player.getTile().getY(),
                xT, yT,
                VIX, VIY;

        for (Map.Entry<Integer, HashMap<Integer, Tile>> rowEntry : player.getVision().getRegion().entrySet()) {
            for (Map.Entry<Integer, Tile> colEntry : rowEntry.getValue().entrySet()) {

                xT = colEntry.getValue().getX();
                yT = colEntry.getValue().getY();

                // Get the vision index for x and y position in state tensor
                VIX = (xT - xP) + xOffset;
                VIY = (yT - yP) + yOffset;

                if (targetArea.within(xT, yT))
                    state[1][VIX][VIY] = 1;

                items = colEntry.getValue().getItems();

                for (Item item : items) {
                    if (item instanceof Guard)
                        state[0][VIX][VIY] = -1;
                    if (item instanceof Intruder)
                        state[0][VIX][VIY] = 1;
                    if (item instanceof Wall)
                        state[1][VIX][VIY] = -1;
                }
            }
        }

        if (direction.equals(intruder.getTargetAngle()))
            state[2][xDirectionTarget(direction)][yDirectionTarget(direction)] = 1;

        int dx, dy;
        for (Sound sound : SoundList) {
            dx = sound.actionDirection().getxIncrement();
            dy = sound.actionDirection().getyIncrement();

            state[2][xOffset + dx][yOffset + dy] += sound.effectLevel();
        }

        return state;
    }

    public boolean collisionForward() throws Exception {

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
            default -> throw new Exception("Player direction = " + direction);
        }
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

    private int predictMove() {
        double[] qValues = policyNetwork.forwardPropagate(updateState());
        return argmax(qValues);
    }


/*    public void saveNetwork(int index){
        policyNetwork.saveNetwork(index);
    }*/

    public void loadNetwork(int index, int saveNumber) throws Exception {
        policyNetwork.loadNetwork(index, saveNumber);
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

}
