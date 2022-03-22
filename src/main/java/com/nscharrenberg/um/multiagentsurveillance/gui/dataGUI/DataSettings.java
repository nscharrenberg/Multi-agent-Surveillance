package com.nscharrenberg.um.multiagentsurveillance.gui.dataGUI;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DataSettings {


    private final ToggleGroup groupX = new ToggleGroup();
    private final ToggleGroup groupY = new ToggleGroup();


    public void start(Stage stage, File directoryPath) throws Exception {
        stage.setTitle("Data Settings");

        File directory = new File(directoryPath.getAbsolutePath() + "\\Agents");

        VBox vBoxAgents = new VBox();


        vBoxAgents.getChildren().add(new Text("Agents:"));

        List<CheckBox> checkBoxList = new ArrayList<>();
        int length = directory.list().length;
        for (int i = 0; i < length; i++) {
            CheckBox tmp = new CheckBox("Agents#" + i);
            checkBoxList.add(tmp);
            vBoxAgents.getChildren().add(tmp);
        }
        vBoxAgents.setSpacing(15);


        VBox vBoxX = createRadioX();
        vBoxX.setSpacing(15);

        VBox vBoxY = createRadioY();
        vBoxY.setSpacing(15);

        HBox hBox = new HBox(vBoxAgents, vBoxX, vBoxY);
        hBox.setSpacing(40);

        Button startButton = new Button("Analyze");
        HBox start = new HBox(startButton);
        start.setAlignment(Pos.CENTER);

        startButton.setOnAction(e -> {

            String[] X_and_Y = new String[2];
            RadioButton selectedButtonX = (RadioButton) groupX.getSelectedToggle();
            X_and_Y[0] = selectedButtonX.getText();
            RadioButton selectedButtonY = (RadioButton) groupY.getSelectedToggle();
            X_and_Y[1] = selectedButtonY.getText();

            List<Integer> agentToCompare = new ArrayList<>();
            int agentId = 0;
            for (CheckBox checkBox : checkBoxList) {
                if(checkBox.isSelected()){
                    agentToCompare.add(agentId);
                }
                agentId++;
            }

            DataHelper dataHelper = new DataHelper(agentToCompare, X_and_Y);

            stage.close();
            try {

            if(X_and_Y[0].equals("X") && X_and_Y[1].equals("Y"))
                new PathData().start(stage, directoryPath, dataHelper);
            else
                new DataCharts().start(stage, directoryPath, dataHelper);

            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });

        VBox main = new VBox(hBox, start);
        main.setSpacing(30);


        Scene scene = new Scene(main);
        stage.setScene(scene);
        stage.show();

    }

    private VBox createRadioX(){
        RadioButton rb1X = new RadioButton("Steps");
        rb1X.setToggleGroup(groupX);
        rb1X.setSelected(true);

        RadioButton rb2X = new RadioButton("Time");
        rb2X.setToggleGroup(groupX);

        RadioButton rb3X = new RadioButton("Time To Decide");
        rb3X.setToggleGroup(groupX);

        RadioButton rb4X = new RadioButton("X");
        rb4X.setToggleGroup(groupX);

        RadioButton rb5X = new RadioButton("Total Exploration Rate");
        rb5X.setToggleGroup(groupX);

        RadioButton rb6X = new RadioButton("Agent Exploration Rate");
        rb6X.setToggleGroup(groupX);

        return new VBox(new Text("X coordinate"), rb1X, rb2X, rb3X, rb4X, rb5X, rb6X);
    }

    private VBox createRadioY(){
        RadioButton rb1Y = new RadioButton("Steps");
        rb1Y.setToggleGroup(groupY);
        rb1Y.setSelected(true);

        RadioButton rb2Y = new RadioButton("Time");
        rb2Y.setToggleGroup(groupY);

        RadioButton rb3Y = new RadioButton("Time To Decide");
        rb3Y.setToggleGroup(groupY);

        RadioButton rb4Y = new RadioButton("Y");
        rb4Y.setToggleGroup(groupY);

        RadioButton rb5Y = new RadioButton("Total Exploration Rate");
        rb5Y.setToggleGroup(groupY);

        RadioButton rb6Y = new RadioButton("Agent Exploration Rate");
        rb6Y.setToggleGroup(groupY);

        return new VBox(new Text("Y coordinate"), rb1Y, rb2Y, rb3Y, rb4Y, rb5Y, rb6Y);
    }
}
