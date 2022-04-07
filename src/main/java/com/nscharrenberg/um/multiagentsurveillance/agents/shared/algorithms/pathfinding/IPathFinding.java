package com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.pathfinding;

import com.nscharrenberg.um.multiagentsurveillance.agents.shared.utils.QueueNode;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Area;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Player;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;

import java.util.Optional;

public interface IPathFinding {
    Optional<QueueNode> execute(Area<Tile> board, Player player, Tile target);
}
