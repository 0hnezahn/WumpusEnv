package de.legoshi.wumpusenv.utils;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class Colorizer {

    /**
     * Helper class to map states to certain picture.
     * @return picture that is associated with the cell state
     */
    public static Node colorize(ArrayList<Status> arrayList, Button button) {

        ArrayList<ImageView> imageViews = new ArrayList<>();

        for(Status status : arrayList) {
            try {
                switch (status) {

                    case HOLE -> {
                        FileInputStream input = new FileInputStream("./src/main/resources/hole.jpg");
                        Image image = new Image(input);
                        imageViews.add(new ImageView(image));
                    }
                    case GOLD -> {
                        FileInputStream input = new FileInputStream("./src/main/resources/gold.jpg");
                        Image image = new Image(input);
                        imageViews.add(new ImageView(image));
                    }
                    case WIND -> {
                        FileInputStream input = new FileInputStream("./src/main/resources/wind.jpg");
                        Image image = new Image(input);
                        imageViews.add(new ImageView(image));
                    }
                    case STENCH -> {
                        FileInputStream input = new FileInputStream("./src/main/resources/stench.png");
                        Image image = new Image(input);
                        imageViews.add(new ImageView(image));
                    }
                    case WUMPUS -> {
                        FileInputStream input = new FileInputStream("./src/main/resources/wumpus.jpg");
                        Image image = new Image(input);
                        imageViews.add(new ImageView(image));
                    }
                    case PLAYER -> {
                        FileInputStream input = new FileInputStream("./src/main/resources/stickman.png");
                        Image image = new Image(input);
                        imageViews.add(new ImageView(image));
                    }
                    case START -> {
                        FileInputStream input = new FileInputStream("./src/main/resources/start.jpg");
                        Image image = new Image(input);
                        imageViews.add(new ImageView(image));
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                System.out.println("Couldn't find the image file");
            }
        }

        Node box;
        double height = button.getHeight();
        double width = button.getWidth();

        if(button.getWidth() < button.getHeight()) {
            box = new VBox();
            ((VBox) box).getChildren().addAll(imageViews);
            ((VBox) box).setAlignment(Pos.CENTER);
            int count = ((VBox) box).getChildren().size();
            for(Node i : ((VBox) box).getChildren()) {
                ((ImageView) i).setFitWidth((width-(width*0.3))/count);
                ((ImageView) i).setPreserveRatio(true);
            }
        } else {
             box = new HBox();
            ((HBox) box).getChildren().addAll(imageViews);
            ((HBox) box).setAlignment(Pos.CENTER);
            int count = ((HBox) box).getChildren().size();
            for(Node i : ((HBox) box).getChildren()) {
                ((ImageView) i).setFitHeight((height-(height*0.3))/count);
                ((ImageView) i).setPreserveRatio(true);
            }
        }
        return box;
    }

}
