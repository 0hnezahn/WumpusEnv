package de.legoshi.wumpusenv.game;

import javafx.geometry.Point2D;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Wumpus extends Entity {

    private int id;
    private Point2D wumpusSpawn;

    public Wumpus(Point2D wumpusSpawn) {
        this.playerVision = new PlayerVision();
        this.wumpusSpawn = wumpusSpawn;
        this.currentPosition = wumpusSpawn;
        this.oldPosition = wumpusSpawn;
    }

    public int spawnDistance() {
        return Math.max(
                (int)Math.abs(currentPosition.getX() - wumpusSpawn.getX()),
                (int)Math.abs(currentPosition.getY() - wumpusSpawn.getY())
        );
    }

}
