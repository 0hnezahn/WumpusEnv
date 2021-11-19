package de.legoshi.wumpusenv.utils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class FileHelper {

    public static File logFile;
    private SimpleDateFormat formatter = new SimpleDateFormat("HH_mm_ss");

    public FileHelper() {
        new File("log/").mkdir();
        logFile = new File("log/" +formatter.format(new Date()) + "-enviroment-log.txt");
    }

    public void generateTextFile() {
        try {
            if (logFile.exists()) if(logFile.delete()) System.out.println("Successfully deleted already existing bot file to create a new one!");
            if (logFile.createNewFile()) System.out.println("Successfully created text file");
            else System.out.println("Couldnt create text file");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Couldnt create file");
            return;
        };
    }

    public static void writeToLog(String message) {
        try {
            String messageBefore = readLog();
            BufferedWriter fileWriter = new BufferedWriter(new FileWriter(logFile));
            fileWriter.write(messageBefore + message + "\r\n");
            fileWriter.close();
            System.out.println(message);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Couldnt open Filewriter");
        }
    }

    private static String readLog() {
        try {
            String line;
            String completeString = "";
            BufferedReader reader = new BufferedReader(new FileReader(logFile));
            while ((line = reader.readLine()) != null) {
                completeString = completeString + line + "\r\n";
            }
            reader.close();
            return completeString;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Couldnt open Filewriter");
            return "ERROR";
        }
    }

}
