package de.legoshi.wumpusenv.game;

import javafx.geometry.Point2D;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class PlayerVision implements Serializable {

    private FieldStatus self;
    private FieldStatus top;
    private FieldStatus bottom;
    private FieldStatus left;
    private FieldStatus right;
    private Point2D scream;

}
