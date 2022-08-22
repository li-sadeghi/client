package view.guicontroller.cw;

import config.Config;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import network.Client;
import network.ServerController;
import sharedmodels.cw.HomeWork;
import sharedmodels.department.Course;
import view.OpenPage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HomeWorkPageGUI implements Initializable {
    public static Client client = ServerController.client;
    public static Config config = Config.getConfig();
    public static boolean isMaster;
    public static HomeWork homeWork;



    @FXML
    AnchorPane background;
    @FXML
    Button refreshButton;
    @FXML
    Label connectionLabel;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }


    public void refresh(ActionEvent actionEvent) throws IOException {
        ServerController.reconnect();
    }

    public void logout(ActionEvent actionEvent) throws IOException {
        String page = config.getProperty(String.class, "loginPage");
        OpenPage.openNewPage(actionEvent, page);
    }

    public void backMainMenu(ActionEvent actionEvent) throws IOException {
        String page = config.getProperty(String.class, "cwMainPage");
        OpenPage.openNewPage(actionEvent, page);
    }
}
