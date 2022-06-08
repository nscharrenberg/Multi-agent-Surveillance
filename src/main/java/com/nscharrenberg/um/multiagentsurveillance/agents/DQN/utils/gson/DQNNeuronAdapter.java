package com.nscharrenberg.um.multiagentsurveillance.agents.DQN.utils.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.FCN.Neuron;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static com.nscharrenberg.um.multiagentsurveillance.agents.DQN.utils.properties.DQNNeuronProperties.*;

public class DQNNeuronAdapter extends TypeAdapter<Neuron> {
    @Override
    public void write(JsonWriter writer, Neuron value) throws IOException {
        writer.beginObject();

        writer.name(WEIGHTS.getKey());

        writer.beginArray();

        for (double weight : value.getWeights()) {
            writer.value(weight);
        }

        writer.endArray();

        writer.endObject();
    }

    @Override
    public Neuron read(JsonReader reader) throws IOException {
        Neuron neuron = new Neuron();

        reader.beginObject();

        String fieldName = null;

        while (reader.hasNext()) {
            JsonToken token = reader.peek();

            if (token.equals(JsonToken.NAME)) {
                fieldName = reader.nextName();
            }

            if (fieldName != null) {
            // TODO: Something here??
                if (fieldName.equals(WEIGHTS.getKey())) {
                    reader.beginArray();

                    List<Double> weights = new ArrayList<>();

                    while (reader.hasNext()) {
                    reader.peek();
                    weights.add(reader.nextDouble());
                    }

                    reader.endArray();

                    double[] weightsArray = new double[weights.size()];

                    for (int i = 0; i < weights.size(); i++) {
                    weightsArray[i] = weights.get(i);
                    }

                    neuron.setWeights(weightsArray);
                }
            }
        }


        reader.endObject();

        return neuron;
    }
}
