package com.nscharrenberg.um.multiagentsurveillance.headless.repositories;

import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IPlayerRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Guard;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Intruder;

import java.util.ArrayList;
import java.util.List;

public class PlayerRepository implements IPlayerRepository {
    private List<Intruder> intruders;
    private List<Guard> guards;

    public PlayerRepository() {
        this.intruders = new ArrayList<>();
        this.guards = new ArrayList<>();
    }

    @Override
    public List<Intruder> getIntruders() {
        return intruders;
    }

    @Override
    public void setIntruders(List<Intruder> intruders) {
        this.intruders = intruders;
    }

    @Override
    public List<Guard> getGuards() {
        return guards;
    }

    @Override
    public void setGuards(List<Guard> guards) {
        this.guards = guards;
    }
}
