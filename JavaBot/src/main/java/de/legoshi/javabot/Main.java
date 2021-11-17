package main.java.de.legoshi.javabot;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main {

    public static void main(String[] args) {

        File file = new File("./"+args[0]);
        File logFile = new File("./log-"+args[0]);

        // File file = new File("./javabot.jar.txt");
        // File logFile = new File("./log-javabot.jar.txt");

        FileHelper fileHelper = new FileHelper(file, logFile);

        Bot bot = new Bot(fileHelper);
        int step = 0;

        while(true) {

            String initMessage = fileHelper.readFile();
            String[] message = initMessage.split(";");

            if(message.length >= 1) {
                if(message[0].equals("C")) {
                    step++;
                    fileHelper.log(step + ". STEP \r\n");
                    fileHelper.log("--------------------------------------- \r\n");
                    fileHelper.log("[" + java.time.LocalDateTime.now() + "] " + "BOT READ: " + "\r\n");
                    fileHelper.log("[" + java.time.LocalDateTime.now() + "] " + initMessage + "\r\n");

                    bot.gameState = initMessage;

                    bot.execute();
                    fileHelper.writeToFile("[" + java.time.LocalDateTime.now() + "] " + bot.command);

                    fileHelper.log("\r\n");
                    fileHelper.log("[" + java.time.LocalDateTime.now() + "] BOT PERFORMED: " + "\r\n");
                    fileHelper.log("[" + java.time.LocalDateTime.now() + "] " + bot.command + "\r\n");
                    fileHelper.log("--------------------------------------- \r\n");
                }
            }

        }

    }

}
