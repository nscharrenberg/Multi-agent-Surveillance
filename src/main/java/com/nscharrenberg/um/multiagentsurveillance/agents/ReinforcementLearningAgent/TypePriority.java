package com.nscharrenberg.um.multiagentsurveillance.agents.ReinforcementLearningAgent;

public enum TypePriority {
    SoundWave(0.50),
    MarkerDeadEnd(-0.85),
    MarkerTarget(0.60),
    MarkerGuard(-0.45),
    MarkerIntruder(0.92),
    MarkerTeleporter(0.20),
    MarkerShaded(-0.30);

    private final double priority;

    TypePriority(double priority) {
        this.priority = priority;
    }

    public double getPriority() {
        return priority;
    }

}

