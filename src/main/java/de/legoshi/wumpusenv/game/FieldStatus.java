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
        if(arrayList.contains(Status.HOLE)) return;
        if(arrayList.contains(status)) return;

        if(status.equals(Status.HOLE))  arrayList.clear();
        arrayList.add(status);
    }
}
