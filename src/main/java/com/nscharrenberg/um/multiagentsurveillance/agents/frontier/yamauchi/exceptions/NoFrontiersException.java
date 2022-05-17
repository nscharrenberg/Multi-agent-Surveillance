package com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi.exceptions;

public class NoFrontiersException extends Exception {

    public NoFrontiersException() {
        super("Unable to find any frontiers");
    }
}
