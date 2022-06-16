package com.nscharrenberg.um.multiagentsurveillance.agents.DQN;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Intruder;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Player;

import java.util.Random;

public class DQN_Agent_Util {

    private static final Random random = new Random();
    private static final int xOffset = DQN_Params.xOffset.valueInt;
    private static final int yOffset = DQN_Params.yOffset.valueInt;


    public static int argmax(double[] input){
        return argmax(input, 0);
    }


    public static int argmax(double[] input, int start){
        int maxInd = start;
        for (int i = start; i < input.length; i++) maxInd = input[i] > input[maxInd] ?  i : maxInd;
        return maxInd;
    }


    public static void normalise(double[] input) {
        int ind = argmax(input);

        for (double value : input)
            value /= input[ind];
    }


    public static Action actionPrediction(int prediction) throws Exception {
        switch (prediction){
            case 0 -> {
                return Action.UP;
            }
            case 1 -> {
                return Action.DOWN;
            }
            case 2 -> {
                return Action.LEFT;
            }
            case 3 -> {
                return Action.RIGHT;
            }
            default -> throw new Exception("Illegal Prediction");
        }
    }


    public static int predictionAction(Action action) throws Exception {
        switch (action) {
            case UP -> {
                return 0;
            }
            case DOWN -> {
                return 1;
            }
            case LEFT -> {
                return 2;
            }
            case RIGHT -> {
                return 3;
            }
            default -> throw new Exception("Illegal Action: " + action);
        }
    }


    public static Action turnRight(Action playerDirection) throws Exception {
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


    public static Action turnLeft(Action playerDirection) throws Exception {
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


    public static Action turnAround(Action playerDirection) throws Exception {
        if (playerDirection.equals(Action.UP))
            return Action.DOWN;
        if (playerDirection.equals(Action.DOWN))
            return Action.UP;
        if (playerDirection.equals(Action.LEFT))
            return Action.RIGHT;
        if (playerDirection.equals(Action.RIGHT))
            return Action.LEFT;
        else
            throw new Exception("Player has no direction");
    }



    public static double soundProximity(double[][][] state){

        int x,y;
        double proximity = 0;
        Action[] checklist = {Action.UP, Action.DOWN, Action.LEFT, Action.RIGHT};

        for (Action check : checklist) {
            x = check.getxIncrement() + xOffset;
            y = check.getyIncrement() + yOffset;

            proximity += state[2][x][y];
        }

        return proximity;
    }


    public static double visionProximity(double[][][] state) {
        int dx, dy;
        double proximity = 0;
        int length = DQN_Params.values().length;

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


    private static int actionIndex(Action action, Player player) throws Exception {
        Action direction = player.getDirection();

        if (action.equals(direction))
            return 0;
        if (action.equals(turnLeft(direction)))
            return 1;
        if (action.equals(turnRight(direction)))
            return 2;
        if (action.equals(turnAround(direction)))
            return 3;

        throw new Exception("");
    }


    public static int xDirectionTarget(Action direction){
        if (direction.equals(Action.UP) || direction.equals(Action.DOWN))
            return xOffset;
        if (direction.equals(Action.LEFT))
            return 0;
        return 2 * xOffset;
    }

    public static int yDirectionTarget(Action direction){
        if (direction.equals(Action.LEFT) || direction.equals(Action.RIGHT))
            return yOffset;
        if (direction.equals(Action.UP))
            return 0;
        return 2 * yOffset;
    }


    public static boolean endState(Intruder intruder){
        return escaped(intruder) || caught(intruder);
    }


    public static boolean escaped(Intruder intruder){
        return intruder.getAgent().getPlayerRepository().getEscapedIntruders().contains(intruder);
    }


    public static boolean caught(Intruder intruder){
        return intruder.getAgent().getPlayerRepository().getCaughtIntruders().contains(intruder);
    }

}
