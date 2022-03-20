package com.nscharrenberg.um.multiagentsurveillance.gui.dataGUI;

import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;


import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class DataCharts {

    private ParseJSONData parseData = new ParseJSONData();

    public void start(Stage stage, File directoryPath) throws Exception {


        List<List<Coordinates>> data = parseData.parseData(directoryPath.getAbsolutePath());

        stage.setTitle("Data Chart");
        //defining the axes
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel(DataHelper.X_and_Y[0]);
        yAxis.setLabel(DataHelper.X_and_Y[1]);
        //creating the chart
        final LineChart<Number,Number> lineChart =
                new LineChart<Number,Number>(xAxis,yAxis);

        for (int i = 0; i < DataHelper.agentToCompare.length; i++) {
            //defining a series
            XYChart.Series series = new XYChart.Series();
            series.setName("Agent#" + DataHelper.agentToCompare[i]);
            List<Coordinates> agentData = data.get(i);
            for (int j = 0; j < agentData.size(); j++) {
                Coordinates coordinates = agentData.get(j);
                series.getData().add(new XYChart.Data(coordinates.x, coordinates.y));
            }
            lineChart.getData().add(series);
        }

        lineChart.setAnimated(false);
        lineChart.setCreateSymbols(false);
        lineChart.setMinWidth(800);
        lineChart.setMinHeight(600);

        VBox vBox = new VBox(lineChart);

        Scene scene  = new Scene(vBox, 800, 600);

        stage.setScene(scene);
        stage.show();
    }
}
