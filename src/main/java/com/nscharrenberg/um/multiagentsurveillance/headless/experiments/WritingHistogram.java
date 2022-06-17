package com.nscharrenberg.um.multiagentsurveillance.headless.experiments;

import java.io.*;
import java.util.*;

public class WritingHistogram {

    public static final int NUMBER_OF_GAMES = 50;
    public static final int BIN_SIZE = 50;

    public static void main(String[] args) throws IOException {
        double[] all_values = new double[50];
        File file = new File("src/main/resources/files/RLAgent_vs_Evader_50_Maze1.csv");
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String data;
        int counter = 0;
        while ((data = reader.readLine()) != null) {
            if (data.contains(":") && data.contains("Game steps")) {
                String[] separated = data.split(": ");
                double value = Double.parseDouble(separated[1]);
                all_values[counter] = value;
                counter++;
            }
        }
        Arrays.sort(all_values);

        Map<Double, Double> map = new HashMap<>();
        int count = 0;
        for (int i = 0; i < all_values.length; i++) {
            if (map.containsKey(all_values[i])) {
                map.put(all_values[i], map.get(all_values[i]) + 1.0);
            }
            else {
                map.put(all_values[i], 1.0);
            }
        }
        FileWriter writer = new FileWriter("src/main/resources/results/Histogram_RLAgent_vs_Evader_50_Maze1.txt");
        for (Map.Entry<Double, Double> entry : map.entrySet()) {
            String s = Integer.toString(entry.getKey().intValue()) + " " + Integer.toString(entry.getValue().intValue());
            writer.write(s);
            writer.write("\n");
        }
        writer.close();
    }
}
