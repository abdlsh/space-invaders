module ca.abdullahs.javafx {
    requires javafx.controls;
    requires javafx.fxml;


    opens ca.abdullahs.gui_game to javafx.fxml;
    exports ca.abdullahs.gui_game;
}