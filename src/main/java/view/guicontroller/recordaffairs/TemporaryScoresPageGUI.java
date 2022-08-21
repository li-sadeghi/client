package view.guicontroller.recordaffairs;


import config.Config;
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
import sharedmodels.department.TemporaryCourse;
import sharedmodels.users.SharedStudent;
import view.OpenPage;
import view.guicontroller.CheckConnection;
import view.guicontroller.Theme;
import view.guicontroller.mainmenu.StudentMainMenuGUI;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class TemporaryScoresPageGUI implements Initializable {
    public static Client client = ServerController.client;
    public static Config config = Config.getConfig();
    public static SharedStudent student = StudentData.student;
    public static List<TemporaryCourse> temporaryCoursesSelected = new ArrayList<>();
    public static TableView<TemporaryCourse> coursesTable;
    @FXML
    Label noticeLabel;
    @FXML
    AnchorPane background;
    @FXML
    VBox coursesListVbox;
    @FXML
    Button refreshButton;
    @FXML
    Label connectionLabel;
    @FXML
    TextArea protestBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        client = ServerController.client;
        int counter = StudentMainMenuGUI.counter;
        Theme.setTheme(counter, background);
        setPage();
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(6), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                CheckConnection.checkConnection(refreshButton, connectionLabel);
                student = StudentData.student;
                setPage();
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.playFromStart();
    }

    private void setPage() {
        //TODO
//        ArrayList<TemporaryCourse> temporaryCourses = student.getTemporaryCourses();


        coursesListVbox.getChildren().clear();
        coursesTable = new TableView<>();

        TableColumn<TemporaryCourse, String> nameColumn = new TableColumn<>("course name");
        nameColumn.setPrefWidth(150);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<TemporaryCourse, String> idColumn = new TableColumn<>("course id");
        idColumn.setPrefWidth(135);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("courseId"));
        TableColumn<TemporaryCourse, Integer> unit = new TableColumn<>("units");
        unit.setPrefWidth(35);
        unit.setCellValueFactory(new PropertyValueFactory<>("unit"));
        TableColumn<TemporaryCourse, Double> markColumn = new TableColumn<>("your mark");
        markColumn.setPrefWidth(80);
        markColumn.setCellValueFactory(new PropertyValueFactory<>("mark"));
        TableColumn<TemporaryCourse, String> protest = new TableColumn<>("your protest");
        protest.setPrefWidth(250);
        protest.setCellValueFactory(new PropertyValueFactory<>("protestText"));
        TableColumn<TemporaryCourse, String> protestAnswer = new TableColumn<>("answer of protest");
        protestAnswer.setPrefWidth(250);
        protestAnswer.setCellValueFactory(new PropertyValueFactory<>("protestAnswer"));

        coursesTable.setPrefWidth(900);
        coursesTable.setPrefHeight(400);
        coursesTable.getColumns().addAll(nameColumn, idColumn, unit, markColumn, protest, protestAnswer);


        //TODO

//        for (TemporaryCourse temporaryCourse : temporaryCourses) {
//            coursesTable.getItems().add(temporaryCourse);
//        }

        coursesListVbox.getChildren().add(coursesTable);
        coursesTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        coursesTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                temporaryCoursesSelected = coursesTable.getSelectionModel().getSelectedItems();
            }
        });
    }

    public void logout(ActionEvent actionEvent) throws IOException {
        String page = config.getProperty(String.class, "loginPage");

        OpenPage.openNewPage(actionEvent, page);
    }

    public void backMainMenu(ActionEvent actionEvent) throws IOException {
        String page =config.getProperty(String.class, "studentMainMenu");
        OpenPage.openNewPage(actionEvent, page);
    }

    public void sendProtest(ActionEvent actionEvent) throws IOException {
        TemporaryCourse temporaryCourse = temporaryCoursesSelected.get(0);
        String protestTex = protestBox.getText();
        String temporaryId = temporaryCourse.getId();
        Response response = client.getServerController().sendNewProtest(temporaryId, protestTex);
        String error = response.getErrorMessage();
        noticeLabel.setText(error);
        noticeLabel.setVisible(true);
        protestBox.clear();
    }

    public void refresh(ActionEvent actionEvent) throws IOException {
        ServerController.reconnect();
    }
}
