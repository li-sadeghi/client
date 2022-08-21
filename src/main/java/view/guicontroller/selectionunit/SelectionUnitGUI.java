package view.guicontroller.selectionunit;

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
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import network.Client;
import network.ServerController;
import network.database.MasterData;
import sharedmodels.users.MasterRole;
import sharedmodels.users.SharedMaster;
import view.OpenPage;
import view.guicontroller.CheckConnection;
import view.guicontroller.Theme;
import view.guicontroller.mainmenu.MasterMainMenuGUI;
import view.guicontroller.mainmenu.StudentMainMenuGUI;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SelectionUnitGUI implements Initializable {
    public static Config config = Config.getConfig();
    public static Client client = ServerController.client;


    @FXML
    Label noticeLabel;
    @FXML
    AnchorPane background;
    @FXML
    Button refreshButton;
    @FXML
    Label connectionLabel;
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        int counter = StudentMainMenuGUI.counter;
        Theme.setTheme(counter, background);
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(6), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                CheckConnection.checkConnection(refreshButton, connectionLabel);
                setPage();
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.playFromStart();
    }

    public void setPage(){

    }


    public void backMainMenu(ActionEvent actionEvent) throws IOException {
        String page = config.getProperty(String.class, "studentMainMenu");
        OpenPage.openNewPage(actionEvent, page);
    }

    public void logout(ActionEvent actionEvent) throws IOException {
        String page = config.getProperty(String.class, "loginPage");
        OpenPage.openNewPage(actionEvent, page);
    }

    public void refresh(ActionEvent actionEvent) throws IOException {
        ServerController.reconnect();
    }
}
