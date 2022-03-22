package com.nscharrenberg.um.multiagentsurveillance.gui.dataGUI;

import com.nscharrenberg.um.multiagentsurveillance.gui.javafx.controllers.GameBoardGUI;
import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.files.MapImporter;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.recorder.ParseJSONData;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.recorder.json.AgentJSON;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.recorder.json.Coordinates;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PathData {

    public void start(Stage stage, File directoryPath) throws Exception {

        ParseJSONData parseData = new ParseJSONData();
        DataHelper dataHelper = new DataHelper();

        List<List<AgentJSON>> data = parseData.parseData(directoryPath.getAbsolutePath());

        List<List<Coordinates>> xyCoordinates = dataHelper.createXYCoordinates(data);

        Factory.init();

        importMap();

        GameBoardGUI boardGUI = new GameBoardGUI();

        boardGUI.showPath(stage, xyCoordinates);
    }

    private void importMap() {
        File file = new File("src/test/resources/maps/testmap4.txt");
        String path = file.getAbsolutePath();
        MapImporter importer = new MapImporter();

        Factory.getGameRepository().setRunning(true);

        try {
            importer.load(path);
        } catch (IOException e) {
//            Factory.getGameRepository().setRunning(false);
        }
    }
}
