package com.nscharrenberg.um.multiagentsurveillance.agents.DQN.utils.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.CNN.ConvLayer;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.CNN.Filter;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.CNN.Kernel;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.utils.properties.DQNConvLayerProperties;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.utils.properties.DQNFilterProperties;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.nscharrenberg.um.multiagentsurveillance.agents.DQN.utils.properties.DQNFilterProperties.INPUT;
import static com.nscharrenberg.um.multiagentsurveillance.agents.DQN.utils.properties.DQNFilterProperties.KERNELS;

public class DQNConvLayerAdapter extends TypeAdapter<ConvLayer> {
    private final Gson gson;

    public DQNConvLayerAdapter() {
        super();

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Filter.class, new DQNFilterAdapter());
        this.gson = builder.create();
    }

    @Override
    public void write(JsonWriter writer, ConvLayer value) throws IOException {
        writer.beginObject();

        writer.name(DQNConvLayerProperties.CHANNELS.getKey());
        writer.value(value.getChannels());

        writer.name(DQNConvLayerProperties.NUM_FILTERS.getKey());
        writer.value(value.getNumFilters());

        writer.name(DQNConvLayerProperties.INPUT_LENGTH.getKey());
        writer.value(value.getInputLength());

        writer.name(DQNConvLayerProperties.KERNEL_SIZE.getKey());
        writer.value(value.getKernelSize());

        writer.name(DQNConvLayerProperties.LENGTH.getKey());
        writer.value(value.getLength());

        writer.name(DQNConvLayerProperties.FILTERS.getKey());

        writer.beginArray();

        for (int i = 0; i < value.getFilters().length; i++) {
            writer.value(gson.toJson(value.getFilters()[i], Kernel.class));
        }

        writer.endArray();

        writer.name(DQNConvLayerProperties.OUTPUT.getKey());

        writer.beginArray();

        for (int i = 0; i < value.getOutput().length; i++) {
            writer.beginArray();
            for (int j = 0; j < value.getOutput()[i].length; i++) {
                writer.beginArray();
                for (int k = 0; k < value.getOutput()[i][j].length; k++) {
                    writer.value(value.getOutput()[i][j][k]);
                }
                writer.endArray();
            }
            writer.endArray();
        }

        writer.endArray();

        writer.endObject();
    }

    @Override
    public ConvLayer read(JsonReader reader) throws IOException {
        ConvLayer convLayer = new ConvLayer();

        reader.beginObject();

        String fieldName = null;

        while (reader.hasNext()) {
            JsonToken token = reader.peek();

            if (token.equals(JsonToken.NAME)) {
                fieldName = reader.nextName();
            }

            if (fieldName != null) {
                token = reader.peek();

                if (fieldName.equals(DQNConvLayerProperties.CHANNELS.getKey())) {
                    convLayer.setChannels(reader.nextInt());
                } else if (fieldName.equals(DQNConvLayerProperties.INPUT_LENGTH.getKey())) {
                    convLayer.setInputLength(reader.nextInt());
                } else if (fieldName.equals(DQNConvLayerProperties.NUM_FILTERS.getKey())) {
                    convLayer.setNumFilters(reader.nextInt());
                } else if (fieldName.equals(DQNConvLayerProperties.KERNEL_SIZE.getKey())) {
                    convLayer.setKernelSize(reader.nextInt());
                } else if (fieldName.equals(DQNConvLayerProperties.FILTERS.getKey())) {
                    reader.beginArray();

                    List<Filter> filters = new ArrayList<>();

                    while (reader.hasNext()) {
                        filters.add(gson.fromJson(reader.nextString(), Filter.class));

                    }

                    reader.endArray();

                    convLayer.setFilters(filters.toArray(new Filter[0]));
                } else if (fieldName.equals(DQNConvLayerProperties.OUTPUT.getKey())) {
                    reader.beginArray();

                    List<List<List<Double>>> biasRow = new ArrayList<>();

                    while (reader.hasNext()) {
                        reader.peek();

                        reader.beginArray();

                        List<List<Double>> biasCol = new ArrayList<>();

                        while (reader.hasNext()) {
                            reader.peek();

                            reader.beginArray();

                            List<Double> biasDepth = new ArrayList<>();

                            while (reader.hasNext()) {
                                reader.peek();

                                biasDepth.add(reader.nextDouble());
                            }

                            reader.endArray();

                            biasCol.add(biasDepth);
                        }

                        reader.endArray();

                        biasRow.add(biasCol);
                    }

                    reader.endArray();

                    // Convert back to primitive - TODO: Improve efficiency
                    double[][][] inputArray = new double[biasRow.size()][biasRow.get(0).size()][biasRow.get(0).get(0).size()];

                    for (int i = 0; i < biasRow.size(); i++) {
                        for (int j = 0; j < biasRow.get(i).size(); j++) {
                            for (int k = 0; k < biasRow.get(i).get(j).size(); k++) {
                                inputArray[i][j][k] = biasRow.get(i).get(j).get(k);
                            }
                        }
                    }

                    convLayer.setOutput(inputArray);
                }
            }
        }

        reader.endObject();

        return convLayer;
    }
}
