package com.nscharrenberg.um.multiagentsurveillance.headless.models.gui.dataGUI;

import com.nscharrenberg.um.multiagentsurveillance.headless.utils.recorder.ParseJSONData;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.recorder.json.AgentJSON;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.recorder.json.Coordinates;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class DataCharts {

    public void start(Stage stage, File directoryPath, DataHelper dataHelper) throws Exception {

        ParseJSONData parseData = new ParseJSONData();

        List<List<AgentJSON>> data = parseData.parseData(directoryPath.getAbsolutePath());

        List<List<Coordinates>> xyCoordinates = dataHelper.createXYCoordinates(data);

        stage.setTitle("Data Chart");
        //defining the axes
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel(dataHelper.X_and_Y[0]);
        yAxis.setLabel(dataHelper.X_and_Y[1]);
        //creating the chart
        final LineChart<Number,Number> lineChart =
                new LineChart<Number,Number>(xAxis,yAxis);

        for (int i = 0; i < dataHelper.agentToCompare.size(); i++) {
            //defining a series
            XYChart.Series series = new XYChart.Series();
            series.setName("Agent#" + dataHelper.agentToCompare.get(i));
            List<Coordinates> agentData = xyCoordinates.get(i);
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
