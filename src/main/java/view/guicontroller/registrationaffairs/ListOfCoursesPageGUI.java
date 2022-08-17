package view.guicontroller.registrationaffairs;

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
import network.database.StudentData;
import response.Response;
import sharedmodels.department.Course;
import sharedmodels.users.MasterRole;
import sharedmodels.users.SharedMaster;
import view.OpenPage;
import view.guicontroller.CheckConnection;
import view.guicontroller.LoginGUI;
import view.guicontroller.Theme;
import view.guicontroller.mainmenu.StudentMainMenuGUI;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ListOfCoursesPageGUI implements Initializable {
    public static Client client = ServerController.client;
    public static Config config = Config.getConfig();
    public static TableView<Course> coursesTable;
    public static List<Course> coursesSelected;
    @FXML
    Label enterLabel;
    @FXML
    TextField idField;
    @FXML
    Button deleteButton;
    @FXML
    Button addButton;
    @FXML
    Button editButton;
    @FXML
    Label errorLabel;
    @FXML
    TextField courseIdField;
    @FXML
    TextField unitField;
    @FXML
    TextField nameField;
    @FXML
    AnchorPane background;
    @FXML
    VBox coursesListVbox;
    @FXML
    Button refreshButton;
    @FXML
    Label connectionLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        client = LoginGUI.client;

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(6), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                CheckConnection.checkConnection(refreshButton, connectionLabel);
                if (Client.clientType.equals(config.getProperty(String.class, "masterType"))) {
                    SharedMaster master = MasterData.master;
                    if (master.getMasterRole() == MasterRole.EDUCATIONAL_ASSISTANT) setPageFrEditor();
                }
                int counter = 0;
                if (Client.clientType.equals(config.getProperty(String.class, "studentType"))) {
                    counter = StudentMainMenuGUI.counter;
                } else {
                    //TODO
                    //counter = MasterMainMenuGUI.counter;
                }
                Theme.setTheme(counter, background);
                setCourseList();
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.playFromStart();

    }

    public void setPageFrEditor() {
        enterLabel.setVisible(true);
        idField.setVisible(true);
        deleteButton.setVisible(true);
        addButton.setVisible(true);
        editButton.setVisible(true);
    }

    public void setCourseList() {
        ArrayList<Course> courses;
        if (Client.clientType.equals(config.getProperty(String.class, "masterType"))){
            courses = MasterData.courses;
        }else {
            courses = StudentData.courses;
        }
        coursesListVbox.getChildren().clear();
        coursesTable = new TableView<>();

        TableColumn<Course, String> nameColumn = new TableColumn<>("course name");
        nameColumn.setPrefWidth(200);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Course, String> idColumn = new TableColumn<>("course id");
        idColumn.setPrefWidth(100);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Course, String> examTime = new TableColumn<>("exam Time");
        examTime.setPrefWidth(250);
        examTime.setCellValueFactory(new PropertyValueFactory<>("examTime"));
        TableColumn<Course, String> weeklyTime = new TableColumn<>("weekly time");
        weeklyTime.setPrefWidth(250);
        weeklyTime.setCellValueFactory(new PropertyValueFactory<>("weeklyTime"));
        TableColumn<Course, Integer> unit = new TableColumn<>("units");
        unit.setPrefWidth(35);
        unit.setCellValueFactory(new PropertyValueFactory<>("unit"));
        TableColumn<Course, Integer> capacity = new TableColumn<>("capacity");
        capacity.setPrefWidth(65);
        capacity.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        coursesTable.setPrefWidth(900);
        coursesTable.setPrefHeight(400);
        coursesTable.getColumns().addAll(nameColumn, idColumn, examTime, weeklyTime, unit, capacity);


        for (Course course : courses) {
            String id = course.getId();
            String name = course.getName();
            String units = String.valueOf(course.getUnit());
            boolean nameOk = StringMatcher.isOk(nameField.getText(), name);
            boolean idOk = StringMatcher.isOk(courseIdField.getText(), id);;
            boolean unitOk = StringMatcher.isOk(unitField.getText(), units);;
            if (nameOk && idOk && unitOk)coursesTable.getItems().add(course);
        }

        coursesListVbox.getChildren().add(coursesTable);
        coursesTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        coursesTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                coursesSelected = coursesTable.getSelectionModel().getSelectedItems();
            }
        });

    }

    public void logout(ActionEvent actionEvent) throws IOException {
        String page = config.getProperty(String.class, "loginPage");
        OpenPage.openNewPage(actionEvent, page);
    }

    public void backMainMenu(ActionEvent actionEvent) throws IOException {
        String type = Client.clientType;
        if (type.equals(config.getProperty(String.class, "studentType"))) {
            String page = config.getProperty(String.class, "studentMainMenu");
            OpenPage.openNewPage(actionEvent, page);
        } else {
            String page = config.getProperty(String.class, "masterMainMenu");
            OpenPage.openNewPage(actionEvent, page);
        }
    }

    public void deleteCourse(ActionEvent actionEvent) throws IOException {
        String courseId = coursesSelected.get(0).getId();
        Response response = client.getServerController().deleteCourse(courseId);
        String error = response.getErrorMessage();
        errorLabel.setText(error);
        errorLabel.setVisible(true);
    }

    public void addCoursePage(ActionEvent actionEvent) throws IOException {
        String page = config.getProperty(String.class, "editCoursePage");
        OpenPage.openNewPage(actionEvent, page);
    }

    public void editCoursePage(ActionEvent actionEvent) throws IOException {
        String page = config.getProperty(String.class, "editCoursePage");
        OpenPage.openNewPage(actionEvent, page);
    }

    public void refresh(ActionEvent actionEvent) throws IOException {
        ServerController.reconnect();
    }
}
