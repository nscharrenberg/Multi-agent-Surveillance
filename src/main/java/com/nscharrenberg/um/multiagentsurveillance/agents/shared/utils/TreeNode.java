package com.nscharrenberg.um.multiagentsurveillance.agents.shared.utils;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Tile;

public class TreeNode extends QueueNode {
    private TreeNode parent;

    public TreeNode(Tile tile, Action entrancePosition, TreeNode parent) {
        super(tile, entrancePosition);
        this.parent = parent;
    }

    public TreeNode getParent() {
        return parent;
    }
}
