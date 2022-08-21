package view.guicontroller.educationalservices;

import config.Config;
import extra.StringMatcher;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import network.Client;
import network.ServerController;
import network.database.MasterData;
import network.database.StudentData;
import sharedmodels.department.Course;
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

public class WeeklySchedulePageGUI implements Initializable {
    public static Client client;
    public static Config config = Config.getConfig();
    public static TableView<Course> coursesTable;
    @FXML
    AnchorPane background;
    @FXML
    Button refreshButton;
    @FXML
    Label connectionLabel;
    @FXML
    VBox coursesVBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        client = ServerController.client;
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(6), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                CheckConnection.checkConnection(refreshButton, connectionLabel);
                String type = Client.clientType;
                int counter = 0;
                if (type.equals(config.getProperty(String.class, "studentType"))) {
                    ArrayList<Course> courses = StudentData.coursesHave;
                    setWeeklySchedule(courses);
                    counter = StudentMainMenuGUI.counter;
                } else {
                    ArrayList<Course> courses = MasterData.coursesHave;
                    setWeeklySchedule(courses);
                    counter = MasterMainMenuGUI.counter;
                }
                Theme.setTheme(counter, background);
            }
        }));

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.playFromStart();
    }

    public void setWeeklySchedule(ArrayList<Course> courses){
        coursesVBox.getChildren().clear();
        coursesTable = new TableView<>();
        TableColumn<Course, String> nameColumn = new TableColumn<>("course name");
        nameColumn.setPrefWidth(200);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Course, String> weeklyTime = new TableColumn<>("weekly time");
        weeklyTime.setPrefWidth(400);
        weeklyTime.setCellValueFactory(new PropertyValueFactory<>("weeklyTime"));
        coursesTable.setPrefWidth(600);
        coursesTable.setPrefHeight(400);
        coursesTable.getColumns().addAll(nameColumn, weeklyTime);
        for (Course course : courses) {
            coursesTable.getItems().add(course);
        }
        coursesVBox.getChildren().add(coursesTable);
    }

    public void logout(ActionEvent actionEvent) throws IOException {
        String page = config.getProperty(String.class, "loginPage");
        OpenPage.openNewPage(actionEvent, page);
    }

    public void backMainMenu(ActionEvent actionEvent) throws IOException {
        String type = Client.clientType;
        String page;
        if (type.equals(config.getProperty(String.class, "studentType"))) {
            page = config.getProperty(String.class, "studentMainMenu");
        } else {
            page = config.getProperty(String.class, "masterMainMenu");
        }
        OpenPage.openNewPage(actionEvent, page);
    }

    public void refresh(ActionEvent actionEvent) throws IOException {
        ServerController.reconnect();
    }
}
