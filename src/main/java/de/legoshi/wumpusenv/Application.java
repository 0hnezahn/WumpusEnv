package de.legoshi.wumpusenv;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("view.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 602.0, 466.0);
        stage.setTitle("Wumpus Simulator!");
        stage.setScene(scene);
        stage.show();

        Controller controller = fxmlLoader.getController();
        GameState gameState = new GameState();
        controller.setGameState(gameState);
    }

    public static void main(String[] args) {
        launch();
    }
}