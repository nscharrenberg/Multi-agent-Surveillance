package com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.structures.FibonacciHeap;

import com.nscharrenberg.um.multiagentsurveillance.agents.shared.utils.TreeNode;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Tile;

public class Node{

    protected Node parent, left, right, child;

    protected int key, degree;

    private Angle direction;

    private TreeNode treeNode;

    protected final Tile tile;

    protected boolean mark;

    public Node(Tile tile, int key, Angle direction, TreeNode treeNode){
        this.key = key;
        this.tile = tile;
        this.treeNode = treeNode;
        this.direction = direction;
        this.degree = 0;
        this.parent = this.child = null;
        this.left = this.right = this;
        this.mark = false;
    }

    public TreeNode getTreeNode() {
        return treeNode;
    }

    public Angle getDirection() {
        return direction;
    }

    public Tile getTile() {
        return tile;
    }
}
