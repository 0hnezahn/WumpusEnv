package de.legoshi.wumpusenv.game;

import de.legoshi.wumpusenv.utils.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

/**
 * @author Benjamin Müller
 */

@Getter
@Setter
public class FieldStatus {

    private ArrayList<Status> arrayList;
    private boolean isVisible;

    public FieldStatus() {
        this.isVisible = false;
        this.arrayList = new ArrayList<>();
    }

    public void addStatus(Status status) {
        if(arrayList.contains(Status.HOLE)) return;
        if(arrayList.contains(status)) return;

        if(status.equals(Status.HOLE))  arrayList.clear();
        arrayList.add(status);
    }
}
