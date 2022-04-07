package com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.structures.FibonacciHeap;

import com.nscharrenberg.um.multiagentsurveillance.agents.shared.utils.TreeNode;

public class Node{

    protected Node parent, left, right, child;

    protected double key;
    protected int degree;

    private TreeNode treeNode;

    protected boolean mark;

    public Node(double key, TreeNode treeNode){
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
