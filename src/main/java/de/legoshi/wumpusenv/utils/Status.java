package de.legoshi.wumpusenv.utils;

import java.io.Serializable;

/**
 * @author Benjamin Müller
 */

public enum Status implements Serializable {

    GOLD,
    STENCH,
    WIND,
    HOLE,
    WUMPUS,
    PLAYER,
    START,
    WALL_TOP,
    WALL_BOTTOM,
    WALL_LEFT,
    WALL_RIGHT;

}
