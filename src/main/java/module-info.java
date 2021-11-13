module de.legoshi.wumpusenv {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;


    opens de.legoshi.wumpusenv to javafx.fxml;
    exports de.legoshi.wumpusenv;
}