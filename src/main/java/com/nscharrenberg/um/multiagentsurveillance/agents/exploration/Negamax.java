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
        //Save the current state of the game
        TileArea cloneState = state.clone();
        Player cloneAgent = agent.clone();

        List<Angle> allMoveSequence = new ArrayList<>();

        //Looping until the whole area will be explored
        while(state.getRegion().size() != state.getExploredArea().size()) {
            // Creating a root of the tree
            Node root = new Node(agent.getTile());

            //TODO Clone state and agent
            //Save the state before pushing to the exploration tree
            TileArea loopState = cloneState.clone();
            Player loopAgent = cloneAgent.clone();

            // Creating a tree
            Node finalMove = explorationTree(loopState, loopAgent, root, 0, agent.getView() + 1);

            //If the agent is stacked, we performs extensive exploration tree, to get out from the stack area
            if(finalMove.getExplorationCost() == 0){
                //TODO Clone state and agent
                loopState = cloneState.clone();
                loopAgent = cloneAgent.clone();
                finalMove = explorationTree(loopState, loopAgent, root, 0, state.getTheFurthestPointOfKnownArea().size());
            }
            //Checking on bugs in the exploration tree
            if(finalMove == null || finalMove.isRoot())
                throw new RuntimeException("The move is null OR it tries to execute Root Node");


            //Save list of moves
            List<Angle> moveSequence = new ArrayList<>();
            while (!finalMove.getParent().isRoot()) {
                moveSequence.add(finalMove.getAction());
                finalMove = finalMove.getParent();
            }

            //Save list of moves for the final result
            allMoveSequence.addAll(moveSequence);


            //TODO Executing move sequence with outcomes
            for (int i = moveSequence.size() - 1; i >= 0; i--) {
                //TODO IMPORTANT execute the move with the cloneState and cloneAgent
                /*
                1)Execute move with the view. (Don't forget set the normal view of the agent)
                2)Add tiles to the Explored Area
                 */
            }

        }

        return allMoveSequence;
    }


    public Node explorationTree(TileArea state, Player agent, Node node, int depth, int base) {

        //Base case
        if(base == depth)
            return node;

        //Base case for the rotation
        if(node.getRotationCount() == 3)
            return node;

        // Creating children for the node
        List<Node> children = createChildren(state, agent, node);

        //Checking for children, we always should have at least 3 or at most 4
        if(children.size() < 3 || children.size() > 4)
            throw new RuntimeException("Children size of the node is out of bounds");

        //Set up the minimum node
        Node comparedNode = new Node(Double.MAX_VALUE, Double.MIN_VALUE);


        // Simulating moves of children
        for (int i = 0; i < children.size(); i++) {
            Node childNode = children.get(i);

            //TODO Executing move without outcome and calculate the cost of the move
            //Set the state of the game which was before iterations
            //TODO Clone state and agent
            TileArea cloneState = state.clone();
            Player cloneAgent = agent.clone();

            /*
            1)Check if the action performs in known area(state)
            2)Calculate the size of unknown area(agent)

            3)Execute the move without outcome, but put unknown area which agent explored as known area.
            Solution -> state = known area without updating, agent = update the known area

            4)childNode.addExplorationCost(size of unknown area(agent)) childNode.addMovementCost()
            5)if action was rotation childNode.addRotationCount()
            6)if action was forward depth++
             */

            //Create a new branch
            Node evalNode = explorationTree(cloneState, cloneAgent, childNode, depth, base);

            //TODO experiments with ==>  Priority Move -> Exploration ||OR|| Exploration -> Move
            if(evalNode.getMovementCost() < comparedNode.getMovementCost()) {
                if (evalNode.getExplorationCost() > comparedNode.getExplorationCost()) {
                    comparedNode = evalNode;
                }
            }
        }
        return comparedNode;
    }



    private List<Node> createChildren(TileArea state, Player agent, Node node){

        //return action WITHOUT going forward to unknown area(state)
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

