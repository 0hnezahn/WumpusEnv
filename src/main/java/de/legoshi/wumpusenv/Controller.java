package de.legoshi.wumpusenv;

import de.legoshi.wumpusenv.game.FieldStatus;
import de.legoshi.wumpusenv.game.GameState;
import de.legoshi.wumpusenv.game.Player;
import de.legoshi.wumpusenv.game.Wumpus;
import de.legoshi.wumpusenv.utils.Communicator;
import de.legoshi.wumpusenv.utils.FileHelper;
import de.legoshi.wumpusenv.utils.Simulator;
import de.legoshi.wumpusenv.utils.Status;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Benjamin Müller
 */

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

    public TextField wumpusField;
    public TextField holeField;
    public TextField removeY;
    public TextField removeX;
    public ChoiceBox addCB;
    public TextField addY;
    public TextField addX;
    public CheckBox customVal;
    public TextField xSpawn;
    public TextField ySpawn;
    public CheckBox cSpawn;
    public Button customSpawnButton;
    public Label messageLabel;
    public Label egg;
    public TextField periodField;

    private GameState gameState;
    public Button[][] buttons;

    private Communicator communicator;
    private Simulator simulator;

    private boolean paused = false;
    private boolean generated = false;
    private boolean visible = true;
    private boolean custom = false;
    private boolean spawn = false;

    private boolean restart = false;
    private int period = 2000;

    private int columnF = 7;
    private int rowF = 7;

    private AtomicInteger step;
    private ScheduledExecutorService scheduledExecutorService;

    /**
     * Initializes the listeners for sliders aswell as buttons
     *
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // add extra display with some logging/data tracking (collects all "Sys.out")
        // add a restart button (resets all bots, the field)

        stepLabel.setText("Step: 0");
        initButtons();
        addCB.getItems().addAll("Hole", "Wumpus");
        addCB.setValue("Hole");

        rowSlider.valueProperty().addListener((observableValue, number, t1) -> {
            if (!gameState.isRunning()) {
                if (Math.floor(rowSlider.getValue()) == rowSlider.getValue()) {
                    rowF = (int) rowSlider.getValue();
                    gameState.updateGameSize(columnF, rowF);
                    initButtons();
                    generated = false;
                    FileHelper.writeToLog("Successfully changed amount of rows to " + rowF);
                    messageLabel.setText("Successfully changed amount of rows to " + rowF);
                }
            } else {
                FileHelper.writeToLog("You cant change the size while the game is running");
                messageLabel.setText("You cant change the size while the game is running");
            }
        });

        columnSlider.valueProperty().addListener((observableValue, number, t1) -> {
            if (!gameState.isRunning()) {
                if (Math.floor(columnSlider.getValue()) == columnSlider.getValue()) {
                    columnF = (int) columnSlider.getValue();
                    gameState.updateGameSize(columnF, rowF);
                    initButtons();
                    generated = false;
                    FileHelper.writeToLog("Successfully changed amount of columns to " + columnF);
                    messageLabel.setText("Successfully changed amount of columns to " + columnF);
                }
            } else {
                FileHelper.writeToLog("You cant change the size while the game is running");
                messageLabel.setText("You cant change the size while the game is running");
            }
        });

    }

    /**
     * Method thats called when the simulate button is pressed
     */
    public void onSimulate() {

        if (gameState.isRunning()) {
            FileHelper.writeToLog("Game is already running");
            messageLabel.setText("Game is already running");
            return;
        }

        if (gameState.getPlayers().size() <= 0) {
            FileHelper.writeToLog("Not enough players added");
            messageLabel.setText("Not enough players added");
            return;
        }

        gameState.setRunning(true);

        boolean temp = true;
        while (temp) {
            if (communicator.isReady()) {
                for (Player all : gameState.getPlayers()) {
                    all.setPlayerVision(simulator.getSelf(all.getCurrentPosition())); // init state
                }
                temp = false;
            }
        }

        this.step = new AtomicInteger(0);
        simulator.sendPlayerStates();

        startRunnable();

        reloadBoard();
        FileHelper.writeToLog("Successfully started the simulation");
        messageLabel.setText("Successfully started the simulation");
    }

    private void startRunnable() {
        this.scheduledExecutorService = new ScheduledThreadPoolExecutor(1);
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            if (!paused) {
                if (communicator.isReady()) {

                    // checks if game is over
                    boolean over = true;
                    for (Player all : gameState.getPlayers()) {
                        if (all.isAlive() && !all.isHasEscaped()) {
                            over = false;
                            break;
                        }
                    }

                    if (over) {
                        FileHelper.writeToLog("All players died or escaped and the game is over");
                        Platform.runLater(() -> messageLabel.setText("All players died or escaped and the game is over"));
                        scheduledExecutorService.shutdown();
                    }

                    step.incrementAndGet();
                    simulator.receiveInstructions();
                    simulator.simulateStep();
                    for (Player all : gameState.getPlayers()) {
                        all.setPlayerVision(simulator.getSelf(all.getCurrentPosition()));
                        simulator.setVisible(all.getPlayerVision(), true);
                    }
                    simulator.sendPlayerStates();
                    Platform.runLater(this::reloadBoard);
                    Platform.runLater(() -> stepLabel.setText("Step: " + step.get()));

                    if (restart) {
                        try {
                            restart = false;
                            period = Integer.parseInt(periodField.getText());
                            scheduledExecutorService.shutdown();
                            startRunnable();
                        } catch (NumberFormatException ignored) { }
                    }
                }
            }
        }, 100, period, TimeUnit.MILLISECONDS);
    }

    /**
     * Method thats called when the add bot button is pressed
     */
    public void onAddBot() {
        if (gameState.isRunning()) {
            FileHelper.writeToLog("Game is currently running, press restart to pause");
            messageLabel.setText("Game is currently running, press restart to pause");
            return;
        }

        String bName = botName.getText();
        if (getPlayer(bName) != null) {
            FileHelper.writeToLog("Bot is already registered under that name");
            messageLabel.setText("Bot is already registered under that name");
            return;
        }

        if (!new File(bName).exists()) {
            FileHelper.writeToLog("Bot is doesnt exist");
            messageLabel.setText("Bot is doesnt exist");
            return;
        }

        Player player = new Player(bName, null);
        if (spawn) {
            try {
                int x = Integer.parseInt(xSpawn.getText());
                int y = Integer.parseInt(ySpawn.getText());
                player.setCustomSpawn(new Point2D(x, y));
                reloadBoard();
            } catch (NumberFormatException e) {
                FileHelper.writeToLog("Please enter a real number");
                messageLabel.setText("Please enter a real number");
                return;
            }
        }

        communicator.initNewPlayer(player);

        gameState.getPlayers().add(player);
        removeBotCB.getItems().add(bName);

        player.setPlayerVision(simulator.getSelf(player.getCurrentPosition()));
        simulator.setVisible(player.getPlayerVision(), true);
        gameState.addPlayer(new Point2D(0, 0));

        removeBot.setDisable(false);
        removeBotCB.setDisable(false);

        removeBotCB.setValue(bName);

        reloadBoard();
        FileHelper.writeToLog("Successfully added player " + bName);
        messageLabel.setText("Successfully added player " + bName);
    }

    /**
     * Method thats called when the remove bot button is pressed
     */
    public void onRemoveBot() {
        if (gameState.isRunning()) {
            FileHelper.writeToLog("Game is currently running, press restart to cancel and remove bots");
            messageLabel.setText("Game is currently running, press restart to cancel and remove bots");
            return;
        }

        String botName = (String) removeBotCB.getValue();

        for (Player all : gameState.getPlayers()) {
            if (all.getId().equals(botName)) {
                gameState.getGame()[(int) all.getCurrentPosition().getY()][(int) all.getCurrentPosition().getX()].getArrayList().remove(Status.PLAYER);
                gameState.getGame()[(int) all.getCurrentPosition().getY()][(int) all.getCurrentPosition().getX()].getArrayList().remove(Status.START);
                all.getProcess().destroy();
                reloadBoard();
            }
        }

        gameState.getPlayers().removeIf(all -> all.getId().equals(botName));
        removeBotCB.getItems().remove(botName);

        if (gameState.getPlayers().size() == 0) {
            removeBot.setDisable(true);
            removeBotCB.setDisable(true);
        }
        FileHelper.writeToLog("Successfully removed player " + botName);
        messageLabel.setText("Successfully removed player " + botName);
    }

    /**
     * Method thats called when the randomize button is pressed
     */
    public void onRandomize() {
        if (gameState.isRunning()) {
            FileHelper.writeToLog("Game is currently still running");
            messageLabel.setText("Game is currently still running");
            return;
        }

        int wumpi = 1;
        int holes = 1;

        try {
            wumpi = Integer.parseInt(wumpusField.getText());
            holes = Integer.parseInt(holeField.getText());
        } catch (NumberFormatException e) {
            FileHelper.writeToLog("Please enter a real number! Standard values used");
            messageLabel.setText("Please enter a real number! Standard values used");
        }

        gameState.setW_COUNT(wumpi);
        gameState.setMIN_H_COUNT(holes);

        generated = true;

        overwriteButtons();
        gameState.generateRandomState();
        for (Player all : gameState.getPlayers()) {
            all.setPlayerVision(simulator.getSelf(all.getCurrentPosition()));
            simulator.setVisible(all.getPlayerVision(), true);
        }
        gameState.colorField(buttons, visible);
        FileHelper.writeToLog("Successfully randomized board");
        messageLabel.setText("Successfully randomized board");
    }

    /**
     * Method thats called when the abort button is pressed
     */
    public void onAbort() {
        for (Player p : gameState.getPlayers()) {
            p.getProcess().destroy();
        }
        gameState.setRunning(false);
        gameState.getPlayers().clear();
        gameState.getWumpuses().clear();

        overwriteButtons();
        gameState.initState();

        stepLabel.setText("Step: 0");
        removeBotCB.getItems().clear();
        this.paused = false;
        if (this.scheduledExecutorService != null) {
            this.scheduledExecutorService.shutdown();
        }

        removeBot.setDisable(true);
        removeBotCB.setDisable(true);
        FileHelper.writeToLog("Successfully aboard the game and removed players");
        messageLabel.setText("Successfully aboard the game and removed players");
    }

    public void onPause() {
        paused = true;
        FileHelper.writeToLog("Successfully set game state to pause");
        messageLabel.setText("Successfully set game state to pause");
    }

    public void onContinue() {
        paused = false;
        FileHelper.writeToLog("Successfully set game state to continue");
        messageLabel.setText("Successfully set game state to continue");
    }

    public void onVis() {
        visible = tilesVis.isSelected();
        if (generated) {
            gameState.colorField(buttons, visible);
        } else {
            initButtons();
        }
    }

    public void onCustom() {
        custom = customVal.isSelected();
        gameState.setCustomValues(custom);
        if (custom) {
            holeField.setDisable(false);
            wumpusField.setDisable(false);
        } else {
            holeField.setDisable(true);
            wumpusField.setDisable(true);
        }
    }

    public void onObjectAdd() {
        int x, y;
        try {
            x = Integer.parseInt(addX.getText());
            y = Integer.parseInt(addY.getText());
        } catch (NumberFormatException e) {
            FileHelper.writeToLog("Please input a real number!");
            messageLabel.setText("Please input a real number!");
            return;
        }

        if (!checkValidInput(x, y)) return;

        if (addCB.getValue().equals("Hole")) {
            if (gameState.getGame()[y][x].getArrayList().contains(Status.WUMPUS) ||
                    gameState.getGame()[y][x].getArrayList().contains(Status.PLAYER)) {
                FileHelper.writeToLog("Already occupied!");
                messageLabel.setText("Already occupied!");
                return;
            } else {
                gameState.addHole(y, x);
            }
        } else if (addCB.getValue().equals("Wumpus")) {
            if (gameState.getGame()[y][x].getArrayList().contains(Status.HOLE) ||
                    gameState.getGame()[y][x].getArrayList().contains(Status.PLAYER)) {
                FileHelper.writeToLog("Already occupied!");
                messageLabel.setText("Already occupied!");
                return;
            } else {
                gameState.addGold(y, x);
                gameState.getWumpuses().add(new Wumpus(new Point2D(x, y)));
            }
        }
        reloadBoard();
        FileHelper.writeToLog("Successfully added entity to the board");
        messageLabel.setText("Successfully added entity to the board");
    }

    public void onObjectRemove() {

        int x, y;
        try {
            x = Integer.parseInt(removeX.getText());
            y = Integer.parseInt(removeY.getText());
        } catch (NumberFormatException e) {
            FileHelper.writeToLog("Please input a real number");
            messageLabel.setText("Please input a real number!");
            return;
        }

        if (!checkValidInput(x, y)) return;

        if (gameState.getGame()[y][x].getArrayList().contains(Status.WUMPUS)) {
            gameState.getWumpuses().removeIf(wumpus -> wumpus.getCurrentPosition().equals(new Point2D(x, y)));
            gameState.getGame()[y][x].getArrayList().remove(Status.GOLD);
            gameState.getGame()[y][x].getArrayList().remove(Status.WUMPUS);
            gameState.removeSurrounding(y, x, Status.STENCH);
            reloadBoard();
            FileHelper.writeToLog("Successfully removed a wumpus from the game");
            messageLabel.setText("Successfully removed a wumpus from the game");
            return;
        }
        if (gameState.getGame()[y][x].getArrayList().contains(Status.HOLE)) {
            gameState.getGame()[y][x].getArrayList().remove(Status.HOLE);
            gameState.removeSurrounding(y, x, Status.WIND);
            reloadBoard();
            FileHelper.writeToLog("Successfully removed a hole from the game");
            messageLabel.setText("Successfully removed a hole from the game");
            return;
        }
        FileHelper.writeToLog("This field cant be deleted. You can only delete: Wumpus, Hole");
        messageLabel.setText("This field cant be deleted. You can only delete: Wumpus, Hole");
    }

    public void onSpawn() {
        spawn = cSpawn.isSelected();
        gameState.setCustomSpawn(spawn);
        if (spawn) {
            xSpawn.setDisable(false);
            ySpawn.setDisable(false);
            customSpawnButton.setDisable(false);
        } else {
            xSpawn.setDisable(true);
            ySpawn.setDisable(true);
            customSpawnButton.setDisable(true);
        }
    }

    public void onCustomSpawn() {
        int x, y;
        try {
            x = Integer.parseInt(xSpawn.getText());
            y = Integer.parseInt(ySpawn.getText());
        } catch (NumberFormatException e) {
            FileHelper.writeToLog("Please input a real number");
            messageLabel.setText("Please input a real number");
            return;
        }

        if (gameState.getGame()[y][x].getArrayList().contains(Status.WUMPUS) ||
                gameState.getGame()[y][x].getArrayList().contains(Status.START) ||
                gameState.getGame()[y][x].getArrayList().contains(Status.HOLE)) {
            FileHelper.writeToLog("Field already occupied");
            messageLabel.setText("Field already occupied");
            return;
        }

        if (removeBotCB.getValue() != null) {
            Player selPlayer = getPlayer(removeBotCB.getValue().toString());
            Point2D newPlayerPos = new Point2D(x, y);
            if (selPlayer != null) {
                if (selPlayer.getCurrentPosition() != null) {
                    int oldX = (int) selPlayer.getCurrentPosition().getX();
                    int oldY = (int) selPlayer.getCurrentPosition().getY();
                    gameState.getGame()[oldY][oldX].getArrayList().remove(Status.PLAYER);
                    gameState.getGame()[oldY][oldX].getArrayList().remove(Status.START);
                }
                gameState.addPlayer(newPlayerPos);
                selPlayer.setCustomSpawn(newPlayerPos);
                selPlayer.setCurrentPosition(newPlayerPos);
            }
        } else {
            FileHelper.writeToLog("No bot is selected");
            messageLabel.setText("No bot is selected");
            return;
        }
        reloadBoard();
        FileHelper.writeToLog("Successfully set a custom spawn for the player " + removeBotCB);
        messageLabel.setText("Successfully set a custom spawn for the player " + removeBotCB);
    }

    public void reloadBoard() {
        overwriteButtons();
        gameState.colorField(buttons, visible);
    }

    public void overwriteButtons() {
        for (int row = 0; row < rowF; row++) {
            for (int column = 0; column < columnF; column++) {
                buttons[row][column].setGraphic(null);
                buttons[row][column].setText("");
                if (visible) buttons[row][column].setStyle("-fx-background-color: WHITE;-fx-border-color: BLACK");
                else buttons[row][column].setStyle("-fx-background-color: GRAY;-fx-border-color: BLACK");
            }
        }
    }

    /**
     * Method that initiates the button field
     */
    private void initButtons() {
        gridPane.getChildren().clear();
        this.buttons = new Button[rowF][columnF];

        for (int row = 0; row < rowF; row++) {
            for (int column = 0; column < columnF; column++) {
                Button b = new Button();
                if (visible) b.setStyle("-fx-background-color: WHITE;-fx-border-color: BLACK");
                else b.setStyle("-fx-background-color: GRAY;-fx-border-color: BLACK");
                b.prefWidthProperty().bind(gridPane.widthProperty());
                b.prefHeightProperty().bind(gridPane.heightProperty());
                gridPane.add(b, column, row);
                buttons[row][column] = b;
            }
        }
        if (gameState != null) this.gameState.updateGameSize(rowF, columnF);
    }

    public boolean checkValidInput(int x, int y) {
        return (x >= 0 && x < columnF && y >= 0 && y < rowF);
    }

    private Player getPlayer(String playerID) {
        for (Player all : gameState.getPlayers()) {
            if (all.getId().equals(playerID)) {
                return all;
            }
        }
        return null;
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

    public void egg() {
        if (egg.getText().equals("sponsored by raid shadow legends")) egg.setText("");
        else egg.setText("sponsored by raid shadow legends");
    }

    public void updatePeriod() {
        restart = true;
    }
}