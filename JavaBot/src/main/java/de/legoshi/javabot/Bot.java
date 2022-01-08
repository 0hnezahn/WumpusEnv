package main.java.de.legoshi.javabot;

import java.util.Arrays;

/**
 * @author Benjamin Müller
 * @author Julia Koch
 * @author Yazar Strulik
 */

/* TODO
1. bump von Benni erkennen und Wände markieren
2. Felder als definitiv sicher einstufen -> Feld aufbauen mit eindeutig sicheren Feldern die noch nicht besucht wurden
3. Notschalter -> Wenn Gestank dann 2 oder 3 Felder weglaufen und als gefährliches Gebiet einstufen
-> Wenn Gold schnell nach Hause
bei beidem Löcher umgehen
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

    public boolean[][] visited;
    public int[][] prob;


    public String up = "B;UP;false;false;false";
    public String down = "B;DOWN;false;false;false";
    public String left = "B;LEFT;false;false;false";
    public String right = "B;RIGHT;false;false;false";
    public String scream = "B;NOTHING;true;false;false";
    public String pickup = "B;NOTHING;false;true;false";
    public String climb = "B;NOTHING;false;false;true";


    //Initialisierungsvariable
    public int s0 = 0;
    int x; //X Position des Bots
    int y; //Y Position des Bots
    int tx = 100; //X in Zeit t-1
    int ty = 100; //Y in Zeit t-1

    public Bot(FileHelper fileHelper) {
        this.fileHelper = fileHelper;
    }


    public void constructField() {
        this.visited = new boolean[width * 3][height * 3];
        this.prob = new int[width * 3][height * 3];
    }


//Dangerfunktion: Setzt Gefährlichkeitswerte für bestimmte Felder außer diese wurden schon besucht

    public void danger(int fx, int fy) {
        if (!visited[width + fx + 1][height + fy]) {
            prob[width + fx + 1][height + fy] += 1;
        }
        if (!visited[width + fx - 1][height + fy]) {
            prob[width + fx - 1][height + fy] += 1;
        }
        if (!visited[width + fx][height + fy + 1]) {
            prob[width + fx][height + fy + 1] += 1;
        }
        if (!visited[width + fx][height + fy - 1]) {
            prob[width + fx][height + fy - 1] += 1;
        }

    }

//Decisionfunktion: Entscheidet sich für das Feld mit der niedrigsten Gefährlichkeit

    public String decision(int fx, int fy) {
        int a = 100;
        int b = 100;
        int c = 100;
        int d = 100;
        if (!visited[width + fx + 1][height + fy]) {
            a = prob[width + fx + 1][height + fy];
        }
        if (!visited[width + fx - 1][height + fy]) {
            b = prob[width + fx - 1][height + fy];
        }
        if (!visited[width + fx][height + fy + 1]) {
            c = prob[width + fx][height + fy + 1];
        }
        if (!visited[width + fx][height + fy - 1]) {
            d = prob[width + fx][height + fy - 1];
        }
        int e = getMin(a, b, c, d);

        if (a == e) {
            x += 1;
            return right;
        }
        if (b == e) {
            x -= 1;
            return left;
        }
        if (c == e) {
            y += 1;
            return up;
        }
        if (d == e) {
            y -= 1;
            return down;
        }
        fileHelper.log("Fehler beim Decisionmaking");
        return null; //Ersetzen
    }

    private int getMin(int a, int b, int c, int d) {
        return Math.min(Math.min(a, b), Math.min(c, d));
    }

    private void notMoved(String move){
        if(x == tx && y == ty){
            if (move.equals(right)) {
                visited[width + x + 1][height + y] = true;
            }
            if (move.equals(left)) {
                visited[width + x - 1][height + y] = true;
            }
            if (move.equals(up)) {
                visited[width + x][height + y + 1] = true;
            }
            if (move.equals(down)) {
                visited[width + x][height + y - 1] = true;
            }
        }
    }

    public void execute() {
    	this.gamearray = gameState.split(";");
        this.statestring = gamearray[1].replaceAll("\\[", "").replaceAll("\\]", "");
        this.statearray = statestring.split(",");

        if (s0 == 0) {
            constructField();
            s0 += 1;
            x = 0;
            y = 0;
        }else {
            notMoved(command);
        }

        visited[width + x][height + y] = true;

        //Wenn Gold und am Eingangs- bzw. Ausgangspunkt -> rausklettern
        if (Arrays.asList(statearray).contains("GOLD") && x == 0 && y == 0){

            command = climb;
            //Wenn Wind -> Felder mit Gefahr markieren
        } else if (Arrays.asList(statearray).contains("WIND")) {

            danger(x, y);
            command = decision(x, y);
            //Wenn Gestank -> Felder mit Gefahr markieren
        } else if (Arrays.asList(statearray).contains("STENCH")) {

            danger(x, y);
            command = decision(x, y);
            //Wenn Gold -> Gold aufheben
        } else if (Arrays.asList(statearray).contains("GOLD")) {

            command = pickup;

        } else {

            command = decision(x, y);

        }
        //Weg mit wenigster Gefahr gehen

        tx = x;
        ty = y;

        fileHelper.log("X = " + x);
        fileHelper.log("Y = " + y);
    }

}
