package de.legoshi.wumpusenv.utils;

import de.legoshi.wumpusenv.game.FieldStatus;
import javafx.geometry.Point2D;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Position {

    private Point2D point2D;
    private FieldStatus fieldStatus;

}
