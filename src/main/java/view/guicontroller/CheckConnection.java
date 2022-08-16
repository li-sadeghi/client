package view.guicontroller;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import network.Client;

public class CheckConnection {
    public static void checkConnection(Button refreshButton, Label connectionLabel){
        if (!Client.isConnect){
            connectionLabel.setText("disconnected...");
            connectionLabel.setStyle("-fx-text-fill: " + "red");
            refreshButton.setVisible(true);
        }
        else{
            connectionLabel.setText("connected...");
            connectionLabel.setStyle("-fx-text-fill: " + "green");
            refreshButton.setVisible(false);
        }

    }
}
