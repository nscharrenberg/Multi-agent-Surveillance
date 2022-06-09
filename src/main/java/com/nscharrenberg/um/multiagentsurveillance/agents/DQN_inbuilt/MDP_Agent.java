package com.nscharrenberg.um.multiagentsurveillance.agents.DQN_inbuilt;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.TileArea;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Intruder;
import com.nscharrenberg.um.multiagentsurveillance.headless.repositories.GameRepository;
import org.deeplearning4j.gym.StepReply;
import org.deeplearning4j.rl4j.mdp.MDP;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import org.deeplearning4j.rl4j.space.ObservationSpace;

public class MDP_Agent implements MDP<GameRepository, Integer, DiscreteSpace> {

    @Override
    public ObservationSpace<GameRepository> getObservationSpace() {
        return null;
    }

    @Override
    public DiscreteSpace getActionSpace() {
        return null;
    }

    @Override
    public GameRepository reset() {
        return null;
    }

    @Override
    public void close() {

    }

    @Override
    public StepReply<GameRepository> step(Integer integer) {

        // Delta Sound Proximity & Delta Vision Proximity
        double dSP, dVP;

        // Positive if sound intensity from all sides and behind is decreasing
        dSP =  -soundProximity(state) / 100;

        // Positive if visible distance from all guards is decreasing
        dVP = -visionProximity(state) / 10;


        Intruder intruder = (Intruder) this.player;
        double reward = 0;

        if (player.getDirection().equals(intruder.getTargetAngle()))
            reward = 0.005;

        double targetDistance = intruder.getDistanceToTarget();

        if (targetDistance < minTargetDistance){
            reward += 0.05;
        }

        TileArea targetArea = mapRepository.getTargetArea();

        if (targetArea.within(player.getTile().getX(), player.getTile().getY()))
            reward += 1;

        reward += dSP + dVP ;


        return null;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public MDP<GameRepository, Integer, DiscreteSpace> newInstance() {
        return null;
    }
}
