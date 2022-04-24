package com.nscharrenberg.um.multiagentsurveillance.agents.DQN;

import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle.Angle;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.Collision.Wall;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.Item;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Guard;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Intruder;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Player;
import com.nscharrenberg.um.multiagentsurveillance.headless.repositories.MapRepository;

import java.util.List;

public class DQN_Agent {

    MapRepository mapRepo;
    int channels = 5;
    int rows = 25;
    int cols = 25;
    int WIDTH;
    int HEIGHT;
    List<List<Tile>> moveList;
    int moveNumber;

    public DQN_Agent(int WIDTH, int HEIGHT){
        this.WIDTH = WIDTH;
        this.HEIGHT = HEIGHT;
    }

    public void preformMove(Player player) throws Exception {
        int move = calculateMove(player);
        Angle currentDirection = player.getDirection();
        List<Tile> vision = (List<Tile>) player.getVision();
        switch (move){
            case 0 -> Factory.getPlayerRepository().move(player, currentDirection);
            case 1 -> Factory.getPlayerRepository().move(player, turnRight(currentDirection));
            case 2 -> Factory.getPlayerRepository().move(player, turnLeft(currentDirection));
            default -> dropMaker(player);
        }
        

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


    private int calculateMove(Player player){

        double[][][] input = createTensor(player);

        return 0;
    }

    private double[][][] createTensor(Player player){

        double[][][] out = new double[channels][rows][cols];

        List<Tile> vision = (List<Tile>) player.getVision();
        List<Item> items;
        int xP = player.getTile().getX();
        int yP = player.getTile().getY();
        int VIX, VIY;

        for (Tile tile : vision) {
            VIX = tile.getX() - xP;
            VIY = tile.getY() - yP;

            items = player.getTile().getItems();

            for (Item item : items) {
                if (item instanceof Guard)
                    out[0][VIX + 6][VIY + 6] = 1;
                if (item instanceof Intruder)
                    out[0][VIX + 6][VIY + 6] = -1;
                if (item instanceof Wall)
                    out[1][VIX + 6][VIY + 6] = 1;
                if (item instanceof Item) // Should be sound
                    out[2][VIX + 6][VIY + 6] = 1;
            }

        }

        return out;
    }






}
