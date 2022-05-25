package com.nscharrenberg.um.multiagentsurveillance.agents.ReinforcementLearningAgent;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.Marker;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Player;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.AngleConverter;


import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;

public class RLmodel {
    private double baseline = 0.20;
    private double newline = 0;

    private Queue<Action> redirect;

    public RLmodel() {
        redirect = new PriorityQueue<>();
    }

    // Agenttype:
    // Guard = 0
    // Intruder = 1
    public void parameterEvaluation(Parameter input, Player player, int agenttype) {

        if (input.owner == player) {
            // Skip its own inputs
        } else if (agenttype == 0) {
            newline = input.type.getPriority() * normalizeStrength(input.strength);

            // Positive feedback
            if(newline > baseline) {
                redirect.addAll(AngleConverter.split(input.direction));
                baseline = newline;
            }

            // Negative feedback
            if((-1 * newline) > baseline) {
                redirect.addAll(AngleConverter.split(AngleConverter.AngleInverter(input.direction)));
                baseline = (-1 * newline);
            }

            // Print assessment
            System.out.println("Input: " + input.type);
            System.out.println("Strength: " + normalizeStrength(input.strength));
            System.out.println("Priority: " + input.type.getPriority());
            System.out.println("New value: " + newline);

        } else if (agenttype == 1) {
            // Intruder
        }
    }

    private double normalizeStrength(int strength) {
        return (1-(strength * (1/(double)(Marker.getRange()+1))));
    }

    public Queue<Action> getRedirect() {
        return redirect;
    }

    public void reset() {
        this.baseline = 0.20;
        this.newline = 0;
        this.redirect = new PriorityQueue<>();
    }

}
