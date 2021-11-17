package main.java.de.legoshi.javabot;

public class Bot {

    private FileHelper fileHelper;

    public String gameState;
    public String command;

    private int i = 0;

    public Bot(FileHelper fileHelper) {
        this.fileHelper = fileHelper;
    }

    public void execute() {

        if(i%4==0) {
            command = "B;UP;false;false;false";
        } else {
            command = "B;RIGHT;false;false;false";
        }
        i++;
        fileHelper.log("test log");
    }

}
