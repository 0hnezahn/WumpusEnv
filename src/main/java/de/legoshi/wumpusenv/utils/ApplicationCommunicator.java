package de.legoshi.wumpusenv.utils;

import de.legoshi.wumpusenv.game.GameState;
import de.legoshi.wumpusenv.game.Player;

import java.io.*;
import java.util.Scanner;

public class ApplicationCommunicator implements Runnable {

    private GameState gameState;

    public ApplicationCommunicator(GameState gameState) {
        this.gameState = gameState;
    }

    public void generateNewPlayer(Player player, String bName) {

        try {
            ProcessBuilder pb = new ProcessBuilder("java", "-jar", bName);
            player.setProcess(pb.start());
            player.collectMessages();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Couldnt start bot!");
            return;
        }

        try {
            File f = new File(bName + player.getId() + ".txt");
            if(f.createNewFile()) {
                System.out.println("Successfully created text file");
            } else {
                System.out.println("Couldnt create text file");
                return;
            }
            player.setFile(f);
            this.writeToFile(player, "INIT");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Couldnt create file");
        }
    }

    private void writeToFile(Player player, String message) {
        try {
            FileWriter fileWriter = new FileWriter(player.getFile());
            fileWriter.write(message);
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Couldnt open Filewriter");
        }
    }

    private String readFile(Player player) {
        try {
            Scanner myReader = new Scanner(player.getFile());
            String data = "";
            while (myReader.hasNext()) data = myReader.nextLine();
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Couldnt open Filewriter");
            return "ERROR";
        }
    }

    public boolean isReady() {
        for(Player p : gameState.getPlayers()) {
            if(!readFile(p).equals("READY")) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void run() {



    }

}
