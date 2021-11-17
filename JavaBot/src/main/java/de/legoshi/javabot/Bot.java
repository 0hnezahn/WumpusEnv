package main.java.de.legoshi.javabot;

public class Bot {

    private FileHelper fileHelper;

    public String gameState;
    public String command;

    public Bot(FileHelper fileHelper) {
        this.fileHelper = fileHelper;
    }

    public void execute() {
        command = "B;NOTHING";
        fileHelper.log("test log");
    }

}
