package com.nscharrenberg.um.multiagentsurveillance.headless.repositories;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Area;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Item;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Tile;

public class MapRepository {

    // List of all tiles -> gui draws loop of all tiles, and then all items on that tile
    //private List<Tile> board;

    // Collection of all items
    private Area<Item> target;
    private Area<Item> guardSpawn;
    private Area<Item> intruderSpawn;

    
}
