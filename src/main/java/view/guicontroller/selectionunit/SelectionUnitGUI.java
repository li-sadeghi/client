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
import network.database.StudentData;
import response.Response;
import sharedmodels.department.Course;
import view.OpenPage;
import view.guicontroller.CheckConnection;
import view.guicontroller.Theme;
import view.guicontroller.mainmenu.StudentMainMenuGUI;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class SelectionUnitGUI implements Initializable {
    public static Config config = Config.getConfig();
    public static Client client = ServerController.client;
    public static TableView<Course> allCoursesTable;
    public static TableView<Course> suggestedCoursesTable;
    public static List<Course> coursesSelected = new ArrayList<>();


    @FXML
    Label noticeLabel;
    @FXML
    AnchorPane background;
    @FXML
    Button refreshButton;
    @FXML
    Label connectionLabel;
    @FXML
    TextField departmentFilter;
    @FXML
    VBox coursesVbox;
    @FXML
    VBox suggestionVbox;
    @FXML
    Label userNoticeLabel;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        int counter = StudentMainMenuGUI.counter;
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
        ArrayList<Course> starredCourses = StudentData.starredCourses;
        ArrayList<Course> allCourses = StudentData.allCourses;
        coursesVbox.getChildren().clear();
        suggestionVbox.getChildren().clear();

        setStarredCourses(allCourses, starredCourses);

        allCoursesTable = new TableView<>();
        suggestedCoursesTable = new TableView<>();

        TableColumn<Course, String> nameColumn = new TableColumn<>("course name");
        nameColumn.setPrefWidth(100);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Course, String> idColumn = new TableColumn<>("course id");
        idColumn.setPrefWidth(100);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Course, String> departmentColumn = new TableColumn<>("department");
        departmentColumn.setPrefWidth(100);
        departmentColumn.setCellValueFactory(new PropertyValueFactory<>("departmentName"));

        TableColumn<Course, Boolean> starredColumn = new TableColumn<>("starred");
        starredColumn.setPrefWidth(100);
        starredColumn.setCellValueFactory(new PropertyValueFactory<>("departmentName"));

        allCoursesTable.setPrefWidth(400);
        allCoursesTable.setPrefHeight(300);

        allCoursesTable.getColumns().addAll(nameColumn, idColumn, departmentColumn, starredColumn);

        for (Course course : allCourses) {
            String depName = course.getDepartmentName();
            boolean departmentOk = StringMatcher.isOk(departmentFilter.getText(), depName);
            if (departmentOk) allCoursesTable.getItems().add(course);
        }

        coursesVbox.getChildren().add(allCoursesTable);

        allCoursesTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                coursesSelected = allCoursesTable.getSelectionModel().getSelectedItems();
            }
        });

        ArrayList<Course> suggestedCourses = StudentData.suggestedCourses;
        setStarredCourses(suggestedCourses, starredCourses);


        TableColumn<Course, String> sugNameColumn = new TableColumn<>("course name");
        sugNameColumn.setPrefWidth(100);
        sugNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));



        TableColumn<Course, String> sugIdColumn = new TableColumn<>("course id");
        sugIdColumn.setPrefWidth(100);
        sugIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Course, String> sugDepartmentColumn = new TableColumn<>("department");
        sugDepartmentColumn.setPrefWidth(100);
        sugDepartmentColumn.setCellValueFactory(new PropertyValueFactory<>("departmentName"));

        TableColumn<Course, Boolean> sugStarredColumn = new TableColumn<>("starred");
        sugStarredColumn.setPrefWidth(100);
        sugStarredColumn.setCellValueFactory(new PropertyValueFactory<>("departmentName"));

        suggestedCoursesTable.setPrefWidth(400);
        suggestedCoursesTable.setPrefHeight(300);


        suggestedCoursesTable.getColumns().addAll(sugNameColumn, sugIdColumn, sugDepartmentColumn, sugStarredColumn);

        for (Course course : suggestedCourses) {
            suggestedCoursesTable.getItems().add(course);
        }

        suggestionVbox.getChildren().add(suggestedCoursesTable);
        suggestedCoursesTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                coursesSelected = allCoursesTable.getSelectionModel().getSelectedItems();
            }
        });

    }

    private void setStarredCourses(ArrayList<Course> allCourses, ArrayList<Course> starredCourses) {
        for (Course course : allCourses) {
            if (isStarred(course, starredCourses)){
                course.setStarred(true);
            }else {
                course.setStarred(false);
            }
        }
    }

    private boolean isStarred(Course course, ArrayList<Course> starredCourses){
        for (Course starredCourse : starredCourses) {
            if (starredCourse.getId().equals(course.getId()))return true;
        }
        return false;
    }
    public void starCourse(ActionEvent actionEvent) throws IOException {
        Course courseSelected = coursesSelected.get(0);
        Response response = client.getServerController().starNewCourse(Client.clientUsername, courseSelected.getId());
        String error = response.getErrorMessage();
        userNoticeLabel.setText(error);
        userNoticeLabel.setVisible(true);
    }
    public void catchCourse(ActionEvent actionEvent) throws IOException {
        Course courseSelected = coursesSelected.get(0);
        Response response = client.getServerController().catchNewCourse(Client.clientUsername, courseSelected.getId());
        String error = response.getErrorMessage();
        userNoticeLabel.setText(error);
        userNoticeLabel.setVisible(true);
    }

    public void removeCourse(ActionEvent actionEvent) throws IOException {
        Course courseSelected = coursesSelected.get(0);
        Response response = client.getServerController().removeCourse(Client.clientUsername, courseSelected.getId());
        String error = response.getErrorMessage();
        userNoticeLabel.setText(error);
        userNoticeLabel.setVisible(true);
    }


    public void backMainMenu(ActionEvent actionEvent) throws IOException {
        String page = config.getProperty(String.class, "studentMainMenu");
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
