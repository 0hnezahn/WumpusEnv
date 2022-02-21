package de.legoshi.wumpusenv.game;

import de.legoshi.wumpusenv.utils.Instruction;
import javafx.geometry.Point2D;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.Arrays;

/**
 * @author Benjamin Müller
 */

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

    private String info;

    private boolean hasGold;
    private boolean hasEscaped;

    public Player(String id, Point2D customSpawn) {
        this.playerVision = new PlayerVision();
        this.id = id;
        this.alive = true;
        this.scream = false;
        this.pickup = false;
        this.climb = false;

        this.info = "";

        this.customSpawn = customSpawn;

        this.hasGold = false;
        this.hasEscaped = false;
    }

    public String perceptionToString() {

        if(playerVision.getScream() == null) this.playerVision.setScream(new Point2D(0,0));

        String s = "C;";
        s = s + listToString(playerVision.getSelf()).replace(" ", "") + ";";
        s = s + "[" + (int)this.getCurrentPosition().getX() + "," + (int)this.getCurrentPosition().getY() + "];";
        s = s + "[" + info + "];";
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
        this.info = args[5];
    }

    public void resetInstructions() {
        this.setInstruction(Instruction.NOTHING);
        this.scream = false;
        this.pickup = false;
        this.climb = false;
        this.info = "";
    }

}
