package com.nscharrenberg.um.multiagentsurveillance.gui.canvas;

import com.nscharrenberg.um.multiagentsurveillance.gui.javafx.controllers.HomeScreen;
import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.*;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class GameView extends StackPane {
    private static Color BASIC_TILE_COLOR = Color.FORESTGREEN;
    private static Color WALL_TILE_COLOR = Color.BROWN;
    private static Color TELEPORT_INPUT_TILE_COLOR = Color.PURPLE;
    private static Color TELEPORT_OUT_TILE_COLOR = Color.MEDIUMPURPLE;
    private static Color SHADED_TILE_COLOR = Color.GRAY;
    private static Color GUARD_COLOR = Color.ALICEBLUE;
    private static Color INTRUDER_COLOR = Color.INDIANRED;
    private static Color VISION_COLOR = Color.LIGHTGOLDENRODYELLOW;
    private static Color KNOWLEDGE_COLOR = Color.LAWNGREEN;

    private Double[] faceUP_Intruder;
    private Double[] faceDOWN_Intruder;
    private Double[] faceLEFT_Intruder;
    private Double[] faceRIGHT_Intruder;

    private Double[] faceUP_Guard;
    private Double[] faceDOWN_Guard;
    private Double[] faceLEFT_Guard;
    private Double[] faceRIGHT_Guard;

    private Image guardImage;

    private int GSSD;

    private static int WIDTH;
    private static int HEIGHT;

    private Canvas canvas;
    private GraphicsContext graphicsContext;

    public GameView(Stage stage) {
        try {
            guardImage = new Image(getClass().getResource("../../images/guard.png").toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Factory.init();
        Factory.getGameRepository().startGame();
        WIDTH = Factory.getGameRepository().getWidth();
        HEIGHT = Factory.getGameRepository().getHeight();


        int lowestWidthOrHeight = (int) stage.getWidth();
        if (stage.getHeight() < stage.getWidth()) {
            lowestWidthOrHeight = (int) stage.getHeight();
        }

        GSSD = Math.min((int) (lowestWidthOrHeight / WIDTH-1), (int) (lowestWidthOrHeight / HEIGHT-1));
        GSSD = 10;

        System.out.println(WIDTH);
        System.out.println(lowestWidthOrHeight);
        System.out.println(GSSD);

        init(stage);

        Pane pane = new Pane(canvas);
        pane.setBackground(new Background(new BackgroundFill(BASIC_TILE_COLOR, CornerRadii.EMPTY, Insets.EMPTY)));

        getChildren().add(pane);

        updateAndDraw();
    }

    public void init(Stage stage) {
        canvas = new Canvas(WIDTH, HEIGHT);
        graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.scale(5, 5);

//        Factory.getGameRepository().setRunning(true);
//
//        for (int i = 0; i < 5; i++) {
//            for (Agent agent : Factory.getPlayerRepository().getAgents()) {
//                agent.execute();
//            }
//        }
    }

    private void initMap() {
        graphicsContext.clearRect(0, 0, WIDTH, HEIGHT);

        for (Map.Entry<Integer, HashMap<Integer, Tile>> rowEntry : Factory.getMapRepository().getBoard().getRegion().entrySet()) {
            for (Map.Entry<Integer, Tile> colEntry : rowEntry.getValue().entrySet()) {
                Tile tile = colEntry.getValue();

                for (Item item : tile.getItems()) {
                    if (item instanceof Wall) {
                        drawWall(tile);
                    } else if (item instanceof Guard) {
                        Player player = (Player) item;
                        drawAgent(tile, player.getDirection());
                    }
                }

                if (tile instanceof ShadowTile) {
                    drawShadow(tile);
                }
            }
        }
    }

    public void updateAndDraw() {
        graphicsContext.clearRect(0, 0, WIDTH, HEIGHT);

        initMap();

//        for (Map.Entry<Integer, HashMap<Integer, Tile>> rowEntry : Factory.getPlayerRepository().getCompleteKnowledgeProgress().getRegion().entrySet()) {
//            for (Map.Entry<Integer, Tile> colEntry : rowEntry.getValue().entrySet()) {
//                drawKnowledge(colEntry.getValue());
//            }
//        }
    }

    private void drawKnowledge(Tile tile) {
        drawTile(tile, KNOWLEDGE_COLOR);
    }

    private void drawAgent(Tile tile, Angle angle) {
        if (angle == Angle.UP)
            graphicsContext.rotate(0);
        else if (angle == Angle.DOWN)
            graphicsContext.rotate(180);
        else if (angle == Angle.LEFT)
            graphicsContext.rotate(-90);
        else if (angle == Angle.RIGHT)
            graphicsContext.rotate(90);

        graphicsContext.drawImage(guardImage, tile.getX(), tile.getY() , 1, 1);
    }

    private void drawWall(Tile tile) {
        drawTile(tile, WALL_TILE_COLOR);
    }

    private void drawShadow(Tile tile) {
        graphicsContext.setGlobalAlpha(0.25);
        drawTile(tile, SHADED_TILE_COLOR);
        graphicsContext.setGlobalAlpha(0.25);
    }

    private void drawTile(Tile tile, Color color) {
        graphicsContext.setFill(color);
        graphicsContext.fillRect(tile.getX(), tile.getY(), 1, 1);
    }
}
