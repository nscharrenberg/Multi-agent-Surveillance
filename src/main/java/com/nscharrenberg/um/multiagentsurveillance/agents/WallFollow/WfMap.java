package com.nscharrenberg.um.multiagentsurveillance.agents.WallFollow;


import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.ArrayList;

public class WfMap {
    private ArrayList<Integer> horizontalWallsCovered = new ArrayList<>();
    private ArrayList<Integer> verticalWallsCovered = new ArrayList<>();
    public WfGraph<Tile, DefaultWeightedEdge> G;
    public WfMap(WfGraph G) {
        this.G = G;;
    }

    public void add_or_adjust_Vertex(Tile position) {
        Tile cell = G.getVertexAt(position);
        if(cell != null) {
            //modifyVertex(cell);
        } else {
            addNewVertex(position);
        }
        connectNeighbouringVertices(position);
    }



    protected void addNewVertex(Tile position)
    {
        Tile vertexCentre = G.determineVertexCentre(position);
        G.addVertex(vertexCentre);
        G.vertices.put(G.keyGenerator(position), position);
    }


    public void connectNeighbouringVertices(Tile currentPosition) {
        for(java.lang.Object cardinalObject: G.cardinalDirections.values()) {
            Tile cardinal = (Tile) cardinalObject;
            Tile neighbouringCell;
            Tile resultingPosition = new Tile(currentPosition.getX()+cardinal.getX(),currentPosition.getY()+cardinal.getY());;
            String neighbourKey = G.keyGenerator(resultingPosition);
            if (G.vertices.containsKey(neighbourKey)) {
                neighbouringCell = (Tile) G.vertices.get(neighbourKey);
            } else {
                neighbouringCell = new Tile(resultingPosition.getX(),resultingPosition.getY());
                G.vertices.put(neighbourKey,neighbouringCell);
                G.addVertex(neighbouringCell);
            }
            if(neighbouringCell != null && !G.containsEdge(currentPosition, neighbouringCell)) {
                DefaultWeightedEdge edge = (org.jgrapht.graph.DefaultWeightedEdge) G.addEdge(currentPosition, neighbouringCell);
                G.setEdgeWeight(edge, G.travelDistance);
            }
        }
    }

    public ArrayList<Tile> getVerticesWithUnexploredNeighbours() {
        return G.getVerticesWithUnexploredNeighbours();
    }

    public void markWallAsCovered(Tile agentPos) {
        // TODO modify hor. and ver. walls covered to be hashmaps with agent names who covered the wall
        if (!horizontalWallsCovered.contains(agentPos.getX())) {
            horizontalWallsCovered.add(agentPos.getX());
        }
        if (!verticalWallsCovered.contains(agentPos.getY())) {
            verticalWallsCovered.add(agentPos.getY());
        }
    }
}
