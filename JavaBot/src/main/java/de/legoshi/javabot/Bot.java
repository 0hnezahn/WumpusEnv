package main.java.de.legoshi.javabot;

import java.util.Arrays;

/**
 * @author Benjamin Müller
 * @author Julia Koch
 * @author Yazar Strulik
 */

/* TODO
1. Eckige Klammern aus String entfernen
2. In Array nach String suchen
3. Decision-Funktion bauen und Returnwert setzen (up, down, left, oder right) + Zufälligkeitsfunktion
4. Grenzenerkennung (bumpfunktion) mit gegenüberliegender Wand
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


    /*The values self, top, bottom, right, left can be repeated such as "["STENCH", "WIND"]". --> Was ist gemeint?*/

    public Bot(FileHelper fileHelper) {
        this.fileHelper = fileHelper;
    }


    public void constructField() {
        this.visited = new boolean[width * 2][height * 2];
        this.prob = new int[width * 2][height * 2];
        this.gamearray = gameState.split(";");
        this.statestring = gamearray[1].replaceAll("\\[", "").replaceAll("\\]", "");
        this.statearray = statestring.split(",");
    }


//Dangerfunktion: Setzt Gefährlichkeitswerte für bestimmte Felder außer diese wurden schon besucht

    public void danger(int x, int y) {
        if (!visited[width + x + 1][height + y]) {
            prob[width + x + 1][height + y] += 1;
        }
        if (!visited[width + x - 1][height + y]) {
            prob[width + x - 1][height + y] += 1;
        }
        if (!visited[width + x][height + y + 1]) {
            prob[width + x][height + y + 1] += 1;
        }
        if (!visited[width + x][height + y - 1]) {
            prob[width + x][height + y - 1] += 1;
        }

    }

//Decisionfunktion: Entscheidet sich für das Feld mit der niedrigsten Gefährlichkeit

    public String decision(int x, int y) {
        int a = 100;
        int b = 100;
        int c = 100;
        int d = 100;
        if (!visited[width + x + 1][height + y]) {
            a = prob[width + x + 1][height + y];
        }
        if (!visited[width + x - 1][height + y]) {
            b = prob[width + x - 1][height + y];
        }
        if (!visited[width + x][height + y + 1]) {
            c = prob[width + x][height + y + 1];
        }
        if (!visited[width + x][height + y - 1]) {
            d = prob[width + x][height + y - 1];
        }
        int e = getMin(a, b, c, d);
        if (a == e) {
            return "right";
        }
        if (b == e) {
            return "left";
        }
        if (c == e) {
            return "up";
        }
        if (d == e) {
            return "down";
        }
        fileHelper.log("Fehler beim Decisionmaking");
        return null; //Ersetzen
    }

    private int getMin(int a, int b, int c, int d) {
        return Math.min(a, Math.min(b, Math.min(c, d)));
    }

    public void execute() {

        int x, y;
        x = 10;
        y = 10;

        if (s0 == 0) {
            constructField();
            s0 += 1;
        }

        //Wenn Gold und am Eingangs- bzw. Ausgangspunkt -> rausklettern
        if (Arrays.asList(statearray).contains("GOLD") && x == 0 && y == 0){

            command = climb;
            //Wenn Wind -> Felder mit Gefahr markieren
        } else if (Arrays.asList(statearray).contains("WIND")) {

            danger(x, y);
            //Wenn Gestank -> Felder mit Gefahr markieren
        } else if (Arrays.asList(statearray).contains("STENCH")) {

            danger(x, y);
            //Wenn Gold -> Gold aufheben
        } else if (Arrays.asList(statearray).contains("GOLD")) {

            command = pickup;

        }
        //Weg mit wenigster Gefahr gehen


    }

}
