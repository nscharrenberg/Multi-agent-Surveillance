package com.nscharrenberg.um.multiagentsurveillance.agents.DQN.training;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;

import java.util.*;

public class TrainingData {

    private int capacity;
    private int index;
    private int count;
    private Random random;

    public ArrayList<double[][][]> states, nextStates;
    public ArrayList<Action> actions;
    public ArrayList<Double> rewards;
    public ArrayList<Boolean> ends;

    public TrainingData(int capacity) {
        this.capacity = capacity;
        this.index = 0;
        this.count = 0;
        this.random = new Random();
        init();
    }

    public TrainingData() {
        this.capacity = 0;
        this.index = 0;
        this.count = 0;
        this.random = new Random();
        init();
    }

    private void init(){
        int initCapacity = capacity == 0 ? 10000 : capacity;
        states = new ArrayList<>(initCapacity);
        nextStates = new ArrayList<>(initCapacity);
        actions = new ArrayList<>(initCapacity);
        rewards = new ArrayList<>(initCapacity);
    }

    public void push(Experience experience) {

        if (capacity != 0 && count >= capacity)
            popClear();
        else index++;

        states.add(experience.state);
        actions.add(experience.action);
        rewards.add(experience.reward);
        nextStates.add(experience.nextState);
        ends.add(experience.done);
        count++;
    }

    public void push(double[][][] states, Action action, double reward, double[][][] nextState, boolean done){
        push(new Experience(states,action,reward,nextState,done));
    }

    public TrainingData randomSample(int batchSize) {

        int bound;
        Set<Integer> indexSet = new HashSet<>();
        TrainingData randomSample = new TrainingData();

        if (count < capacity)
            bound = count;
        else bound = capacity;

        while (indexSet.size() != batchSize)
            indexSet.add(random.nextInt(bound));

        Iterator<Integer> iterator = indexSet.iterator();
        Experience sampleExperience;

        int i;
        while (iterator.hasNext()) {
            i = iterator.next();
            sampleExperience = new Experience(  states.get(i),
                                                actions.get(i),
                                                rewards.get(i),
                                                nextStates.get(i),
                                                ends.get(i));
            randomSample.push(sampleExperience);
        }

        return randomSample;
    }

    public boolean hasBatch(int batchSize) {
        return index >= batchSize;
    }

    private void popClear() {
        states.remove(0);
        actions.remove(0);
        rewards.remove(0);
        nextStates.remove(0);
        ends.remove(0);
    }
}