package com.nscharrenberg.um.multiagentsurveillance.agents.shared.utils;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Tile;

import java.util.LinkedList;
import java.util.Queue;

public class TreeNode extends QueueNode {
    private TreeNode parent;
    private LinkedList<TreeNode> children = new LinkedList<>();


    public TreeNode(Tile tile, Angle entrancePosition, TreeNode parent) {
        super(tile, entrancePosition);
        this.parent = parent;
    }

    public TreeNode getParent() {
        return parent;
    }

    public Queue<TreeNode> getChildren() {
        return children;
    }

}
