package com.nscharrenberg.um.multiagentsurveillance.headless.exceptions;

public class ItemNotOnTileException extends Exception {
    public ItemNotOnTileException() {
        super("Given item is not present on the tile.");
    }
}
