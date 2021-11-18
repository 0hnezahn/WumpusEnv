package de.legoshi.wumpusenv.game;

import de.legoshi.wumpusenv.utils.Colorizer;
import de.legoshi.wumpusenv.utils.Status;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

@Getter
@Setter
public class GameState {

    public final int W_COUNT = 1;
    public final int MIN_H_COUNT = 1;

    private FieldStatus[][] game;
    private ArrayList<Player> players;
    private Wumpus wumpus;
    private boolean isRunning;

    public GameState() {
        this.game = new FieldStatus[7][7];
        this.isRunning = false;
        this.players = new ArrayList<>();
    }

    /**
     * Updates the current size of the game
     * @param height height of the games cells
     * @param width width of the games cells
     */
    public void updateGameSize(int height, int width) {
        this.game = new FieldStatus[height][width]; //[row - zeile - y][column - spalte - x]
    }

    /**
     * Generates a random field with currently:
     * |Agents| + 1 Holes
     * 1 Gold and Wumpus
     * n Players
     */
    public void generateRandomState() {
        ArrayList<Integer> arrayListChosen = new ArrayList<>();
        initState();

        int fieldSize = getHeight()*getWidth();
        int playerCount = players.size();
        if((W_COUNT+MIN_H_COUNT+playerCount*2) > fieldSize) {
            System.out.println("Nicht genügend Felder zur Verfügung!");
            return;
        }

        ThreadLocalRandom.current().ints(0, fieldSize).distinct().limit(W_COUNT+MIN_H_COUNT+playerCount*2L).forEach(arrayListChosen::add);

        int goldPos = arrayListChosen.get(0);
        addGold(goldPos/getWidth(),goldPos%getWidth());
        Point2D wPos = new Point2D(goldPos%getWidth(),goldPos/getWidth());
        wumpus = new Wumpus(wPos);

        for(int i = 0; i < playerCount+1; i++) {
            int holePos = arrayListChosen.get(W_COUNT+i);
            addHole(holePos/getWidth(),holePos%getWidth());
        }

        for(int i = 0; i < playerCount; i++) {
            int playerPos = arrayListChosen.get(W_COUNT+MIN_H_COUNT+i+playerCount);
            addPlayer(playerPos/getWidth(),playerPos%getWidth());
            Player player = players.get(i);
            player.setCurrentPosition(new Point2D(playerPos%getWidth(),playerPos/getWidth()));
        }

    }

    /**
     * Iterates through all buttons existing and adds a picture onto them
     */
    public void colorField(Button[][] buttons) {
        for(int column = 0; column < getWidth(); column++) {
            for(int row = 0; row < getHeight(); row++) {
                if(game[row][column].getArrayList().size() > 0) {
                    try {
                        Button button = buttons[row][column];
                        Node box = Colorizer.colorize(game[row][column].getArrayList(), button);
                        button.setGraphic(box);
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("Something went wrong coloring the buttons");
                    }
                }
            }
        }
    }

    /**
     * Helper function to add a player to a field
     * @param posY position of players y coordinate
     * @param posX position of players x coordinate
     */
    private void addPlayer(int posY, int posX) {
        game[posY][posX].addStatus(Status.START);
        game[posY][posX].addStatus(Status.PLAYER);
    }

    /**
     * Helper function to add a gold to a field
     * @param posY position of golds y coordinate
     * @param posX position of golds x coordinate
     */
    private void addGold(int posY, int posX) {
        game[posY][posX].addStatus(Status.GOLD);
        game[posY][posX].addStatus(Status.WUMPUS);
        addSurrounding(posY, posX, Status.STENCH);
    }

    /**
     * Helper function to add a hole to a field
     * @param posY position of hole y coordinate
     * @param posX position of hole x coordinate
     */
    private void addHole(int posY, int posX) {
        game[posY][posX].addStatus(Status.HOLE);
        addSurrounding(posY, posX, Status.WIND);
    }

    /**
     * Helper function to add surrounding field states
     * @param posY position of field y coordinate
     * @param posX position of field x coordinate
     */
    public void addSurrounding(int posY, int posX, Status status) {
        if(posX-1 >= 0) game[posY][posX-1].addStatus(status);
        if(posY-1 >= 0) game[posY-1][posX].addStatus(status);
        if(posX+1 < getWidth()) game[posY][posX+1].addStatus(status);
        if(posY+1 < getHeight()) game[posY+1][posX].addStatus(status);
    }

    /**
     * Helper function to remove surrounding field states
     * @param posY position of field y coordinate
     * @param posX position of field x coordinate
     */
    public void removeSurrounding(int posY, int posX, Status status) {
        if(posX-1 >= 0) game[posY][posX-1].getArrayList().remove(status);
        if(posY-1 >= 0) game[posY-1][posX].getArrayList().remove(status);
        if(posX+1 < getWidth()) game[posY][posX+1].getArrayList().remove(status);
        if(posY+1 < getHeight()) game[posY+1][posX].getArrayList().remove(status);
    }

    /**
     * Helper function to initialize the field
     */
    public void initState() {
        for(int column = 0; column < getWidth(); column++) {
            for(int row = 0; row < getHeight(); row++) {
                game[row][column] = new FieldStatus();
            }
        }
    }

    /**
     * Getter for width of game field
     * @return width of game field
     */
    public int getWidth() {
        return this.game[0].length;
    }

    /**
     * Getter for height of game field
     * @return height of game field
     */
    public int getHeight() {
        return this.game.length;
    }

}
