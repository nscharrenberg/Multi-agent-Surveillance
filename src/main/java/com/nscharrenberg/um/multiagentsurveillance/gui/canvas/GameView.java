package com.nscharrenberg.um.multiagentsurveillance.gui.canvas;

import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Tile;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
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

    private int tileSize;

    private static int WIDTH;
    private static int HEIGHT;

    private Canvas canvas;
    private GraphicsContext graphicsContext;

    public GameView(Stage stage) {
        WIDTH = Factory.getGameRepository().getWidth();
        HEIGHT = Factory.getGameRepository().getHeight();
        int lowestWidthOrHeight = (int) stage.getWidth();
        if (stage.getHeight() < stage.getWidth()) {
            lowestWidthOrHeight = (int) stage.getHeight();
        }

        tileSize = Math.min((int) Math.floor(Factory.getGameRepository().getWidth() / lowestWidthOrHeight), (int) Math.floor(Factory.getGameRepository().getHeight() / lowestWidthOrHeight));

        init(stage);

        Pane pane = new Pane(canvas);
        pane.setBackground(new Background(new BackgroundFill(BASIC_TILE_COLOR, CornerRadii.EMPTY, Insets.EMPTY)));

        getChildren().add(pane);

        updateAndDraw();
    }

    public void init(Stage stage) {
        Factory.init();
        Factory.getGameRepository().star;
        spawn();

        canvas = new Canvas(WIDTH, HEIGHT);
        graphicsContext = canvas.getGraphicsContext2D();


    }

    public void updateAndDraw() {
        graphicsContext.clearRect(0, 0, WIDTH, HEIGHT);

        for (Map.Entry<Integer, HashMap<Integer, Tile>> rowEntry : Factory.getPlayerRepository().getCompleteKnowledgeProgress().getRegion().entrySet()) {
            for (Map.Entry<Integer, Tile> colEntry : rowEntry.getValue().entrySet()) {
                drawKnowledge(colEntry.getValue());
            }
        }
    }

    private void drawKnowledge(Tile tile) {
        graphicsContext.setFill(KNOWLEDGE_COLOR);
        graphicsContext.fillRect(tile.getX(), tile.getY(), tileSize, tileSize);
    }
}
