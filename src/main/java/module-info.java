module de.legoshi.wumpusenv {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires static lombok;


    opens de.legoshi.wumpusenv to javafx.fxml;
    exports de.legoshi.wumpusenv;
}