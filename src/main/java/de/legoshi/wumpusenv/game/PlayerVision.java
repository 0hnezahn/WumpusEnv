package de.legoshi.wumpusenv.game;

import javafx.geometry.Point2D;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author Benjamin Müller
 */

@Getter
@Setter
public class PlayerVision implements Serializable {

    private FieldStatus self;
    private Point2D scream;

}
