package com.nscharrenberg.um.multiagentsurveillance.agents.DQN.utils.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.CNN.Kernel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.nscharrenberg.um.multiagentsurveillance.agents.DQN.utils.properties.DQNKernelProperties.*;

public class DQNKernelAdapter extends TypeAdapter<Kernel> {
    @Override
    public void write(JsonWriter writer, Kernel value) throws IOException {
        writer.beginObject();

        writer.name(SIZE.getKey());
        writer.value(value.getSize());

        writer.name(WEIGHTS.getKey());

        writer.beginArray();

       for (int i = 0; i < value.getWeights().length; i++) {
           writer.beginArray();
           for (int j = 0; j < value.getWeights()[i].length; i++) {
               writer.value(value.getWeights()[i][j]);
           }
           writer.endArray();
       }

        writer.endArray();

        writer.endObject();
    }

    @Override
    public Kernel read(JsonReader reader) throws IOException {
        Kernel kernel = new Kernel();

        reader.beginObject();

        String fieldName = null;

        while (reader.hasNext()) {
            JsonToken token = reader.peek();

            if (token.equals(JsonToken.NAME)) {
                fieldName = reader.nextName();
            }

            if (fieldName != null) {
                token = reader.peek();

                if (fieldName.equals(SIZE.getKey())) {
                    kernel.setSize(reader.nextInt());
                } else if (fieldName.equals(WEIGHTS.getKey())) {
                    reader.beginArray();

                    List<List<Double>> weightsRow = new ArrayList<>();

                    while (reader.hasNext()) {
                        reader.peek();

                        reader.beginArray();

                        List<Double> weightsCol = new ArrayList<>();

                        while (reader.hasNext()) {
                            reader.peek();

                            weightsCol.add(reader.nextDouble());
                        }

                        reader.endArray();

                        weightsRow.add(weightsCol);
                    }

                    reader.endArray();

                    // Convert back to primitive - TODO: Improve efficiency
                    double[][] inputArray = new double[weightsRow.size()][weightsRow.get(0).size()];

                    for (int i = 0; i < weightsRow.size(); i++) {
                        for (int j = 0; j < weightsRow.get(i).size(); j++) {
                            inputArray[i][j] = weightsRow.get(i).get(j);
                        }
                    }

                    kernel.setWeights(inputArray);
                }
            }
        }

        reader.endObject();

        return kernel;
    }
}
