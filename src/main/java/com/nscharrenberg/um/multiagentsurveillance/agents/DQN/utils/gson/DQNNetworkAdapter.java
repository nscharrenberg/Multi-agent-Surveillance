package com.nscharrenberg.um.multiagentsurveillance.agents.DQN.utils.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.neuralNetwork.Network;

import java.io.IOException;

import static com.nscharrenberg.um.multiagentsurveillance.agents.DQN.utils.properties.DQNNetworkProperties.*;

public class DQNNetworkAdapter extends TypeAdapter<Network> {
    @Override
    public void write(JsonWriter out, Network value) throws IOException {

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

                    reader.endObject();
                } else if (fieldName.equals(DENSE_LAYER.getKey())) {

                } else if (fieldName.equals(CONV_LAYER.getKey())) {
                    // tODO: Store convolutional layers
                }
            }
        }

        reader.endObject();

        return network;
    }
}
