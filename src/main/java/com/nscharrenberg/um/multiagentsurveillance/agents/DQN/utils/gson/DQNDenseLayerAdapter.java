package com.nscharrenberg.um.multiagentsurveillance.agents.DQN.utils.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.FCN.DenseLayer;

import java.io.IOException;

public class DQNDenseLayerAdapter extends TypeAdapter<DenseLayer> {
    @Override
    public void write(JsonWriter out, DenseLayer value) throws IOException {

    }

    @Override
    public DenseLayer read(JsonReader in) throws IOException {
        return null;
    }
}
