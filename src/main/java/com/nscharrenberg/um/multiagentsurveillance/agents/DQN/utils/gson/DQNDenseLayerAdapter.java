package com.nscharrenberg.um.multiagentsurveillance.agents.DQN.utils.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.FCN.DenseLayer;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.FCN.Neuron;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.nscharrenberg.um.multiagentsurveillance.agents.DQN.utils.properties.DQNDenseLayerProperties.*;

public class DQNDenseLayerAdapter extends TypeAdapter<DenseLayer> {
    private Gson gson;

    public DQNDenseLayerAdapter() {
        super();

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Neuron.class, new DQNNeuronAdapter());
        this.gson = builder.create();
    }

    @Override
    public void write(JsonWriter writer, DenseLayer value) throws IOException {
        writer.beginObject();

        writer.name(BIAS.getKey());

        writer.beginArray();

        for (double weight : value.getBias())
            writer.value(weight);

        writer.endArray();

        writer.name(NEURONS.getKey());

        writer.beginArray();

        for (Neuron input : value.getNeurons()) {
            writer.value(gson.toJson(input, Neuron.class));
        }

        writer.endArray();

        writer.endObject();
    }

    @Override
    public DenseLayer read(JsonReader reader) throws IOException {
        DenseLayer denseLayer = new DenseLayer();

        reader.beginObject();

        String fieldName = null;

        while (reader.hasNext()) {
            JsonToken token = reader.peek();

            if (token.equals(JsonToken.NAME)) {
                fieldName = reader.nextName();
            }

            if (fieldName != null) {
                token = reader.peek();
                if (fieldName.equals(NEURONS.getKey())) {
                    reader.beginArray();

                    List<Neuron> outputs = new ArrayList<>();

                    while (reader.hasNext()) {
                        outputs.add(gson.fromJson(reader.nextString(), Neuron.class));

                    }

                    reader.endArray();

                    denseLayer.setNeurons(outputs.toArray(new Neuron[0]));
                }
            }
        }

        reader.endObject();

        return denseLayer;
    }
}
