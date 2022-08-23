package view.guicontroller.cw;

import config.Config;
import extra.Deadline;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import network.Client;
import network.ServerController;
import network.database.MasterData;
import network.database.StudentData;
import request.Request;
import response.Response;
import sharedmodels.cw.HomeWork;
import sharedmodels.department.Course;
import sharedmodels.users.MasterRole;
import sharedmodels.users.SharedMaster;
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

public class CwMainPageGUI implements Initializable {

    public static Client client = ServerController.client;
    public static Config config = Config.getConfig();
    public static boolean isMaster;
    private Timeline timeline;
    private Timeline newTimeline;

    public static TableView<Course> coursesTable;
    public static List<Course> coursesSelected = new ArrayList<>();

    public static TableView<Deadline> calendarTable;

    @FXML
    AnchorPane background;
    @FXML
    VBox coursesVbox;
    @FXML
    VBox calendarVbox;
    @FXML
    Button refreshButton;
    @FXML
    Label connectionLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        int counter ;
        if (Client.clientType.equals(config.getProperty(String.class, "studentType"))) {
            counter = StudentMainMenuGUI.counter;
            isMaster = false;
        } else {
            isMaster = true;
            counter = MasterMainMenuGUI.counter;
        }
        Theme.setTheme(counter, background);

        timeline = new Timeline(new KeyFrame(Duration.seconds(6), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                CheckConnection.checkConnection(refreshButton, connectionLabel);
                ArrayList<Course> courses;
                ArrayList<HomeWork> homeWorks;
                CheckConnection.checkConnection(refreshButton, connectionLabel);
                if (Client.clientType.equals(config.getProperty(String.class, "studentType"))) {
                    courses = StudentData.coursesHave;
                    try {
                        homeWorks = getAllHomeworks(courses);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    courses = MasterData.coursesHave;
                    try {
                        homeWorks = getAllHomeworks(courses);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                setPage(courses, homeWorks);

            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.playFromStart();
    }

    private ArrayList<HomeWork> getAllHomeworks(ArrayList<Course> courses) throws IOException {
        ArrayList<HomeWork>homeWorks = new ArrayList<>();
        for (Course course : courses) {
            final int size = course.getHomeWorksId().size();
            final int[] copySize = {size - 1};
            newTimeline = new Timeline(new KeyFrame(Duration.millis(50), new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    int id = course.getHomeWorksId().get(copySize[0]);
                    Response response = null;
                    try {
                        response = client.getServerController().getHomework(id);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    HomeWork homeWork= (HomeWork) response.getData("homework");
                    homeWorks.add(homeWork);
                    copySize[0]--;
                }
            }));
            timeline.setCycleCount(size);
            timeline.playFromStart();

        }
        newTimeline.stop();
        return homeWorks;
    }


    public void setPage(ArrayList<Course> courses, ArrayList<HomeWork> homeWorks){
        ArrayList<Deadline> deadlines = getDeadlines(courses, homeWorks);
        calendarVbox.getChildren().clear();
        coursesVbox.getChildren().clear();
        coursesTable = new TableView<>();
        TableColumn<Course, String> nameColumn = new TableColumn<>("course name");
        nameColumn.setPrefWidth(300);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Course, String> idColumn = new TableColumn<>("course id");
        idColumn.setPrefWidth(200);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        coursesTable.setPrefWidth(500);
        coursesTable.setPrefHeight(300);
        coursesTable.getColumns().addAll(nameColumn, idColumn);

        for (Course course : courses) {
            if (course.isHaveCwPage())coursesTable.getItems().add(course);
        }
        coursesVbox.getChildren().add(coursesTable);
        coursesTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                timeline.stop();
                coursesSelected = coursesTable.getSelectionModel().getSelectedItems();
                CwCoursePageGUI.course = coursesSelected.get(0);
                String page = config.getProperty(String.class, "cwCoursePage");
                try {
                    OpenPage.openNewPage(event, page);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        calendarTable = new TableView<>();
        TableColumn<Deadline, String> deadLineNameColumn = new TableColumn<>("name");
        deadLineNameColumn.setPrefWidth(100);
        deadLineNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Deadline, String> timeColumn = new TableColumn<>("deadLine/examTime");
        timeColumn.setPrefWidth(250);
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));

        calendarTable.setPrefWidth(350);
        calendarTable.setPrefHeight(200);

        calendarTable.getColumns().addAll(deadLineNameColumn, timeColumn);

        for (Deadline deadline : deadlines) {
            calendarTable.getItems().add(deadline);
        }

        calendarVbox.getChildren().add(calendarTable);

    }

    public ArrayList<Deadline> getDeadlines(ArrayList<Course> courses, ArrayList<HomeWork> homeWorks){
        ArrayList<Deadline> deadlines = new ArrayList<>();
        for (Course course : courses) {
            Deadline deadline = new Deadline(course.getName(), course.getExamTime());
            deadlines.add(deadline);
        }
        for (HomeWork homeWork : homeWorks) {
            Deadline deadline = new Deadline(homeWork.getHomeWorkName(), homeWork.getEndTime());
            deadlines.add(deadline);
        }
        return deadlines;

    }


    public void refresh(ActionEvent actionEvent) throws IOException {
        ServerController.reconnect();
    }

    public void backMainMenu(ActionEvent actionEvent) throws IOException {
        timeline.stop();
        String page;
        if (isMaster) {
            page = config.getProperty(String.class, "masterMainMenu");
        } else {
            page = config.getProperty(String.class, "studentMainMenu");
        }
        OpenPage.openNewPage(actionEvent, page);
    }

    public void logout(ActionEvent actionEvent) throws IOException {
        timeline.stop();
        String page = config.getProperty(String.class, "loginPage");
        OpenPage.openNewPage(actionEvent, page);
    }
}
