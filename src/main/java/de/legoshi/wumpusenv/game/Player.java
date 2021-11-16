package de.legoshi.wumpusenv.game;

import de.legoshi.wumpusenv.utils.Instruction;
import de.legoshi.wumpusenv.utils.Status;
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
public class Player {

    private int id;
    private Instruction instruction;
    private ArrayList<Status> perception;
    private Process process;
    private File file;
    private String botName;

    public Player(int id) {
        this.instruction = Instruction.NOTHING;
        this.perception = new ArrayList<>();
    }

    public void recieveInstruction(Instruction instruction) {
        this.instruction = instruction;
    }

    public void recievePerception(ArrayList<Status> perception) {
        this.instruction = Instruction.NOTHING;
        this.perception = perception;
    }

    public void collectMessages() {

        InputStream is = process.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.schedule(() -> {
            try {
                System.out.println("started!");
                while (true) {
                    String line = br.readLine();
                    if(line != null) System.out.println(line);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 100, TimeUnit.MILLISECONDS);

    }

}
