package com.nscharrenberg.um.multiagentsurveillance.agents.shared.utils;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Tile;

import java.util.LinkedList;
import java.util.Queue;

public class TreeNode extends QueueNode {
    private TreeNode parent = null;
    private Queue<TreeNode> children = new LinkedList<>();

    public TreeNode(Tile tile, Angle entrancePosition) {
        super(tile, entrancePosition);
    }

    public TreeNode(Tile tile, Angle entrancePosition, Queue<Angle> moves) {
        super(tile, entrancePosition, moves);
    }

    public TreeNode(Tile tile, Angle entrancePosition, TreeNode parent) {
        super(tile, entrancePosition);
        this.parent = parent;
    }

    public TreeNode(Tile tile, Angle entrancePosition, Queue<Angle> moves, TreeNode parent) {
        super(tile, entrancePosition, moves);
        this.parent = parent;
    }

    public TreeNode(Tile tile, Angle entrancePosition, TreeNode parent, Queue<TreeNode> children) {
        super(tile, entrancePosition);
        this.parent = parent;
        this.children = children;
    }

    public TreeNode(Tile tile, Angle entrancePosition, Queue<Angle> moves, TreeNode parent, Queue<TreeNode> children) {
        super(tile, entrancePosition, moves);
        this.parent = parent;
        this.children = children;
    }

    public TreeNode(Tile tile, Angle entrancePosition, Queue<Angle> moves, Queue<TreeNode> children) {
        super(tile, entrancePosition, moves);
        this.children = children;
    }

    public TreeNode getParent() {
        return parent;
    }

    public void setParent(TreeNode parent) {
        this.parent = parent;
    }

    public Queue<TreeNode> getChildren() {
        return children;
    }

    public void setChildren(Queue<TreeNode> children) {
        this.children = children;
    }
}
