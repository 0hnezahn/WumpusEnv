package de.legoshi.wumpusenv.game;

import de.legoshi.wumpusenv.utils.Status;

import java.util.ArrayList;

public class FieldStatus {

    private ArrayList<Status> arrayList;

    public FieldStatus() {

        this.arrayList = new ArrayList<>();

    }

    public ArrayList<Status> getArrayList() {
        return arrayList;
    }
}
