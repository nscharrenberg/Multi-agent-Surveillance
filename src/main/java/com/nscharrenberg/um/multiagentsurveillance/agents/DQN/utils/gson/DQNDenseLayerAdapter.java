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

import static com.nscharrenberg.um.multiagentsurveillance.agents.DQN.utils.properties.DQNActivationLayerProperties.OUTPUTS;
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

        writer.name(NUM_INPUTS.getKey());
        writer.value(value.getNumInputs());

        writer.name(NUM_OUTPUTS.getKey());
        writer.value(value.getNumNeurons());

        writer.name(INPUTS.getKey());

        writer.beginArray();

        for (double input : value.getInputs()) {
            writer.value(input);
        }

        writer.endArray();

        writer.name(OUTPUTS.getKey());

        writer.beginArray();

        for (double input : value.getOutputs()) {
            writer.value(input);
        }

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

                if (fieldName.equals(NUM_INPUTS.getKey())) {
                   denseLayer.setNumInputs(reader.nextInt());
                } else if (fieldName.equals(NUM_OUTPUTS.getKey())) {
                    denseLayer.setNumNeurons(reader.nextInt());
                } else if (fieldName.equals(INPUTS.getKey())) {
                    reader.beginArray();

                    List<Double> inputs = new ArrayList<>();

                    while (reader.hasNext()) {
                        reader.peek();
                        inputs.add(reader.nextDouble());
                    }

                    reader.endArray();

                    // Convert back to primitive - TODO: Improve efficiency
                    double[] inputArray = new double[inputs.size()];

                    for (int i = 0; i < inputs.size(); i++) {
                        inputArray[i] = inputs.get(i);
                    }

                    denseLayer.setInputs(inputArray);
                } else if (fieldName.equals(OUTPUTS.getKey())) {
                    reader.beginArray();

                    List<Double> outputs = new ArrayList<>();

                    while (reader.hasNext()) {
                        outputs.add(reader.nextDouble());
                    }

                    reader.endArray();

                    // Convert back to primitive - TODO: Improve efficiency
                    double[] outputArray = new double[outputs.size()];

                    for (int i = 0; i < outputs.size(); i++) {
                        outputArray[i] = outputs.get(i);
                    }

                    denseLayer.setOutputs(outputArray);
                } else if (fieldName.equals(NEURONS.getKey())) {
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
