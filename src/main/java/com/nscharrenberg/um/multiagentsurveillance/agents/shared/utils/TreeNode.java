package com.nscharrenberg.um.multiagentsurveillance.agents.shared.utils;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle.Angle;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;

public class TreeNode extends QueueNode {
    private TreeNode parent;

    public TreeNode(Tile tile, Angle entrancePosition, TreeNode parent) {
        super(tile, entrancePosition);
        this.parent = parent;
    }

    public TreeNode getParent() {
        return parent;
    }
}
