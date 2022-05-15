package com.nscharrenberg.um.multiagentsurveillance.agents.DQN.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.FCN.ActivationLayer;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.neuralNetwork.Network;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.utils.gson.DQNActivationLayerAdapter;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class NetworkUtils {
    public static void saveNetwork(Network network, String path, String fileName) throws IOException {
        saveNetwork(network, path + "/" + fileName);
    }

    public static void saveNetwork(Network network, String pathToFile) throws IOException {
        Gson gson = new Gson();
        gson.toJson(network, new FileWriter(pathToFile));
    }
}
