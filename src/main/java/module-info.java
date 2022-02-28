module com.nscharrenberg.um.multiagentsurveillance {
    requires javafx.controls;
    requires javafx.fxml;
    requires cloning;

    opens com.nscharrenberg.um.multiagentsurveillance to javafx.fxml, cloning;
    exports com.nscharrenberg.um.multiagentsurveillance;
    exports com.nscharrenberg.um.multiagentsurveillance.gui.javafx.controllers;
    opens com.nscharrenberg.um.multiagentsurveillance.gui.javafx.controllers to javafx.fxml;
    exports com.nscharrenberg.um.multiagentsurveillance.headless.repositories;
    opens com.nscharrenberg.um.multiagentsurveillance.headless.repositories to cloning;
    exports com.nscharrenberg.um.multiagentsurveillance.headless.models;
    opens com.nscharrenberg.um.multiagentsurveillance.headless.models to cloning;
    opens com.nscharrenberg.um.multiagentsurveillance.agents.random to cloning;
    opens com.nscharrenberg.um.multiagentsurveillance.agents.shared to cloning;
    opens com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi to cloning;
    opens com.nscharrenberg.um.multiagentsurveillance.agents.shared.utils to cloning;
    opens com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi.comparator to cloning;
}