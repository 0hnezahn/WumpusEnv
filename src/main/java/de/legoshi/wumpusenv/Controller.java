package de.legoshi.wumpusenv;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.BitSet;
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

    }

    public void onSimulate(ActionEvent actionEvent) {

        // System.out.println(Color.BLACK.toString());
        gameState.generateRandomState();
        gameState.colorField(gridPane);

    }

    public void onAddBot(ActionEvent actionEvent) {

        // 

    }

    public void onRemoveBot(ActionEvent actionEvent) {


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