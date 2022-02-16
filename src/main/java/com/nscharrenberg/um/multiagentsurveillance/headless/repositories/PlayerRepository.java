package com.nscharrenberg.um.multiagentsurveillance.headless.repositories;

import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IPlayerRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.CollisionException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.InvalidTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.ItemAlreadyOnTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.ItemNotOnTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.*;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class PlayerRepository implements IPlayerRepository {
    private SecureRandom random;
    private List<Intruder> intruders;
    private List<Guard> guards;

    public PlayerRepository() {
        this.intruders = new ArrayList<>();
        this.guards = new ArrayList<>();

        try {
            this.random = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error while generating Random Class");
        }
    }

    @Override
    public void spawn(Player player) {
        if (player instanceof Intruder) {
            spawnIntruder(player);
            return;
        }

        spawnGuard(player);
    }

    private void spawnGuard(Player guard) {
        TileArea guardSpawnArea = Factory.getMapRepository().getGuardSpawnArea();
        spawn(guard, guardSpawnArea);
    }

    private void spawnIntruder(Player intruder) {
        TileArea guardSpawnArea = Factory.getMapRepository().getIntruderSpawnArea();
        spawn(intruder, guardSpawnArea);
    }

    private void spawn(Player player, TileArea playerSpawnArea) {
        HashMap<Integer, HashMap<Integer, Tile>> spawnArea = playerSpawnArea.getRegion();

        boolean tileAssigned = false;

        while (!tileAssigned) {
            int rowIndex = random.nextInt(playerSpawnArea.width());
            HashMap<Integer, Tile> row = spawnArea.get(rowIndex);

            int colIndex = random.nextInt(playerSpawnArea.height());
            Tile tile = row.get(colIndex);

            boolean invalid = false;

            for (Item item : tile.getItems()) {
                if (item instanceof Collision) {
                    invalid = true;
                    break;
                }
            }

            if (!invalid) {
                try {
                    tile.add(player);
                    player.setTile(tile);
                    tileAssigned = true;
                } catch (ItemAlreadyOnTileException e) {
                    System.out.println("Player Already on tile - this shouldn't happen");
                }
            }
        }
    }

    @Override
    public void move(Player player, Angle direction) throws CollisionException, InvalidTileException, ItemNotOnTileException, ItemAlreadyOnTileException {
        Angle currentDirection = player.getDirection();

        // Rotate the player when it's not facing the same direction as it wants to go to.
        if (!currentDirection.equals(direction)) {
            player.setDirection(direction);
            return;
        }

        Tile currentPosition = player.getTile();

        int nextX = player.getTile().getX() + direction.getxIncrement();
        int nextY = player.getTile().getY() + direction.getyIncrement();
        Optional<Tile> nextPositionOpt = Factory.getMapRepository().getBoardAsArea().getByCoordinates(nextX, nextY);

        if (nextPositionOpt.isEmpty()) {
            throw new InvalidTileException(nextX, nextY);
        }

        Tile nextPosition = nextPositionOpt.get();

        Optional<Item> collisionFound = nextPosition.getItems().stream().filter(item -> item instanceof Collision).findFirst();

        if (collisionFound.isPresent()) {
            throw new CollisionException();
        }

        Optional<Item> teleporterFoundOpt = nextPosition.getItems().stream().filter(item -> item instanceof Teleporter).findFirst();

        // remove player from tile
        currentPosition.remove(player);

        // Teleport to destination instead of the source tile
        if (teleporterFoundOpt.isPresent()) {
            Teleporter teleporter = (Teleporter) teleporterFoundOpt.get();

            teleporter.getTile().add(player);
            player.setTile(teleporter.getTile());
            player.setDirection(teleporter.getDirection());

            return;
        }

        // add player to tile
        nextPosition.add(player);
        player.setTile(nextPosition);
    }

    @Override
    public boolean isLegalMove(Player player, Angle direction) {
        Angle currentDirection = player.getDirection();

        // Rotate the player when it's not facing the same direction as it wants to go to.
        if (!currentDirection.equals(direction)) {
            return true;
        }

        int nextX = player.getTile().getX() + direction.getxIncrement();
        int nextY = player.getTile().getY() + direction.getyIncrement();

        Optional<Tile> nextPositionOpt = Factory.getMapRepository().getBoardAsArea().getByCoordinates(nextX, nextY);

        if (nextPositionOpt.isEmpty()) {
            return false;
        }

        Tile nextPosition = nextPositionOpt.get();

        Optional<Item> collisionFound = nextPosition.getItems().stream().filter(item -> item instanceof Collision).findFirst();

        return collisionFound.isEmpty();
    }

    @Override
    public List<Intruder> getIntruders() {
        return intruders;
    }

    @Override
    public void setIntruders(List<Intruder> intruders) {
        this.intruders = intruders;
    }


    public void setGuards(List<Guard> guards) {

    }

    public List<Guard> getGuards() {
        return guards;
    }

}
