package de.legoshi.wumpusenv.utils;

import de.legoshi.wumpusenv.game.GameState;
import de.legoshi.wumpusenv.game.Player;

import java.io.*;
import java.util.Scanner;

public class Communicator {

    private GameState gameState;

    public Communicator(GameState gameState) {
        this.gameState = gameState;
    }

    public void initNewPlayer(Player player) {
        File textFile = generateTextFile(player);
        player.setFile(textFile);

        try {
            Runtime re = Runtime.getRuntime();
            Process process = re.exec("java -jar javabot.jar " + player.getId()+".txt");
            player.setProcess(process);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Couldnt start bot!");
        }
        writeToFile(player,"C;INIT");
    }

    private File generateTextFile(Player player) {
        File playerTextFile;
        try {
            playerTextFile = new File(player.getId() + ".txt");
            if(playerTextFile.exists()) System.out.println("Successfully deleted already existing bot file to create a new one!");
            if(playerTextFile.createNewFile()) System.out.println("Successfully created text file");
            else System.out.println("Couldnt create text file");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Couldnt create file");
            return null;
        }
        return playerTextFile;
    }

    public void writeToFile(Player player, String message) {
        try {
            BufferedWriter fileWriter = new BufferedWriter(new FileWriter(player.getFile()));
            fileWriter.write(message);
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Couldnt open Filewriter");
        }
    }

    public String readFile(Player player) {
        try {
            Scanner myReader = new Scanner(player.getFile());
            String data = "";
            while (myReader.hasNext()) data = data + myReader.nextLine();
            myReader.close();
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Couldnt open Filewriter");
            return "ERROR";
        }
    }

    public boolean isReady() {
        for(Player p : gameState.getPlayers()) {
            String[] m = readFile(p).split(";");
            if(!m[0].equals("B")) {
                return false;
            }
        }
        return true;
    }

}
