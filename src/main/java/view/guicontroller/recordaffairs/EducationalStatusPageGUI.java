package view.guicontroller.recordaffairs;

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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import network.Client;
import network.ServerController;
import network.database.MasterData;
import network.database.StudentData;
import sharedmodels.department.PassedCourse;
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

public class EducationalStatusPageGUI implements Initializable {
    public static Client client = ServerController.client;
    public static Config config = Config.getConfig();

    public static TableView<PassedCourse> coursesTable;
    @FXML
    AnchorPane background;
    @FXML
    VBox coursesListVbox;
    @FXML
    Button refreshButton;
    @FXML
    Label connectionLabel;
    @FXML
    Label searchLabel;
    @FXML
    TextField nameFilter;
    @FXML
    TextField idFilter;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(6), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                CheckConnection.checkConnection(refreshButton, connectionLabel);
                int counter;
                if (Client.clientType.equals(config.getProperty(String.class, "studentType"))) {
                    counter = StudentMainMenuGUI.counter;
                } else {
                    searchLabel.setVisible(true);
                    idFilter.setVisible(true);
                    nameFilter.setVisible(true);
                    counter = MasterMainMenuGUI.counter;
                }
                Theme.setTheme(counter, background);
                setPage();
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.playFromStart();

    }

    private void setPage()  {
        ArrayList<PassedCourse> passedCourses;
        if (Client.clientType.equals(config.getProperty(String.class, "studentType"))){
            passedCourses = StudentData.passedCourses;
        }else {
            passedCourses = MasterData.passedCourses;
        }


        coursesListVbox.getChildren().clear();
        coursesTable = new TableView<>();

        TableColumn<PassedCourse, String> nameColumn = new TableColumn<>("course name");
        nameColumn.setPrefWidth(300);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<PassedCourse, String> idColumn = new TableColumn<>("course id");
        idColumn.setPrefWidth(200);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("courseId"));
        TableColumn<PassedCourse, Integer> unit = new TableColumn<>("units");
        unit.setPrefWidth(100);
        unit.setCellValueFactory(new PropertyValueFactory<>("unit"));
        TableColumn<PassedCourse, Double> markColumn = new TableColumn<>("your mark");
        markColumn.setPrefWidth(100);
        markColumn.setCellValueFactory(new PropertyValueFactory<>("mark"));

        coursesTable.setPrefWidth(700);
        coursesTable.setPrefHeight(400);
        coursesTable.getColumns().addAll(nameColumn, idColumn, unit, markColumn);

        for (PassedCourse passedCourse : passedCourses) {
            String name = passedCourse.getName();
            String id = passedCourse.getCourseId();
            boolean nameOk = StringMatcher.isOk(idFilter.getText(), id);
            boolean idOk = StringMatcher.isOk(nameFilter.getText(), name);
            if (idOk && nameOk){
                coursesTable.getItems().add(passedCourse);
            }
        }

        coursesListVbox.getChildren().add(coursesTable);
        coursesTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public void logout(ActionEvent actionEvent) throws IOException {
        String page = config.getProperty(String.class, "loginPage");
        OpenPage.openNewPage(actionEvent, page);
    }

    public void backMainMenu(ActionEvent actionEvent) throws IOException {
        String page;
        if (LoginGUI.type.equals(config.getProperty(String.class, "studentType"))) {
            page = config.getProperty(String.class, "studentMainMenu");
        } else page = config.getProperty(String.class, "masterMainMenu");

        OpenPage.openNewPage(actionEvent, page);
    }

    public void refresh(ActionEvent actionEvent) throws IOException {
        ServerController.reconnect();
    }
}
