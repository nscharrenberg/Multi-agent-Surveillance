package com.nscharrenberg.um.multiagentsurveillance.gui.javafx.controllers;

import com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi.Frontier;
import com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi.YamauchiAgent;
import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.*;
import javafx.application.Application ;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.*;

public class GameBoardGUI extends Application {
    private final int FRAME_WIDTH = (int) (Screen.getPrimary().getBounds().getWidth() * 0.9);
    private final int FRAME_HEIGHT = (int) (Screen.getPrimary().getBounds().getHeight() * 0.9);
    private final int GRID_WIDTH;
    private final int GRID_HEIGHT;
    private final int GRID_SQUARE_SIZE;
    private final double GSSD;
    private Scene scene;
    private Stage stage;
    private ArrayList<GridIndex> oldPlayerIndices;

    private Double[] faceUP_Intruder;
    private Double[] faceDOWN_Intruder;
    private Double[] faceLEFT_Intruder;
    private Double[] faceRIGHT_Intruder;

    private Double[] faceUP_Guard;
    private Double[] faceDOWN_Guard;
    private Double[] faceLEFT_Guard;
    private Double[] faceRIGHT_Guard;

    private List<Guard> guards;
    private HashMap<Integer, HashMap<Integer, StackPane>> gridPanes = new HashMap<>();


    private ArrayList<TileComponents> components = new ArrayList<TileComponents>(Arrays.asList(TileComponents.SHADED, TileComponents.WALL, TileComponents.DOOR, TileComponents.WINDOW, TileComponents.TELEPORTER,
            TileComponents.GUARD, TileComponents.INTRUDER));

    public enum TileComponents {
        DOOR,
        SHADED,
        GUARD,
        INTRUDER,
        TELEPORTER,
        WALL,
        WINDOW;
    }

    public GameBoardGUI(){
        GRID_WIDTH = Factory.getGameRepository().getWidth();
        GRID_HEIGHT = Factory.getGameRepository().getHeight();

        System.out.println("Grid width " + GRID_WIDTH);
        System.out.println("Grid height " + GRID_HEIGHT);

        if (FRAME_HEIGHT/GRID_HEIGHT < FRAME_WIDTH/GRID_WIDTH)
            GRID_SQUARE_SIZE = FRAME_HEIGHT/GRID_HEIGHT - 1;
        else
            GRID_SQUARE_SIZE = FRAME_WIDTH/GRID_WIDTH - 1;

        GSSD = GRID_SQUARE_SIZE;

        createPolygons();
    }

    @Override
    public void start(Stage st) {

        TileArea board = Factory.getMapRepository().getBoardAsArea();
        guards = Factory.getPlayerRepository().getGuards();

        GridPane grid = buildEmptyBoard(board);
        Group group = new Group(grid);
        BorderPane borderPane = new BorderPane();
        borderPane.setLeft(grid);
        borderPane.setRight(createLegend());

        oldPlayerIndices = new ArrayList<>();

        scene = new Scene(borderPane, FRAME_WIDTH, FRAME_HEIGHT);

        this.stage = st;
        stage.setMaximized(true);

        stage.setTitle(" Multi-Agent Surveillance ");
        stage.setScene(scene);

        stage.show();
    }

    private GridPane createLegend(){
        GridPane legend = new GridPane();

        int LEGEND_WIDTH = FRAME_WIDTH - (GRID_WIDTH * GRID_SQUARE_SIZE);
        legend.setMinWidth(LEGEND_WIDTH);
        legend.setMaxWidth(LEGEND_WIDTH);
        legend.setPadding(new Insets(10, 30, 30, 30));

        Rectangle item = new Rectangle(LEGEND_WIDTH,100);
        item.setStroke(Color.YELLOW);
        item.setFill(Color.WHITE);

        Label lb = new Label("Legend");
        lb.setFont(new Font("Arial", 30));


        BorderPane wallPane = new BorderPane();
        wallPane.setLeft(wallRectangle(new Rectangle((int)LEGEND_WIDTH*0.05,(int)LEGEND_WIDTH*0.05)));
        wallPane.setRight(new Label("= Wall Tile"));
        BorderPane windowPane = new BorderPane();
        windowPane.setLeft(windowRectangle(new Rectangle((int)LEGEND_WIDTH*0.05,(int)LEGEND_WIDTH*0.05)));
        windowPane.setRight(new Label("= Window Tile"));
        BorderPane doorPane = new BorderPane();
        doorPane.setLeft(doorRectangle(new Rectangle((int)LEGEND_WIDTH*0.05,(int)LEGEND_WIDTH*0.05)));
        doorPane.setRight(new Label("= Door Tile"));

        BorderPane guardPane = new BorderPane();
        Polygon polygon = new Polygon();
        polygon.getPoints().addAll(createGuardLegend(LEGEND_WIDTH*0.05));
        guardPane.setLeft(polygon);
        guardPane.setRight(new Label("= Guard"));

        Polygon polygon1 = new Polygon();
        BorderPane intruderPane = new BorderPane();
        polygon1.setFill(Color.BLUE);
        polygon1.getPoints().addAll(createIntruderLegend(LEGEND_WIDTH*0.05));
        intruderPane.setLeft(polygon1);
        intruderPane.setRight(new Label(" = Intruder"));

        legend.add(lb,0, 0);
        legend.setVgap(20);
        legend.add(wallPane, 0,1);
        legend.setVgap(5);
        legend.add(windowPane, 0,2);
        legend.setVgap(5);
        legend.add(doorPane, 0,3);
        legend.setVgap(5);
        legend.add(guardPane, 0,4);
        legend.setVgap(5);
        legend.add(intruderPane, 0,5);

        return legend;
    }

    public void updateGUI(){
        try {
            Platform.runLater(() -> {

                StackPane pane, vkPane;
                Tile tile, vkTile;
                Area<Tile> combinedVisions = new TileArea();
                Area<Tile> combinedKnowledge = new TileArea();

                for (GridIndex  gridIndex : oldPlayerIndices){
                    pane = gridPanes.get(gridIndex.x).get(gridIndex.y);
                    pane.getChildren().remove(1);
                }

                oldPlayerIndices = new ArrayList<>();

                for (Player player : guards) {
                    tile = player.getTile();
                    pane = gridPanes.get(tile.getX()).get(tile.getY());

                    combinedVisions = combinedVisions.merge(player.getVision());
                    combinedKnowledge = combinedKnowledge.merge(player.getAgent().getKnowledge());

                    for (Map.Entry<Integer, HashMap<Integer, Tile>> rowEntry : combinedKnowledge.getRegion().entrySet()) {
                        for (Map.Entry<Integer, Tile> colEntry : rowEntry.getValue().entrySet()) {
                            if (combinedVisions.getRegion().containsKey(rowEntry.getKey())) {
                                if (combinedVisions.getRegion().get(rowEntry.getKey()).containsKey(colEntry.getKey())) {
                                    continue;
                                }
                            }
                            vkTile = getTileOnIndex(rowEntry.getKey(), colEntry.getKey());
                            vkPane = gridPanes.get(rowEntry.getKey()).get(colEntry.getKey());
                            if (vkPane != null)
                                vkPane.getChildren().set(0,createTile(vkTile, false, true));
                        }
                    }

                    for (Map.Entry<Integer, HashMap<Integer, Tile>> rowEntry : combinedVisions.getRegion().entrySet()) {
                        for (Map.Entry<Integer, Tile> colEntry : rowEntry.getValue().entrySet()) {
                            vkTile = getTileOnIndex(rowEntry.getKey(), colEntry.getKey());
                            vkPane = gridPanes.get(rowEntry.getKey()).get(colEntry.getKey());
                            if (vkPane != null)
                                vkPane.getChildren().set(0,createTile(vkTile, true, true));
                        }
                    }

                    oldPlayerIndices.add(new GridIndex(tile.getX(), tile.getY()));
                    pane.getChildren().add(createGuard(player.getDirection()));
                }
            });

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private Tile getTileOnIndex(int x, int y){
        return Factory.getMapRepository().getBoardAsArea().getByCoordinates(x,y).get();
    }

    public GridPane buildEmptyBoard(TileArea board) {
        GridPane gameGrid = new GridPane();
        Optional<Tile> optTile;
        Tile tile;
        Rectangle rectangle;
        StackPane stackPane;


        for (int i = 0; i < GRID_WIDTH; i++) {
            for (int j = 0; j < GRID_HEIGHT; j++) {

                rectangle = new Rectangle(GRID_SQUARE_SIZE, GRID_SQUARE_SIZE);
                rectangle.setStroke(Color.BLACK);

                optTile = board.getByCoordinates(i,j);

                tile = optTile.get();
                stackPane = createTile(tile);

                gameGrid.add(stackPane, i, j);

                if (!gridPanes.containsKey(i))
                    gridPanes.put(i, new HashMap<>());

                gridPanes.get(i).put(j, stackPane);
            }
        }

        return gameGrid;
    }

    /**
     *
     *
     * @param board
     * @return
     */
    public GridPane createBoard(TileArea board) {

        GridPane gameGrid = new GridPane();
        Optional<Tile> optTile;
        Tile tile;
        Rectangle rectangle;
        StackPane stackPane = new StackPane();


        for (int i = 0; i < GRID_WIDTH; i++) {
            for (int j = 0; j < GRID_HEIGHT; j++) {

                rectangle = new Rectangle(GRID_SQUARE_SIZE, GRID_SQUARE_SIZE);
                rectangle.setStroke(Color.BLACK);

                optTile = board.getByCoordinates(i,j);
                if (optTile.isEmpty())
                    System.out.println("Tile is Empty");

                else{
                    tile = optTile.get();
                    stackPane = createTile(tile);
                }


                //gameGrid.add(new StackPane(tile, text), i, j);
                gameGrid.add(stackPane, i, j);
            }
        }

        Area<Tile> combinedVisions = new TileArea();
        Area<Tile> combinedKnowledge = new TileArea();

        for (Player player : guards) {
            combinedVisions = combinedVisions.merge(player.getVision());
            combinedKnowledge = combinedKnowledge.merge(player.getAgent().getKnowledge());

            if (player.getAgent() instanceof YamauchiAgent) {
                if (((YamauchiAgent) player.getAgent()).getChosenFrontier() != null) {
                    Rectangle r = new Rectangle(GRID_SQUARE_SIZE, GRID_SQUARE_SIZE);
                    r.setFill(Color.GREEN);

                    Frontier chosenFrontier = ((YamauchiAgent) player.getAgent()).getChosenFrontier();

                    if (chosenFrontier.getQueueNode() != null) {
                        gameGrid.add(r, chosenFrontier.getQueueNode().getTile().getX(), chosenFrontier.getQueueNode().getTile().getY());
                    } else {
                        System.out.println("No QueueNode for Frontier Found");
                    }
                } else {
                    System.out.println("No chosen frontier");
                }
            }
        }

        for (Map.Entry<Integer, HashMap<Integer, Tile>> rowEntry : combinedVisions.getRegion().entrySet()) {
            for (Map.Entry<Integer, Tile> colEntry : rowEntry.getValue().entrySet()) {
                StackPane visionPane = createTile(colEntry.getValue(), true, false);
                gameGrid.add(visionPane, rowEntry.getKey(), colEntry.getKey());
            }
        }

        for (Map.Entry<Integer, HashMap<Integer, Tile>> rowEntry : combinedKnowledge.getRegion().entrySet()) {
            for (Map.Entry<Integer, Tile> colEntry : rowEntry.getValue().entrySet()) {
                if (combinedVisions.getRegion().containsKey(rowEntry.getKey())) {
                    if (combinedVisions.getRegion().get(rowEntry.getKey()).containsKey(colEntry.getKey())) {
                        continue;
                    }
                }

                StackPane visionPane = createTile(colEntry.getValue(), false, true);
                gameGrid.add(visionPane, rowEntry.getKey(), colEntry.getKey());
            }
        }


        //gameGrid.setPadding(new Insets(10, 10, 10, 10));
        return gameGrid;
    }

    private StackPane createTile(Tile tile) {
        return createTile(tile, false, false);
    }

    private StackPane createTile(Tile tile, boolean isVision, boolean isKnowledge){
        Rectangle rectangle = new Rectangle(GRID_SQUARE_SIZE, GRID_SQUARE_SIZE);
        Polygon polygon = null;
        Player player;

        if (tile instanceof ShadowTile)
            rectangle.setFill(Color.GREY);
        else
            rectangle.setFill(Color.SILVER);

        ArrayList<Item> orderedList = orderList((ArrayList<Item>) tile.getItems());

        if (isVision) {
            rectangle.setStroke(Color.YELLOW);
            rectangle.setOpacity(.6);
        } else if (isKnowledge)
            rectangle.setStroke(Color.AQUA);


        for (Item item : orderedList) {
            if (item instanceof Wall) {
                rectangle = wallRectangle(rectangle);
            } else if (item instanceof Window) {
                rectangle = windowRectangle(rectangle);
            } else if (item instanceof Door) {
                rectangle = doorRectangle(rectangle);
            } else if (item instanceof Guard) {
                player = (Player) item;
                polygon = createGuard(player.getDirection());
                polygon.setFill(Color.BLUE);
            } else if (item instanceof Intruder) {
                player = (Player) item;
                polygon = createIntruder(player.getDirection());
                polygon.setFill(Color.BLUE);
            } else if (item instanceof  Teleporter){
                rectangle.setFill(Color.PURPLE);
            }
        }

        if (!isKnowledge && !isVision)
            rectangle.setStroke(Color.BLACK);

        if (polygon == null)
            return new StackPane(rectangle);
        else
            return new StackPane(rectangle, polygon);
    }

    private Rectangle wallRectangle(Rectangle rectangle){
        rectangle.setFill(Color.BLACK);
        //rectangle.setOpacity(0.5);
        return rectangle;
    }

    private Rectangle windowRectangle(Rectangle rectangle){
        rectangle.setFill(Color.BLUE);
        return rectangle;
    }

    private Rectangle doorRectangle(Rectangle rectangle){
        rectangle.setFill(Color.BROWN);
        return rectangle;
    }

    private Polygon createGuard(Angle angle){

        Polygon polygon = new Polygon();

        if (angle == Angle.UP)
            polygon.getPoints().addAll(faceUP_Guard);
        else if (angle == Angle.DOWN)
            polygon.getPoints().addAll(faceDOWN_Guard);
        else if (angle == Angle.LEFT)
            polygon.getPoints().addAll(faceLEFT_Guard);
        else if (angle == Angle.RIGHT)
            polygon.getPoints().addAll(faceRIGHT_Guard);

        return polygon;
    }

    private Polygon createIntruder(Angle angle){

        Polygon polygon = new Polygon();

        if (angle == Angle.UP)
            polygon.getPoints().addAll(faceUP_Intruder);
        else if (angle == Angle.DOWN)
            polygon.getPoints().addAll(faceDOWN_Intruder);
        else if (angle == Angle.LEFT)
            polygon.getPoints().addAll(faceLEFT_Intruder);
        else if (angle == Angle.RIGHT)
            polygon.getPoints().addAll(faceRIGHT_Intruder);

        return polygon;

    }

    private ArrayList<Item> orderList(ArrayList<Item> itemList){

        ArrayList<Item> out = new ArrayList<>(Collections.nCopies(6, null));

        int index;

        for (Item item : itemList) {
            if (item instanceof Wall) {
                index = components.indexOf(TileComponents.WALL);
                out.set(index, item);
            } else if (item instanceof Window) {
                index = components.indexOf(TileComponents.WINDOW);
                out.set(index, item);
            } else if (item instanceof Door) {
                index = components.indexOf(TileComponents.DOOR);
                out.set(index, item);
            } else if (item instanceof Guard) {
                index = components.indexOf(TileComponents.GUARD);
                out.set(index, item);
            } else if (item instanceof Intruder) {
                index = components.indexOf(TileComponents.INTRUDER);
                out.set(index, item);
            }else if (item instanceof  Teleporter){
                index = components.indexOf(TileComponents.TELEPORTER);
                out.set(index, item);
            }
        }

        out.removeAll(Collections.singleton(null));

        return out;
    }


    private void createPolygons() {
        faceUP_Intruder = new Double[]{GSSD / 2.0, GSSD / 6.0, GSSD * 5.0 / 6.0, GSSD * 5.0 / 6.0, GSSD / 2.0, GSSD * 2.0 / 3.0, GSSD * 5.0 / 6.0, GSSD * 5.0 / 6.0, GSSD / 2.0, GSSD / 6.0};
        faceDOWN_Intruder = new Double[]{GSSD / 2.0, GSSD * 5.0 / 6.0, GSSD / 6.0, GSSD / 6.0, GSSD / 2.0, GSSD / 3.0, GSSD * 5.0 / 6.0, GSSD / 6.0, GSSD / 2.0, GSSD * 5.0 / 6.0};
        faceLEFT_Intruder = new Double[]{GSSD / 6.0, GSSD / 2.0, GSSD * 5.0 / 6.0, GSSD / 6.0, 20.0, GSSD / 2.0, GSSD * 5.0 / 6.0, GSSD * 5.0 / 6.0, GSSD / 6.0, GSSD / 2.0};
        faceRIGHT_Intruder = new Double[]{GSSD * 5.0 / 6.0, GSSD / 2.0, GSSD / 6.0, 5.0, GSSD / 3.0, GSSD / 2.0, GSSD / 6.0, GSSD * 5.0 / 6.0, GSSD * 5.0 / 6.0, GSSD / 2.0};

        faceUP_Guard = new Double[]{GSSD / 2.0, GSSD / 6.0, GSSD / 6.0, GSSD / 2.0, GSSD / 6.0, GSSD * 5.0 / 6.0, GSSD * 5.0 / 6.0, GSSD * 5.0 / 6.0, GSSD * 5.0 / 6.0, GSSD / 2.0, GSSD / 2.0, GSSD / 6.0};
        faceDOWN_Guard = new Double[]{GSSD / 2.0, GSSD * 5.0 / 6.0, GSSD / 6.0, GSSD / 2.0, GSSD / 6.0, GSSD / 6.0, GSSD * 5.0 / 6.0, GSSD / 6.0, GSSD * 5.0 / 6.0, GSSD / 2.0, GSSD / 2.0, GSSD * 5.0 / 6.0};
        faceLEFT_Guard = new Double[]{GSSD / 6.0, GSSD / 2.0, GSSD / 2.0, GSSD * 5.0 / 6.0, GSSD * 5.0 / 6.0, GSSD * 5.0 / 6.0, GSSD * 5.0 / 6.0, GSSD / 6.0, GSSD / 2.0, GSSD / 6.0, GSSD / 6.0, GSSD / 2.0};
        faceRIGHT_Guard = new Double[]{GSSD * 5.0 / 6.0, GSSD / 2.0, GSSD / 2.0, GSSD / 6.0, GSSD / 6.0, GSSD / 6.0, GSSD / 6.0, GSSD * 5.0 / 6.0, GSSD / 2.0, GSSD * 5.0/ 6.0, GSSD * 5.0 / 6.0, GSSD / 2.0};
    }


    private Double[] createGuardLegend(double size){
        return new Double[]{size / 2.0, size / 6.0, size / 6.0, size / 2.0, size / 6.0, size * 5.0 / 6.0, size * 5.0 / 6.0, size * 5.0 / 6.0, size * 5.0 / 6.0, size / 2.0, size / 2.0, size / 6.0};
    }

    private Double[] createIntruderLegend(double size){
        return new Double[]{size / 2.0, size / 6.0, size * 5.0 / 6.0, size * 5.0 / 6.0, size / 2.0, size * 2.0 / 3.0, size * 5.0 / 6.0, size * 5.0 / 6.0, size / 2.0, size / 6.0};
    }


    class GridIndex{
        int x;
        int y;

        GridIndex(int x, int y){
            this.x = x;
            this.y = y;
        }
    }

}