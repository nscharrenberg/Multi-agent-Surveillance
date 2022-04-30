module com.nscharrenberg.um.multiagentsurveillance {
    requires javafx.controls;
    requires javafx.fxml;
    requires cloning;
    requires com.google.gson;

    opens com.nscharrenberg.um.multiagentsurveillance to javafx.fxml, cloning;
    exports com.nscharrenberg.um.multiagentsurveillance;
    opens com.nscharrenberg.um.multiagentsurveillance.headless.models.gui.dataGUI to javafx.fxml;
    exports com.nscharrenberg.um.multiagentsurveillance.headless.models.gui.dataGUI;
    opens com.nscharrenberg.um.multiagentsurveillance.headless.models.gui.javafx.controllers to javafx.fxml, javafx.graphics;
    exports com.nscharrenberg.um.multiagentsurveillance.headless.models.gui.canvas;
    opens com.nscharrenberg.um.multiagentsurveillance.headless.models.gui.canvas to javafx.fxml;
    exports com.nscharrenberg.um.multiagentsurveillance.headless.repositories;
    opens com.nscharrenberg.um.multiagentsurveillance.headless.repositories to cloning;
    exports com.nscharrenberg.um.multiagentsurveillance.headless.models;
    opens com.nscharrenberg.um.multiagentsurveillance.headless.models to cloning;
    opens com.nscharrenberg.um.multiagentsurveillance.agents.random to cloning;
    opens com.nscharrenberg.um.multiagentsurveillance.agents.shared to cloning;
    opens com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi to cloning;
    opens com.nscharrenberg.um.multiagentsurveillance.agents.SBO to cloning;
    opens com.nscharrenberg.um.multiagentsurveillance.agents.shared.utils to cloning;
    opens com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi.comparator to cloning;
    opens com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.pathfinding.AStar to cloning;
    opens com.nscharrenberg.um.multiagentsurveillance.headless.utils to cloning;
    exports com.nscharrenberg.um.multiagentsurveillance.headless.utils.recorder;
    opens com.nscharrenberg.um.multiagentsurveillance.headless.utils.recorder to com.google.gson, javafx.fxml;
    exports com.nscharrenberg.um.multiagentsurveillance.headless.utils.recorder.json;
    opens com.nscharrenberg.um.multiagentsurveillance.headless.utils.recorder.json to com.google.gson, javafx.fxml;
}