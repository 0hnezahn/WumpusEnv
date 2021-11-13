package de.legoshi.wumpusenv;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class GameState {

    private FieldStatus[][] game;
    private ArrayList<Integer> playerIDs;

    public GameState() {

        this.game = new FieldStatus[3][3];
        this.playerIDs = new ArrayList<>();
        playerIDs.add(1);
    }

    public void updateGameSize(int height, int width) {

        this.game = new FieldStatus[height][width]; //[y][x]
    }

    /**
     * Generates a random field with currently:
     * 1 Hole
     * 1 Gold and Wumpus
     * n Players
     */
    public void generateRandomState() {

        ArrayList<Integer> arrayListAvailable = new ArrayList<>();
        ArrayList<Integer> arrayListChosen = new ArrayList<>();

        for(int i = 0; i < getHeight()*getWidth(); i++) {
            arrayListAvailable.add(i);
        }

        for(int i = 0; i < 2 + playerIDs.size(); i++) {
            int randVal = (int) Math.floor(Math.random() * arrayListAvailable.size());
            arrayListChosen.add(randVal);
            arrayListAvailable.remove((Object) randVal);
        }

        // problem with different sizes :(
        addGold((int)Math.floor(arrayListChosen.get(0)/getWidth()), arrayListChosen.get(0)%getWidth());
        addHole((int)Math.floor(arrayListChosen.get(1)/getWidth()), arrayListChosen.get(1)%getWidth());
        addPlayer((int)Math.floor(arrayListChosen.get(2)/getWidth()), arrayListChosen.get(2)%getWidth());

        for(int column = 0; column < getWidth(); column++) {
            for(int row = 0; row < getHeight(); row++) {
                initRemaining(row, column);
            }
        }

    }

    public void colorField(GridPane gridPane) {

        ObservableList<Node> buttonList = gridPane.getChildren();

        for(int column = 0; column < getWidth(); column++) {
            for(int row = 0; row < getHeight(); row++) {
                if(game[row][column].getArrayList().size() > 0) {
                    try {
                        ImageView imageView = Colorizer.colorize(game[row][column].getArrayList().get(0));
                        if(imageView != null) {
                            Button button = ((Button) buttonList.get(column + row));
                            imageView.setFitHeight(button.getHeight()/2);
                            imageView.setFitWidth(button.getWidth()/2);
                            button.setGraphic(imageView);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("Something went wrong");
                    }
                }
            }
        }


    }

    private void addPlayer(int posY, int posX) {

        FieldStatus fieldStatus = new FieldStatus();
        fieldStatus.getArrayList().add(Status.START);
        fieldStatus.getArrayList().add(Status.OCCUPIED);
        game[posY][posX] = fieldStatus;

    }

    private void addGold(int posY, int posX) {

        FieldStatus fieldStatus = new FieldStatus();
        fieldStatus.getArrayList().add(Status.GOLD);
        fieldStatus.getArrayList().add(Status.WUMPUS);
        fieldStatus.getArrayList().add(Status.OCCUPIED);
        game[posY][posX] = fieldStatus;

        addSurrounding(posY, posX, Status.STENCH);

    }

    private void addHole(int posY, int posX) {

        FieldStatus fieldStatus = new FieldStatus();
        fieldStatus.getArrayList().add(Status.HOLE);
        game[posY][posX] = fieldStatus;

        addSurrounding(posY, posX, Status.WIND);

    }

    private void initRemaining(int posY, int posX) {
        if(game[posY][posX] == null) game[posY][posX] = new FieldStatus();
    }

    private void addSurrounding(int posY, int posX, Status status) {

        if(posX-1 >= 0) {
            FieldStatus fieldStatus = new FieldStatus();
            fieldStatus.getArrayList().add(status);
            game[posY][posX-1] = fieldStatus;
        }

        if(posY-1 >= 0) {
            FieldStatus fieldStatus = new FieldStatus();
            fieldStatus.getArrayList().add(status);
            game[posY-1][posX] = fieldStatus;
        }

        if(posX+1 < getHeight()) {
            FieldStatus fieldStatus = new FieldStatus();
            fieldStatus.getArrayList().add(status);
            game[posY][posX+1] = fieldStatus;
        }

        if(posY+1 < getWidth()) {
            FieldStatus fieldStatus = new FieldStatus();
            fieldStatus.getArrayList().add(status);
            game[posY+1][posX] = fieldStatus;
        }
    }

    public int getHeight() {

        return this.game[0].length;
    }

    public int getWidth() {

        return this.game.length;
    }

}
