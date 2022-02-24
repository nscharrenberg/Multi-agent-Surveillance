package com.nscharrenberg.um.multiagentsurveillance.headless.exceptions;

public class BoardNotBuildException extends Exception {


    /**
     * Construct a new exception with "The game board does not have any tiles. Initialize the game first!" as its detail message.
     * The cause of this exception would be that the board has not been initialized yet and is therefore empty.
     */
    public BoardNotBuildException() {
        super("The Game Board does not have any tiles. Initialize the game first!");
    }
}
