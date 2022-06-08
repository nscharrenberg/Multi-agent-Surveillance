package com.nscharrenberg.um.multiagentsurveillance.gui.dqn;

public class DQNLauncher {

    public static void main(String[] args) {
        try {
            DQNApp.main(args);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}