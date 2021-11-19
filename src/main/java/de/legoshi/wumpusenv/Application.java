package de.legoshi.wumpusenv;

import de.legoshi.wumpusenv.game.GameState;
import de.legoshi.wumpusenv.game.Player;
import de.legoshi.wumpusenv.utils.Communicator;
import de.legoshi.wumpusenv.utils.FileHelper;
import de.legoshi.wumpusenv.utils.Simulator;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.SortedMap;

public class Application extends javafx.application.Application {

    private GameState gameState;

    /**
     * Standard method to start application.
     * @param stage
     * @throws IOException
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("view.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 1000.0, 1000.0);
        stage.setTitle("Wumpus Simulator!");
        stage.setScene(scene);
        stage.show();

        Controller controller = fxmlLoader.getController();
        FileHelper fileHelper = new FileHelper();
        fileHelper.generateTextFile();

        this.gameState = new GameState();
        this.gameState.initState();
        Communicator communicator = new Communicator(gameState);
        Simulator simulator = new Simulator(gameState, communicator);
        controller.setGameState(gameState);
        controller.setCommunicator(communicator);
        controller.setSimulator(simulator);

        simulator.setMessageLabel(controller.messageLabel);
        gameState.setMessageLabel(controller.messageLabel);
        communicator.setMessageLabel(controller.messageLabel);

        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
    }

    public void stop() {
        for(Player all : gameState.getPlayers()) {
            all.getProcess().destroy();
            System.out.println("Process " + all.getId() + " terminated!");
        }
    }

    public static void main(String[] args) {
        launch();
    }
}