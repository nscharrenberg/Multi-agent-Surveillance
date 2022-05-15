package com.nscharrenberg.um.multiagentsurveillance.agents.DQN.utils.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.CNN.Filter;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.CNN.Kernel;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.FCN.Neuron;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.utils.properties.DQNFilterProperties;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DQNFilterAdapter extends TypeAdapter<Filter> {
    private Gson gson;

    public DQNFilterAdapter() {
        super();

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Kernel.class, new DQNKernelAdapter());
        this.gson = builder.create();
    }

    @Override
    public void write(JsonWriter writer, Filter value) throws IOException {
        writer.beginObject();

        writer.name(DQNFilterProperties.CHANNELS.getKey());
        writer.value(value.getChannels());

        writer.name(DQNFilterProperties.KERNEL_SIZE.getKey());
        writer.value(value.getKernelSize());

        writer.name(DQNFilterProperties.INPUT_LENGTH.getKey());
        writer.value(value.getInputLength());

        writer.name(DQNFilterProperties.SIZE.getKey());
        writer.value(value.getSize());

        writer.name(DQNFilterProperties.LEARNING_RATE.getKey());
        writer.value(value.getLearningRate());

        writer.name(DQNFilterProperties.INPUT.getKey());

        writer.beginArray();

        for (int i = 0; i < value.getInput().length; i++) {
            writer.beginArray();
            for (int j = 0; j < value.getInput()[i].length; i++) {
                writer.beginArray();
                for (int k = 0; k < value.getInput()[i][j].length; k++) {
                    writer.value(value.getInput()[i][j][k]);
                }
                writer.endArray();
            }
            writer.endArray();
        }

        writer.endArray();

        writer.name(DQNFilterProperties.BIAS.getKey());

        writer.beginArray();

        for (int i = 0; i < value.getBias().length; i++) {
            writer.beginArray();
            for (int j = 0; j < value.getBias()[i].length; i++) {
                writer.value(value.getBias()[i][j]);
            }
            writer.endArray();
        }

        writer.endArray();

        writer.name(DQNFilterProperties.KERNELS.getKey());

        writer.beginArray();

        for (int i = 0; i < value.getBias().length; i++) {
            writer.value(gson.toJson(value.getKernels()[i], Kernel.class));
        }

        writer.endArray();

        writer.endObject();
    }

    @Override
    public Filter read(JsonReader reader) throws IOException {
        Filter filter = new Filter();

        reader.beginObject();

        String fieldName = null;

        while (reader.hasNext()) {
            JsonToken token = reader.peek();

            if (token.equals(JsonToken.NAME)) {
                fieldName = reader.nextName();
            }

            if (fieldName != null) {
                token = reader.peek();

                if (fieldName.equals(DQNFilterProperties.CHANNELS.getKey())) {
                    filter.setChannels(reader.nextInt());
                } else if (fieldName.equals(DQNFilterProperties.KERNEL_SIZE.getKey())) {
                    filter.setKernelSize(reader.nextInt());
                } else if (fieldName.equals(DQNFilterProperties.INPUT_LENGTH.getKey())) {
                    filter.setInputLength(reader.nextInt());
                } else if (fieldName.equals(DQNFilterProperties.SIZE.getKey())) {
                    filter.setSize(reader.nextInt());
                } else if (fieldName.equals(DQNFilterProperties.LEARNING_RATE.getKey())) {
                    filter.setLearningRate(reader.nextDouble());
                } else if (fieldName.equals(DQNFilterProperties.INPUT.getKey())) {

                } else if (fieldName.equals(DQNFilterProperties.KERNELS.getKey())) {
                    reader.beginArray();

                    List<Kernel> outputs = new ArrayList<>();

                    while (reader.hasNext()) {
                        outputs.add(gson.fromJson(reader.nextString(), Kernel.class));

                    }

                    reader.endArray();

                    filter.setKernels(outputs.toArray(new Kernel[0]));
                } else if (fieldName.equals(DQNFilterProperties.BIAS.getKey())) {

                }
            }
        }

        reader.endObject();

        return filter;
    }
}
