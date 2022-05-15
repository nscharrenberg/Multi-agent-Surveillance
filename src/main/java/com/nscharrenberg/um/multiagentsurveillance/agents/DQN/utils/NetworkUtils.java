package com.nscharrenberg.um.multiagentsurveillance.agents.DQN.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.FCN.ActivationLayer;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.neuralNetwork.Network;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.utils.gson.DQNActivationLayerAdapter;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class NetworkUtils {
    private static Gson buildGson() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(ActivationLayer.class, new DQNActivationLayerAdapter());
        return builder.create();
    }

    public static void saveNetwork(Network network, String path, String fileName) throws IOException {
        saveNetwork(network, path + "/" + fileName);
    }

    /**
     * Saves the DQN Network values to a specified file
     * @param network - the DQN Network
     * @param pathToFile - The path to write the file and values to
     * @throws IOException
     */
    public static void saveNetwork(Network network, String pathToFile) throws IOException {
        Gson gson = buildGson();

        FileWriter fr = new FileWriter(pathToFile);

        gson.toJson(network, fr);
        fr.close();
    }

    /**
     * Read the DQN Network values from a file and build a network instance
     * @param pathToFile - the file to read
     * @return - the DQN Network Instance
     * @throws IOException
     */
    public static Network readNetwork(String pathToFile) throws IOException {
        Gson gson = buildGson();

        FileReader fr = new FileReader(pathToFile);

        Network network = gson.fromJson(fr, Network.class);
        fr.close();

        return network;
    }
}
