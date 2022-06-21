package com.nscharrenberg.um.multiagentsurveillance.headless.experiments;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.inference.ChiSquareTest;
import org.apache.commons.math3.stat.inference.WilcoxonSignedRankTest;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Experiments {

    public static final int NUMBER_OF_GAMES = 1000;


    public static void main(String[] args) throws IOException {
        String input = "src/main/resources/files/DQN_Agent_vs_RL_Agent.csv";
        String output = "src/main/resources/results/DQN_output.txt";
        runsTest(input);
        intrudersHistogram(input, output);
        chiSquareTest(output);
    }


    public static void runsTest(String input) throws IOException {
        System.out.println(input);
        double[] all_values = new double[NUMBER_OF_GAMES];
        File file = new File(input);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String data;
        int counter = 0;
        while ((data = reader.readLine()) != null) {
            if (data.contains(":") && data.contains("Intruders caught")) {
                String[] separated = data.split(": ");
                double value = Double.parseDouble(separated[1]);
                all_values[counter] = value;
                counter++;
            }
        }

        double sum = 0;
        for (double number : all_values) {
            sum = sum + number;
        }
        double mean = sum / all_values.length;

        System.out.println("MEAN: " + mean);

        int above_mean_count = 0;
        int below_mean_count = 0;
        int runs_count = 0;
        int previous_value = -1;
        for (int i = 0; i < all_values.length; i++) {
            if (all_values[i] > mean) {
                above_mean_count++;
                if (previous_value != 0) {
                    runs_count++;
                }
                previous_value = 0;
            } else {
                below_mean_count++;
                if (previous_value != 1) {
                    runs_count++;
                }
                previous_value = 1;
            }
        }
        System.out.println("ABOVE: " + above_mean_count);
        System.out.println("BELOW: " + below_mean_count);
        System.out.println("RUNS: " + runs_count);

        double two_a_b = (double)(2*above_mean_count*below_mean_count);
        double mean_r = two_a_b / (all_values.length) + 0.5;
        double variance_r = (two_a_b*(two_a_b - all_values.length)) / (Math.pow(all_values.length, 2)*(all_values.length-1));
        double test_statistic = (runs_count - mean_r) / (Math.sqrt(variance_r));
        System.out.println("INDEPENDENCE TEST STATISTIC: " + test_statistic + "\n");
    }


    public static void chiSquareTest(String input) throws IOException {
        System.out.println(input);
        long[] observed = new long[7];
        File file = new File(input);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String data;
        int counter = 0;
        while ((data = reader.readLine()) != null) {
            String[] separated = data.split(" ");
            long value = Long.parseLong(separated[1]);
            observed[Integer.parseInt(separated[0])] = value;
            counter++;
        }

        double sum = 0;
        for (int i = 0; i < observed.length; i++) {
            sum += observed[i] * i;
        }
        double mean = sum / 1000;

        double sum2 = 0;
        for (double datum : observed) {
            sum2 = sum2 + Math.pow((datum - mean), 2);
        }
        double std = Math.sqrt(sum / (1000));

        NormalDistribution normal = new NormalDistribution(mean, std);

        double[] expected = new double[]{normal.density(0)*1000, normal.density(1)*1000, normal.density(2)*1000, normal.density(3)*1000, normal.density(4)*1000, normal.density(5)*1000, normal.density(6)*1000 };

        double test_statistic = 0;
        for (int i = 0; i< observed.length; i++) {
            double num = 0;
            num = Math.pow(observed[i] - expected[i], 2);
            test_statistic += num / expected[i];
        }
        System.out.println("NORMALITY TEST STATISTIC: " + test_statistic + "\n");
    }


    public static void wilcoxonTest(String input1, String input2) throws IOException {
        System.out.println(input1 + " " + input2);
        double[] all_values1 = new double[30];
        File file1 = new File(input1);
        BufferedReader reader1 = new BufferedReader(new FileReader(file1));
        String data1;
        int counter1 = 0;
        while ((data1 = reader1.readLine()) != null && counter1 < 30) {
            if (data1.contains(":") && data1.contains("Intruders caught")) {
                String[] separated = data1.split(": ");
                double value = Double.parseDouble(separated[1]);
                all_values1[counter1] = value;
                counter1++;
            }
        }
        Arrays.sort(all_values1);

        double[] all_values2 = new double[30];
        File file2 = new File(input2);
        BufferedReader reader2 = new BufferedReader(new FileReader(file2));
        String data2;
        int counter2 = 0;
        while ((data2 = reader2.readLine()) != null && counter2 < 30) {
            if (data2.contains(":") && data2.contains("Intruders caught")) {
                String[] separated = data2.split(": ");
                double value = Double.parseDouble(separated[1]);
                all_values2[counter2] = value;
                counter2++;
            }
        }
        Arrays.sort(all_values2);
        WilcoxonSignedRankTest test = new WilcoxonSignedRankTest();
        double test_statistic = test.wilcoxonSignedRank(all_values1, all_values2);
        System.out.println("TEST STATISTIC: " + test_statistic + "\n");
    }



    public static void intrudersHistogram(String input, String output) throws IOException {
        double[] all_values = new double[NUMBER_OF_GAMES];
        File file = new File(input);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String data;
        int counter = 0;
        while ((data = reader.readLine()) != null) {
            if (data.contains(":") && data.contains("Intruders caught")) {
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

        FileWriter writer = new FileWriter(output);
        for (Map.Entry<Double, Double> entry : map.entrySet()) {
            String s = Integer.toString(entry.getKey().intValue()) + " " + Integer.toString(entry.getValue().intValue());
            writer.write(s);
            writer.write("\n");
        }
        writer.close();

    }
}
