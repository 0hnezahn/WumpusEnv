package main.java.de.legoshi.javabot;

import java.util.Arrays;

/*
TODO:
Explorefunktion (erforschen)
erkennen mit Löchern umzugehen
erkennen mit dem Wumpus umzugehen
erkennen mit Gold umzugehen
erkennen wie man laufen soll wenn man die Win-Condition erreicht hat
erkennen mit Wänden (dem Rand) umzugehen
sich merken welche Felder grundsätzlich keine Löcher beinhalten können
*/


public class Bot {

    private FileHelper fileHelper;
    //gameState = "C;[self];[x,y];hasgold;escaped;alive" = C;[START,PLAYER];[0.0,0.0];false;false;true
    public String gameState;
    //gamearray[1] = "[START,PLAYER]"
    public String[] gamearray;
    public String command;

    public String statestring;
    public String[] statearray;

    //Width und Height werden beim execute gesetzt
    public int width;
    public int height;

    public String up = "B;UP;false;false;false";
    public String down = "B;DOWN;false;false;false";
    public String left = "B;LEFT;false;false;false";
    public String right = "B;RIGHT;false;false;false";
    public String scream = "B;NOTHING;true;false;false";
    public String pickup = "B;NOTHING;false;true;false";
    public String climb = "B;NOTHING;false;false;true";

    public Bot(FileHelper fileHelper) {
        this.fileHelper = fileHelper;
    }

    public void execute() {
        this.gamearray = gameState.split(";");
        this.statestring = gamearray[1].replaceAll("\\[", "").replaceAll("\\]", "");
        this.statearray = statestring.split(",");
        
    }

}