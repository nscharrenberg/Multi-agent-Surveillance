package com.nscharrenberg.um.multiagentsurveillance.agents.DQN.neuralNetwork;

import java.io.*;
import java.util.ArrayList;
import java.io.IOException;
import java.util.Arrays;

public class NetworkWriter {

    private static final String PATH = "src/main/resources/network_weights/";
    private static final String split = ",";
    private static int saveNum = 0;

    public static void writeNetwork(ArrayList<String[][]> convLayer, ArrayList<String[]> denseLayer, String network){

        File csvFile = new File(PATH + saveNum + "." + network + ".csv");

        try {
            FileWriter outputFile = new FileWriter(csvFile);
            PrintWriter writer = new PrintWriter(outputFile);

            int conv = 0;
            for (String[][] layer : convLayer) {
                writer.println(convID(conv++));
                writeConvLayer(layer, writer);
            }

            int dense = 0;
            for (String[] layer : denseLayer) {
                writer.println(denseID(dense++));
                writeDenseLayer(layer, writer);
            }

            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void readNetwork(int networkNumber, Network network) throws Exception {
        int convInd = 0, denseInd = 0;
        String line;
        try {
            FileReader file = new FileReader(PATH + networkNumber + ".csv");
            BufferedReader reader = new BufferedReader(file);

            while ((line = reader.readLine()) != null){
                String[] test = line.split(split);

                if (test[0].equals(convID(convInd)))
                    convInd = readConvLayer(network, convInd, reader);

                if (test[0].equals(denseID(denseInd)))
                    denseInd = readDenseLayer(network, denseInd, reader);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Successful Load");
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

    private static int readConvLayer(Network network, int layerInd, BufferedReader reader) throws IOException {

        String line;
        String[] splitLine;

        int     numFilters = network.getFilterNumber(layerInd),
                numChannels = network.getChannels(layerInd),
                numWeights = network.getKernelSize(layerInd),
                filterInd = 0,
                biasInd = 0,
                channelInd = 0;

        double[][][] layerWeights = new double[numFilters][numChannels][numWeights];
        double[][] layerBias = new double[numFilters][numWeights];

        while (biasInd < numFilters) {
            line = reader.readLine();
            splitLine = line.split(split);

            if (splitLine[0].equals(filterID(filterInd))){
                while (channelInd++ < numChannels) {
                    line = reader.readLine();
                    splitLine = line.split(split);
                    layerWeights[filterInd++][channelInd] = Arrays.stream(splitLine).mapToDouble(Double::parseDouble).toArray();
                }
                channelInd = 0;
                continue;
            }

            if (splitLine[0].equals(biasID(biasInd))){
                line = reader.readLine();
                splitLine = line.split(split);
                layerBias[biasInd++] = Arrays.stream(splitLine).mapToDouble(Double::parseDouble).toArray();
            }
        }

        network.loadConvLayer(layerInd, layerWeights, layerBias);

        return ++layerInd;
    }
    
    private static int readDenseLayer(Network network, int denseInd, BufferedReader reader) throws Exception {

        String line;
        String[] splitLine;
        int     numNeurons = network.getDenseLayers()[denseInd].getNumNeurons(),
                numOutputs = network.getDenseLayers()[denseInd].getNumOutputs(),
                neuronInd = 0;

        double[][] layerWeights = new double[numNeurons][numOutputs];
        double[] layerBias = new double[numOutputs];
        
        while (neuronInd < numNeurons) {
            line = reader.readLine();
            splitLine = line.split(split);

            if (splitLine[0].equals(neuronID(neuronInd))) {
                while (neuronInd++ < numNeurons)
                    layerWeights[neuronInd] = Arrays.stream(splitLine).mapToDouble(Double::parseDouble).toArray();
            }

            if (splitLine[0].equals("bias,")) {
                layerBias = Arrays.stream(splitLine).mapToDouble(Double::parseDouble).toArray();
                break;
            }
        }

        network.loadDenseLayer(denseInd, layerWeights, layerBias);

        return ++denseInd;
    }

    private static String denseID(int number){
        return "dense" + number + split;
    }

    private static String neuronID(int number){
        return "neuron" + number + split;
    }

    private static String convID(int number){
        return "conv" + number + split;
    }

    private static String biasID(int number){
        return "bias" + number + split;
    }

    private static String filterID(int number){
        return "filter" + number + split;
    }

    public static void newSave(){
        saveNum++;
    }
}
