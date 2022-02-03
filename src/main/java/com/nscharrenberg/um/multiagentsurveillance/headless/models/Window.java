package com.nscharrenberg.um.multiagentsurveillance.headless.models;

public class Window extends Collision {
    private boolean isOpen;
    private boolean isBlinded;
    private boolean isBroken;

    public Window(Tile tile) {
        super(tile);
    }

    public boolean isBroken() {
        return isBroken;
    }

    public void setBroken(boolean broken) {
        isBroken = broken;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public boolean isBlinded() {
        return isBlinded;
    }

    public void setBlinded(boolean blinded) {
        isBlinded = blinded;
    }
}
