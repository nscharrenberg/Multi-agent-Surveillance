package com.nscharrenberg.um.multiagentsurveillance.headless.exceptions;

public class ItemAlreadyOnTileException extends Exception {
    public ItemAlreadyOnTileException() {
        super("Given item is already on this tile.");
    }
}
