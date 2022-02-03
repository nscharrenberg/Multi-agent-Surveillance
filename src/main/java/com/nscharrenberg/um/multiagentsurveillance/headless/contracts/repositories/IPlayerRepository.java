package com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Guard;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Intruder;

import java.util.List;

public interface IPlayerRepository {
    List<Intruder> getIntruders();

    void setIntruders(List<Intruder> intruders);

    List<Guard> getGuards();

    void setGuards(List<Guard> guards);
}
