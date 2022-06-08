package com.nscharrenberg.um.multiagentsurveillance.agents.DQN.neuralNetwork;

import java.io.*;
import java.util.ArrayList;

public class NetworkWriter {

    public static void writeNetwork(ArrayList<String[][]> convLayer, ArrayList<String[]> denseLayer, String network){

        File csvFile = new File(network + ".csv");

        try {
            FileWriter outputFile = new FileWriter(csvFile);
            PrintWriter writer = new PrintWriter(outputFile);

            int conv = 0;
            for (String[][] layer : convLayer) {
                writer.println("conv" + conv++);
                writeConvLayer(layer, writer);
            }

            int dense = 0;
            for (String[] layer : denseLayer) {
                writer.println("dense" + dense++);
                writeDenseLayer(layer, writer);
            }

            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void writeConvLayer(String[][] layer, PrintWriter writer){
        for (String[] row : layer)
            for (String line : row)
                writer.println(line);
    }

    private static void writeDenseLayer(String[] layer, PrintWriter writer){
        for (String line : layer)
            writer.println(line);
    }
}
