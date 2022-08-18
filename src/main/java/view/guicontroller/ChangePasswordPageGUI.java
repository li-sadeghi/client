package view.guicontroller;

import config.Config;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.AnchorPane;
import network.Client;
import network.ServerController;
import response.Response;
import view.OpenPage;

import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.ResourceBundle;

public class ChangePasswordPageGUI implements Initializable {
    public static Client client = ServerController.client;
    public static Thread thread;
    public static Config config = Config.getConfig();
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
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    CheckConnection.checkConnection(refreshButton, connectionLabel);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public void changePass(ActionEvent actionEvent) throws NoSuchAlgorithmException, IOException {
        String enteredPrePass = prePassField.getText();
        String enteredNewPass = newPassField.getText();
        Response response = client.getServerController().sendChangePasswordRequest(enteredPrePass, enteredNewPass, Client.clientUsername);
//        if (response.getStatus() == ResponseStatus.OK) {
//            showError(config.getProperty(String.class, "changePassNotice"));
//        } else {
//            showError(config.getProperty(String.class, "tryError"));
//        }
        showError(response.getErrorMessage());
    }

    public void showError(String errorName) {
        loginNotice.setText(errorName);
        loginNotice.setVisible(true);
        newPassField.clear();
        prePassField.clear();
    }

    public void backLoginPage(ActionEvent actionEvent) throws IOException {
        thread.interrupt();
        String page = config.getProperty(String.class, "loginPage");
        OpenPage.openNewPage(actionEvent, page);
    }

    public void refresh(ActionEvent actionEvent) throws IOException {
        ServerController.reconnect();
    }
}
