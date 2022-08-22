package view.guicontroller;

import config.Config;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import network.Client;
import network.ServerController;
import network.database.StudentData;
import response.Response;
import sharedmodels.users.SharedStudent;
import view.OpenPage;

import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.ResourceBundle;

public class ChangePasswordPageGUI implements Initializable {
    public static Client client = ServerController.client;
    public static Thread thread;
    public static Config config = Config.getConfig();
    private Timeline timeline;
    @FXML
    Label loginNotice;
    @FXML
    PasswordField newPassField;
    @FXML
    PasswordField prePassField;
    @FXML
    AnchorPane background;
    @FXML
    Button refreshButton;
    @FXML
    Label connectionLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Theme.setTheme(2, background);
        timeline = new Timeline(new KeyFrame(Duration.seconds(6), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                CheckConnection.checkConnection(refreshButton, connectionLabel);
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.playFromStart();
    }

    public void changePass(ActionEvent actionEvent) throws NoSuchAlgorithmException, IOException {
        String enteredPrePass = prePassField.getText();
        String enteredNewPass = newPassField.getText();
        Response response = client.getServerController().sendChangePasswordRequest(enteredPrePass, enteredNewPass, Client.clientUsername);
        showError(response.getErrorMessage());
    }

    public void showError(String errorName) {
        loginNotice.setText(errorName);
        loginNotice.setVisible(true);
        newPassField.clear();
        prePassField.clear();
    }

    public void backLoginPage(ActionEvent actionEvent) throws IOException {
        timeline.stop();
        String page = config.getProperty(String.class, "loginPage");
        OpenPage.openNewPage(actionEvent, page);
    }

    public void refresh(ActionEvent actionEvent) throws IOException {
        ServerController.reconnect();
    }
}
