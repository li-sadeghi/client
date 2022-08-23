package view.guicontroller.cw;

import config.Config;
import extra.Deadline;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import network.Client;
import network.ServerController;
import response.Response;
import sharedmodels.cw.EducationalThing;
import sharedmodels.cw.HomeWork;
import sharedmodels.department.Course;
import time.DateAndTime;
import util.extra.EncodeDecodeFile;
import view.OpenPage;
import view.guicontroller.CheckConnection;
import view.guicontroller.Theme;
import view.guicontroller.mainmenu.MasterMainMenuGUI;
import view.guicontroller.mainmenu.StudentMainMenuGUI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

public class CwCoursePageGUI implements Initializable {
    public static Client client = ServerController.client;
    public static Config config = Config.getConfig();
    public static boolean isMaster;
    public static Course course;

    public static TableView<EducationalThing> educationalTable;
    public static List<EducationalThing> educationalSelected = new ArrayList<>();

    public static TableView<HomeWork> homeworksTable;
    public static List<HomeWork> homeworksSelected = new ArrayList<>();

    public static TableView<Deadline> calendarTable;
    private Timeline timeline;
    private Timeline newTimeline;
    private Thread thread;
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
    @FXML
    Label noticeLabel;
    @FXML
    VBox educationalVbox;
    @FXML
    TextField eduNameField;
    @FXML
    TextField hwNameField;
    @FXML
    TextField endTimeField;
    @FXML
    Button educationalAddButton;
    @FXML
    Button homeworkAddButton;
    @FXML
    Label label1;
    @FXML
    Label label2;
    @FXML
    Button deleteButton;
    @FXML
    Label deleteLabel;
    @FXML
    Label hwAddNotice;
    @FXML
    Label eduAddNotice;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        courseNameLabel.setText(course.getName());
        int counter ;
        if (Client.clientType.equals(config.getProperty(String.class, "studentType"))) {
            counter = StudentMainMenuGUI.counter;
            isMaster = false;
        } else {
            deleteButton.setVisible(true);
            label1.setVisible(true);
            eduNameField.setVisible(true);
            hwNameField.setVisible(true);
            endTimeField.setVisible(true);
            educationalAddButton.setVisible(true);
            homeworkAddButton.setVisible(true);
            label2.setVisible(true);
            isMaster = true;
            counter = MasterMainMenuGUI.counter;
        }
        Theme.setTheme(counter, background);
        timeline = new Timeline(new KeyFrame(Duration.seconds(6), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                CheckConnection.checkConnection(refreshButton, connectionLabel);
                ArrayList<HomeWork> homeWorks = null;
                ArrayList<EducationalThing> educationalThings = null;
                try {
                    homeWorks = getAllHomeworks(course);
//                    Thread.sleep(2000);
                    PauseTransition pause = new PauseTransition(Duration.millis(2000));
                    educationalThings = getAllEducations(course);
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
                setPage( homeWorks, educationalThings);
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.playFromStart();

    }

    private ArrayList<EducationalThing> getAllEducations(Course course) throws InterruptedException, IOException {
        ArrayList<EducationalThing> educationalThings = new ArrayList<>();
        for (Integer id : course.getEducationalThingsId()) {
            Response response = client.getServerController().getEducational(id);
            PauseTransition pause = new PauseTransition(Duration.millis(5));
            EducationalThing educationalThing= (EducationalThing) response.getData("educational");
            educationalThings.add(educationalThing);
//            Thread.sleep(50);

        }
        return educationalThings;
    }

    private ArrayList<HomeWork> getAllHomeworks(Course course) throws IOException, InterruptedException {
        ArrayList<HomeWork> homeWorks = new ArrayList<>();
        for (Integer id : course.getHomeWorksId()) {
            Response response = client.getServerController().getHomework(id);
            HomeWork homeWork= (HomeWork) response.getData("homework");
            homeWorks.add(homeWork);
//            Thread.sleep(30);
            PauseTransition pause = new PauseTransition(Duration.millis(20));
        }
        return homeWorks;
    }


    public void setPage(ArrayList<HomeWork> homeWorks, ArrayList<EducationalThing> educationalThings){
        ArrayList<Deadline> deadlines = getDeadlines( homeWorks);
        calendarVbox.getChildren().clear();
        homeworksVbox.getChildren().clear();
        homeworksTable = new TableView<>();
        calendarTable = new TableView<>();
        TableColumn<Deadline, String> deadLineNameColumn = new TableColumn<>("name");
        deadLineNameColumn.setPrefWidth(150);
        deadLineNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Deadline, String> timeColumn = new TableColumn<>("deadLine");
        timeColumn.setPrefWidth(200);
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
                timeline.stop();
                homeworksSelected = homeworksTable.getSelectionModel().getSelectedItems();
                HomeWork homeWork = homeworksSelected.get(0);
                String startTime = homeWork.getStartTime();
                String endTime = homeWork.getEndTime();
                if (DateAndTime.isSelectionUnitTime(startTime, endTime)){
                    HomeWorkPageGUI.homeWork = homeWork;
                    HomeWorkPageGUI.isMaster = isMaster;
                    String page = config.getProperty(String.class, "homeWorkPage");
                    try {
                        OpenPage.openNewPage(event, page);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }else {
                    noticeLabel.setVisible(true);
                }
            }
        });



        educationalVbox.getChildren().clear();
        educationalTable = new TableView<>();


        TableColumn<EducationalThing, String> eduNameColumn = new TableColumn<>("name");
        eduNameColumn.setPrefWidth(300);
        eduNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        educationalTable.setPrefWidth(300);
        educationalTable.setPrefHeight(100);

        educationalTable.getColumns().addAll(eduNameColumn);

        for (EducationalThing educationalThing : educationalThings) {
            educationalTable.getItems().add(educationalThing);
        }
        educationalVbox.getChildren().add(educationalTable);

        educationalTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                educationalSelected = educationalTable.getSelectionModel().getSelectedItems();

            }
        });

    }

    public ArrayList<Deadline> getDeadlines(ArrayList<HomeWork> homeWorks){
        ArrayList<Deadline> deadlines = new ArrayList<>();
        if (homeWorks == null) return deadlines;
        for (HomeWork homeWork : homeWorks) {
            if (homeWork == null) continue;
            Deadline deadline = new Deadline(homeWork.getHomeWorkName(), homeWork.getEndTime());
            deadlines.add(deadline);
        }
        return deadlines;
    }


    public void refresh(ActionEvent actionEvent) throws IOException {
        ServerController.reconnect();
    }

    public void logout(ActionEvent actionEvent) throws IOException {
        timeline.stop();
        String page = config.getProperty(String.class, "loginPage");
        OpenPage.openNewPage(actionEvent, page);
    }

    public void backMainMenu(ActionEvent actionEvent) throws IOException {
        timeline.stop();
        String page = config.getProperty(String.class, "cwMainPage");
        OpenPage.openNewPage(actionEvent, page);
    }

    private void downloadFileAndSave(String encoded, String fileType) throws IOException {
        byte[] decoded = EncodeDecodeFile.decode(encoded);
        String path = "./src/main/resources/downloadedfiles/" + new Random().nextLong() + "." + fileType ;
        File file = new File(path);
        file.createNewFile();
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(decoded);
        fileOutputStream.close();
        fileOutputStream.flush();
    }

    public void addEducational(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf"),
                new FileChooser.ExtensionFilter("AUDIO FILES", "*.mp3"),
                new FileChooser.ExtensionFilter("IMAGES", "*.jpg"),
                new FileChooser.ExtensionFilter("VIDEOS", "*.mp4"));


        File selectedFile = fileChooser.showOpenDialog(new Stage());
        byte[] byteArray = null;
        if (selectedFile != null) {
//            if(selectedFile.length()>2000000){ //2 MB
//                throw new SizeLimitExceededException();
//            }
            try {
                byteArray = Files.readAllBytes(selectedFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String text = EncodeDecodeFile.byteArrayToString(byteArray);
        String fileType = EncodeDecodeFile.getFormat(String.valueOf(selectedFile.toPath()));
        EducationalThing educationalThing = new EducationalThing();
        educationalThing.setName(eduNameField.getText());
        educationalThing.setFileType(fileType);
        educationalThing.setFileString(text);
        client.getServerController().addNewEducational(educationalThing, course);
        eduAddNotice.setVisible(true);
    }

    public void addHomeWork(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf"),
                new FileChooser.ExtensionFilter("AUDIO FILES", "*.mp3"),
                new FileChooser.ExtensionFilter("IMAGES", "*.jpg"),
                new FileChooser.ExtensionFilter("VIDEOS", "*.mp4"));


        File selectedFile = fileChooser.showOpenDialog(new Stage());
        byte[] byteArray = null;
        if (selectedFile != null) {
//            if(selectedFile.length()>2000000){ //2 MB
//                throw new SizeLimitExceededException();
//            }
            try {
                byteArray = Files.readAllBytes(selectedFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String text = EncodeDecodeFile.byteArrayToString(byteArray);
        String fileType = EncodeDecodeFile.getFormat(String.valueOf(selectedFile.toPath()));
        HomeWork homeWork = new HomeWork();
        homeWork.setHomeWorkFileType(fileType);
        homeWork.setHomeworkFileString(text);
        homeWork.setCourseId(course.getId());
        homeWork.setStartTime(DateAndTime.getDateAndTime());
        homeWork.setHomeWorkName(hwNameField.getText());
        homeWork.setEndTime(endTimeField.getText());
        client.getServerController().addNewHomeworkToCourse(homeWork);
        hwAddNotice.setVisible(true);
    }

    public void downloadEducational(ActionEvent actionEvent) {
        EducationalThing educationalThing = educationalSelected.get(0);
        try {
            downloadFileAndSave(educationalThing.getFileString(), educationalThing.getFileType());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteEducational(ActionEvent actionEvent) {
        EducationalThing educationalThing = educationalSelected.get(0);
        client.getServerController().deleteEducational(educationalThing.getId());
        deleteLabel.setVisible(true);
    }
}
