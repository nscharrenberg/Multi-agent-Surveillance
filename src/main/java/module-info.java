module com.nscharrenberg.um.multiagentsurveillance {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.nscharrenberg.um.multiagentsurveillance to javafx.fxml;
    exports com.nscharrenberg.um.multiagentsurveillance;
    exports com.nscharrenberg.um.multiagentsurveillance.gui.javafx.controllers;
    opens com.nscharrenberg.um.multiagentsurveillance.gui.javafx.controllers to javafx.fxml;
}