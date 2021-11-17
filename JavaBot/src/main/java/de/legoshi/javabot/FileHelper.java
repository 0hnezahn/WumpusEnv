package main.java.de.legoshi.javabot;

import java.io.*;

public class FileHelper {

    private File file;
    private File log;


    public FileHelper(File file, File log) {
        this.file = file;
        this.log = log;
        initLog();
    }

    public void writeToFile(String message) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(message);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Couldnt open Filewriter");
        }
    }

    public String readFile() {
        try {
            String line;
            String completeString = "";
            BufferedReader reader = new BufferedReader(new FileReader(file));
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

    public void log(String message) {
        try {
            String before = readLog();
            BufferedWriter writer = new BufferedWriter(new FileWriter(log));
            writer.write(before + message);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Couldnt open Filewriter");
        }
    }

    private String readLog() {
        try {
            String line;
            String completeString = "";
            BufferedReader reader = new BufferedReader(new FileReader(log));
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

    private void initLog() {
        try {
            if(log.exists()) {
                if(log.delete()) {
                    System.out.println("Successfully deleted already existing log file!");
                }
            }
            if(log.createNewFile()) {
                log("Successfully created the log file");
                log("\r\n");
            }
        } catch (Exception e) {
            System.out.println("Well... thats too bad.");
        }
    }

}
