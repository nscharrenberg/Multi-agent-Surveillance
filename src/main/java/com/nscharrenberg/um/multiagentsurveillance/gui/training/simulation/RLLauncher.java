package com.nscharrenberg.um.multiagentsurveillance.gui.training.simulation;

public class RLLauncher {

    public static void main(String[] args) {
        try {
            RLApp.main(args);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}