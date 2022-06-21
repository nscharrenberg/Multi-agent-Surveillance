package com.nscharrenberg.um.multiagentsurveillance.gui.dqn;

import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.DQN_Agent;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.DQN_Params;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.neuralNetwork.Network;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.training.Experience;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.training.TrainingData;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.Agent;
import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IGameRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IMapRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IPlayerRepository;
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
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static com.nscharrenberg.um.multiagentsurveillance.agents.DQN.DQN_Agent_Util.endState;
import static com.nscharrenberg.um.multiagentsurveillance.agents.DQN.neuralNetwork.NetworkWriter.newSave;

public class DQNView extends StackPane {
    private static final int DELAY = -1;
    protected static Color BASIC_TILE_COLOR = Color.LIGHTGREY;
    protected static Color WALL_TILE_COLOR = Color.DARKBLUE.darker().darker();
    protected static Color TELEPORT_INPUT_TILE_COLOR = Color.PURPLE;
    protected static Color TELEPORT_OUT_TILE_COLOR = Color.MEDIUMPURPLE;
    protected static Color SHADED_TILE_COLOR = Color.BLACK;
    protected static Color GUARD_COLOR = Color.BLUE;
    protected static Color INTRUDER_COLOR = Color.INDIANRED;
    protected static Color VISION_COLOR = Color.YELLOW;
    protected static Color KNOWLEDGE_COLOR = Color.LIGHTBLUE;
    protected static Color TARGET_COLOR = Color.TEAL;

    private IGameRepository gameRepository;
    private IPlayerRepository playerRepository;
    private IMapRepository mapRepository;

    private String NETWORK_PATH = "dqn_network_activation_layer";
    private String NETWORK_EXTENSION = ".agent";
    private Stack<Network> networks = new Stack<>();
    private DQN_Agent[] intruders;
    private final int batchSize = 128;
    private final int numEpisodes = 2000;

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

    public DQNView(Stage stage) throws Exception {

        this.stage = stage;

        init();

        WIDTH = gameRepository.getWidth() + 1;
        HEIGHT = gameRepository.getHeight() + 1;

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

                return null;
            }
        };

        task.setOnSucceeded(e -> {
            System.out.println(" Training has done id! ");
            gameRepository.setRunning(false);
        });

        task.exceptionProperty().addListener((observable, oldValue, newValue) ->  {
            if(newValue != null) {
                Exception ex = (Exception) newValue;
                ex.printStackTrace();
            }
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                try {
                    if (gameRepository.isRunning()) {
                        playerRepository.calculateExplorationPercentage();
                        updateAndDraw();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        timer.start();
    }

    private void init(){
        Factory.init();

        gameRepository = Factory.getGameRepository();
        playerRepository = Factory.getPlayerRepository();
        mapRepository = Factory.getMapRepository();
        gameRepository.importMap();
    }

    private void setupGuards() {
        gameRepository.setupAgents(Guard.class);
    }

    private void setupIntruders() {
        gameRepository.setupAgents(Intruder.class);

        int index = 0;
        if (playerRepository.getIntruders().size() != intruders.length) {
            throw new IllegalStateException("The 2 known intruder lists are not of the same size");
        }

        for (Iterator<Intruder> itr = playerRepository.getIntruders().iterator(); itr.hasNext();) {
            Intruder intruder = itr.next();

            intruders[index].initRepositories();
            playerRepository.getAgents().remove(intruder.getAgent());
            intruder.setAgent(intruders[index]);
            intruders[index].setPlayer(intruder);
            playerRepository.getAgents().add(intruder.getAgent());

            index++;
        }
    }

    private void setupDQNAgents() {
        intruders = new DQN_Agent[gameRepository.getIntruderCount()];

        for (int i = 0; i < gameRepository.getIntruderCount(); i++) {
            intruders[i] = new DQN_Agent(mapRepository, gameRepository, playerRepository);
            intruders[i].setTrainingData(new TrainingData());
        }
    }

    private void reset(){

        Factory.reset();

        this.mapRepository = Factory.getMapRepository();
        this.gameRepository = Factory.getGameRepository();
        this.playerRepository = Factory.getPlayerRepository();
        gameRepository.importMap();
    }

    private void gameLoop() throws Exception {

        setupDQNAgents();
        loadNetwork(0);
        saveProgress(0);

        for (int episode = 449; episode <= numEpisodes ; episode++) {

            System.out.println("Episode number = " + episode);
            reset();

            setupGuards();
            setupIntruders();

            gameRepository.setRunning(true);

            runGame(episode);

            //if (episode % 10 == 0)
            saveProgress(episode);
        }
    }

    private void saveProgress(int episode){
        //newSave();
        for (int i = 0; i < intruders.length; i++) {
            intruders[i].newGame(i, episode);
        }
    }

    private void loadNetwork(int saveNumber) throws Exception {
        for (int i = 0; i < intruders.length; i++) {
            intruders[i].loadNetwork(i, saveNumber);
        }
    }


    private void resetToTargetNet(){
        for (DQN_Agent intruder : intruders) {
            intruder.resetToTargetNet();
        }
    }


    private void runGame(int episode) {

        double[][][] state, nextState;
        Experience experience;
        boolean done;
        Action action;
        double reward;
        int moveNumber = 0;
        caughtIntruders = new ArrayList<>();

        while (gameRepository.isRunning()) {
            try {
                mapRepository.checkMarkers();
            } catch (BoardNotBuildException | InvalidTileException | ItemNotOnTileException e) {
            }

            if (moveNumber++ > DQN_Params.maxMoves.valueInt){
                resetToTargetNet();
                gameRepository.setRunning(false);
                break;
            }

            playerRepository.updateSounds(playerRepository.getAgents());

            try {
                for (Iterator<Guard> itr = playerRepository.getGuards().iterator(); itr.hasNext(); ) {
                    Agent agent = itr.next().getAgent();

                    try {
                        agent.execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (!gameRepository.isRunning()) {
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!gameRepository.isRunning()) {
                continue;
            }

            try {

                intruderCaught(moveNumber);

                for (Iterator<Intruder> itr = playerRepository.getIntruders().iterator(); itr.hasNext(); ) {

                    if (!gameRepository.isRunning()) {
                        break;
                    }

                    Intruder intruder = itr.next();
                    DQN_Agent agent = (DQN_Agent) intruder.getAgent();

                    state = agent.updateState();
                    action = agent.selectAction(episode, state);
                    agent.execute(action);

                    if (endState(intruder)) {
                        agent.endTrain(state, action, moveNumber);
                        continue;
                    }

                    done = !agent.getGameRepository().isRunning();

                    if (!done) {
                        nextState = agent.updateState();
                        reward = agent.calculateReward(nextState, action);
                        experience = new Experience(state, action, reward, nextState, done);
                        agent.getTrainingData().push(experience);
                        agent.trainAgent(experience);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!gameRepository.isRunning()) {
                break;
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

    private ArrayList<Intruder> caughtIntruders;

    private void intruderCaught(int moveNumber) throws Exception {

        DQN_Agent agent;
        for (Intruder intruder : playerRepository.getCaughtIntruders()) {
            if (caughtIntruders.contains(intruder))
                continue;

            caughtIntruders.add(intruder);
            agent = (DQN_Agent) intruder.getAgent();
            agent.trainAgentEndCaught(moveNumber);
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

    public void init(Stage stage) throws Exception {
        canvas = new Canvas(stage.getWidth(), stage.getHeight());
        graphicsContext = canvas.getGraphicsContext2D();

        initMap();

        gameRepository.setRunning(true);

        for (int i = 0; i < 5; i++) {
            for (Agent agent : playerRepository.getAgents()) {
                agent.execute();
            }
        }
    }

    private void initMap() {
        graphicsContext.clearRect(0, 0, screenWidth, screenHeight);

        // Fill Background
        graphicsContext.setFill(BASIC_TILE_COLOR);
        graphicsContext.fillRect(0, 0, screenWidth, screenHeight);

        for (Map.Entry<Integer, HashMap<Integer, Tile>> rowEntry : mapRepository.getBoard().getRegion().entrySet()) {
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
            if (tile == null) return;

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

        if (playerRepository.getExplorationPercentage() >= 100) {
            stage.setTitle("Map Explored. Game Finished!");
        } else if (!gameRepository.isRunning()) {
            stage.setTitle("Game Stopped at an exploration rate of " + decimalFormat.format(playerRepository.getExplorationPercentage()) + "% - " + gameRepository.getGameMode().getName() + " - " + gameRepository.getGameState().getMessage());
        } else {
            stage.setTitle("Exploration Percentage: " + decimalFormat.format(playerRepository.getExplorationPercentage()) + "% - " + gameRepository.getGameMode().getName()+ " - " + gameRepository.getGameState().getMessage());
        }

        if (gameRepository.getGameMode().equals(GameMode.EXPLORATION)) {
            drawAllKnowledge();
        }

        for (Agent agent : playerRepository.getAgents()) {
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
        TileArea targetArea = mapRepository.getTargetArea();

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
        for (Map.Entry<Integer, HashMap<Integer, Tile>> rowEntry : playerRepository.getCompleteKnowledgeProgress().getRegion().entrySet()) {
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
        drawTile(tile, KNOWLEDGE_COLOR, .3);
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
