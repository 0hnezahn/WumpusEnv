package de.legoshi.wumpusenv;

import de.legoshi.wumpusenv.game.GameState;
import de.legoshi.wumpusenv.game.Player;
import de.legoshi.wumpusenv.utils.ApplicationCommunicator;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Controller implements Initializable {

    public Slider rowSlider;
    public Slider columnSlider;
    public Button simulateButton;
    public Button addBot;
    public ChoiceBox removeBotCB;
    public Button removeBot;
    public CheckBox tilesVis;

    public GridPane gridPane;
    public BorderPane borderPane;
    public Pane pane;
    public HBox hBox;
    public TextField botName;

    public Button abortButton;

    private GameState gameState;
    public Button[][] buttons;

    private ApplicationCommunicator applicationCommunicator;
    private ScheduledExecutorService executorService;

    /**
     * Initializes the listeners for sliders aswell as buttons
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        initButtons();
        this.applicationCommunicator = new ApplicationCommunicator(gameState);

        StackPane paneL = new StackPane();
        StackPane paneR = new StackPane();
        StackPane paneU = new StackPane();

        paneL.setPrefWidth(150);
        paneR.setPrefWidth(150);
        paneU.setPrefHeight(15);
        borderPane.setLeft(paneL);
        borderPane.setRight(paneR);
        borderPane.setTop(paneU);

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

    public void onSimulate() {
        if(gameState.isRunning()) {
            System.out.println("Game is currently running");
            return;
        }

        if(gameState.getPlayers().size() <= 1) {
            System.out.println("Not enough players added");
            return;
        }

        this.executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(applicationCommunicator,100, 100, TimeUnit.MILLISECONDS);
        // start all bots
        // application waits until it recieves "READY" from all bots
        // timeout after 5 seconds
    }

    public void onAbort() {
        for(Player p : gameState.getPlayers()) {
            p.getFile().delete();
            p.getProcess().destroy();
        }

        gameState.getPlayers().clear();
        executorService.shutdown();
    }

    public void onAddBot() {
        ArrayList<Player> players = gameState.getPlayers();
        if(gameState.isRunning()) {
            System.out.println("Game is currently running, press restart to pause");
            return;
        }

        Player player = new Player(players.size());

        // for now only .jars
        String bName = botName.getText();
        applicationCommunicator.generateNewPlayer(player, bName);

        // end process after app closed

        players.add(player);
        System.out.println("Successfully added player!");
    }

    public void onRandomize(ActionEvent event) {
        if(gameState.isRunning()) {
            System.out.println("Game is currently still running!");
            return;
        }

        for(int row = 0; row < rowSlider.getValue(); row++) {
            for(int column = 0; column < columnSlider.getValue(); column++) {
                buttons[row][column].setGraphic(null);
            }
        }
        gameState.generateRandomState();
        gameState.colorField(gridPane, buttons);
    }

    public void onRemoveBot() {
        if(gameState.isRunning()) {
            System.out.println("Game is currently running, press restart to cancel and remove bots");
            return;
        }

        if(gameState.getPlayers().isEmpty()) {
            System.out.println("There are no players in the game!");
            return;
        }
        System.out.println("Successfully removed player!");
    }

    private void initButtons() {
        double sliderValRow = rowSlider.getValue();
        double sliderValCol = columnSlider.getValue();
        gridPane.getChildren().clear();
        this.buttons = new Button[(int)sliderValRow][(int)sliderValCol];

        for(int row = 0; row < sliderValRow; row++) {
            for(int column = 0; column < sliderValCol; column++) {
                Button b = new Button();
                b.setStyle("-fx-background-color: WHITE");
                b.prefWidthProperty().bind(gridPane.widthProperty());
                b.prefHeightProperty().bind(gridPane.heightProperty());
                gridPane.add(b, column, row);
                buttons[row][column] = b;
            }
        }
        if(gameState != null) this.gameState.updateGameSize((int) sliderValRow, (int) sliderValCol);
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

}