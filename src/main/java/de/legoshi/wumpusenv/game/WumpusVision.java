package de.legoshi.wumpusenv.game;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Benjamin Müller
 */

@Getter
@Setter
public class WumpusVision {

    private FieldStatus self;
    private FieldStatus top;
    private FieldStatus bottom;
    private FieldStatus left;
    private FieldStatus right;

}
