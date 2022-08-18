package view.guicontroller.mainmenu;

import config.Config;
import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import network.Client;
import network.ServerController;
import network.database.MasterData;
import sharedmodels.chatroom.MessageType;
import sharedmodels.users.MasterRole;
import sharedmodels.users.SharedMaster;
import time.DateAndTime;
import view.OpenPage;
import view.guicontroller.CheckConnection;
import view.guicontroller.Theme;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MasterMainMenuGUI implements Initializable {

    public static Client client = ServerController.client;
    public static SharedMaster master = MasterData.master;
    public static int counter = 0;
    public static Config config = Config.getConfig();
    @FXML
    Label currentTimeLabel;
    @FXML
    Label lastLoginTimeLabel;
    @FXML
    Label nameLabel;
    @FXML
    Label emailAddressLabel;
    @FXML
    Label addNewUserLabel;
    @FXML
    Label educationalStatus;
    @FXML
    AnchorPane background;
    @FXML
    VBox userImageVBox;
    @FXML
    Button refreshButton;
    @FXML
    Label connectionLabel;
    @FXML
    TextArea messageBox;
    @FXML
    Label errorLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                master = MasterData.master;
                CheckConnection.checkConnection(refreshButton, connectionLabel);
                Theme.setTheme(counter, background);
                if (master.getMasterRole() == MasterRole.EDUCATIONAL_ASSISTANT) {
                    addNewUserLabel.setVisible(true);
                    educationalStatus.setVisible(true);
                }
                setSpecifications();
            }
        }));
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                String time = DateAndTime.getDateAndTime();
                currentTimeLabel.setText(time);
            }
        };
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.playFromStart();
        timer.start();
    }

    public void setSpecifications()  {
        SetPage.setPage(userImageVBox, nameLabel, emailAddressLabel, lastLoginTimeLabel, userImageVBox, master);
    }

    public void logout(ActionEvent actionEvent) throws IOException {
        String page = config.getProperty(String.class, "loginPage");
        OpenPage.openNewPage(actionEvent, page);
    }

    public void changeTheme(ActionEvent actionEvent) {
        counter++;
        Theme.setTheme(counter, background);
    }

    public void profilePage(MouseEvent mouseEvent) throws IOException {
        String page = config.getProperty(String.class, "masterProfile");
        OpenPage.openNewPage(mouseEvent, page);
    }

    public void listOfCoursesPage(MouseEvent mouseEvent) throws IOException {
        String page = config.getProperty(String.class, "listOfCoursesPage");
        OpenPage.openNewPage(mouseEvent, page);
    }

    public void listOfMastersPage(MouseEvent mouseEvent) throws IOException {
        String page = config.getProperty(String.class, "listOfMastersPage");
        OpenPage.openNewPage(mouseEvent, page);
    }

    public void openAddNewUserPage(MouseEvent mouseEvent) throws IOException {
        String page = config.getProperty(String.class, "addNewUserPage");
        OpenPage.openNewPage(mouseEvent, page);
    }

    public void weeklySchedulePage(MouseEvent mouseEvent) throws IOException {
        String page = config.getProperty(String.class, "weeklySchedulePage");
        OpenPage.openNewPage(mouseEvent, page);
    }

    public void examListPage(MouseEvent mouseEvent) throws IOException {
        String page = config.getProperty(String.class, "examListPage");
        OpenPage.openNewPage(mouseEvent, page);
    }


    public void masterRequestPage(MouseEvent mouseEvent) throws IOException {
        String page = config.getProperty(String.class, "masterRequestPage");
        OpenPage.openNewPage(mouseEvent, page);
    }

    public void educationalStatusPage(MouseEvent mouseEvent) throws IOException {
        String page = config.getProperty(String.class, "educationalStatusPage");
        OpenPage.openNewPage(mouseEvent, page);
    }

    public void temporaryScoresPage(MouseEvent mouseEvent) throws IOException {
        String page = config.getProperty(String.class, "masterTemporaryScores");
        OpenPage.openNewPage(mouseEvent, page);
    }

    public void openEduGram(ActionEvent actionEvent) throws IOException {
        String page = config.getProperty(String.class, "eduGramPage");
        OpenPage.openNewPage(actionEvent, page);
    }

    public void sendMessageToAdmin(ActionEvent actionEvent) {
        String text = messageBox.getText();
        String senderUsername = Client.clientUsername;
        String receiverUsername = "1";
        if (Client.isConnect) {
            client.getServerController().sendNewMessage(senderUsername, receiverUsername, text, MessageType.TEXT);
        } else {
            //TODO
            //message offline
        }
        messageBox.clear();
        errorLabel.setVisible(true);
    }

    public void refresh(ActionEvent actionEvent) throws IOException {
        ServerController.reconnect();
    }
}
