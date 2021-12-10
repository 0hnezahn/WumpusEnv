package de.legoshi.wumpusenv.utils;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Setter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * @author Benjamin Müller
 */

public class Colorizer {

    /**
     * Helper class to map states to certain picture.
     * @return picture that is associated with the cell state
     */
    public Node colorize(ArrayList<Status> arrayList, Button button) {

        ArrayList<ImageView> imageViews = new ArrayList<>();

        for(Status status : arrayList) {
            switch (status) {

                case HOLE -> {
                    Image image = new Image("/hole.jpg");
                    imageViews.add(new ImageView(image));
                }
                case GOLD -> {
                    Image image = new Image("/gold.jpg");
                    imageViews.add(new ImageView(image));
                }
                case WIND -> {

                    Image image = new Image("/wind.jpg");
                    imageViews.add(new ImageView(image));
                }
                case STENCH -> {
                    Image image = new Image("/stench.PNG");
                    imageViews.add(new ImageView(image));
                }
                case WUMPUS -> {
                    Image image = new Image("/wumpus.jpg");
                    imageViews.add(new ImageView(image));
                }
                case PLAYER -> {
                    Image image = new Image("/stickman.png");
                    imageViews.add(new ImageView(image));
                }
                case START -> {
                    Image image = new Image("/start.jpg");
                    imageViews.add(new ImageView(image));
                }
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
