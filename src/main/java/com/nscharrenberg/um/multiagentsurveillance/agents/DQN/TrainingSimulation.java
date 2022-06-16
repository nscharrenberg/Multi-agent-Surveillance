package com.nscharrenberg.um.multiagentsurveillance.agents.DQN;

import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.training.Experience;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.training.TrainingData;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.Agent;
import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IGameRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IMapRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IPlayerRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.BoardNotBuildException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.InvalidTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.ItemNotOnTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Guard;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Intruder;
import javafx.animation.AnimationTimer;
import javafx.concurrent.Task;


import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;

public class TrainingSimulation {
    private static final int DELAY = -1;

    private IGameRepository gameRepository;
    private IPlayerRepository playerRepository;
    private IMapRepository mapRepository;

    private DQN_Agent[] intruders;
    private final int batchSize = 128;
    private final int numEpisodes = 10;


    public TrainingSimulation(boolean loadWeights) throws Exception {

        init();

        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                gameLoop();
                return null;
            }
        };

        task.setOnSucceeded(e -> {

            System.out.println(numEpisodes + " Training Episodes Complete");
            gameRepository.setRunning(false);
        });

        task.exceptionProperty().addListener((observable, oldValue, newValue) ->  {
            if(newValue != null) {
                Exception ex = (Exception) newValue;
                ex.printStackTrace();
            }
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();

/*        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                try {
                    if (gameRepository.isRunning()) {
                        playerRepository.calculateExplorationPercentage();
                        updateAndDraw();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }*/

    }

    private void init(){
        Factory.init();

        gameRepository = Factory.getGameRepository();
        playerRepository = Factory.getPlayerRepository();
        mapRepository = Factory.getMapRepository();
        gameRepository.importMap();
    }

    private void setupGuards() {
        gameRepository.setupAgents(Guard.class);
    }

    private void setupIntruders() {
        gameRepository.setupAgents(Intruder.class);

        int index = 0;
        if (playerRepository.getIntruders().size() != intruders.length) {
            throw new IllegalStateException("Player Repo no# intruders != training no#");
        }

        for (Iterator<Intruder> itr = playerRepository.getIntruders().iterator(); itr.hasNext();) {
            Intruder intruder = itr.next();

            intruders[index].initRepositories();
            playerRepository.getAgents().remove(intruder.getAgent());
            intruder.setAgent(intruders[index]);
            intruders[index++].setPlayer(intruder);
            playerRepository.getAgents().add(intruder.getAgent());
        }
    }

    private void setupDQNAgents() {
        intruders = new DQN_Agent[gameRepository.getIntruderCount()];

        for (int i = 0; i < gameRepository.getIntruderCount(); i++) {
            intruders[i] = new DQN_Agent(mapRepository, gameRepository, playerRepository);
            intruders[i].setTrainingData(new TrainingData());
        }
    }

    private void reset(){

        Factory.reset();

        this.mapRepository = Factory.getMapRepository();
        this.gameRepository = Factory.getGameRepository();
        this.playerRepository = Factory.getPlayerRepository();
        gameRepository.importMap();
    }

    private void gameLoop() throws Exception {

        setupDQNAgents();

        for (int episode = 1; episode <= numEpisodes ; episode++) {

            reset();

            setupGuards();
            setupIntruders();

            gameRepository.setRunning(true);

            runGame(episode);
        }

        for (int i = 0; i < intruders.length; i++) {
            //intruders[i].saveNetwork(i);
            //intruders[i].loadNetwork(i);
        }
    }


    private void runGame(int episode) {

        double[][][] state, nextState;
        Experience experience;
        boolean done, escaped;
        Action action;
        double reward;

        while (gameRepository.isRunning()) {
            try {
                mapRepository.checkMarkers();
            } catch (BoardNotBuildException | InvalidTileException | ItemNotOnTileException e) {
            }
            playerRepository.updateSounds(playerRepository.getAgents());

            try {
                for (Iterator<Guard> itr = playerRepository.getGuards().iterator(); itr.hasNext(); ) {
                    Agent agent = itr.next().getAgent();

                    try {
                        agent.execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (!gameRepository.isRunning()) {
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!gameRepository.isRunning()) {
                continue;
            }

            try {

                for (Iterator<Intruder> itr = playerRepository.getIntruders().iterator(); itr.hasNext(); ) {
                    if (!gameRepository.isRunning()) {
                        break;
                    }

                    Intruder intruder = itr.next();
                    DQN_Agent agent = (DQN_Agent) intruder.getAgent();

                    state = agent.updateState();
                    action = agent.selectAction(episode, state);
                    agent.execute(action);

                    if (endState(intruder)) {
                        endTrain(agent, intruder, state, action);
                        continue;
                    }


                    done = !agent.getGameRepository().isRunning();

                    if (!done) {
                        nextState = agent.updateState();
                        reward = agent.calculateReward(nextState, action);                         // TODO: Check if final state is reached
                        experience = new Experience(state, action, reward, nextState, done);
                        agent.getTrainingData().push(experience);

                        // Preform training on a batch of experiences
                        if (agent.getTrainingData().hasBatch(batchSize) && 0.5 > ThreadLocalRandom.current().nextDouble() && false) {
                            System.out.println("Batch Training");
                            agent.trainAgent(agent.getTrainingData().randomSample(batchSize));
                            agent.getTrainingData().clearBatch();
                        }
                        // Preform a single step of back propagation
                        // This might need to be done with a certain probability, or it might be too slow
                        // Who knows though
                        else agent.trainAgent(experience);
                    } else System.out.println("Endeded");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!gameRepository.isRunning()) {
                break;
            }

            if (DELAY > 0) {
                try {
                    Thread.sleep(DELAY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void endTrain(DQN_Agent agent, Intruder intruder, double[][][] state, Action action) throws Exception {

        double reward;
        double[][][] nextState = new double[state.length][state[0].length][state[0].length];

        reward = caught(intruder) ? agent.calculateEndReward(false) : agent.calculateEndReward(true);

        Experience experience = new Experience(state, action, reward, nextState, true);
        agent.getTrainingData().push(experience);

        agent.trainAgent(experience);
    }

    private boolean endState(Intruder intruder){
        return escaped(intruder) || caught(intruder);
    }

    private boolean escaped(Intruder intruder){
        return playerRepository.getEscapedIntruders().contains(intruder);
    }

    private boolean caught(Intruder intruder){
        return playerRepository.getCaughtIntruders().contains(intruder);
    }

    /*
    * For Guards:
    *   Need a function to train if it catches an intruder
    *
    * For Intruders:
    *   Need to balance the reward for being caught or escaping
    *
    * This loop would be nice with a GUI to set which weights to load
    *
    *
    * */

}
