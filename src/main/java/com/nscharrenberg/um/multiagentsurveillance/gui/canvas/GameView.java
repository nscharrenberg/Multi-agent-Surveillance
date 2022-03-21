package com.nscharrenberg.um.multiagentsurveillance.gui.canvas;

import com.nscharrenberg.um.multiagentsurveillance.agents.shared.Agent;
import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.*;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GameView extends StackPane {
    private static Color BASIC_TILE_COLOR = Color.FORESTGREEN;
    private static Color WALL_TILE_COLOR = Color.BROWN;
    private static Color TELEPORT_INPUT_TILE_COLOR = Color.PURPLE;
    private static Color TELEPORT_OUT_TILE_COLOR = Color.MEDIUMPURPLE;
    private static Color SHADED_TILE_COLOR = Color.BLACK;
    private static Color GUARD_COLOR = Color.BLUE;
    private static Color INTRUDER_COLOR = Color.INDIANRED;
    private static Color VISION_COLOR = Color.LIGHTGOLDENRODYELLOW;
    private static Color KNOWLEDGE_COLOR = Color.LAWNGREEN;
    private static Color TARGET_COLOR = Color.TEAL;

    private Stage stage;

    private Image guardImage;

    private int GSSD;

    private static int WIDTH;
    private static int HEIGHT;
    private int screenWidth;
    private int screenHeight;

    private Canvas canvas;
    private GraphicsContext graphicsContext;

    private WritableImage initialBoard;

    public GameView(Stage stage) {
        try {
            guardImage = new Image(Objects.requireNonNull(getClass().getResource("../../images/guard.png")).toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.stage = stage;
        Factory.init();
        Factory.getGameRepository().startGame();
        WIDTH = Factory.getGameRepository().getWidth() + 1;
        HEIGHT = Factory.getGameRepository().getHeight() + 1;

        screenWidth = (int) stage.getWidth();
        screenHeight = (int) stage.getHeight();

        int tileWidth = screenWidth / WIDTH;
        int tileHeight = screenHeight / HEIGHT;

        GSSD = Math.min(tileWidth, tileHeight);

        init(stage);

        Pane pane = new Pane(canvas);
        pane.setBackground(new Background(new BackgroundFill(BASIC_TILE_COLOR, CornerRadii.EMPTY, Insets.EMPTY)));

        getChildren().add(pane);

        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                gameLoop();

                System.out.println(" Game Finished ");
                gameFinished();
                Factory.getGameRepository().setRunning(false);

                return null;
            }
        };

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (Factory.getGameRepository().isRunning()) {
                    updateAndDraw();
                }
            }
        };

        timer.start();
    }

    private void gameFinished() {
        Factory.getGameRepository().stopGame();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game is Finished");
        alert.setHeaderText("Game Finished");
        String s =" gone over all steps";
        alert.setContentText(s);
        alert.show();
    }

    private void gameLoop() {
        Factory.getGameRepository().setRunning(true);
        while (Factory.getGameRepository().isRunning()) {
            for (Agent agent : Factory.getPlayerRepository().getAgents()) {
                try {
                    agent.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void drawText(String text) {
        graphicsContext.setFill(Color.BLACK);
        graphicsContext.setFont(new Font(12));
        graphicsContext.fillText(text, 10, 10);
    }

    private int getPoint(int point) {
        return point * GSSD;
    }

    public void init(Stage stage) {
        canvas = new Canvas(stage.getWidth(), stage.getHeight());
        graphicsContext = canvas.getGraphicsContext2D();

        initMap();

        Factory.getGameRepository().setRunning(true);

        for (int i = 0; i < 5; i++) {
            for (Agent agent : Factory.getPlayerRepository().getAgents()) {
                agent.execute();
            }
        }
    }

    private void initMap() {
        graphicsContext.clearRect(0, 0, screenWidth, screenHeight);

        // Fill Background
        graphicsContext.setFill(BASIC_TILE_COLOR);
        graphicsContext.fillRect(0, 0, screenWidth, screenHeight);

        for (Map.Entry<Integer, HashMap<Integer, Tile>> rowEntry : Factory.getMapRepository().getBoard().getRegion().entrySet()) {
            for (Map.Entry<Integer, Tile> colEntry : rowEntry.getValue().entrySet()) {
                Tile tile = colEntry.getValue();

                detectAndDrawTile(tile);
            }
        }

        drawTargetArea();

        SnapshotParameters sp = new SnapshotParameters();
        sp.setFill(Color.TRANSPARENT);

        initialBoard = canvas.snapshot(new SnapshotParameters(), null);
    }

    private void detectAndDrawTile(Tile tile) {
        for (Item item : tile.getItems()) {
            if (item instanceof Wall) {
                drawWall(tile);
            } else if (item instanceof Teleporter teleporter) {
                if (teleporter.getTile().equals(tile)) {
                    drawTeleportOutput(tile);
                } else {
                    drawTeleportInput(tile);
                }
            }
        }

        if (tile instanceof ShadowTile) {
            drawShadow(tile);
        }
    }

    private void drawAgents(Tile tile) {
        for (Item item : tile.getItems()) {
            if (item instanceof Guard) {
                Player player = (Player) item;
                drawAgent(tile, player.getDirection());
            }
        }
    }

    public void updateAndDraw() {
        graphicsContext.setGlobalAlpha(1);
        graphicsContext.clearRect(0, 0, screenWidth, screenHeight);
        graphicsContext.drawImage(initialBoard, 0, 0);
        DecimalFormat decimalFormat = new DecimalFormat("##.00");

        if (Factory.getPlayerRepository().getExplorationPercentage() >= 100) {
            stage.setTitle("Map Explored. Game Finished!");
        } else if (!Factory.getGameRepository().isRunning()) {
            stage.setTitle("Game Stopped at an exploration rate of " + decimalFormat.format(Factory.getPlayerRepository().getExplorationPercentage()) + "%");
        } else {
            stage.setTitle("Exploration Percentage: " + decimalFormat.format(Factory.getPlayerRepository().getExplorationPercentage()) + "%");
        }

        drawAllKnowledge();

        for (Agent agent : Factory.getPlayerRepository().getAgents()) {
            drawAgents(agent.getPlayer().getTile());

            if (agent.getPlayer().getVision() != null) {
                for (Map.Entry<Integer, HashMap<Integer, Tile>> rowEntry : agent.getPlayer().getVision().getRegion().entrySet()) {
                    for (Map.Entry<Integer, Tile> colEntry : rowEntry.getValue().entrySet()) {
                        drawVision(colEntry.getValue());
                    }
                }
            }
        }
    }

    private void drawTargetArea() {
        TileArea targetArea = Factory.getMapRepository().getTargetArea();

        if (targetArea != null) {
            for (Map.Entry<Integer, HashMap<Integer, Tile>> rowEntry : targetArea.getRegion().entrySet()) {
                for (Map.Entry<Integer, Tile> colEntry : rowEntry.getValue().entrySet()) {
                    Tile tile = colEntry.getValue();

                    drawTarget(tile);
                }
            }
        }
    }

    private void drawTarget(Tile tile) {
        drawTile(tile, TARGET_COLOR, .5);
    }

    public void drawAllKnowledge() {
        for (Map.Entry<Integer, HashMap<Integer, Tile>> rowEntry : Factory.getPlayerRepository().getCompleteKnowledgeProgress().getRegion().entrySet()) {
            for (Map.Entry<Integer, Tile> colEntry : rowEntry.getValue().entrySet()) {
                drawKnowledge(colEntry.getValue());
            }
        }
    }

    private void drawVision(Tile tile) {
        drawTile(tile, VISION_COLOR, .1);
    }

    private void drawTeleportInput(Tile tile) {
        drawTile(tile, TELEPORT_INPUT_TILE_COLOR);
    }

    private void drawTeleportOutput(Tile tile) {
        drawTile(tile, TELEPORT_OUT_TILE_COLOR);
    }

    private void drawKnowledge(Tile tile) {
        drawTile(tile, KNOWLEDGE_COLOR, .1);
    }

    private void drawAgent(Tile tile, Angle angle) {
        drawTile(tile, GUARD_COLOR);
    }

    private void drawWall(Tile tile) {
        drawTile(tile, WALL_TILE_COLOR);
    }

    private void drawShadow(Tile tile) {
        drawTile(tile, SHADED_TILE_COLOR, 0.25);
    }

    private void drawTile(Tile tile, Color color) {;
        drawTile(tile, color, 1);
    }

    private void drawTile(Tile tile, Color color, double alpha) {
        graphicsContext.setGlobalAlpha(alpha);
        graphicsContext.setFill(color);
        graphicsContext.fillRect(getPoint(tile.getX()), getPoint(tile.getY()), GSSD, GSSD);
    }
}