package de.legoshi.wumpusenv.game;

import de.legoshi.wumpusenv.utils.Instruction;
import javafx.geometry.Point2D;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.Arrays;

@Getter @Setter
public class Player extends Entity {

    private String id;
    private String name;
    private Process process;
    private File file;

    private Point2D customSpawn;

    private boolean scream;
    private boolean pickup;
    private boolean climb;
    private boolean alive;

    private boolean hasGold;
    private boolean hasEscaped;

    public Player(String id, Point2D customSpawn) {
        this.playerVision = new PlayerVision();
        this.id = id;
        this.alive = true;
        this.scream = false;
        this.pickup = false;
        this.climb = false;

        this.customSpawn = customSpawn;

        this.hasGold = false;
        this.hasEscaped = false;
    }

    //B;MOVE;SCREAM(b);PICKUP(b);CLIMB(b)
    //C;[TOP,TOP];[BOTTOM];[RIGHT];[LEFT];[X,Y];HASGOLD(b);ESCAPED(b);ALIVE(b)
    public String perceptionToString() {

        if(playerVision.getScream() == null) this.playerVision.setScream(new Point2D(0,0));

        String s = "C;";
        s = s + listToString(playerVision.getSelf()) + ";";
        s = s + listToString(playerVision.getTop()) + ";";
        s = s + listToString(playerVision.getBottom()) + ";";
        s = s + listToString(playerVision.getRight()) + ";";
        s = s + listToString(playerVision.getLeft()) + ";";
        s = s + "[" + playerVision.getScream().getX() + "," + playerVision.getScream().getY() + "]" + ";";
        s = s + hasGold + ";";
        s = s + hasEscaped + ";";
        s = s + alive;
        return s;
    }

    public String listToString(FieldStatus fieldStatus) {
        if(fieldStatus != null) {
            return Arrays.toString(fieldStatus.getArrayList().toArray());
        } else return "null";
    }

    public void setStringToPlayer(String s) {
        String[] args = s.split(";");
        this.setInstruction(Instruction.toInstruction(args[1]));
        this.scream = args[2].equals("true");
        this.pickup = args[3].equals("true");
        this.climb = args[4].equals("true");
    }

    public void resetInstructions() {
        this.setInstruction(Instruction.NOTHING);
        this.scream = false;
        this.pickup = false;
        this.climb = false;
    }

}
