package com.nscharrenberg.um.multiagentsurveillance.agents.shared;

import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.pathfinding.IPathFinding;
import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IGameRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IMapRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IPlayerRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.BoardNotBuildException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.InvalidTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.ItemNotOnTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.Collision.Wall;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.Item;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.Teleporter;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Area;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.ShadowTile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.TileArea;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Marker;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.MarkerSmell;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Guard;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Intruder;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Player;

import java.awt.*;
import java.lang.annotation.Target;
import java.util.*;
import java.util.List;

public abstract class Agent {
    protected Player player;
    protected Area<Tile> knowledge;
    protected Area<Tile> vision;
    protected Queue<Action> plannedMoves;

    protected final IMapRepository mapRepository;
    protected final IGameRepository gameRepository;
    protected final IPlayerRepository playerRepository;

    protected int deadEndMarkers;
    protected int targetMarkers;
    protected int teleporterMarkers;
    protected int intruderSpottedMarkers;
    protected int guardSpottedMarkers;
    protected int shadedMarkers;

    public Agent(Player player) {
        this.player = player;
        if (player instanceof Guard) {
            deadEndMarkers = 5;
            teleporterMarkers = 5;
            intruderSpottedMarkers = 5;
            shadedMarkers = 5;
        }

        else if (player instanceof Intruder) {
            deadEndMarkers = 5;
            targetMarkers = 5;
            teleporterMarkers = 5;
            guardSpottedMarkers = 5;
            shadedMarkers = 5;
        }

        this.knowledge = new TileArea();
        this.vision = new TileArea();
        this.plannedMoves = new PriorityQueue<>();

        this.mapRepository = Factory.getMapRepository();
        this.playerRepository = Factory.getPlayerRepository();
        this.gameRepository = Factory.getGameRepository();
    }

    public Agent(Player player, IMapRepository mapRepository, IGameRepository gameRepository, IPlayerRepository playerRepository) {
        this.player = player;
        if (player instanceof Guard) {
            deadEndMarkers = 5;
            teleporterMarkers = 5;
            intruderSpottedMarkers = 5;
            shadedMarkers = 5;
        }

        else if (player instanceof Intruder) {
            deadEndMarkers = 5;
            targetMarkers = 5;
            teleporterMarkers = 5;
            guardSpottedMarkers = 5;
            shadedMarkers = 5;
        }

        this.knowledge = new TileArea();
        this.vision = new TileArea();
        this.plannedMoves = new PriorityQueue<>();

        this.mapRepository = mapRepository;
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
    }

    public Agent(Player player, Area<Tile> knowledge, Queue<Action> plannedMoves, IMapRepository mapRepository, IGameRepository gameRepository, IPlayerRepository playerRepository) {
        this.player = player;
        if (player instanceof Guard) {
            deadEndMarkers = 5;
            teleporterMarkers = 5;
            intruderSpottedMarkers = 5;
            shadedMarkers = 5;
        }

        else if (player instanceof Intruder) {
            deadEndMarkers = 5;
            targetMarkers = 5;
            teleporterMarkers = 5;
            guardSpottedMarkers = 5;
            shadedMarkers = 5;
        }

        this.knowledge = knowledge;
        this.vision = new TileArea();
        this.plannedMoves = plannedMoves;

        this.mapRepository = mapRepository;
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
    }

    public Area<Tile> getKnowledge() {
        return knowledge;
    }

    public void setKnowledge(Area<Tile> knowledge) {
        this.knowledge = knowledge;
    }

    public Queue<Action> getPlannedMoves() {
        return plannedMoves;
    }

    public void setPlannedMoves(Queue<Action> plannedMoves) {
        this.plannedMoves = plannedMoves;
    }

    public void addKnowledge(Tile tile) {
        knowledge.add(tile, false);
    }

    public void addMove(Action action) {
        plannedMoves.add(action);
    }

    public void addKnowledge(HashMap<Integer, HashMap<Integer, Tile>> tiles) {
        for (Map.Entry<Integer, HashMap<Integer, Tile>> rowEntry : tiles.entrySet()) {
            for (Map.Entry<Integer, Tile> colEntry : rowEntry.getValue().entrySet()) {
                knowledge.add(colEntry.getValue(), false);
            }
        }
    }

    public void addKnowledge(List<Tile> tiles) {
        for (Tile tile : tiles) {
            knowledge.add(tile, false);
        }
    }

    public void addKnowledge(Tile... tiles) {
        for (Tile tile : tiles) {
            knowledge.add(tile, false);
        }
    }

    public Action move() {
        return plannedMoves.poll();
    }

    public IMapRepository getMapRepository() {
        return mapRepository;
    }

    public IGameRepository gameRepository() {
        return gameRepository;
    }

    public IPlayerRepository getPlayerRepository() {
        return playerRepository;
    }

    public Player getPlayer() {
        return player;
    }

    public IPathFinding getPathFindingAlgorithm(){
        return null;
    }

    public abstract void execute(Action action);

    public abstract Action decide() throws Exception;

    public void execute() throws Exception {
        execute(decide());
    }

    public Action markerCheck() throws InvalidTileException, BoardNotBuildException {
        HashMap<Integer, HashMap<Integer, Tile>> vision = player.getVision().getRegion();

        for (Map.Entry<Integer, HashMap<Integer, Tile>> rowEntry : vision.entrySet()) {
            for (Map.Entry<Integer, Tile> colEntry : rowEntry.getValue().entrySet()) {
                if (player instanceof Guard) {
                    if (colEntry.getValue() instanceof ShadowTile && getShadedMarkers() > 0) {
                        if (!lookForSameMarker(vision, Marker.MarkerType.SHADED, player)) {
                            if (!lookForMarkerPlacedByPlayer(player, Marker.MarkerType.SHADED)) {
                                return Action.PLACE_MARKER_SHADED;
                            }
                        }
                    }
                    for (Item item : colEntry.getValue().getItems()) {
                        if (item instanceof Intruder && getIntruderSpottedMarkers() > 0) {
                            if (!lookForSameMarker(vision, Marker.MarkerType.INTRUDER_SPOTTED, player)) {
                                if (!lookForMarkerPlacedByPlayer(player, Marker.MarkerType.INTRUDER_SPOTTED)) {
                                    return Action.PLACE_MARKER_INTRUDERSPOTTED;
                                }
                            }
                        }
                        else if (item instanceof Teleporter && getTeleporterMarkers() > 0) {
                            if (!lookForSameMarker(vision, Marker.MarkerType.TELEPORTER, player)) {
                                if (!lookForMarkerPlacedByPlayer(player, Marker.MarkerType.TELEPORTER)) {
                                    return Action.PLACE_MARKER_TELEPORTER;
                                }
                            }
                        }
                        else if (item instanceof Wall && getDeadEndMarkers() > 0) {
                            if (!lookForSameMarker(vision, Marker.MarkerType.DEAD_END, player)) {
                                if (!lookForMarkerPlacedByPlayer(player, Marker.MarkerType.DEAD_END)) {
                                    if (checkForDeadEnd(player, vision, colEntry.getValue())) {
                                        return Action.PLACE_MARKER_DEADEND;
                                    }
                                }
                            }
                        }
                    }
                }

                else if (player instanceof Intruder) {
                    if (colEntry.getValue() instanceof ShadowTile && getShadedMarkers() > 0) {
                        if (!lookForSameMarker(vision, Marker.MarkerType.SHADED, player)) {
                            if (!lookForMarkerPlacedByPlayer(player, Marker.MarkerType.SHADED)) {
                                return Action.PLACE_MARKER_SHADED;
                            }
                        }
                    }
                    else if ((mapRepository.getTargetArea().within(colEntry.getValue().getX(), colEntry.getValue().getY())) && getTargetMarkers() > 0) {
                        if (!lookForSameMarker(vision, Marker.MarkerType.TARGET, player)) {
                            if (!lookForMarkerPlacedByPlayer(player, Marker.MarkerType.TARGET)) {
                                return Action.PLACE_MARKER_TARGET;
                            }
                        }
                    }
                    for (Item item : colEntry.getValue().getItems()) {
                        if (item instanceof Guard && getGuardSpottedMarkers() > 0) {
                            if (!lookForSameMarker(vision, Marker.MarkerType.GUARD_SPOTTED, player)) {
                                if (!lookForMarkerPlacedByPlayer(player, Marker.MarkerType.GUARD_SPOTTED)) {
                                    return Action.PLACE_MARKER_GUARDSPOTTED;
                                }
                            }
                        }
                        else if (item instanceof Teleporter && getTeleporterMarkers() > 0) {
                            if (!lookForSameMarker(vision, Marker.MarkerType.TELEPORTER, player)) {
                                if (!lookForMarkerPlacedByPlayer(player, Marker.MarkerType.TELEPORTER)) {
                                    return Action.PLACE_MARKER_TELEPORTER;
                                }
                            }
                        }
                        else if (item instanceof Wall && getDeadEndMarkers() > 0) {
                            if (!lookForSameMarker(vision, Marker.MarkerType.DEAD_END, player)) {
                                if (!lookForMarkerPlacedByPlayer(player, Marker.MarkerType.DEAD_END)) {
                                    if (checkForDeadEnd(player, vision, colEntry.getValue())) {
                                        return Action.PLACE_MARKER_DEADEND;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public boolean lookForSameMarker(HashMap<Integer, HashMap<Integer, Tile>> vision, Marker.MarkerType typeOfMarker, Player player) {
        for (Map.Entry<Integer, HashMap<Integer, Tile>> rowEntry : vision.entrySet()) {
            for (Map.Entry<Integer, Tile> colEntry : rowEntry.getValue().entrySet()) {
                for (Item item : colEntry.getValue().getItems()) {
                    if ((item instanceof MarkerSmell) && (((MarkerSmell) item).getType() == typeOfMarker) && (((MarkerSmell) item).getPlayer().getClass().equals(player.getClass()))) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean lookForMarkerPlacedByPlayer(Player player, Marker.MarkerType typeOfMarker) {
        ArrayList<MarkerSmell> listOfPlacedMarkers = mapRepository.getListOfPlacedMarkers();

        if (listOfPlacedMarkers == null) {
            return false;
        }

        for (int i = 0; i < listOfPlacedMarkers.size(); i++) {
            if ((listOfPlacedMarkers.get(i).getType() == typeOfMarker) && (listOfPlacedMarkers.get(i).getPlayer() == player)) {
                return true;
            }
        }
        return false;
    }


    public boolean checkForDeadEnd(Player player, HashMap<Integer, HashMap<Integer, Tile>> vision, Tile wallTile) throws InvalidTileException, BoardNotBuildException {
        int wallX = wallTile.getX();
        int wallY = wallTile.getY();
        int currentX = player.getTile().getX();
        int currentY = player.getTile().getY();
        Point[] deadEndTilesTopLeft = new Point[]{new Point(wallX, wallY-1), new Point(wallX, wallY-2), new Point(wallX, wallY-3), new Point(wallX, wallY-4), new Point(wallX-1, wallY), new Point(wallX-2, wallY), new Point(wallX-3, wallY), new Point(wallX-4, wallY)};
        Point[] deadEndTilesTopRight = new Point[]{new Point(wallX, wallY-1), new Point(wallX, wallY-2), new Point(wallX, wallY-3), new Point(wallX, wallY-4), new Point(wallX+1, wallY), new Point(wallX+2, wallY), new Point(wallX+3, wallY), new Point(wallX+4, wallY)};
        Point[] deadEndTilesBottomLeft = new Point[]{new Point(wallX, wallY+1), new Point(wallX, wallY+2), new Point(wallX, wallY+3), new Point(wallX, wallY+4), new Point(wallX-1, wallY), new Point(wallX-2, wallY), new Point(wallX-3, wallY), new Point(wallX-4, wallY)};
        Point[] deadEndTilesBottomRight = new Point[]{new Point(wallX, wallY+1), new Point(wallX, wallY+2), new Point(wallX, wallY+3), new Point(wallX, wallY+4), new Point(wallX+1, wallY), new Point(wallX+2, wallY), new Point(wallX+3, wallY), new Point(wallX+4, wallY)};

        int differenceFromWallToCurrentX = currentX - wallX;
        int differenceFromWallToCurrentY = currentY - wallY;

        if (differenceFromWallToCurrentX > 0 && differenceFromWallToCurrentY >= 0) {
            for (Point coordinate : deadEndTilesBottomLeft) {
                boolean tileFound = false;
                if (coordinate.getX() >= 0 && coordinate.getY() >= 0 && coordinate.getX() <= gameRepository.getWidth() && coordinate.getY() <= gameRepository.getHeight()) {
                    Tile currentTile = mapRepository.findTileByCoordinates((int) coordinate.getX(), (int) coordinate.getY());
                    for (Map.Entry<Integer, HashMap<Integer, Tile>> rowEntry : vision.entrySet()) {
                        for (Map.Entry<Integer, Tile> colEntry : rowEntry.getValue().entrySet()) {
                            if (colEntry.getValue() == currentTile) {
                                tileFound = true;
                                break;
                            }
                        }
                    }
                    for (Map.Entry<Integer, HashMap<Integer, Tile>> rowEntry : getKnowledge().getRegion().entrySet()) {
                        for (Map.Entry<Integer, Tile> colEntry : rowEntry.getValue().entrySet()) {
                            if (colEntry.getValue() == currentTile) {
                                tileFound = true;
                                break;
                            }
                        }
                    }

                    boolean wallFound = false;
                    if (tileFound) {
                        for (Item item : currentTile.getItems()) {
                            if (item instanceof Wall) {
                                wallFound = true;
                                break;
                            }
                        }
                        if (wallFound) {
                            continue;
                        } else {return false;}
                    } else {return false;}
                }
            }
        }

        else if (differenceFromWallToCurrentX <= 0 && differenceFromWallToCurrentY > 0) {
            for (Point coordinate : deadEndTilesBottomLeft) {
                boolean tileFound = false;
                if (coordinate.getX() >= 0 && coordinate.getY() >= 0 && coordinate.getX() <= gameRepository.getWidth() && coordinate.getY() <= gameRepository.getHeight()) {
                    Tile currentTile = mapRepository.findTileByCoordinates((int) coordinate.getX(), (int) coordinate.getY());
                    for (Map.Entry<Integer, HashMap<Integer, Tile>> rowEntry : vision.entrySet()) {
                        for (Map.Entry<Integer, Tile> colEntry : rowEntry.getValue().entrySet()) {
                            if (colEntry.getValue() == currentTile) {
                                tileFound = true;
                                break;
                            }
                        }
                    }
                    for (Map.Entry<Integer, HashMap<Integer, Tile>> rowEntry : getKnowledge().getRegion().entrySet()) {
                        for (Map.Entry<Integer, Tile> colEntry : rowEntry.getValue().entrySet()) {
                            if (colEntry.getValue() == currentTile) {
                                tileFound = true;
                                break;
                            }
                        }
                    }

                    boolean wallFound = false;
                    if (tileFound) {
                        for (Item item : currentTile.getItems()) {
                            if (item instanceof Wall) {
                                wallFound = true;
                                break;
                            }
                        }
                        if (wallFound) {
                            continue;
                        } else {return false;}
                    } else {return false;}
                }
            }
        }

        else if (differenceFromWallToCurrentX >= 0 && differenceFromWallToCurrentY < 0) {
            for (Point coordinate : deadEndTilesTopRight) {
                boolean tileFound = false;
                if (coordinate.getX() >= 0 && coordinate.getY() >= 0 && coordinate.getX() <= gameRepository.getWidth() && coordinate.getY() <= gameRepository.getHeight()) {
                    Tile currentTile = mapRepository.findTileByCoordinates((int) coordinate.getX(), (int) coordinate.getY());
                    for (Map.Entry<Integer, HashMap<Integer, Tile>> rowEntry : vision.entrySet()) {
                        for (Map.Entry<Integer, Tile> colEntry : rowEntry.getValue().entrySet()) {
                            if (colEntry.getValue() == currentTile) {
                                tileFound = true;
                                break;
                            }
                        }
                    }
                    for (Map.Entry<Integer, HashMap<Integer, Tile>> rowEntry : getKnowledge().getRegion().entrySet()) {
                        for (Map.Entry<Integer, Tile> colEntry : rowEntry.getValue().entrySet()) {
                            if (colEntry.getValue() == currentTile) {
                                tileFound = true;
                                break;
                            }
                        }
                    }

                    boolean wallFound = false;
                    if (tileFound) {
                        for (Item item : currentTile.getItems()) {
                            if (item instanceof Wall) {
                                wallFound = true;
                                break;
                            }
                        }
                        if (wallFound) {
                            continue;
                        } else {return false;}
                    } else {return false;}
                }
            }
        }

        else if (differenceFromWallToCurrentX < 0 && differenceFromWallToCurrentY <= 0) {
            for (Point coordinate : deadEndTilesTopLeft) {
                boolean tileFound = false;
                if (coordinate.getX() >= 0 && coordinate.getY() >= 0 && coordinate.getX() <= gameRepository.getWidth() && coordinate.getY() <= gameRepository.getHeight()) {
                    Tile currentTile = mapRepository.findTileByCoordinates((int) coordinate.getX(), (int) coordinate.getY());
                    for (Map.Entry<Integer, HashMap<Integer, Tile>> rowEntry : vision.entrySet()) {
                        for (Map.Entry<Integer, Tile> colEntry : rowEntry.getValue().entrySet()) {
                            if (colEntry.getValue() == currentTile) {
                                tileFound = true;
                                break;
                            }
                        }
                    }
                    for (Map.Entry<Integer, HashMap<Integer, Tile>> rowEntry : getKnowledge().getRegion().entrySet()) {
                        for (Map.Entry<Integer, Tile> colEntry : rowEntry.getValue().entrySet()) {
                            if (colEntry.getValue() == currentTile) {
                                tileFound = true;
                                break;
                            }
                        }
                    }

                    boolean wallFound = false;
                    if (tileFound) {
                        for (Item item : currentTile.getItems()) {
                            if (item instanceof Wall) {
                                wallFound = true;
                                break;
                            }
                        }
                        if (wallFound) {
                            continue;
                        } else {return false;}
                    } else {return false;}
                }
            }
        }

        return true;
    }

    public void setPlayer(Player player){
        this.player = player;
    }

    public int getDeadEndMarkers() {
        return deadEndMarkers;
    }

    public void decrementDeadEndMarkers() {
        deadEndMarkers--;
    }

    public int getTargetMarkers() {
        return targetMarkers;
    }

    public void decrementTargetMarkers() {
        targetMarkers--;
    }

    public int getTeleporterMarkers() {
        return teleporterMarkers;
    }

    public void decrementTeleporterMarkers() {
        teleporterMarkers--;
    }

    public int getIntruderSpottedMarkers() {
        return intruderSpottedMarkers;
    }

    public void decrementIntruderSpottedMarkers() {
        intruderSpottedMarkers--;
    }

    public int getGuardSpottedMarkers() {
        return guardSpottedMarkers;
    }

    public void decrementGuardSpottedMarkers() {
        guardSpottedMarkers--;
    }

    public int getShadedMarkers() {
        return shadedMarkers;
    }

    public void decrementShadedMarkers() {
        shadedMarkers--;
    }

}
