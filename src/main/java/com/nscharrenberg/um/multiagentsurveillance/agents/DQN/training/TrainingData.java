package com.nscharrenberg.um.multiagentsurveillance.agents.DQN.training;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class TrainingData {

    private int capacity, count;
    private final Random random;
    public ArrayList<Experience> experiences;

    public TrainingData(int capacity) {
        this.capacity = capacity;
        this.count = 0;
        this.random = new Random();
        init();
    }

    public TrainingData() {
        this.capacity = 0;
        this.count = 0;
        this.random = new Random();
        init();
    }

    public void clearBatch(){
        experiences.clear();
        count = 0;
    }

    private void init(){
        capacity = capacity == 0 ? 10000 : capacity;
        experiences = new ArrayList<>(capacity);
    }

    public void push(Experience experience) {

        if (count++ >= capacity) {
            popClear();
            count = capacity;
        }

        experiences.add(experience);
    }

    public TrainingData randomSample(int batchSize) {

        Set<Integer> indexSet = new HashSet<>();
        TrainingData randomSample = new TrainingData();

        while (indexSet.size() != batchSize)
            indexSet.add(random.nextInt(count));

        Iterator<Integer> iterator = indexSet.iterator();

        while (iterator.hasNext()) {
            randomSample.push(experiences.get(iterator.next()));
        }

        if (ThreadLocalRandom.current().nextDouble() < 0.5)
            clearBatch();

        return randomSample;
    }

    public boolean hasBatch(int batchSize) {
        return count >= batchSize;
    }

    private void popClear() {
        experiences.remove(0);
    }
}