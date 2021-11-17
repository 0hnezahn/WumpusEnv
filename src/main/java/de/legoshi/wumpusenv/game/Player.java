package de.legoshi.wumpusenv.game;

import de.legoshi.wumpusenv.utils.Instruction;
import de.legoshi.wumpusenv.utils.Status;
import javafx.geometry.Point2D;
import lombok.Getter;
import lombok.Setter;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Getter @Setter
public class Player extends Entity {

    private String id;
    private Process process;
    private File file;

    private boolean scream;
    private boolean alive;

    public Player(String id) {
        this.id = id;
        this.alive = true;
        this.scream = false;
    }

    public void setScream() {
        this.scream = true;
    }

}
