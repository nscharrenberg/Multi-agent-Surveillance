package com.nscharrenberg.um.multiagentsurveillance.agents.DQN.utils.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.FCN.ActivationLayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.nscharrenberg.um.multiagentsurveillance.agents.DQN.utils.properties.DQNActivationLayerProperties.*;

public class DQNActivationLayerAdapter extends TypeAdapter<ActivationLayer> {
    @Override
    public void write(JsonWriter writer, ActivationLayer value) throws IOException {
        writer.beginObject();

        writer.name(NUM_INPUTS.getKey());
        writer.value(value.getNumInputs());

        writer.name(INPUTS.getKey());

        writer.beginArray();

        for (double input : value.getInputs()) {
            writer.value(input);
        }

        writer.endArray();


        writer.name(OUTPUTS.getKey());

        writer.beginArray();

        for (double output : value.getOutputs()) {
            writer.value(output);
        }

        writer.endArray();

        writer.endObject();
    }

    @Override
    public ActivationLayer read(JsonReader reader) throws IOException {
        ActivationLayer activationLayer = new ActivationLayer();

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
                    activationLayer.setNumInputs(reader.nextInt());
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

                    activationLayer.setInputs(inputArray);
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

                    activationLayer.setOutputs(outputArray);
                }
            }
        }

        reader.endObject();

        return activationLayer;
    }
}
