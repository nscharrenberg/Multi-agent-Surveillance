package com.nscharrenberg.um.multiagentsurveillance.headless.experiments;

import java.io.*;

public class PairedTwoSampleTTest {

    // We will use a significance level of 0.05 resulting in a confidence interval of 95%.
    public static final double SIGNIFICANCE_LEVEL = 0.05;

    public static final int SAMPLE_SIZE_STANDARD = 9;
    public static final int SAMPLE_SIZE_NEW = 9;

    public static void main(String[] args) throws IOException {
        double[] intruders_caught_standard = new double[SAMPLE_SIZE_STANDARD];
        File file = new File("src/main/resources/files/Test1.txt");
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String data;
        int counter = 0;
        while ((data = reader.readLine()) != null) {
            if (data.contains(":") && data.contains("Intruders caught")) {
                String[] separated = data.split(": ");
                double value = Double.parseDouble(separated[1]);
                intruders_caught_standard[counter] = value;
                counter++;
            }
        }

        int sample_size_standard = intruders_caught_standard.length;
        double sample_mean_standard = sample_mean(intruders_caught_standard);
        double sample_standard_deviation_standard = sample_standard_deviation(intruders_caught_standard, sample_mean_standard, sample_size_standard);

        double[] intruders_caught_new = new double[SAMPLE_SIZE_NEW];
        File file2 = new File("src/main/resources/files/Test2.txt");
        BufferedReader reader2 = new BufferedReader(new FileReader(file2));
        String data2;
        int counter2 = 0;
        while ((data2 = reader2.readLine()) != null) {
            if (data2.contains(":") && data2.contains("Intruders caught")) {
                String[] separated = data2.split(": ");
                double value = Double.parseDouble(separated[1]);
                intruders_caught_new[counter2] = value;
                counter2++;
            }
        }

        int sample_size_new = intruders_caught_new.length;
        double sample_mean_new = sample_mean(intruders_caught_new);
        double sample_standard_deviation_new = sample_standard_deviation(intruders_caught_new, sample_mean_new, sample_size_new);


        int degrees_of_freedom = sample_size_standard + sample_size_new - 2;
        double test_statistic_num = sample_mean_standard - sample_mean_new;
        double test_statistic_den = Math.sqrt((Math.pow(sample_standard_deviation_standard, 2) / sample_size_standard) + (Math.pow(sample_standard_deviation_new, 2) / sample_size_new));
        double test_statistic = test_statistic_num / test_statistic_den;
        System.out.println("SAMPLE MEAN STANDARD: " + sample_mean_standard);
        System.out.println("SAMPLE MEAN NEW: " + sample_mean_new);
        System.out.println("SAMPLE STANDARD DEVIATION STANDARD: " + sample_standard_deviation_standard);
        System.out.println("SAMPLE STANDARD DEVIATION NEW: " + sample_standard_deviation_new);
        System.out.println("TEST STATISTIC: " + test_statistic);
    }


    public static double sample_mean(double[] data) {
        int length = data.length;
        double sum = 0;
        for (double datum : data) {
            sum = sum + datum;
        }
        return sum / length;
    }


    public static double sample_standard_deviation(double[] data, double sample_mean, int sample_size) {
        double sum = 0;
        for (double datum : data) {
            sum = sum + Math.pow((datum - sample_mean), 2);
        }
        double variance = sum / (sample_size - 1);
        return Math.sqrt(variance);
    }















}
