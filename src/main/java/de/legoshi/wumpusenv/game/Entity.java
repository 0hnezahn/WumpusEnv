package de.legoshi.wumpusenv.game;

import de.legoshi.wumpusenv.utils.Instruction;
import javafx.geometry.Point2D;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
abstract class Entity {

    protected Point2D oldPosition;
    protected Point2D currentPosition;

    protected PlayerVision playerVision;
    private Instruction instruction;

    public void updatePosition() {
        oldPosition = currentPosition;
        switch (instruction) {
            case UP -> currentPosition.add(0,1);
            case DOWN -> currentPosition.subtract(0,1);
            case LEFT -> currentPosition.subtract(1,0);
            case RIGHT -> currentPosition.add(1,0);
        }
    }

}
