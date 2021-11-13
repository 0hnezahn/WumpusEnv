package de.legoshi.wumpusenv;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Colorizer {

    public static ImageView colorize(Status status) throws FileNotFoundException {

        switch (status) {

            case HOLE -> {
                System.out.println(new File("./").getAbsolutePath());
                FileInputStream input = new FileInputStream("./src/main/resources/hole.png");
                Image image = new Image(input);
                return new ImageView(image);
            }
            case GOLD -> {
                FileInputStream input = new FileInputStream("./src/main/resources/gold.jpg");
                Image image = new Image(input);
                return new ImageView(image);
            }
            case WIND -> {
                FileInputStream input = new FileInputStream("./src/main/resources/wind.jpg");
                Image image = new Image(input);
                return new ImageView(image);
            }
            case STENCH -> {
                FileInputStream input = new FileInputStream("./src/main/resources/stench.png");
                Image image = new Image(input);
                return new ImageView(image);
            }
            case WUMPUS -> {
                FileInputStream input = new FileInputStream("./src/main/resources/wumpus.jpg");
                Image image = new Image(input);
                return new ImageView(image);
            }
            default -> {
                return null;
            }
        }

    }

}
