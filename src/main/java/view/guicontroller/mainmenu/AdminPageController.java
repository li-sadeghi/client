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
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import network.Client;
import network.ServerController;
import network.database.AdminData;
import sharedmodels.chatroom.Message;
import sharedmodels.chatroom.MessageType;
import sharedmodels.users.SharedUser;
import time.DateAndTime;
import util.extra.EncodeDecodeFile;
import view.OpenPage;
import view.guicontroller.CheckConnection;
import view.guicontroller.Theme;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AdminPageController implements Initializable {
    public static Client client = ServerController.client;
    public static Timeline timeline;
    public static Config config = Config.getConfig();
    public static int counter = 0;
    @FXML
    Label currentTimeLabel;
    @FXML
    Label lastLoginTimeLabel;
    @FXML
    Label nameLabel;
    @FXML
    Label emailAddressLabel;
    @FXML
    AnchorPane background;
    @FXML
    VBox userImageVBox;
    @FXML
    TextField usernameField;
    @FXML
    TextField messageField;
    @FXML
    TextArea messageBox;
    @FXML
    Button refreshButton;
    @FXML
    Label connectionLabel;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Theme.setTheme(counter, background);
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {

                String time = DateAndTime.getDateAndTime();
                currentTimeLabel.setText(time);
            }
        };
        timeline = new Timeline(new KeyFrame(Duration.seconds(2), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                CheckConnection.checkConnection(refreshButton, connectionLabel);
                SharedUser admin = AdminData.admin;
                ArrayList<Message> messages = AdminData.messages;
                setPage(messages, admin);
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.playFromStart();
        timer.start();
    }

    private void setPage(ArrayList<Message> messages, SharedUser admin) {
        SetPage.setPage(userImageVBox, nameLabel, emailAddressLabel, lastLoginTimeLabel, userImageVBox, admin);
        messages.clear();
        StringBuilder text = new StringBuilder();
        for (Message message : messages) {
            String newMessage = getMessage(message);
            text.append(newMessage);
        }
        messageBox.setText(String.valueOf(text));
    }

    public String getMessage(Message message){
        String text = "";
        text += message.getSender().getUsername();
        text += ":\n";
        text += message.getMessageText();
        text += "\n\n";
        return text;
    }
    public void logout(ActionEvent actionEvent) throws IOException {
        timeline.stop();
        Client.pingThread.interrupt();
        String page = config.getProperty(String.class, "loginPage");
        OpenPage.openNewPage(actionEvent, page);
    }

    public void changeTheme(ActionEvent actionEvent) {
        counter = counter + 1;
        Theme.setTheme(counter, background);
    }

    public void sendMessage(ActionEvent actionEvent) {
        String messageText = messageField.getText();
        String receiverUsername = usernameField.getText();
        String senderUsername = "1";
        client.getServerController().sendNewMessage(senderUsername, receiverUsername, messageText, MessageType.TEXT);
        messageField.clear();
        usernameField.clear();
    }

    public void refresh(ActionEvent actionEvent) throws IOException {
        ServerController.reconnect();
    }
}
