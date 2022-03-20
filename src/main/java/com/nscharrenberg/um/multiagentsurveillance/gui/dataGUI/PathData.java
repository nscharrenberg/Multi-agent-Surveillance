package com.nscharrenberg.um.multiagentsurveillance.gui.dataGUI;

import com.nscharrenberg.um.multiagentsurveillance.gui.javafx.controllers.GameBoardGUI;
import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.files.MapImporter;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PathData {
    private ParseJSONData parseData = new ParseJSONData();

    public void start(Stage stage, File directoryPath) throws Exception {

        List<List<Coordinates>> data = parseData.parseData(directoryPath.getAbsolutePath());

        Factory.init();

        importMap();

        GameBoardGUI boardGUI = new GameBoardGUI();

        boardGUI.showPath(stage, data);
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
