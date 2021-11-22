package de.legoshi.wumpusenv.game;

import de.legoshi.wumpusenv.utils.Colorizer;
import de.legoshi.wumpusenv.utils.FileHelper;
import de.legoshi.wumpusenv.utils.Status;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

@Getter
@Setter
public class GameState {

    public int W_COUNT;
    public int MIN_H_COUNT;

    private FieldStatus[][] game;
    private ArrayList<Player> players;
    private ArrayList<Wumpus> wumpuses = new ArrayList<>();
    private boolean isRunning = false;
    private boolean customValues = false;
    private boolean customSpawn = false;

    @Setter private Label messageLabel;

    public GameState() {
        this.game = new FieldStatus[7][7];
        this.players = new ArrayList<>();
    }

    /**
     * Updates the current size of the game
     *
     * @param height height of the games cells
     * @param width  width of the games cells
     */
    public void updateGameSize(int height, int width) {
        this.game = new FieldStatus[height][width]; //[row - zeile - y][column - spalte - x]
        initState();
    }

    /**
     * Generates a random field with currently:
     * |Agents| + 1 Holes
     * 1 Gold and Wumpus
     * n Players
     */
    public void generateRandomState() {

        ArrayList<Integer> arrayListChosen = new ArrayList<>();
        this.wumpuses = new ArrayList<>();
        initState();

        int fieldSize = getHeight() * getWidth();
        int playerCount = players.size();
        if ((W_COUNT + MIN_H_COUNT + playerCount * 2) > fieldSize) {
            messageLabel.setText("Not enough fields available");
            FileHelper.writeToLog("Not enough fields available");
            return;
        }

        int goldNumber = 1;
        int holeNumber = playerCount + 1;

        if (customValues) {
            goldNumber = W_COUNT;
            holeNumber = MIN_H_COUNT;
        }

        ThreadLocalRandom.current().ints(0, fieldSize).distinct().limit(goldNumber + holeNumber + playerCount).forEach(arrayListChosen::add);

        for (int i = 0; i < goldNumber; i++) {
            int goldPos = arrayListChosen.get(i);
            addGold(goldPos / getWidth(), goldPos % getWidth());
            Point2D wPos = new Point2D(goldPos % getWidth(), goldPos / getWidth());
            wumpuses.add(new Wumpus(wPos));
        }

        for (int i = 0; i < holeNumber; i++) {
            int holePos = arrayListChosen.get(goldNumber + i);
            addHole(holePos / getWidth(), holePos % getWidth());
        }

        for (int i = 0; i < playerCount; i++) {
            int playerPos = arrayListChosen.get(goldNumber + holeNumber + i);
            Player player = players.get(i);
            if (player.getCustomSpawn() == null || !customSpawn) {
                addPlayer(new Point2D(playerPos % getWidth(), playerPos / getWidth()));
                player.setCurrentPosition(new Point2D(playerPos % getWidth(), playerPos / getWidth()));
            } else {
                addPlayer(player.getCustomSpawn());
                player.setCurrentPosition(player.getCustomSpawn());
            }
        }
    }

    /**
     * Iterates through all buttons existing and adds a picture onto them
     */
    public void colorField(Button[][] buttons, boolean visible) {
        Colorizer colorizer = new Colorizer();
        for (int column = 0; column < getWidth(); column++) {
            for (int row = 0; row < getHeight(); row++) {
                if (game[row][column].getArrayList().size() > 0) {
                    try {
                        Button button = buttons[row][column];
                        if (visible || game[row][column].isVisible()) {
                            Node box = colorizer.colorize(game[row][column].getArrayList(), button);
                            button.setStyle("-fx-background-color: WHITE");
                            button.setGraphic(box);
                            if (game[row][column].getArrayList().contains(Status.PLAYER)) {
                                for (Player all : players) {
                                    if (all.getCurrentPosition().equals(new Point2D(column, row))) {
                                        button.setText(all.getId());
                                    }
                                }
                            }
                        } else if (!game[row][column].isVisible()) {
                            button.setStyle("-fx-background-color: GRAY");
                            button.setGraphic(null);
                            button.setText("");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        messageLabel.setText("Something went wrong coloring the buttons");
                        FileHelper.writeToLog("Something went wrong coloring the buttons");
                        FileHelper.writeToLog(e.getMessage());
                    }
                } else {
                    Button button = buttons[row][column];
                    if (visible || game[row][column].isVisible()) {
                        button.setStyle("-fx-background-color: WHITE");
                    } else if (!game[row][column].isVisible()) {
                        button.setStyle("-fx-background-color: GRAY");
                    }
                }
            }
        }

    }

    /**
     * Helper function to add a player to a field
     */
    public void addPlayer(Point2D point2D) {
        int posX = (int) point2D.getX();
        int posY = (int) point2D.getY();
        game[posY][posX].addStatus(Status.START);
        game[posY][posX].addStatus(Status.PLAYER);
    }

    public void removePlayer(Point2D point2D) {
        int posX = (int) point2D.getX();
        int posY = (int) point2D.getY();
        game[posY][posX].getArrayList().remove(Status.START);
        game[posY][posX].getArrayList().remove(Status.PLAYER);
    }

    /**
     * Helper function to add a gold to a field
     *
     * @param posY position of golds y coordinate
     * @param posX position of golds x coordinate
     */
    public void addGold(int posY, int posX) {
        game[posY][posX].addStatus(Status.GOLD);
        game[posY][posX].addStatus(Status.WUMPUS);
        addSurrounding(posY, posX, Status.STENCH);
    }

    /**
     * Helper function to add a hole to a field
     *
     * @param posY position of hole y coordinate
     * @param posX position of hole x coordinate
     */
    public void addHole(int posY, int posX) {
        game[posY][posX].addStatus(Status.HOLE);
        addSurrounding(posY, posX, Status.WIND);
    }

    /**
     * Helper function to add surrounding field states
     *
     * @param posY position of field y coordinate
     * @param posX position of field x coordinate
     */
    public void addSurrounding(int posY, int posX, Status status) {
        if (posX - 1 >= 0) game[posY][posX - 1].addStatus(status);
        if (posY - 1 >= 0) game[posY - 1][posX].addStatus(status);
        if (posX + 1 < getWidth()) game[posY][posX + 1].addStatus(status);
        if (posY + 1 < getHeight()) game[posY + 1][posX].addStatus(status);
    }

    /**
     * Helper function to remove surrounding field states
     *
     * @param posY position of field y coordinate
     * @param posX position of field x coordinate
     */
    public void removeSurrounding(int posY, int posX, Status status) {
        if (posX - 1 >= 0) game[posY][posX - 1].getArrayList().remove(status);
        if (posY - 1 >= 0) game[posY - 1][posX].getArrayList().remove(status);
        if (posX + 1 < getWidth()) game[posY][posX + 1].getArrayList().remove(status);
        if (posY + 1 < getHeight()) game[posY + 1][posX].getArrayList().remove(status);
    }

    /**
     * Helper function to initialize the field
     */
    public void initState() {
        this.game = new FieldStatus[getHeight()][getWidth()];
        for (int column = 0; column < getWidth(); column++) {
            for (int row = 0; row < getHeight(); row++) {
                game[row][column] = new FieldStatus();
            }
        }
    }

    /**
     * Getter for width of game field
     *
     * @return width of game field
     */
    public int getWidth() {
        return this.game[0].length;
    }

    /**
     * Getter for height of game field
     *
     * @return height of game field
     */
    public int getHeight() {
        return this.game.length;
    }

}
