package de.legoshi.wumpusenv;

import de.legoshi.wumpusenv.game.GameState;
import de.legoshi.wumpusenv.game.Player;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.layout.*;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    public Slider rowSlider;
    public Slider columnSlider;
    public Button simulateButton;
    public Button addBot;
    public ChoiceBox removeBotCB;
    public Button removeBot;
    public CheckBox tilesVis;
    public GridPane gridPane;

    private GameState gameState;

    /**
     * Initializes the listeners for sliders aswell as buttons
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        gridPane.setPrefSize(300, 300);

        initButtons();

        rowSlider.valueProperty().addListener((observableValue, number, t1) -> { // check if double is integer
            if(Math.floor(rowSlider.getValue()) == rowSlider.getValue()) initButtons();
        });

        columnSlider.valueProperty().addListener((observableValue, number, t1) -> {
            if(Math.floor(columnSlider.getValue()) == columnSlider.getValue()) initButtons();
        });

        // add that if application is closed, all running bots are shut down aswell
        // add extra display with some logging/data tracking (collects all "Sys.out")
        // add a restart button (resets all bots, the field)

    }

    public void onSimulate(ActionEvent actionEvent) {

        /* if(playerIds.size() <= 1) {
            System.out.println("Not enough players added");
            return;
        } */

        // System.out.println(Color.BLACK.toString());
        gameState.generateRandomState();
        gameState.colorField(gridPane);

        // start all bots
        // application waits until it recieves "READY" from all bots
        // timeout after 5 seconds



    }

    public void onAddBot(ActionEvent actionEvent) {

        ArrayList<Player> players = gameState.getPlayerIDs();

        if(gameState.isRunning()) {
            System.out.println("Game is currently running, press restart to pause");
            return;
        }

        Player player = new Player(players.size());
        players.add(player);

    }

    public void onRemoveBot(ActionEvent actionEvent) {

        if(gameState.getPlayerIDs().isEmpty()) {
            System.out.println("Game is currently running, press restart to pause");
        }

        if(gameState.isRunning()) {
            System.out.println("Game is currently running, press restart to cancel and remove bots");
            return;
        }

    }

    public void onSliderEvent(DragEvent event) {


    }

    private void initButtons() {
        double sliderValRow = rowSlider.getValue();
        double sliderValCol = columnSlider.getValue();
        gridPane.getChildren().clear();
        for(int row = 0; row < sliderValRow; row++) {
            for(int column = 0; column < sliderValCol; column++) {
                Button b = new Button();
                b.setPrefHeight(101);
                b.setPrefWidth(101);
                gridPane.add(b, column, row);
            }
        }
        if(gameState != null) this.gameState.updateGameSize((int) sliderValRow, (int) sliderValCol);
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

}