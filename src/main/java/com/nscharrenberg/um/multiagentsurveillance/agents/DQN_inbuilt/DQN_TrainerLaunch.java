package com.nscharrenberg.um.multiagentsurveillance.agents.DQN_inbuilt;

import java.io.IOException;

public class DQN_TrainerLaunch {

    public static void main(String[] args) throws IOException {
        String text = "test9";


        new DQN_Main(text + ".bin");
    }
}
