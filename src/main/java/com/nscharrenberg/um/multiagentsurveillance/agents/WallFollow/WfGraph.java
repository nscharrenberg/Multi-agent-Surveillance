package com.nscharrenberg.um.multiagentsurveillance.agents.WallFollow;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.Collision.Collision;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.Item;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.util.*;

public class WfGraph<Object, DefaultWeightedEdge> extends SimpleWeightedGraph
{
   // public Tile initialWallFollowPos;
    public int travelDistance=0;
   // public double obstaclePheromoneValue = 1000.0;
    public HashMap<String, Tile> vertices = new HashMap<>();
    public HashMap<String, Tile> cardinalDirections = new HashMap<>();

    public WfGraph(int distance) {
        super(org.jgrapht.graph.DefaultWeightedEdge.class);
        this.travelDistance = distance;
        populateCardinalVectors();
    }

    private void populateCardinalVectors()
    {
        cardinalDirections.put("North", new Tile(0, -travelDistance));
        cardinalDirections.put("East", new Tile(travelDistance, 0));
        cardinalDirections.put("South", new Tile(0, travelDistance));
        cardinalDirections.put("West", new Tile(-travelDistance, 0));
    }

    public Tile getVertexAt(Tile position)
    {
        return vertices.get(keyGenerator(position));
    }

    public ArrayList<Tile> getVerticesWithUnexploredNeighbours()
    {
        // TODO currently checking if vertex has less than 4 neighbours
        //  but should also check if they're direct neighbours or neighbours through portals?
        ArrayList<Tile> unexploredFrontier = new ArrayList<>();
        for (String v : vertices.keySet()) {
            Tile vertex = vertices.get(v);
            if (noWallDetected(vertex) && edgesOf(vertex).size() < 4) {
                unexploredFrontier.add(vertex);
            }
        }
        return unexploredFrontier;
    }

    public boolean noWallDetected(Tile tile) {

        boolean flag = true;
        for (Item items : tile.getItems()) {
            if (items instanceof Collision) {
                flag = false;
                break;
            }
        }
        return flag;
    }


    public String keyGenerator(Tile position)
    {
        Tile centrePosition = determineVertexCentre(position);
        return centrePosition.getX() + " " + centrePosition.getY();
    }


    public Tile determineVertexCentre(Tile position)
    {
        int x_centre = calculateDimensionCentre(position.getX());
        int y_centre = calculateDimensionCentre(position.getY());

        return new Tile(x_centre, y_centre);
    }

    private int calculateDimensionCentre(double axisPosition)
    {
        int centreDistance = travelDistance / 2;
        int axis_start = (int)(axisPosition / travelDistance) * travelDistance;
        if(axisPosition >=0) {
            return axis_start + centreDistance;
        }
        else {
            return axis_start - centreDistance;
        }
    }




}
