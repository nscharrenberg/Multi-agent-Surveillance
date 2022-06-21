package com.nscharrenberg.um.multiagentsurveillance.agents.DQN;

import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.neuralNetwork.Network;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.training.EpsilonGreedy;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.training.Experience;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.training.TrainingData;
import com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi.YamauchiAgent;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.Agent;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.pathfinding.AStar.AStar;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.utils.QueueNode;
import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IGameRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IMapRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IPlayerRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.*;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.Collision.Wall;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.Item;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.ShadowTile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.TileArea;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Guard;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Intruder;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Player;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.AreaEffects.AudioEffect.Sound;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.RandomUtil;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;

import static com.nscharrenberg.um.multiagentsurveillance.agents.DQN.DQN_Agent_Util.*;
import static com.nscharrenberg.um.multiagentsurveillance.agents.DQN.DQN_Params.batchSize;

public class DQN_Agent extends Agent {

    private final int channels = DQN_Params.inputChannels.valueInt;
    private final int length = DQN_Params.inputLength.valueInt;
    private final int xOffset = DQN_Params.xOffset.valueInt;
    private final int yOffset = DQN_Params.yOffset.valueInt;
    private final int targetUpdate = DQN_Params.targetUpdate.valueInt;
    private final double gamma = DQN_Params.gamma.valueDbl;
    private final Random random = new Random();
    private final Agent explorationAgent = new YamauchiAgent(player);
    private EpsilonGreedy strategy;
    private Network policyNetwork, targetNetwork;
    private TrainingData trainingData;
    private double minTargetDistance = Double.POSITIVE_INFINITY;
    private int updateCount = 0;
    Queue<Action> queue;

    public void newGame(int networkNum, int episode){
        policyNetwork.saveNetwork(networkNum, episode);
        updateCount = 0;
        minTargetDistance = Double.POSITIVE_INFINITY;
        knowledgeSize = 0;
    }

    public DQN_Agent(){
        super(null, Factory.getMapRepository(), Factory.getGameRepository(), Factory.getPlayerRepository());
        try {
            this.random = RandomUtil.seeded();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        initAgent();
    }

    public DQN_Agent(Player player) {
        super(player, Factory.getMapRepository(), Factory.getGameRepository(), Factory.getPlayerRepository());
        try {
            this.random = RandomUtil.seeded();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        initAgent();
    }

    public DQN_Agent(IMapRepository mapRepository, IGameRepository gameRepository, IPlayerRepository playerRepository){
        super(null, mapRepository, gameRepository, playerRepository);
        try {
            this.random = RandomUtil.seeded();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        initAgent();
    }

    public DQN_Agent(Player player, IMapRepository mapRepository, IGameRepository gameRepository, IPlayerRepository playerRepository) {
        super(player, mapRepository, gameRepository, playerRepository);
        try {
            this.random = RandomUtil.seeded();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
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
        if (strategy.explorationRate(episodeNum) > random.nextDouble() && true) {
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


    private boolean colliding = false;

    private Action predictAction(double[][][] state) throws Exception {

        double[] qValues = policyNetwork.forwardPropagate(state);

        int prediction = argmax(qValues);

        if (predictionToAction(prediction).equals(player.getDirection()) && collisionForward()){
            if (colliding){
                colliding = false;
                return explorationAgent.decide();
            }
            colliding = true;
            System.out.println("DQN trying to collide");
            trainAgentCollision(qValues);
            return predictAction(state);
        }

        colliding = false;
        System.out.println(predictionToAction(prediction));
        System.out.println(Arrays.toString(qValues));


        return predictionToAction(prediction);
    }

    public void trainAgentCollision(double[] predictedQV) throws Exception {

        Action[] checkList = {Action.UP, Action.DOWN, Action.LEFT, Action.RIGHT};
        double targetQV;

        for (Action check : checkList) {
            if (!check.equals(player.getDirection()) && !collisionForward(check)) {
                targetQV = predictedQV[actionToPrediction(check)] + 0.005;
                policyNetwork.backwardPropagate(targetQV, predictedQV[actionToPrediction(check)], actionToPrediction(player.getDirection()));
            }
        }
    }

    public void trainAgentEndCaught(int moveNumber) throws Exception {
        Experience experience = trainingData.getLastExperience();

        trainAgentEnd(new Experience(experience.nextState, experience.action, calculateEndReward(false, moveNumber), null, true));
        targetNetwork = policyNetwork.clone();
    }

    public void endTrain(double[][][] state, Action action, int moveNumber) throws Exception {

        double reward;
        double[][][] nextState = state.clone();

        reward = calculateEndReward(escaped((Intruder) this.player), moveNumber);

        Experience experience = new Experience(state, action, reward, nextState, true);
        trainingData.push(experience);

        trainAgentEnd(experience);
    }

    public void trainAgentEnd(Experience experience) throws Exception {

        double predictedQV, targetQV;

        int prediction = actionToPrediction(experience.action);

        predictedQV = policyNetwork.forwardPropagate(experience.state)[prediction];
        targetQV = predictedQV * gamma +  experience.getReward();
        policyNetwork.backwardPropagate(targetQV, predictedQV, prediction);

        if (escaped((Intruder) player)) {
            System.out.println("Updating Target Network");
            targetNetwork = policyNetwork.clone();
        }

        if (trainingData.hasBatch(batchSize.valueInt)) {
            trainAgent(getTrainingData().randomSample(batchSize.valueInt));
        }
    }


    public void trainAgent(Experience experience) throws Exception {

        double predictedQV, targetQV;
        int prediction = actionToPrediction(experience.action);

        predictedQV = policyNetwork.forwardPropagate(experience.state)[prediction];
        targetQV = targetQValues(experience.nextState, experience.reward);
        policyNetwork.backwardPropagate(targetQV, predictedQV, prediction);

    }

    public void trainAgent(TrainingData batch) throws Exception {

        ArrayList<Experience> experiences = batch.experiences;

        for (Experience experience : experiences)
            trainAgent(experience);
    }

    private double targetQValues(double[][][] state, double reward) {

        double[] target = targetNetwork.forwardPropagate(state);

        return (target[argmax(target)] * gamma) + reward;
    }

    public double calculateEndReward(boolean escaped, int moveNumber) {
        if (moveNumber < 0.1 * DQN_Params.maxMoves.valueInt && escaped)
            return 2;
        int reward = 1;
        return escaped ? reward : -reward;
    }

    private int knowledgeSize = 0;

    public double calculateReward(double[][][] state, Action action) throws Exception {

        // Delta Sound Proximity & Delta Vision Proximity
        double dSP, dVP, reward = 0;;

        Action direction = player.getDirection();

        dSP = -soundProximity(state) / 100;

        dVP = -visionProximity(state) / 10;

        Intruder intruder = (Intruder) this.player;
        double targetDistance = intruder.getDistanceToTarget();
        TileArea targetArea = mapRepository.getTargetArea();

        if (targetArea.within(player.getTile().getX(), player.getTile().getY()))
            reward += 0.01;

        /*     if (isDeadEnd(player))
        reward -= 0.03;

        if (player.getTile() instanceof ShadowTile) {
        reward -= 0.03;
        System.out.println("Shadow Time");
        }

        if (targetDistance < minTargetDistance){
        minTargetDistance = targetDistance;
        reward += 0.01;
        }

        if (knowledgeSize < getKnowledge().size()){
        knowledgeSize = getKnowledge().size();
        reward += 0.01;
        }

        if (direction.equals(Action.negateAction(gameRepository.getTargetGameAngle(player))))
        reward += 0.005;

*/
        if (updateCount++ > 2) {
            AStar aStar = new AStar();
            queue = aStar.execute(mapRepository.getBoard(), player, mapRepository.getTargetCenter()).get().getMoves();
            if (queue.poll().equals(action))
                reward += 0.01;
        }


/*        if (action.equals(explorationAgent.decide()))
            reward += 0.01;*/

        reward = (dSP + dVP + reward);

        return reward;
    }


    public double[][][] updateState() throws Exception {

        double[][][] state = new double[channels][length][length];
        List<Sound> SoundList = player.getSoundEffects();
        TileArea targetArea = mapRepository.getTargetArea();
        Intruder intruder = (Intruder) this.player;
        List<Item> items;
        Tile tile;

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


                tile = colEntry.getValue();
                items = tile.getItems();

                if (tile instanceof ShadowTile) {
                    state[0][VIX][VIY] = -0.5;
                }

                for (Item item : items) {
                    if (item instanceof Guard)
                        state[0][VIX][VIY] = -1;
                    if (item instanceof Intruder)
                        state[0][VIX][VIY] = 1;
                    if (item instanceof Intruder)
                        state[0][VIX][VIY] = 1;
                    if (item instanceof Wall)
                        state[1][VIX][VIY] = -1;
                }
            }
        }

        double targetDistance = intruder.getDistanceToTarget();
        Action targetDirection = Action.negateAction(gameRepository.getTargetGameAngle(player));

        if (targetDistance < 100)
            state[2][xDirectionTarget(targetDirection)][yDirectionTarget(targetDirection)] = targetDistance;

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
        return collisionForward(direction);
    }

    public boolean collisionForward(Action direction) throws Exception {

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

    private int predictMove() throws Exception {
        double[] qValues = policyNetwork.forwardPropagate(updateState());
        return argmax(qValues);
    }


    public void resetToTargetNet(){
        policyNetwork = targetNetwork.clone();
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
