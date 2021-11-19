package main.java.de.legoshi.javabot;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        File file = new File("./"+args[0]);
        File logFile = new File("./log-"+args[0]);

        // File file = new File("./javabot.jar.txt");
        // File logFile = new File("./log-javabot.jar.txt");

        FileHelper fileHelper = new FileHelper(file, logFile);

        Bot bot = new Bot(fileHelper);
        int step = 0;

        while(true) {

            TimeUnit.MILLISECONDS.sleep(500);

            String initMessage = fileHelper.readFile();
            String[] message = initMessage.split(";");

            if(message.length >= 1) {
                if(message.length > 2) {
                    if(message[0].equals("C") && message[1].equals("INIT")) {
                        String readyMessage = "B;READY";
                        fileHelper.writeToFile(readyMessage);
                        message = readyMessage.split(";");
                    }
                }
                if(message[0].equals("C")) {
                    step++;
                    fileHelper.log(step + ". STEP \r\n");
                    fileHelper.log("--------------------------------------- \r\n");
                    fileHelper.log("[" + java.time.LocalDateTime.now() + "] " + "BOT READ: " + "\r\n");
                    fileHelper.log("[" + java.time.LocalDateTime.now() + "] " + initMessage + "\r\n");

                    bot.gameState = initMessage;

                    bot.execute();
                    fileHelper.writeToFile(bot.command);
                    fileHelper.log("[" + java.time.LocalDateTime.now() + "] BOT PERFORMED: " + "\r\n");
                    fileHelper.log("[" + java.time.LocalDateTime.now() + "] " + bot.command + "\r\n");
                    fileHelper.log("--------------------------------------- \r\n");
                }
            }

        }

    }

}
