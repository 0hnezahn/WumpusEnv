package de.legoshi.wumpusenv.utils;

import de.legoshi.wumpusenv.game.GameState;
import de.legoshi.wumpusenv.game.Player;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import lombok.Setter;

import java.io.*;
import java.util.Collection;
import java.util.Scanner;

public class Communicator {

    private GameState gameState;
    @Setter private Label messageLabel;

    public Communicator(GameState gameState) {
        this.gameState = gameState;
    }

    public void initNewPlayer(Player player) {
        String[] ending = player.getId().split("\\.");
        player.setName(ending[0]);
        File textFile = generateTextFile(player);
        player.setFile(textFile);

        player.setCurrentPosition(new Point2D(0,0));
        while(gameState.getGame()[(int) player.getCurrentPosition().getY()][(int) player.getCurrentPosition().getX()].getArrayList().contains(Status.PLAYER) ||
                gameState.getGame()[(int) player.getCurrentPosition().getY()][(int) player.getCurrentPosition().getX()].getArrayList().contains(Status.HOLE) ||
                gameState.getGame()[(int) player.getCurrentPosition().getY()][(int) player.getCurrentPosition().getX()].getArrayList().contains(Status.WUMPUS)) {
            player.setCurrentPosition(new Point2D(Math.random()* gameState.getWidth(),Math.random()*gameState.getHeight()));
        }

        try {
            Runtime re = Runtime.getRuntime();
            if (ending[1].equals("jar")) {
                Process process = re.exec("java -jar " + player.getId() + " " + player.getName() + ".txt");
                player.setProcess(process);
            } else if (ending[1].equals("py")) {
                Process process = re.exec("python " + player.getId() + " " + player.getName() + ".txt");
                player.setProcess(process);
            }
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Couldnt start bot");
            FileHelper.writeToLog("Couldnt start bot");
            FileHelper.writeToLog(e.getMessage());
        }

        writeToFile(player, "C;INIT");
    }

    private File generateTextFile(Player player) {
        File playerTextFile;
        try {
            playerTextFile = new File(player.getName() + ".txt");
            if (playerTextFile.exists()) {
                messageLabel.setText("Successfully deleted already existing bot file to create a new one");
                FileHelper.writeToLog("Successfully deleted already existing bot file to create a new one");
            }
            if (playerTextFile.createNewFile()) {
                FileHelper.writeToLog("Successfully created text file");
            }
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Couldnt create file");
            FileHelper.writeToLog("Couldnt create file");
            FileHelper.writeToLog(e.getMessage());
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
            messageLabel.setText("Couldnt open Filewriter");
            FileHelper.writeToLog("Couldnt open Filewriter");
            FileHelper.writeToLog(e.getMessage());
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
            messageLabel.setText("Couldnt open Filewriter");
            FileHelper.writeToLog("Couldnt open Filewriter");
            FileHelper.writeToLog(e.getMessage());
            return "ERROR";
        }
    }

    public boolean isReady() {
        for (Player p : gameState.getPlayers()) {
            String[] m = readFile(p).split(";");
            if (!m[0].equals("B")) {
                return false;
            }
        }
        return true;
    }

}
