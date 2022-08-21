package view.guicontroller.selectionunit;

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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import network.Client;
import network.ServerController;
import network.database.MasterData;
import sharedmodels.department.Course;
import sharedmodels.users.MasterRole;
import sharedmodels.users.SharedMaster;
import sharedmodels.users.SharedStudent;
import view.OpenPage;
import view.guicontroller.CheckConnection;
import view.guicontroller.Theme;
import view.guicontroller.mainmenu.MasterMainMenuGUI;
import view.guicontroller.mainmenu.StudentMainMenuGUI;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class SetSelectionTimePageGUI implements Initializable {
    public static Config config = Config.getConfig();
    public static Client client = ServerController.client;
    public static List<SharedStudent> studentsSelected ;
    public static TableView<SharedStudent> usersTable;


    @FXML
    Label noticeLabel;
    @FXML
    TextField idFilter;
    @FXML
    TextField enteringYearFilter;
    @FXML
    TextField gradeFilter;
    @FXML
    TextField startTimeField;
    @FXML
    TextField endTimeField;
    @FXML
    AnchorPane background;
    @FXML
    VBox usersListVbox;
    @FXML
    Button refreshButton;
    @FXML
    Label connectionLabel;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        int counter = MasterMainMenuGUI.counter;
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
        List<SharedStudent> students = MasterData.allStudents;
        usersTable = new TableView<>();
        usersListVbox.getChildren().clear();

        TableColumn<SharedStudent, String> nameColumn = new TableColumn<>("student name");
        nameColumn.setPrefWidth(200);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));

        TableColumn<SharedStudent, String> idColumn = new TableColumn<>("student id");
        idColumn.setPrefWidth(200);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("username"));

        TableColumn<SharedStudent, String> enteringYearColumn = new TableColumn<>("entering year");
        enteringYearColumn.setPrefWidth(150);
        enteringYearColumn.setCellValueFactory(new PropertyValueFactory<>("enteringYear"));

        TableColumn<SharedStudent, String> gradeColumn = new TableColumn<>("student grade");
        gradeColumn.setPrefWidth(350);
        gradeColumn.setCellValueFactory(new PropertyValueFactory<>("grade"));

        usersTable.setPrefWidth(900);
        usersTable.setPrefHeight(400);
        usersTable.getColumns().addAll(nameColumn, idColumn, enteringYearColumn, gradeColumn);


        for (SharedStudent student : students) {
            String id = student.getUsername();
            String entering = student.getEnteringYear();
            String grade = student.getGrade().toString();
            boolean enteringOk = StringMatcher.isOk(enteringYearFilter.getText(), entering);
            boolean idOk = StringMatcher.isOk(idFilter.getText(), id);;
            boolean gradeOk = StringMatcher.isOk(gradeFilter.getText(), grade);;
            if (enteringOk && idOk && gradeOk)usersTable.getItems().add(student);
        }

        usersListVbox.getChildren().add(usersTable);
        usersTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        usersTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                studentsSelected = usersTable.getSelectionModel().getSelectedItems();
            }
        });

    }

    public void setSelectionTime(ActionEvent actionEvent) {
        String startTime = startTimeField.getText();
        String endTime = endTimeField.getText();
        for (SharedStudent student : studentsSelected) {
            client.getServerController().setSelectionTime(student.getUsername(), startTime, endTime);
        }
        noticeLabel.setVisible(true);
        startTimeField.clear();
        endTimeField.clear();
    }

    public void backMainMenu(ActionEvent actionEvent) throws IOException {
        String page = config.getProperty(String.class, "masterMainMenu");
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
