package view.guicontroller.chatroom;

import config.Config;
import extra.StringMatcher;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import network.Client;
import network.ServerController;
import network.database.MasterData;
import network.database.StudentData;
import sharedmodels.chatroom.Chat;
import sharedmodels.chatroom.MessageType;
import sharedmodels.department.Course;
import sharedmodels.users.SharedStudent;
import sharedmodels.users.SharedUser;
import view.OpenPage;
import view.guicontroller.CheckConnection;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class EduGramController implements Initializable {
    public static Client client = ServerController.client;
    public static Config config = Config.getConfig();
    public static ArrayList<Chat> chats;
    public static TableView<Chat> chatsTable;
    public static List<Chat> chatsSelected = new ArrayList<>();
    public static ArrayList<SharedStudent> students;

    public static TableView<SharedStudent> usersTable;
    public static List<SharedStudent> studentsSelected = new ArrayList<>();

    @FXML
    Button refreshButton;
    @FXML
    Label connectionLabel;
    @FXML
    VBox chatsVbox;
    @FXML
    VBox usersVbox;
    @FXML
    TextArea messageBox;
    @FXML
    Label sentNoticeLabel;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(6), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                CheckConnection.checkConnection(refreshButton, connectionLabel);
                if (Client.clientType.equals(config.getProperty(String.class, "studentType"))){
                    chats = StudentData.chats;
                    students = StudentData.students;
                }else {
                    chats = MasterData.chats;
                    students = MasterData.studentsHelper;
                }
                setPage();
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.playFromStart();
    }
    public void setPage(){
        chatsVbox.getChildren().clear();
        chatsTable = new TableView<>();

        TableColumn<Chat, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setPrefWidth(150);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("receiverName"));


        TableColumn<Chat, String> lastMessage = new TableColumn<>("Last message");
        lastMessage.setPrefWidth(350);
        lastMessage.setCellValueFactory(new PropertyValueFactory<>("lastMessage"));

        chatsTable.setPrefWidth(500);
        chatsTable.setPrefHeight(300);
        chatsTable.getColumns().addAll(nameColumn, lastMessage);

        for (Chat chat : chats) {
            chatsTable.getItems().add(chat);
        }

        chatsVbox.getChildren().add(chatsTable);
        chatsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        chatsTable.getSortOrder().add(lastMessage);

        chatsTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                chatsSelected = chatsTable.getSelectionModel().getSelectedItems();
                ChatPvController.thisChat = chatsSelected.get(0);
                String receiverId = chatsSelected.get(0).getReceiverId();
                int index = 0;
                for (int i = 0; i < StudentData.chats.size(); i++) {
                    if (StudentData.chats.get(i).getReceiverId().equals(receiverId)){
                        index = i;
                    }
                }
                ChatPvController.indexOfChat = index;
                String page = config.getProperty(String.class, "chatPvPage");
                try {
                    OpenPage.openNewPage(event, page);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        usersVbox.getChildren().clear();
        usersTable = new TableView<>();

        TableColumn<SharedStudent, String> studentNameColumn = new TableColumn<>("Name");
        nameColumn.setPrefWidth(200);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));


        TableColumn<SharedStudent, String> studentIdColumn = new TableColumn<>("Student Id");
        lastMessage.setPrefWidth(300);
        lastMessage.setCellValueFactory(new PropertyValueFactory<>("username"));

        usersTable.setPrefWidth(500);
        usersTable.setPrefHeight(300);
        usersTable.getColumns().addAll(studentNameColumn, studentIdColumn);

        for (SharedStudent student : students) {
            usersTable.getItems().add(student);
        }

        usersVbox.getChildren().add(usersVbox);
        usersTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        usersTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                studentsSelected = usersTable.getSelectionModel().getSelectedItems();
            }
        });


    }


    public void createChat(ActionEvent actionEvent) {
        String messageText = messageBox.getText();
        for (SharedStudent student : studentsSelected) {
            String senderId = Client.clientUsername;
            String receiverUsername = student.getUsername();
            client.getServerController().sendNewMessage(senderId, receiverUsername, messageText, MessageType.TEXT );
        }
        sentNoticeLabel.setVisible(true);
    }

    public void logout(ActionEvent actionEvent) throws IOException {
        String page = config.getProperty(String.class, "loginPage");
        OpenPage.openNewPage(actionEvent, page);
    }

    public void backMainMenu(ActionEvent actionEvent) throws IOException {
        String page = config.getProperty(String.class, "studentMainMenu");
        if (Client.clientType.equals("masterType")){
            page = config.getProperty(String.class, "masterMainMenu");
        }
        OpenPage.openNewPage(actionEvent, page);
    }

    public void refresh(ActionEvent actionEvent) throws IOException {
        ServerController.reconnect();
    }
}
