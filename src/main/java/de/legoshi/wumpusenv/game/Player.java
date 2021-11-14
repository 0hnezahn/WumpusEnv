package de.legoshi.wumpusenv.game;

import de.legoshi.wumpusenv.utils.Instruction;
import de.legoshi.wumpusenv.utils.Status;

public class Player {

    private int id;
    private Instruction instruction;
    private ArrayList<Status> perception;

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

}
