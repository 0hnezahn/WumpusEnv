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
4. Grenzenerkennung (bumpfunktion) mit gegenüberliegender Wand
*/



public class Bot {




    private FileHelper fileHelper;
    //gameState = "C;[self];[x,y];hasgold;escaped;alive" = C;[START,PLAYER];[0.0,0.0];false;false;true
    public String gameState;
    //statearray[1] = "[START,PLAYER]"
    public String[] statearray = gameState.split(";");
    //Botanweisungen werden aus command gezogen
    public String command;

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




    //Initialisierungsvariable
    public int s0 = 0;


    /*The values self, top, bottom, right, left can be repeated such as "["STENCH", "WIND"]". --> Was ist gemeint?*/

    public Bot(FileHelper fileHelper) {
        this.fileHelper = fileHelper;
    }


    public void constructField(){
        public boolean [][] visited = new boolean [width*2][height*2];
        public int [][] prob = new int [width*2][height*2];
    }


//Dangerfunktion: Setzt Gefährlichkeitswerte für bestimmte Felder außer diese wurden schon besucht

    public void danger(int x, int y) {
    	if(visited[width+x+1][height+y] == false) {
    		prob[width+x+1][height+y] += 1;
    	}
    	if(visited[width+x-1][height+y] == false) {
    		prob[width+x-1][height+y] += 1;
    	}
    	if(visited[width+x][height+y+1] == false) {
    		prob[width+x][height+y+1] += 1;
    	}
    	if(visited[width+x][height+y-1] == false) {
    		prob[width+x][height+y-1] += 1;
    	}

    }

//Decisionfunktion: Entscheidet sich für das Feld mit der niedrigsten Gefährlichkeit

    public String decision(int x, int y) {
      int a, b, c, d = 100;
      if(visited[width+x+1][height+y] == false) {
    		a = prob[width+x+1][height+y];
    	}
    	if(visited[width+x-1][height+y] == false) {
    		b = prob[width+x-1][height+y];
    	}
    	if(visited[width+x][height+y+1] == false) {
    		c = prob[width+x][height+y+1];
    	}
    	if(visited[width+x][height+y-1] == false) {
    		d = prob[width+x][height+y-1];
    	}
      e = Math.min(a, b, c, d);
      if(a == e){
        return "right";
      }
      if(b == e){
        return "left";
      }
      if(c == e){
        return "up"
      }
      if(d == e){
        return "down"
      }
      fileHelper.log("Fehler beim Decisionmaking");
      return null; //Ersetzen
    }

    public void execute() {

      if(s0 == 0){
          constructField();
          s0 += 1;
      }

        //Wenn Gold und am Eingangs- bzw. Ausgangspunkt -> rausklettern
    		if(statearray[1].equals("GOLD") && x == x0 && y == y0) {

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


    }

}
