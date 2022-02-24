package com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Tile;

import java.util.LinkedList;
import java.util.List;

public class TreeNode {

    private Tile tile;
    private Angle direction;
    private TreeNode parent = null;
    private LinkedList<TreeNode> children = new LinkedList<>();

    public TreeNode(Tile tile,Angle direction) {
        this.tile = tile;
        this.direction = direction;
    }

    public TreeNode(Tile tile, Angle direction, TreeNode parent) {
        this.tile = tile;
        this.direction = direction;
        this.parent = parent;
    }


    public TreeNode getParent() {
        return parent;
    }

    public void addChildren(TreeNode children) {
        this.children.add(children);
    }

    public Tile getTile() {
        return tile;
    }

    public Angle getDirection() {
        return direction;
    }

}
