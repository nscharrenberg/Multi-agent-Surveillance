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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

        writer.name(DQNConvLayerProperties.FILTERS.getKey());

        writer.beginArray();

        for (int i = 0; i < value.getFilters().length; i++) {
            writer.value(gson.toJson(value.getFilters()[i], Kernel.class));
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

                if (fieldName.equals(DQNConvLayerProperties.FILTERS.getKey())) {
                    reader.beginArray();

                    List<Filter> filters = new ArrayList<>();

                    while (reader.hasNext()) {
                        filters.add(gson.fromJson(reader.nextString(), Filter.class));
                    }

                    reader.endArray();

                    convLayer.setFilters(filters.toArray(new Filter[0]));
                }
            }
        }

        reader.endObject();

        return convLayer;
    }
}
