package com.nscharrenberg.um.multiagentsurveillance.gui.dataGUI;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class ParseJSONData {

    public List<List<Coordinates>> parseData(String directoryPath) throws Exception {

        List<List<Coordinates>> result = new ArrayList<>();
        for (int i = 0; i < DataHelper.agentToCompare.length; i++) {
            JSONArray JSONArray = new JSONArray(new JSONTokener
                    (new FileReader(directoryPath + "\\Agents\\Agent#" + DataHelper.agentToCompare[i])));

            int length = JSONArray.length();

            List<Coordinates> data = new ArrayList<>();
            for (int j = 0; j < length; j++) {
                JSONObject object = JSONArray.getJSONObject(j);
                data.add(new Coordinates(object.get(DataHelper.X_and_Y[0]),
                        object.get(DataHelper.X_and_Y[1])));
            }
            result.add(data);
        }
        return result;
    }
}
