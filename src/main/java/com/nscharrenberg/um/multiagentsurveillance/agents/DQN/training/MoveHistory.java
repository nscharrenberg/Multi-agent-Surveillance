package com.nscharrenberg.um.multiagentsurveillance.agents.DQN.training;

public class MoveHistory {

    private int capacity;
    private Experience[] history;
    private int count;

    public MoveHistory(int capacity){
        this.capacity = capacity;
        this.history = new Experience[capacity];
        this.count = 0;
    }

    public void push(Experience experience){
        if (count < capacity)
            history[count] = experience;
        else history[count++ % capacity] = experience;
    }


}
