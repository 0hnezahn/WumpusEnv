package de.legoshi.wumpusenv.game;

import de.legoshi.wumpusenv.utils.Status;
import lombok.Getter;

import java.util.ArrayList;

@Getter
public class FieldStatus {

    private ArrayList<Status> arrayList;

    public FieldStatus() {
        this.arrayList = new ArrayList<>();
    }

    public void addStatus(Status status) {

        if(this.arrayList.contains(Status.HOLE)) return;
        if(status.equals(Status.GOLD) && this.arrayList.contains(Status.GOLD)) return;
        if(status.equals(Status.WIND) && this.arrayList.contains(Status.WIND)) return;
        if(status.equals(Status.WUMPUS) && this.arrayList.contains(Status.WUMPUS)) return;
        if(status.equals(Status.STENCH) && this.arrayList.contains(Status.STENCH)) return;
        this.arrayList.add(status);
        if(status.equals(Status.HOLE) && !this.arrayList.isEmpty()) {
            this.arrayList.clear();
            this.arrayList.add(status);
        };

    }
}
