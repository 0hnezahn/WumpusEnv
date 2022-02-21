package main.java.de.legoshi.javabot;

public class BotTesting {

    private FileHelper fileHelper;

    public String gameState;
    public String up = "B;UP;false;false;false";
    public String down = "B;DOWN;false;false;false";
    public String left = "B;LEFT;false;false;false";
    public String right = "B;RIGHT;false;false;false";
    public String scream = "B;NOTHING;true;false;false";
    public String pickup = "B;NOTHING;false;true;false";
    public String climb = "B;NOTHING;false;false;true";
    public String nothing = "B;NOTHING;false;false;false";

    public String command = nothing;

    private int i = 1;

    public BotTesting(FileHelper fileHelper) {
        this.fileHelper = fileHelper;
    }

    public void execute() {
        // bot1Behaviour(i);
        // bot2Behaviour(i);
        command = scream;
        fileHelper.log("test log");
        i++;
    }

    private void bot1Behaviour(int i) {
        switch (i) {
            case 0 -> command = nothing;
            case 1, 2 -> command = up;
            case 3 -> command = pickup;
            case 4, 5 -> command = down;
            case 6 -> command = scream;
            case 7 -> command = climb;
        }
    }

    private void bot2Behaviour(int i) {
        switch (i) {
            case 0, 3 -> command = down;
            case 1, 6 -> command = up;
            case 2, 5, 4 -> command = left;
            case 7, 8, 9 -> command = right;
            case 10 -> command = climb;
        }
    }
}
