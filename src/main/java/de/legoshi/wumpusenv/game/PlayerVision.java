package de.legoshi.wumpusenv.game;

import javafx.geometry.Point2D;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerVision {

    private FieldStatus top;
    private FieldStatus bottom;
    private FieldStatus left;
    private FieldStatus right;
    private Point2D scream;

}
