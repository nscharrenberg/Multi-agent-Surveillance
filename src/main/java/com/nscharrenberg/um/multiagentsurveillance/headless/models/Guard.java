package com.nscharrenberg.um.multiagentsurveillance.headless.models;

public class Guard extends Player {
    public Guard(Tile position, Angle direction) {
        // TODO: Read speed from Configuration
        super(position, direction, 10);
    }
}
