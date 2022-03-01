package com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.structures.FibonacciHeap;

import com.nscharrenberg.um.multiagentsurveillance.agents.shared.utils.TreeNode;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Tile;

public class Node{

    protected Node parent, left, right, child;

    protected int key, degree;

    private TreeNode treeNode;

    protected boolean mark;

    public Node(int key, TreeNode treeNode){
        this.key = key;
        this.treeNode = treeNode;
        this.degree = 0;
        this.parent = this.child = null;
        this.left = this.right = this;
        this.mark = false;
    }

    public TreeNode getTreeNode() {
        return treeNode;
    }
}
