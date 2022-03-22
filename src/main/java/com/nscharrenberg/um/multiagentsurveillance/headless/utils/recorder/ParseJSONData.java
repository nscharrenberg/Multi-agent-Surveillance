package com.nscharrenberg.um.multiagentsurveillance.headless.utils.recorder;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.recorder.json.AgentJSON;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ParseJSONData {

    public List<List<AgentJSON>> parseData(String directoryPath) {

        List<List<AgentJSON>> data = new ArrayList<>();

        Gson gson = new Gson();

        File directory = new File(directoryPath + "\\Agents");

        int length = directory.list().length;

        for (int i = 0; i < length; i++) {
            try (Reader reader = new FileReader(directory.getPath() + "\\Agent#" + i + ".json")) {

                // Convert JSON File to Java Object
                List<AgentJSON> agent = gson.fromJson(reader, new TypeToken<List<AgentJSON>>() {}.getType());
                data.add(agent);
                System.out.println("Successfully read the Agent#" + i + ".json");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return data;
    }
}
