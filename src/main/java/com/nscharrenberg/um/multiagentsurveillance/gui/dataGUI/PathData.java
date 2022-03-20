package com.nscharrenberg.um.multiagentsurveillance.gui.dataGUI;

import com.nscharrenberg.um.multiagentsurveillance.gui.javafx.controllers.GameBoardGUI;
import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
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
