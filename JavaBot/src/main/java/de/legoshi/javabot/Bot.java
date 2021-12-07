package main.java.de.legoshi.javabot;

public class Bot {

    private FileHelper fileHelper;

    public String gameState;
    public String[] statearray = gameState.split(";"); 
    public String command;
    public int width = 20;
    public int height = 20;
    public String up = "B;UP;false;false;false";
    public String down = "B;DOWN;false;false;false";
    public String left = "B;LEFT;false;false;false";
    public String right = "B;RIGHT;false;false;false";
    public String scream = "B;NOTHING;true;false;false";
    public String pickup = "B;NOTHING;false;true;false";
    public String climb = "B;NOTHING;false;false;true";
    public boolean [][] visited = new boolean [width-1][height-1]; //Benni fragen, ob wir die Spielfeldgröße erfahren
    public int [][] prob = new int [width-1][height-1];


    /*The values self, top, bottom, right, left can be repeated such as "["STENCH", "WIND"]". --> Was ist gemeint?*/
    
    public Bot(FileHelper fileHelper) {
        this.fileHelper = fileHelper;
    }
    
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
    
    public String decision(int x, int y) {
    	
    	
    	if(prob[(width/2)-1+x+1][(width/2)-1+y] < prob[(width/2)-1+x-1][(width/2)-1+y]) {
    		
    	}
    	
    	if(prob[(width/2)-1+x][(width/2)-1+y+1] < prob[(width/2)-1+x][(width/2)-1+y-1]) {
    		
    	}
    	
    }

    public void execute() {
    	
    	int x = 0;
    	int y = 0;
    	
    	while(statearray[5] == true && statearray[4] == false) {
    		
    		if(statearray [1] == "GOLD" && x == 0 && y == 0) {
    			
    			command = climb;
    			
    		} else if(statearray[1] == "WIND") {
    			
    			danger(x,y);
    			
    		} else if(statearray[1] == "STENCH") {
    			
    			danger(x,y);
    			
    		} else if(statearray[1] == "GOLD") {
    			
    			command = pickup;
    			
    		} else {
    			
    			
    			visited[(width/2)-1+x][(width/2)-1+y] = true;
    		}
    		
    	}
    	
        command = "B;UP;false;false;false";
        fileHelper.log("test log");
    }

}
