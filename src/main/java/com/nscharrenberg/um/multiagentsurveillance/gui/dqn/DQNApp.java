package com.nscharrenberg.um.multiagentsurveillance.gui.dqn;

import com.nscharrenberg.um.multiagentsurveillance.agents.shared.Agent;
import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.List;

public class DQNApp extends Application {
    private DQNView view;
    private static int WIDTH = 1200;
    private static int HEIGHT = 1000;
    public static final boolean MANUAL_PLAYER = false;

    @Override
    public void start(Stage primaryStage) {
        try {
            primaryStage.setWidth(WIDTH);
            primaryStage.setHeight(HEIGHT);
            DQNView view = new DQNView(primaryStage);

            Scene scene = new Scene(view, 800, 800);

            scene.setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.L) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Legend");
                    alert.setHeaderText("");
                    //alert.setContentText(" FOREST GREEN = NORMAL TILE \n BROWN = WALL \n PURPLE = Teleport entrance \n LIGHT PURPLE = Teleport exit \n Black = Shadow \n Light Green = Knowledge \n Yellow/White = Vision \n Blue = Guard");
                    alert.setGraphic(createLegend());
                    alert.show();
                }

                if(MANUAL_PLAYER && Factory.getPlayerRepository().getAgents().size() == 1){
                    List<Agent> listAgent = Factory.getPlayerRepository().getAgents();
                    Agent agent = listAgent.get(0);
                    if (e.getCode() == KeyCode.UP) {
                        agent.execute(Action.UP);
                    } else if (e.getCode() == KeyCode.DOWN){
                        agent.execute(Action.DOWN);
                    } else if (e.getCode() == KeyCode.LEFT){
                        agent.execute(Action.LEFT);
                    } else if (e.getCode() == KeyCode.RIGHT){
                        agent.execute(Action.RIGHT);
                    }
                }
            });

            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        Platform.exit();
        System.exit(0);
    }

    public static void main(String[] args) {
        try {
            launch(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private GridPane createLegend(){
        int size = 20;
        GridPane legend = new GridPane();

        HashMap<String, Color> legendItems = DQNView.getMapColours();
        Color color;
        Polygon polygon;

        int row = 0;
        for (String item : legendItems.keySet()) {
            color = legendItems.get(item);

            if (item.equals("Guard") || item.equals("Intruder")) {
                polygon = createAgent(item, color, size);
                legend.add(polygon, 0, row);
                GridPane.setHalignment(polygon, HPos.CENTER);
            }
            else
                legend.add(createTile(color,size), 0, row);

            legend.add(new Label("= " + item), 1, row++);
        }

        legend.setVgap(2);
        legend.setHgap(10);

        return legend;
    }

    private Rectangle createTile(Color color, int size){
        Rectangle out = new Rectangle(size,size);
        out.setFill(color);
        return out;
    }

    private Polygon createAgent(String name, Color color, int size){
        Polygon agent = new Polygon();
        agent.setFill(color);

        if (name == "Guard")
            agent.getPoints().addAll(createGuardLegend(size));
        else
            agent.getPoints().addAll(createIntruderLegend(size));

        return agent;
    }

    private Double[] createGuardLegend(double size){
        return new Double[]{size / 2.0, size / 6.0, size / 6.0, size / 2.0, size / 6.0, size * 5.0 / 6.0, size * 5.0 / 6.0, size * 5.0 / 6.0, size * 5.0 / 6.0, size / 2.0, size / 2.0, size / 6.0};
    }

    private Double[] createIntruderLegend(double size){
        return new Double[]{size / 6.0, size * 5.0 / 6.0, size / 2.0, size / 6.0, size * 5.0 / 6.0, size * 5.0 / 6.0, size / 2.0, size * 2.0 / 3.0, size / 6.0, size * 5.0 / 6.0};
    }
}
