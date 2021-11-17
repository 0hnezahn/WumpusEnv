package de.legoshi.wumpusenv;

import de.legoshi.wumpusenv.game.GameState;
import de.legoshi.wumpusenv.game.Player;
import de.legoshi.wumpusenv.utils.Communicator;
import de.legoshi.wumpusenv.utils.Simulator;
import javafx.application.Platform;
import javafx.beans.value.ObservableStringValue;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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
    public TextField botName;

    public Button abortButton;
    public Label stepLabel;

    private GameState gameState;
    public Button[][] buttons;

    private Communicator communicator;
    private Simulator simulator;

    private boolean paused;
    private boolean generated;

    private AtomicInteger step;

    /**
     * Initializes the listeners for sliders aswell as buttons
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // add extra display with some logging/data tracking (collects all "Sys.out")
        // add a restart button (resets all bots, the field)

        initButtons();
        paused = false;
        generated = false;

        rowSlider.valueProperty().addListener((observableValue, number, t1) -> {
            if(Math.floor(rowSlider.getValue()) == rowSlider.getValue()) initButtons();
        });

        columnSlider.valueProperty().addListener((observableValue, number, t1) -> {
            if(Math.floor(columnSlider.getValue()) == columnSlider.getValue()) initButtons();
        });

    }

    /**
     * Method thats called when the simulate button is pressed
     */
    public void onSimulate() {

        if(gameState.isRunning()) {
            System.out.println("Game is currently running");
            return;
        }

        if(!generated) {
            System.out.println("Nothing generated yet!");
            return;
        }

        if(gameState.getPlayers().size() <= 0) {
            System.out.println("Not enough players added");
            return;
        }

        gameState.setRunning(true);

        boolean temp = true;
        while(temp) {
            if (communicator.isReady()) {
                for (Player all : gameState.getPlayers()) {
                    all.setPlayerVision(simulator.getSurroundings(all.getCurrentPosition())); // init state
                }
                temp = false;
            }
        }

        this.step = new AtomicInteger(1);
        simulator.sendPlayerStates();
        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            if(!paused) {
                if (communicator.isReady()) {
                    simulator.receiveInstructions();
                    simulator.simulateStep();
                    for (Player all : gameState.getPlayers()) {
                        all.setPlayerVision(simulator.getSurroundings(all.getCurrentPosition()));
                    }
                    simulator.sendPlayerStates();
                    Platform.runLater(this::reloadBoard);
                }
            }
        }, 100, 100, TimeUnit.MILLISECONDS);

    }

    /**
     * Method thats called when the abort button is pressed
     */
    public void onAbort() {
        for(Player p : gameState.getPlayers()) {
            p.getProcess().destroy();
        }
        gameState.setRunning(false);
        gameState.getPlayers().clear();

        for(int row = 0; row < rowSlider.getValue(); row++) {
            for(int column = 0; column < columnSlider.getValue(); column++) {
                buttons[row][column].setGraphic(null);
            }
        }
    }

    /**
     * Method thats called when the add bot button is pressed
     */
    public void onAddBot() {
        if(gameState.isRunning()) {
            System.out.println("Game is currently running, press restart to pause");
            return;
        }

        // for now only .jars
        String bName = botName.getText();
        Player player = new Player(bName);
        communicator.initNewPlayer(player);

        gameState.getPlayers().add(player);
        System.out.println("Successfully added player!");
    }

    /**
     * Method thats called when the randomize button is pressed
     */
    public void onRandomize() {
        if(gameState.isRunning()) {
            System.out.println("Game is currently still running!");
            return;
        }

        generated = true;

        for(int row = 0; row < rowSlider.getValue(); row++) {
            for(int column = 0; column < columnSlider.getValue(); column++) {
                buttons[row][column].setGraphic(null);
            }
        }
        gameState.generateRandomState();
        gameState.colorField(buttons);
    }

    public void reloadBoard() {
        for(int row = 0; row < rowSlider.getValue(); row++) {
            for(int column = 0; column < columnSlider.getValue(); column++) {
                buttons[row][column].setGraphic(null);
            }
        }

        gameState.colorField(buttons);
    }

    /**
     * Method thats called when the remove bot button is pressed
     */
    public void onRemoveBot() {
        if(gameState.isRunning()) {
            System.out.println("Game is currently running, press restart to cancel and remove bots");
            return;
        }

        System.out.println("Successfully removed player!");
    }

    /**
     * Method that initiates the button field
     */
    private void initButtons() {
        int sliderValRow = (int)rowSlider.getValue();
        int sliderValCol = (int)columnSlider.getValue();
        gridPane.getChildren().clear();
        this.buttons = new Button[sliderValRow][sliderValCol];

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
        if(gameState != null) this.gameState.updateGameSize(sliderValRow, sliderValCol);
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public void setCommunicator(Communicator communicator) {
        this.communicator = communicator;
    }

    public void setSimulator(Simulator simulator) {
        this.simulator = simulator;
    }

    public void onPause() {
        paused = true;
    }

    public void onContinue() {
        paused = false;
    }
}