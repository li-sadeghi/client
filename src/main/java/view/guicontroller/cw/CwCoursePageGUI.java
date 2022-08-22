package view.guicontroller.cw;

import config.Config;
import extra.Deadline;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
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
import response.Response;
import sharedmodels.cw.HomeWork;
import sharedmodels.department.Course;
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

public class CwCoursePageGUI implements Initializable {
    public static Client client = ServerController.client;
    public static Config config = Config.getConfig();
    public static boolean isMaster;
    public static Course course;

    public static TableView<HomeWork> homeworksTable;
    public static List<HomeWork> homeworksSelected = new ArrayList<>();

    public static TableView<Deadline> calendarTable;
    @FXML
    AnchorPane background;
    @FXML
    VBox homeworksVbox;
    @FXML
    VBox calendarVbox;
    @FXML
    Button refreshButton;
    @FXML
    Label connectionLabel;
    @FXML
    Label courseNameLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        courseNameLabel.setText(course.getName());
        int counter ;
        if (Client.clientType.equals(config.getProperty(String.class, "studentType"))) {
            counter = StudentMainMenuGUI.counter;
            isMaster = false;
        } else {
            isMaster = true;
            counter = MasterMainMenuGUI.counter;
        }
        Theme.setTheme(counter, background);
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(6), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                CheckConnection.checkConnection(refreshButton, connectionLabel);
                ArrayList<HomeWork> homeWorks = null;
                try {
                    homeWorks = getAllHomeworks(course);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                setPage( homeWorks);
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.playFromStart();

    }

    private ArrayList<HomeWork> getAllHomeworks(Course course) throws IOException {
        ArrayList<HomeWork> homeWorks = new ArrayList<>();
        for (Integer id : course.getHomeWorksId()) {
            Response response = client.getServerController().getHomework(id);
            HomeWork homeWork = (HomeWork) response.getData("homework");
            homeWorks.add(homeWork);
        }
        return homeWorks;
    }


    public void setPage(ArrayList<HomeWork> homeWorks){
        ArrayList<Deadline> deadlines = getDeadlines( homeWorks);
        calendarVbox.getChildren().clear();
        homeworksVbox.getChildren().clear();
        homeworksTable = new TableView<>();
        calendarTable = new TableView<>();
        TableColumn<Deadline, String> deadLineNameColumn = new TableColumn<>("name");
        deadLineNameColumn.setPrefWidth(100);
        deadLineNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Deadline, String> timeColumn = new TableColumn<>("deadLine");
        timeColumn.setPrefWidth(250);
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));

        calendarTable.setPrefWidth(350);
        calendarTable.setPrefHeight(200);

        calendarTable.getColumns().addAll(deadLineNameColumn, timeColumn);

        for (Deadline deadline : deadlines) {
            calendarTable.getItems().add(deadline);
        }

        calendarVbox.getChildren().add(calendarTable);

        TableColumn<HomeWork, String> nameColumn = new TableColumn<>("homework name");
        nameColumn.setPrefWidth(100);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("homeWorkName"));
        TableColumn<HomeWork, String> startColumn = new TableColumn<>("start time");
        startColumn.setPrefWidth(200);
        startColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));

        TableColumn<HomeWork, String> endColumn = new TableColumn<>("end time");
        endColumn.setPrefWidth(200);
        endColumn.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        homeworksTable.setPrefWidth(500);
        homeworksTable.setPrefHeight(300);
        homeworksTable.getColumns().addAll(nameColumn, startColumn, endColumn);

        for (HomeWork homeWork : homeWorks) {
            homeworksTable.getItems().add(homeWork);
        }
        homeworksVbox.getChildren().add(homeworksTable);
        homeworksTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                homeworksSelected = homeworksTable.getSelectionModel().getSelectedItems();
            }
        });

    }

    public ArrayList<Deadline> getDeadlines(ArrayList<HomeWork> homeWorks){
        ArrayList<Deadline> deadlines = new ArrayList<>();
        for (HomeWork homeWork : homeWorks) {
            Deadline deadline = new Deadline(homeWork.getHomeWorkName(), homeWork.getEndTime());
            deadlines.add(deadline);
        }
        return deadlines;
    }


    public void refresh(ActionEvent actionEvent) throws IOException {
        ServerController.reconnect();
    }

    public void logout(ActionEvent actionEvent) throws IOException {
        String page = config.getProperty(String.class, "loginPage");
        OpenPage.openNewPage(actionEvent, page);
    }

    public void backMainMenu(ActionEvent actionEvent) throws IOException {
        String page = config.getProperty(String.class, "cwMainPage");
        OpenPage.openNewPage(actionEvent, page);
    }
}
