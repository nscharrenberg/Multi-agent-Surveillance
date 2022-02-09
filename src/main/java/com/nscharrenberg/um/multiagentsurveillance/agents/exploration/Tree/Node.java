package com.nscharrenberg.um.multiagentsurveillance.agents.exploration.Tree;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Tile;

import java.util.ArrayList;
import java.util.List;

public class Node {

    private boolean root = false;
    private Tile position;
    private Angle action;
    private Node parent;
    private List<Node> children = new ArrayList<>();
    private double explorationCost;
    private double movementCost;
    private double rotationCount;


    public Node(Tile position) {
        this.root = true;
        this.position = position;
    }

    public Node(Tile position, Angle action, Node parent) {
        this.parent = parent;
        this.position = position;
        this.action = action;
        this.explorationCost += parent.explorationCost;
        this.movementCost += parent.movementCost;
        this.rotationCount = parent.rotationCount;
    }

    public double getRotationCount() {
        return rotationCount;
    }

    public void removeRotationCount(){
        this.rotationCount = 0;
    }

    public void addRotationCount() {
        this.rotationCount += 1;
    }

    public double getMovementCost() {
        return movementCost;
    }

    public void addMovementCost() {
        this.movementCost += 1;
    }

    public double getExplorationCost() {
        return explorationCost;
    }

    public void addExplorationCost(double explorationCost) {
        this.explorationCost += explorationCost;
    }

    public Node getParent() {
        return parent;
    }

    public boolean isRoot() {
        return root;
    }

    public Tile getPosition() {
        return position;
    }


    public Angle getAction() {
        return action;
    }


    public List<Node> getChildren() {
        return children;
    }

    public void addChild(Node child) {
        this.children.add(child);
    }
}