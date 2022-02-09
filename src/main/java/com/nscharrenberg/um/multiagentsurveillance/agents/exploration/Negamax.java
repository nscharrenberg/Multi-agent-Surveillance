package com.nscharrenberg.um.multiagentsurveillance.agents.exploration;

import com.nscharrenberg.um.multiagentsurveillance.agents.exploration.Tree.Node;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.TileArea;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Negamax {


    //TODO parameters: State of the board, Agent
    public List<Angle> calculateExplorationPath(TileArea state, Player agent){

        //TODO Clone state and agent
        TileArea cloneState = state.clone();
        Player cloneAgent = agent.clone();

        List<Angle> allMoveSequence = new ArrayList<>();

        while(state.getRegion().size() != state.getExploredArea().size()) {
            // Creating a root of the tree
            Node root = new Node(agent.getTile());

            //TODO Clone state and agent
            TileArea loopState = state.clone();
            Player loopAgent = agent.clone();

            // Creating a tree
            Node finalMove = explorationTree(state, agent, root, 0, agent.getView() + 1);

            //If the agent is stacked, we performs extensive exploration tree, to get out from the stack area
            if(finalMove.getExplorationCost() == 0)
                finalMove = explorationTree(state, agent, root, 0, state.getTheFurthestPointOfKnownArea().size());


            //Save list of moves
            List<Angle> moveSequence = new ArrayList<>();
            while (!finalMove.getParent().isRoot()) {
                moveSequence.add(finalMove.getAction());
                finalMove = finalMove.getParent();
            }

            //Save list of moves for the final result
            allMoveSequence.addAll(moveSequence);

            //Set up to initial state which was before the tree
            state = loopState;
            agent = loopAgent;

            //TODO Executing move sequence with outcomes
            for (int i = moveSequence.size() - 1; i >= 0; i--) {
                /*
                1)Execute move with the view. (Don't forget set the normal view of the agent)
                2)Add tiles to the Explored Area
                 */
            }

        }

        //Set up to initial state of the game
        state = cloneState;
        agent = cloneAgent;

        return allMoveSequence;
    }


    public Node explorationTree(TileArea state, Player agent, Node node, int depth, int base) {

        //Base case: Size of the map == Size of explored area
        if(base == depth)
            return node;

        // Creating children for the node
        List<Node> children = createChildren(state, agent, node);

        if(children.size() == 0)
            throw new RuntimeException("Children size of the node is equal to 0");

        //Set up the minimum node
        Node minValue = new Node(Double.MAX_VALUE);

        //TODO Clone state and agent
        TileArea cloneState = state.clone();
        Player cloneAgent = agent.clone();

        // Simulating moves of children
        for (int i = 0; i < children.size(); i++) {
            Node childNode = children.get(i);

            //TODO Executing move without outcome and calculate the cost of the move
            state = cloneState;
            agent = cloneAgent;

            /*
            1)Check if the action performs in known area
            2)Execute the move without view. Solution -> Set view of the agent 0
            3)Define the size of known area
            4)childNode.addExplorationCost(size of known area) childNode.addMovementCost(1)
            5)if action was forward depth++
             */

            //Create a new branch
            Node evalNode = explorationTree(state, agent, childNode, depth, base);

            //TODO experiments with ==>  Priority Move -> Exploration ||OR|| Exploration -> Move
            if(evalNode.getMovementCost() < minValue.getMovementCost()) {
                if (evalNode.getExplorationCost() < minValue.getExplorationCost()) {
                    minValue = evalNode;
                }
            }
        }
        return minValue;
    }



    private List<Node> createChildren(TileArea state, Player agent, Node node){

        //return action without going forward to unknown area
        List<Angle> listOfLegalMoves = agent.getAllPossibleMoves(state, agent);

        for (int i = 0; i < listOfLegalMoves.size(); i++) {

            //Get the action
            Angle action = listOfLegalMoves.get(i);

            //Define the tile after execution the action
            int x = agent.getTile().getX() + action.getxIncrement();
            int y = agent.getTile().getY() + action.getyIncrement();
            Optional<Tile> tile = state.getByCoordinates(x, y);

            //Add child to the node(tile, action parent)
            tile.ifPresent(value -> node.addChild(new Node(value, action, node)));
        }

        return node.getChildren();
    }
}

