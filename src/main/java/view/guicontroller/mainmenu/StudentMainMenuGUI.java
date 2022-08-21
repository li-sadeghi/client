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
import network.database.StudentData;
import sharedmodels.chatroom.MessageType;
import sharedmodels.users.SharedStudent;
import sharedmodels.users.StudentGrade;
import time.DateAndTime;
import view.OpenPage;
import view.guicontroller.CheckConnection;
import view.guicontroller.Theme;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class StudentMainMenuGUI implements Initializable {
    public static Client client = ServerController.client;
    public static SharedStudent student;
    public static Config config = Config.getConfig();
    public static String requestType;
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
    Label statusLabel;
    @FXML
    Label helperMasterLabel;
    @FXML
    Label licenseLabel;
    @FXML
    Label regTimeLabel;
    @FXML
    Label recommendationLabel;
    @FXML
    Label certificateLabel;
    @FXML
    Label minorLabel;
    @FXML
    Label withdrawLabel;
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
        student = StudentData.student;
        Theme.setTheme(counter, background);
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(6), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                CheckConnection.checkConnection(refreshButton, connectionLabel);
                student = StudentData.student;
                setPageItems();
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

    public void setPageItems() {
        if (student.getGrade() == StudentGrade.GRADUATED) {
            minorLabel.setText(config.getProperty(String.class, "dormLabel"));
            minorLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    StudentMainMenuGUI.requestType = config.getProperty(String.class, "dormRequest");
                    try {
                        openRequestPage(event);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if (student.getGrade() == StudentGrade.PHD) {
            withdrawLabel.setVisible(false);
            recommendationLabel.setText(config.getProperty(String.class, "certificateLabel"));
            certificateLabel.setText(config.getProperty(String.class, "withdrawLabel"));
            minorLabel.setText(config.getProperty(String.class, "thesisDefenseLabel"));
            recommendationLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    StudentMainMenuGUI.requestType = config.getProperty(String.class, "certificateRequest");
                    try {
                        openRequestPage(event);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            certificateLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    StudentMainMenuGUI.requestType = config.getProperty(String.class, "withdrawRequest");
                    try {
                        openRequestPage(event);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            minorLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    StudentMainMenuGUI.requestType = config.getProperty(String.class, "thesisDefenseRequest");
                    ;
                    try {
                        openRequestPage(event);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else return;
    }

    public void openRequestPage(MouseEvent mouseEvent) throws IOException {
        String page = config.getProperty(String.class, "requestPage");
        OpenPage.openNewPage(mouseEvent, page);
    }

    public void setSpecifications() {
        SetPage.setPage(userImageVBox, nameLabel, emailAddressLabel, lastLoginTimeLabel, userImageVBox, student);
        statusLabel.setText(student.getStatus().toString());
        //TODO
//        if (student.getHelperMaster() == null){
//            helperMasterLabel.setText("none");
//        }else {
//            helperMasterLabel.setText(student.getHelperMaster().getFullName());
//        }
        regTimeLabel.setText(student.getRegistrationTime());
        licenseLabel.setText(student.getRegistrationLicence().toString());
    }

    public void changeTheme(ActionEvent actionEvent) {
        counter++;
        Theme.setTheme(counter, background);
    }

    public void logout(ActionEvent actionEvent) throws IOException {
        String page = config.getProperty(String.class, "loginPage");
        OpenPage.openNewPage(actionEvent, page);
    }

    public void profilePage(MouseEvent mouseEvent) throws IOException {
        String page = config.getProperty(String.class, "studentProfile");
        OpenPage.openNewPage(mouseEvent, page);
    }

    public void listOfCoursesPage(MouseEvent mouseEvent) throws IOException {
        String page = config.getProperty(String.class, "listOfCoursesPage");
        OpenPage.openNewPage(mouseEvent, page);
    }

    public void listOfMastersPage(MouseEvent mouseEvent) throws IOException {
        String page = config.getProperty(String.class, "listOfMastersPage");
        System.out.println(page);
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

    public void recommendationPage(MouseEvent mouseEvent) throws IOException {
        StudentMainMenuGUI.requestType = config.getProperty(String.class, "recommendationRequest");
        openRequestPage(mouseEvent);
    }

    public void CertificateStudentPage(MouseEvent mouseEvent) throws IOException {
        StudentMainMenuGUI.requestType = config.getProperty(String.class, "certificateRequest");
        ;
        openRequestPage(mouseEvent);
    }

    public void minorPage(MouseEvent mouseEvent) throws IOException {
        StudentMainMenuGUI.requestType = config.getProperty(String.class, "minorRequest");
        ;
        openRequestPage(mouseEvent);
    }

    public void withdrawPage(MouseEvent mouseEvent) throws IOException {
        StudentMainMenuGUI.requestType = config.getProperty(String.class, "withdrawRequest");
        ;
        openRequestPage(mouseEvent);
    }

    public void temporaryScoresPage(MouseEvent mouseEvent) throws IOException {
        String page = config.getProperty(String.class, "temporaryScoresPage");
        OpenPage.openNewPage(mouseEvent, page);
    }

    public void educationalStatusPage(MouseEvent mouseEvent) throws IOException {
        String page = config.getProperty(String.class, "educationalStatusPage");
        OpenPage.openNewPage(mouseEvent, page);
    }

    public void openEduGram(ActionEvent actionEvent) throws IOException {
        String page = config.getProperty(String.class, "eduGramPage");
        OpenPage.openNewPage(actionEvent, page);
    }

    public void refresh(ActionEvent actionEvent) throws IOException {
        ServerController.reconnect();
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
}
