package view.guicontroller.mainmenu;

import config.Config;
import extra.StringMatcher;
import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import network.Client;
import network.ServerController;
import network.database.AdminData;
import network.database.MohseniData;
import sharedmodels.chatroom.Message;
import sharedmodels.chatroom.MessageType;
import sharedmodels.users.SharedStudent;
import sharedmodels.users.SharedUser;
import time.DateAndTime;
import view.OpenPage;
import view.guicontroller.CheckConnection;
import view.guicontroller.Theme;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MohseniPageController implements Initializable {


    public static Client client = ServerController.client;
    public static Timeline timeline;
    public static Config config = Config.getConfig();
    public static int counter = 0;
    public static TableView<SharedStudent> studentsTable = new TableView<>();
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
    Button refreshButton;
    @FXML
    Label connectionLabel;
    @FXML
    VBox studentsVbox;
    @FXML
    TextField idField;
    @FXML
    TextField phoneField;
    @FXML
    TextField nameField;
    @FXML
    TextArea messageBox;
    @FXML
    Label errorLabel;

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
                SharedUser mohseni = MohseniData.mohseni;
                ArrayList<SharedStudent> students = MohseniData.students;
                setPage(mohseni, students);
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.playFromStart();
        timer.start();
    }

    private void setPage(SharedUser mohseni, ArrayList<SharedStudent> students) {
        SetPage.setPage(userImageVBox, nameLabel, emailAddressLabel, lastLoginTimeLabel, userImageVBox, mohseni);
        TableColumn<SharedStudent, String> nameColumn = new TableColumn<>("full name");
        nameColumn.setPrefWidth(200);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        TableColumn<SharedStudent, String> idColumn = new TableColumn<>("student id");
        idColumn.setPrefWidth(200);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        TableColumn<SharedStudent, String> phoneColumn = new TableColumn<>("phone number");
        phoneColumn.setPrefWidth(200);
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        TableColumn<SharedStudent, String> enteringYear = new TableColumn<>("entering Year");
        enteringYear.setPrefWidth(100);
        enteringYear.setCellValueFactory(new PropertyValueFactory<>("enteringYear"));
        studentsTable.setPrefWidth(700);
        studentsTable.setPrefHeight(400);
        studentsTable.getColumns().addAll(nameColumn, idColumn, phoneColumn, enteringYear);
        for (SharedStudent student : students) {
            String phone = student.getPhoneNumber();
            String id = student.getUsername();
            String name = student.getFullName();
            boolean nameOk = StringMatcher.isOk(nameField.getText(), name);
            boolean idOk = StringMatcher.isOk(idField.getText(), id);;
            boolean phoneOk = StringMatcher.isOk(phoneField.getText(), phone);;
            if (nameOk && idOk && phoneOk)studentsTable.getItems().add(student);
        }
        studentsVbox.getChildren().add(studentsTable);
        studentsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
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

    public void refresh(ActionEvent actionEvent) throws IOException {
        ServerController.reconnect();
    }


    public void sendMessageToAll(ActionEvent actionEvent) {
        ArrayList<SharedStudent> studentsSelected = (ArrayList<SharedStudent>) studentsTable.getSelectionModel().getSelectedItems();
        String text = messageBox.getText();
        String senderUser = MohseniData.mohseni.getUsername();
        for (SharedStudent student : studentsSelected) {
            String receiver = student.getUsername();
            client.getServerController().sendNewMessage(senderUser, receiver, text, MessageType.TEXT);
        }
        errorLabel.setVisible(true);
        messageBox.clear();
    }
}
