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
        this.gson = builder.create();
    }

    @Override
    public void write(JsonWriter writer, Network value) throws IOException {
        writer.beginObject();

        writer.value(gson.toJson(value.getActivationLayer(), ActivationLayer.class));

        writer.endObject();

        writer.name(DENSE_LAYER.getKey());

        writer.beginArray();

        for (int i = 0; i <= value.getDenseLayers().length; i++) {
            writer.value(gson.toJson(value.getDenseLayers()[i], DenseLayer.class));
        }

        writer.endArray();

        writer.name(CONV_LAYER.getKey());

        writer.beginArray();

        for (int i = 0; i <= value.getConvLayers().length; i++) {
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


                if (fieldName.equals(DENSE_LAYER.getKey())) {
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
