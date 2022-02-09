package com.nscharrenberg.um.multiagentsurveillance.headless.exceptions;

public class CollisionException extends Exception {
    /**
     * Constructs a new exception with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     */
    public CollisionException() {
        super("Unable to move due to a collision");
    }
}
