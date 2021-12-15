package main.java.de.legoshi.javabot;

/**
 * @author Benjamin Müller
 * @author Julia
 * @author Yazar Strulik
 */

/* TODO
1. Eckige Klammern aus String entfernen
2. In Array nach String suchen
3. Decision-Funktion bauen und Returnwert setzen (up, down, left, oder right) + Zufälligkeitsfunktion
*/



public class Bot {




    private FileHelper fileHelper;
    //gameState = "C;[self];[x,y];hasgold;escaped;alive" = C;[START,PLAYER];[0.0,0.0];false;false;true
    public String gameState;
    //statearray[1] = "[START,PLAYER]"
    public String[] statearray = gameState.split(";");
    public String command;
    public int width;
    public int height;
    public String up = "B;UP;false;false;false";
    public String down = "B;DOWN;false;false;false";
    public String left = "B;LEFT;false;false;false";
    public String right = "B;RIGHT;false;false;false";
    public String scream = "B;NOTHING;true;false;false";
    public String pickup = "B;NOTHING;false;true;false";
    public String climb = "B;NOTHING;false;false;true";
    public boolean [][] visited = new boolean [width-1][height-1];
    public int [][] prob = new int [width-1][height-1];


    /*The values self, top, bottom, right, left can be repeated such as "["STENCH", "WIND"]". --> Was ist gemeint?*/

    public Bot(FileHelper fileHelper) {
        this.fileHelper = fileHelper;
    }

//Dangerfunktion: Setzt Gefährlichkeitswerte für bestimmte Felder außer diese wurden schon besucht

    public void danger(int x, int y) {
    	if(visited[(width/2)-1+x+1][(width/2)-1+y] == false) {
    		prob[(width/2)-1+x+1][(width/2)-1+y] += 1;
    	}
    	if(visited[(width/2)-1+x-1][(width/2)-1+y] == false) {
    		prob[(width/2)-1+x-1][(width/2)-1+y] += 1;
    	}
    	if(visited[(width/2)-1+x][(width/2)-1+y+1] == false) {
    		prob[(width/2)-1+x][(width/2)-1+y+1] += 1;
    	}
    	if(visited[(width/2)-1+x][(width/2)-1+y-1] == false) {
    		prob[(width/2)-1+x][(width/2)-1+y-1] += 1;
    	}

    }

//Decisionfunktion: Entscheidet sich für das Feld mit der niedrigsten Gefährlichkeit

    public String decision(int x, int y) {


    	if(prob[(width/2)-1+x+1][(width/2)-1+y] < prob[(width/2)-1+x-1][(width/2)-1+y]) {

    	}

    	if(prob[(width/2)-1+x][(width/2)-1+y+1] < prob[(width/2)-1+x][(width/2)-1+y-1]) {

    	}

        return null;
    }

    public void execute() {


    	int x = 0;
    	int y = 0;
        //Wenn Gold und am Eingangs- bzw. Ausgangspunkt -> rausklettern
    		if(statearray[1].equals("GOLD") && x == 0 && y == 0) {

    			command = climb;
        //Wenn Wind -> Felder mit Gefahr markieren
    		} else if(statearray[1].equals("WIND")) {

    			danger(x,y);
        //Wenn Gestank -> Felder mit Gefahr markieren
    		} else if(statearray[1].equals("STENCH")) {

    			danger(x,y);
        //Wenn Gold -> Gold aufheben
    		} else if(statearray[1].equals("GOLD")) {

    			command = pickup;

    		}
        //Weg mit wenigster Gefahr gehen


        command = "B;UP;false;false;false";
        fileHelper.log("test log");
    }

}
