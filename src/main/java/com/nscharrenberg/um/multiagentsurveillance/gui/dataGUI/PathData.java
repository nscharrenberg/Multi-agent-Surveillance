package com.nscharrenberg.um.multiagentsurveillance.gui.dataGUI;

import com.nscharrenberg.um.multiagentsurveillance.gui.javafx.controllers.GameBoardGUI;
import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class PathData {
    private ParseJSONData parseData = new ParseJSONData();

    public void start(Stage stage, File directoryPath) throws Exception {

        List<List<Coordinates>> data = parseData.parseData(directoryPath.getAbsolutePath());

        Factory.init();

        Factory.getGameRepository().startGame();

        GameBoardGUI boardGUI = new GameBoardGUI();

        boardGUI.showPath(stage, data);
    }
}
