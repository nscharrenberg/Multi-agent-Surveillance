package com.nscharrenberg.um.multiagentsurveillance.gui.canvas;

import com.nscharrenberg.um.multiagentsurveillance.agents.shared.Agent;
import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.BoardNotBuildException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.InvalidTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.ItemNotOnTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.GameMode;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.Collision.Wall;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.Item;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.Teleporter;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.ShadowTile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.TileArea;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Guard;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Intruder;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Player;
import javafx.animation.AnimationTimer;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.text.DecimalFormat;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;

import static com.nscharrenberg.um.multiagentsurveillance.gui.canvas.CanvasApp.MANUAL_PLAYER;

public class GameView extends StackPane {
    private static final int DELAY = 50;
    protected static Color BASIC_TILE_COLOR = Color.FORESTGREEN;
    protected static Color WALL_TILE_COLOR = Color.BROWN;
    protected static Color TELEPORT_INPUT_TILE_COLOR = Color.PURPLE;
    protected static Color TELEPORT_OUT_TILE_COLOR = Color.MEDIUMPURPLE;
    protected static Color SHADED_TILE_COLOR = Color.BLACK;
    protected static Color GUARD_COLOR = Color.BLUE;
    protected static Color INTRUDER_COLOR = Color.INDIANRED;
    protected static Color VISION_COLOR = Color.LIGHTGOLDENRODYELLOW;
    protected static Color KNOWLEDGE_COLOR = Color.LAWNGREEN;
    protected static Color TARGET_COLOR = Color.TEAL;


    public static HashMap<String, Color> getMapColours(){
        HashMap<String, Color> out = new HashMap<>();

        out.put("Basic Tile", BASIC_TILE_COLOR);
        out.put("Wall Tile", WALL_TILE_COLOR);
        out.put("Teleport In", TELEPORT_INPUT_TILE_COLOR);
        out.put("Teleport Out", TELEPORT_OUT_TILE_COLOR);
        out.put("Shaded Tile", SHADED_TILE_COLOR);
        out.put("Guard", GUARD_COLOR);
        out.put("Intruder", INTRUDER_COLOR);
        out.put("Vision", VISION_COLOR);
        out.put("Knowledge", KNOWLEDGE_COLOR);
        out.put("Target", TARGET_COLOR);

        return out;
    }

    private Stage stage;

    private int GSSD;

    private static int WIDTH;
    private static int HEIGHT;
    private int screenWidth;
    private int screenHeight;

    private Canvas canvas;
    private GraphicsContext graphicsContext;

    private WritableImage initialBoard;

    public GameView(Stage stage) throws InvalidTileException, BoardNotBuildException, ItemNotOnTileException {

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
                    Factory.getPlayerRepository().calculateExplorationPercentage();
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
        if(!MANUAL_PLAYER) {
            Factory.getGameRepository().setRunning(true);
            while (Factory.getGameRepository().isRunning()) {
                try {
                    Factory.getMapRepository().checkMarkers();
                } catch (BoardNotBuildException | InvalidTileException | ItemNotOnTileException e) {
                }
                Factory.getPlayerRepository().updateSounds(Factory.getPlayerRepository().getAgents());
                for (Agent agent : Factory.getPlayerRepository().getAgents()) {
                    try {
                        agent.execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
    }

    private void drawText(String text) {
        graphicsContext.setFill(Color.BLACK);
        graphicsContext.setFont(new Font(12));
        graphicsContext.fillText(text, 10, 10);
    }

    private int getPoint(int point) {
        return point * GSSD;
    }

    public void init(Stage stage) throws InvalidTileException, BoardNotBuildException, ItemNotOnTileException {
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
        try {
            for (Item item : tile.getItems()) {
                if (item instanceof Guard) {
                    Player player = (Player) item;
                    drawGuard(tile, player.getDirection());
                } else if (item instanceof Intruder) {
                    Player player = (Player) item;
                    drawIntruder(tile, player.getDirection());
                }
            }
        } catch (ConcurrentModificationException e) {
            // do nothing
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
            stage.setTitle("Game Stopped at an exploration rate of " + decimalFormat.format(Factory.getPlayerRepository().getExplorationPercentage()) + "% - " + Factory.getGameRepository().getGameMode().getName());
        } else {
            stage.setTitle("Exploration Percentage: " + decimalFormat.format(Factory.getPlayerRepository().getExplorationPercentage()) + "% - " + Factory.getGameRepository().getGameMode().getName());
        }

        if (Factory.getGameRepository().getGameMode().equals(GameMode.EXPLORATION)) {
            drawAllKnowledge();
        }

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
            try {
                for (Map.Entry<Integer, Tile> colEntry : rowEntry.getValue().entrySet()) {
                    drawKnowledge(colEntry.getValue());
                }
            } catch (ConcurrentModificationException e) {
                // do nothing
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

    private void drawGuard(Tile tile, Action angle) {
        drawTile(tile, GUARD_COLOR);
    }

    private void drawIntruder(Tile tile, Action angle) {
        drawTile(tile, INTRUDER_COLOR);
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
