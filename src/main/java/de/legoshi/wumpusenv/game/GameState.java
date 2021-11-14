package de.legoshi.wumpusenv.game;

import de.legoshi.wumpusenv.utils.Colorizer;
import de.legoshi.wumpusenv.utils.Status;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.FileInputStream;
import java.util.ArrayList;

public class GameState {

    private FieldStatus[][] game;
    private ArrayList<Player> players;
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

        this.game = new FieldStatus[height][width]; //[y][x]
    }

    /**
     * Generates a random field with currently:
     * |Agents| + 1 Holes
     * 1 Gold and Wumpus
     * n Players
     */
    public void generateRandomState() {

        ArrayList<Integer> arrayListAvailable = new ArrayList<>();
        ArrayList<Integer> arrayListChosen = new ArrayList<>();

        initState();

        if((2 + players.size()*2) > getHeight()*getWidth()) {
            System.out.println("Nicht genügend Felder zur Verfügung!");
            return;
        }

        for(int i = 0; i < getHeight()*getWidth(); i++) {
            arrayListAvailable.add(i);
        }

        int playerCount = players.size();

        for(int i = 0; i < 2 + playerCount*2; i++) {
            int randVal;
            do { randVal = (int) Math.floor(Math.random() * arrayListAvailable.size());
            } while (arrayListChosen.contains(randVal));
            arrayListChosen.add(randVal);
        }

        addGold((int)Math.floor(arrayListChosen.get(0)/getWidth()),arrayListChosen.get(0)%getWidth());
        for(int i = 0; i < playerCount+1; i++) addHole((int)Math.floor(arrayListChosen.get(1+i)/getWidth()),arrayListChosen.get(1+i)%getWidth());
        for(int i = 0; i < playerCount; i++) addPlayer((int)Math.floor(arrayListChosen.get(2+i+playerCount)/getWidth()),arrayListChosen.get(2+i+playerCount) % getWidth());

        for(int column = 0; column < getWidth(); column++) {
            for(int row = 0; row < getHeight(); row++) {
                if(!game[row][column].getArrayList().isEmpty()) {
                    System.out.println("x: " + column + "y: " + row + ": " + game[row][column].getArrayList().get(0).toString());
                } else System.out.println("x: " + column + "y: " + row + ": NOTHING");
            }
        }

    }

    /**
     * Iterates through all buttons existing and adds a picture onto them
     * @param gridPane Pane that holds all the buttons
     */
    public void colorField(GridPane gridPane, Button[][] buttons) {

        // column - spalte - x
        // row - zeile - y
        for(int column = 0; column < getWidth(); column++) {
            for(int row = 0; row < getHeight(); row++) {
                if(game[row][column].getArrayList().size() > 0) {
                    try {
                        Button button = buttons[row][column];
                        Node box = Colorizer.colorize(game[row][column].getArrayList(), button);
                        // vBox.setAlignment(Pos.CENTER);
                        button.setGraphic(box);
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("Something went wrong");
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
    }

    /**
     * Helper function to add a gold to a field
     * @param posY position of golds y coordinate
     * @param posX position of golds x coordinate
     */
    private void addGold(int posY, int posX) {
        game[posY][posX].addStatus(Status.GOLD);
        game[posY][posX].addStatus(Status.WUMPUS);
        game[posY][posX].addStatus(Status.OCCUPIED);
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
     * Helper function to add surrounding fieldstates
     * @param posY position of field y coordinate
     * @param posX position of field x coordinate
     */
    private void addSurrounding(int posY, int posX, Status status) {
        if(posX-1 >= 0) game[posY][posX-1].addStatus(status);
        if(posY-1 >= 0) game[posY-1][posX].addStatus(status);
        if(posX+1 < getWidth()) game[posY][posX+1].addStatus(status);
        if(posY+1 < getHeight()) game[posY+1][posX].addStatus(status);
    }

    // is height and width correct?
    // I think i chose first line and want length of it, so it should be width (?)
    public int getWidth() {
        return this.game[0].length;
    }

    public int getHeight() {
        return this.game.length;
    }

    public ArrayList<Player> getPlayerIDs() {
        return this.players;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }
}
