package view.guicontroller.editpages;

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
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import network.Client;
import network.ServerController;
import network.database.MasterData;
import network.database.StudentData;
import response.Response;
import sharedmodels.department.Course;
import sharedmodels.department.Department;
import sharedmodels.users.SharedMaster;
import view.OpenPage;
import view.guicontroller.CheckConnection;
import view.guicontroller.LoginGUI;
import view.guicontroller.Theme;
import view.guicontroller.mainmenu.MasterMainMenuGUI;
import view.guicontroller.mainmenu.StudentMainMenuGUI;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class EditCoursePageGUI implements Initializable {
    public static Client client;
    public static SharedMaster master;
    public static Config config = Config.getConfig();
    public static ArrayList<String> studentIDs = new ArrayList<>();
    public static ArrayList<String> TAsIds = new ArrayList<>();
    private Timeline timeline;
    @FXML
    Label editCourseLabel;
    @FXML
    Label addCourseLabel;
    @FXML
    TextField courseIdField;
    @FXML
    TextField courseNameField;
    @FXML
    TextField masterIdField;
    @FXML
    TextField departmentIdField;
    @FXML
    TextField unitField;
    @FXML
    TextField studentIdField;
    @FXML
    TextField weeklyTimeField;
    @FXML
    TextField examTimeField;
    @FXML
    AnchorPane background;
    @FXML
    TextField idTAField;
    @FXML
    TextField capacityField;
    @FXML
    TextField prerequisiteField;
    @FXML
    Button refreshButton;
    @FXML
    Label connectionLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        client = ServerController.client;
        int counter = MasterMainMenuGUI.counter;
        Theme.setTheme(counter, background);
        timeline = new Timeline(new KeyFrame(Duration.seconds(6), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                CheckConnection.checkConnection(refreshButton, connectionLabel);
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.playFromStart();
    }

    public void addCourse(ActionEvent actionEvent) throws IOException {
        Course newCourse = new Course();
        String id = courseIdField.getText();
        newCourse.setName(courseNameField.getText());
        String departmentId = departmentIdField.getText();
        String masterId = masterIdField.getText();
        int units = Integer.parseInt(unitField.getText());
        String weeklyTime = weeklyTimeField.getText();
        String examTime = examTimeField.getText();
        int capacity = Integer.parseInt(capacityField.getText());
        String prerequisiteId = prerequisiteField.getText();
        newCourse.setId(id);
        newCourse.setDepartmentId(departmentId);
        newCourse.setMaster(new SharedMaster());
        newCourse.setUnit(units);
        newCourse.setWeeklyTime(weeklyTime);
        newCourse.setExamTime(examTime);
        newCourse.setCapacity(capacity);
        newCourse.setPrerequisiteId(prerequisiteId);
        Response response = client.getServerController().addCourseRequest(newCourse, departmentId, masterId, prerequisiteId, studentIDs, TAsIds);
        String error = response.getErrorMessage();
        showAddNotice(error);
    }

    private void showAddNotice(String notice) {
        editCourseLabel.setVisible(false);
        addCourseLabel.setText(notice);
        addCourseLabel.setVisible(true);
    }

    public void editCourse(ActionEvent actionEvent) throws IOException {
        Course newCourse = new Course();
        String id = courseIdField.getText();
        newCourse.setName(courseNameField.getText());
        String departmentId = departmentIdField.getText();
        String masterId = masterIdField.getText();
        int units = Integer.parseInt(unitField.getText());
        String weeklyTime = weeklyTimeField.getText();
        String examTime = examTimeField.getText();
        int capacity = Integer.parseInt(capacityField.getText());
        String prerequisiteId = prerequisiteField.getText();
        newCourse.setId(id);
        newCourse.setDepartmentId(departmentId);
        newCourse.setMaster(new SharedMaster());
        newCourse.setUnit(units);
        newCourse.setWeeklyTime(weeklyTime);
        newCourse.setExamTime(examTime);
        newCourse.setCapacity(capacity);
        newCourse.setPrerequisiteId(prerequisiteId);
        Response response = client.getServerController().editCourseRequest(newCourse, departmentId, masterId, prerequisiteId, studentIDs, TAsIds);
        String error = response.getErrorMessage();
        showEditNotice(error);
    }

    private void showEditNotice(String notice) {
        addCourseLabel.setVisible(false);
        editCourseLabel.setText(notice);
        editCourseLabel.setVisible(true);
    }

    public void logout(ActionEvent actionEvent) throws IOException {
        timeline.stop();
        String page = config.getProperty(String.class, "loginPage");
        OpenPage.openNewPage(actionEvent, page);
    }

    public void backMainMenu(ActionEvent actionEvent) throws IOException {
        timeline.stop();
        String page = config.getProperty(String.class, "masterMainMenu");
        OpenPage.openNewPage(actionEvent, page);
    }

    public void addStudent(ActionEvent actionEvent) {
        studentIDs.add(studentIdField.getText());
        studentIdField.clear();
    }

    public void addTA(ActionEvent actionEvent) {
        TAsIds.add(idTAField.getText());
        idTAField.clear();
    }

    public void refresh(ActionEvent actionEvent) throws IOException {
        ServerController.reconnect();
    }
}
