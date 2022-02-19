module com.nscharrenberg.um.multiagentsurveillance {
    requires javafx.controls;
    requires javafx.fxml;
    requires cloning;

    opens com.nscharrenberg.um.multiagentsurveillance to javafx.fxml, cloning;
    exports com.nscharrenberg.um.multiagentsurveillance;
    exports com.nscharrenberg.um.multiagentsurveillance.gui.javafx.controllers;
    opens com.nscharrenberg.um.multiagentsurveillance.gui.javafx.controllers to javafx.fxml;
    opens com.nscharrenberg.um.multiagentsurveillance.headless.repositories to cloning;
    opens com.nscharrenberg.um.multiagentsurveillance.headless.models to cloning;
    opens com.nscharrenberg.um.multiagentsurveillance.agents.random to cloning;
    opens com.nscharrenberg.um.multiagentsurveillance.agents.shared to cloning;
    exports com.nscharrenberg.um.multiagentsurveillance.headless.models;
    exports com.nscharrenberg.um.multiagentsurveillance.headless.repositories;
    exports com.nscharrenberg.um.multiagentsurveillance.agents.random;
    exports com.nscharrenberg.um.multiagentsurveillance.agents.shared;
}