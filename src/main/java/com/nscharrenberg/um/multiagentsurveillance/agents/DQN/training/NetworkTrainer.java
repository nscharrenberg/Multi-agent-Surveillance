package com.nscharrenberg.um.multiagentsurveillance.agents.DQN.training;

import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.DQN_Agent;
import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.BoardNotBuildException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.InvalidTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle.Angle;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Player;

public class NetworkTrainer {

    private int velocity = 1;
    private double[] error;
    private double[] output;
    private final int batchSize = 256;
    private final double gamma = 0.999;
    private final double epsStart = 1;
    private final double epsEnd = 0.1;
    private final double epsDecay = 0.001;
    private final int targetUpdate = 10;
    private final int memorySize = 10000;
    private final double lr = 0.001;
    private final int numEpisodes = 1000;
    private MoveHistory moveHistory;
    private DQN_Agent agent;


    public NetworkTrainer(DQN_Agent agent){
        moveHistory = new MoveHistory(memorySize);
        this.agent = agent;
    }

    public void runTraining() throws Exception {
        Angle action;
        double reward;
        double[][][] state = agent.getState();
        double[][][] nextState;
        Experience[] samples;

        for (int ts = 0; ts < 1; ts++) {
            action = agent.decide();
            reward = agent.preformMove(action);
            nextState = agent.getState();
            moveHistory.push(new Experience(state,action,reward,nextState));
            state = nextState;

            if (moveHistory.hasBatch(batchSize)){
                samples = moveHistory.randomSample(batchSize);


            }
        }
    }



    public double[] checkMoves(double[] output, Player player) throws InvalidTileException, BoardNotBuildException {
        error = new double[output.length];
        this.output = output;

        Angle direction = player.getDirection();

        int x = player.getTile().getX();
        int y = player.getTile().getY();

        // what way is up
        switch (direction){
            case UP -> verticalCheck(x, y, true);
            case DOWN -> verticalCheck(x, y, false);
            case LEFT -> horizontalCheck(x, y, false);
            case RIGHT -> horizontalCheck(x, y, true);
        }

        return error;
    }

    private double collisionError(Tile selected){
        if (selected.isCollision())
            return -output[0];
        return  0;
    }

    private void verticalCheck(int x, int y, boolean increasing) throws InvalidTileException, BoardNotBuildException {

        // seriously what way is up lol
        int displacement;
        if (increasing)
            displacement = velocity;
        else displacement = -velocity;

        Tile selected;
        selected = Factory.getMapRepository().findTileByCoordinates(x,y+displacement);
        error[0] = collisionError(selected);

        selected = Factory.getMapRepository().findTileByCoordinates(x-displacement,y);
        error[1] = collisionError(selected);

        selected = Factory.getMapRepository().findTileByCoordinates(x+displacement,y);
        error[2] = collisionError(selected);
    }

    private void horizontalCheck(int x, int y, boolean increasing) throws InvalidTileException, BoardNotBuildException {
        int displacement;
        if (increasing)
            displacement = velocity;
        else displacement = -velocity;

        Tile selected;
        selected = Factory.getMapRepository().findTileByCoordinates(x,y-displacement);
        error[0] = collisionError(selected);

        selected = Factory.getMapRepository().findTileByCoordinates(x-displacement,y);
        error[1] = collisionError(selected);

        selected = Factory.getMapRepository().findTileByCoordinates(x+displacement,y);
        error[2] = collisionError(selected);
    }


}
