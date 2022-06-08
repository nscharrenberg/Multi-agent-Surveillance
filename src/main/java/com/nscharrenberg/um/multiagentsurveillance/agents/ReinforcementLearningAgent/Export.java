package com.nscharrenberg.um.multiagentsurveillance.agents.ReinforcementLearningAgent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Export {

    private final File file;
    private FileWriter writer;

    private List<List<String>> values = new ArrayList<>();

    public Export(){
        this.file = new File("src/main/java/com/nscharrenberg/um/multiagentsurveillance/agents/ReinforcementLearningAgent/Rloutput.csv");
        try{
            this.writer = new FileWriter(file, true);
            writer.append("\n");
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public void addValue(String name, double value){
        values.add(Arrays.asList(name, String.valueOf(value)));
    }

    public void parseValues(){
        try{
            for(List<String> record : values){
                writer.append(String.join("", record));
                writer.append("\n");
            }
            writer.flush();
            values = new ArrayList<>();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

}