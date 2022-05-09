package com.nscharrenberg.um.multiagentsurveillance.agents.DQN.training;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

public class MoveHistory {

    private int capacity;
    private int index;
    private int count;
    private Experience[] history;
    private Random random;


    public MoveHistory(int capacity){
        this.capacity = capacity;
        this.history = new Experience[capacity];
        this.index = 0;
        this.count = 0;
        this.random = new Random();
    }

    public void push(Experience experience){
        if (index >= capacity)
            index = 0;
        history[index++] = experience;
        count++;
    }

    public Experience[] randomSample(int batchSize){
        int index;
        int bound;
        Set<Integer> indexSet = new HashSet<>();

        if (count < capacity)
            bound = count;
        else bound = capacity;

        while (indexSet.size() <= batchSize){
            index = random.nextInt(bound);
            if (!indexSet.contains(index))
                indexSet.add(index);
        }

        Iterator<Integer> iterator = indexSet.iterator();
        Experience[] out = new Experience[batchSize];

        int i = 0;
        while (iterator.hasNext())
            out[i++] = history[iterator.next()];

        return out;
    }

    public boolean hasBatch(int batchSize){
        return count >= batchSize;
    }

}
