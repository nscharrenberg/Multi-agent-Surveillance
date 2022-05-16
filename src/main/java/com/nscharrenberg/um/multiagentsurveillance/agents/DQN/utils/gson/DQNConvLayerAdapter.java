package com.nscharrenberg.um.multiagentsurveillance.agents.DQN.utils.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.CNN.ConvLayer;

import java.io.IOException;

public class DQNConvLayerAdapter extends TypeAdapter<ConvLayer> {
    @Override
    public void write(JsonWriter writer, ConvLayer value) throws IOException {

    }

    @Override
    public ConvLayer read(JsonReader reader) throws IOException {
        return null;
    }
}
