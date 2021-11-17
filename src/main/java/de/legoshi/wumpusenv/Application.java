package de.legoshi.wumpusenv;

import de.legoshi.wumpusenv.game.GameState;
import de.legoshi.wumpusenv.game.Player;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

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
        this.gameState = new GameState();
        controller.setGameState(gameState);

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