package com.nscharrenberg.um.multiagentsurveillance.headless.repositories;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Guard;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Intruder;

import java.util.ArrayList;
import java.util.List;

public class PlayerRepository {
    private List<Intruder> intruders;
    private List<Guard> guards;

    public PlayerRepository() {
        this.intruders = new ArrayList<>();
        this.guards = new ArrayList<>();
    }

    public List<Intruder> getIntruders() {
        return intruders;
    }

    public void setIntruders(List<Intruder> intruders) {
        this.intruders = intruders;
    }

    public List<Guard> getGuards() {
        return guards;
    }

    public void setGuards(List<Guard> guards) {
        this.guards = guards;
    }
}
