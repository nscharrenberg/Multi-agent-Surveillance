package com.nscharrenberg.um.multiagentsurveillance.agents.DQN.utils.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.CNN.ConvLayer;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.CNN.Kernel;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.FCN.ActivationLayer;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.FCN.DenseLayer;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.neuralNetwork.Network;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.utils.properties.DQNNetworkProperties;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.nscharrenberg.um.multiagentsurveillance.agents.DQN.utils.properties.DQNNetworkProperties.*;

public class DQNNetworkAdapter extends TypeAdapter<Network> {
    final Gson gson;

    public DQNNetworkAdapter() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(ConvLayer.class, new DQNConvLayerAdapter());
        builder.registerTypeAdapter(DenseLayer.class, new DQNDenseLayerAdapter());
        builder.registerTypeAdapter(ActivationLayer.class, new DQNActivationLayerAdapter());
        this.gson = builder.create();
    }

    @Override
    public void write(JsonWriter writer, Network value) throws IOException {
        writer.beginObject();

        writer.name(C1_FILTERS.getKey());
        writer.value(value.getC1Filters());

        writer.name(C2_FILTERS.getKey());
        writer.value(value.getC2Filters());

        writer.name(C3_FILTERS.getKey());
        writer.value(value.getC3Filters());

        writer.name(LEARNING_RATE.getKey());
        writer.value(value.getLearningRate());

        writer.name(KERNEL_SIZE.getKey());
        writer.value(value.getKernelSize());

        writer.name(OUTPUT_LENGTH.getKey());
        writer.value(value.getOutputLength());

        writer.name(CONV3_LENGTH.getKey());
        writer.value(value.getConv3Length());

        writer.name(ACTIVATION_LAYER.getKey());

        writer.beginObject();

        writer.value(gson.toJson(value.getActivationLayer(), ActivationLayer.class));

        writer.endObject();

        writer.name(DENSE_LAYER.getKey());

        writer.beginArray();

        for (int i = 0; i < value.getDenseLayers().length; i++) {
            writer.value(gson.toJson(value.getDenseLayers()[i], DenseLayer.class));
        }

        writer.endArray();

        writer.name(CONV_LAYER.getKey());

        writer.beginArray();

        for (int i = 0; i < value.getConvLayers().length; i++) {
            writer.value(gson.toJson(value.getConvLayers()[i], ConvLayer.class));
        }

        writer.endArray();

        writer.endObject();
    }

    @Override
    public Network read(JsonReader reader) throws IOException {
        Network network = new Network();

        reader.beginObject();

        String fieldName = null;

        while (reader.hasNext()) {
            JsonToken token = reader.peek();

            if (token.equals(JsonToken.NAME)) {
                fieldName = reader.nextName();
            }
            if (fieldName != null) {
                // Assumption that if fieldNAme isn't null that we always want to peek at this point.
                token = reader.peek();

                if (fieldName.equals(LEARNING_RATE.getKey())) {
                    network.setLearningRate(reader.nextDouble());
                } else if (fieldName.equals(KERNEL_SIZE.getKey())) {
                    network.setKernelSize(reader.nextInt());
                } else if (fieldName.equals(OUTPUT_LENGTH.getKey())) {
                    network.setOutputLength(reader.nextInt());
                } else if (fieldName.equals(C1_FILTERS.getKey())) {
                    network.setC1Filters(reader.nextInt());
                } else if (fieldName.equals(C2_FILTERS.getKey())) {
                    network.setC2Filters(reader.nextInt());
                } else if (fieldName.equals(C3_FILTERS.getKey())) {
                    network.setC3Filters(reader.nextInt());
                } else if (fieldName.equals(CONV3_LENGTH.getKey())) {
                    network.setConv3Length(reader.nextInt());
                } else if (fieldName.equals(ACTIVATION_LAYER.getKey())) {
                    reader.beginObject();
                    network.setActivationLayer(gson.fromJson(reader.nextString(), ActivationLayer.class));
                    reader.endObject();
                } else if (fieldName.equals(DENSE_LAYER.getKey())) {
                    reader.beginArray();

                    List<DenseLayer> denseLayers = new ArrayList<>();

                    while (reader.hasNext()) {
                        denseLayers.add(gson.fromJson(reader.nextString(), DenseLayer.class));

                    }

                    reader.endArray();

                    network.setDenseLayers(denseLayers.toArray(new DenseLayer[0]));
                } else if (fieldName.equals(CONV_LAYER.getKey())) {
                    reader.beginArray();

                    List<ConvLayer> convLayers = new ArrayList<>();

                    while (reader.hasNext()) {
                        convLayers.add(gson.fromJson(reader.nextString(), ConvLayer.class));

                    }

                    reader.endArray();

                    network.setConvLayers(convLayers.toArray(new ConvLayer[0]));
                }
            }
        }

        reader.endObject();

        return network;
    }
}
