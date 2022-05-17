package com.nscharrenberg.um.multiagentsurveillance.headless.models.Player;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;

public class Guard extends Player {

    private boolean isHunting = false;

    public Guard(Tile position, Action direction) {
        // TODO: Read speed from Configuration
        super(position, direction, 10);
    }

    public boolean isHunting() {
        return isHunting;
    }

    public void setHunting(boolean hunting) {
        isHunting = hunting;
    }
}
