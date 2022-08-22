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
import sharedmodels.cw.HomeWork;
import sharedmodels.cw.Solution;
import sharedmodels.department.Course;
import time.DateAndTime;
import util.extra.EncodeDecodeFile;
import view.OpenPage;
import view.guicontroller.CheckConnection;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

public class HomeWorkPageGUI implements Initializable {
    public static Client client = ServerController.client;
    public static Config config = Config.getConfig();
    public static boolean isMaster;
    public static HomeWork homeWork;

    public static TableView<Solution> solutionsTable;

    public static List<Solution> solutionsSelected = new ArrayList<>();
    private Timeline timeline;


    @FXML
    AnchorPane background;
    @FXML
    Button refreshButton;
    @FXML
    Label connectionLabel;
    @FXML
    Label downloadNotice;
    @FXML
    Label uploadNotice;
    @FXML
    VBox solutionsVbox;
    @FXML
    Label solutionsLabel;
    @FXML
    TextField markField;
    @FXML
    Button downloadButton;
    @FXML
    Button registerButton;
    @FXML
    Label registerNotice;
    @FXML
    Label markLabel;
    @FXML
    Label nameLabel;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        timeline = new Timeline(new KeyFrame(Duration.seconds(6), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                CheckConnection.checkConnection(refreshButton, connectionLabel);
                if (isMaster){
                    markField.setVisible(true);
                    solutionsLabel.setVisible(true);
                    registerButton.setVisible(true);
                    downloadButton.setVisible(true);
                    try {
                        setPageVip();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                try {
                    setPage();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.playFromStart();



    }

    public void setPage() throws IOException {
        nameLabel.setText(homeWork.getHomeWorkName());
        if (isMaster){
            markLabel.setText("none");
        }else {
            if (studentHaveSolution(homeWork)){
                Solution solution = getSolution(homeWork);
                markLabel.setText(String.valueOf(solution.getMark()));
            }else {
                markLabel.setText("none");
            }
        }
    }

    private Solution getSolution(HomeWork homeWork) throws IOException {
        Response response = client.getServerController().getSolutionForHomeWork(homeWork.getId(), Client.clientUsername);
        Solution solution = (Solution) response.getData("solution");
        return solution;
    }

    private boolean studentHaveSolution(HomeWork homeWork) throws IOException {
        Response response = client.getServerController().checkHaveSolutionToHomeWork(homeWork.getId(), Client.clientUsername);
        return (boolean) response.getData("result");
    }

    private void setPageVip() throws IOException {
        Response response = client.getServerController().getAllSolutionsToHomework(homeWork.getId());
        ArrayList<Solution> solutions = (ArrayList<Solution>) response.getData("solutions");
        solutionsVbox.getChildren().clear();
        solutionsTable = new TableView<>();

        TableColumn<Solution, String> nameColumn = new TableColumn<>("name");
        nameColumn.setPrefWidth(130);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("responsiveName"));

        TableColumn<Solution, String> idColumn = new TableColumn<>("student id");
        idColumn.setPrefWidth(130);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("responsiveId"));

        TableColumn<Solution, String> timeColumn = new TableColumn<>("send time");
        timeColumn.setPrefWidth(170);
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));

        TableColumn<Solution, Double> markColumn = new TableColumn<>("mark");
        timeColumn.setPrefWidth(70);
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("mark"));
        solutionsTable.setPrefWidth(500);
        solutionsTable.setPrefHeight(300);

        solutionsTable.getColumns().addAll(nameColumn, idColumn, timeColumn, markColumn);

        for (Solution solution : solutions) {
            solutionsTable.getItems().add(solution);
        }


        solutionsVbox.getChildren().add(solutionsTable);

        solutionsTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                solutionsSelected = solutionsTable.getSelectionModel().getSelectedItems();
            }
        });

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

    public void downloadMedia(ActionEvent actionEvent) throws IOException {
        Solution solution = solutionsSelected.get(0);
        downloadFileAndSave(solution.getAnswerFileString(), solution.getAnswerFileType());
        downloadNotice.setVisible(true);
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

    public void registerMark(ActionEvent actionEvent) {
        Solution solution = solutionsSelected.get(0);
        double mark = Double.parseDouble(markField.getText());
        client.getServerController().registerMarkForSolution(solution.getId(), mark);
        registerNotice.setVisible(true);
    }

    public void uploadSolution(ActionEvent actionEvent) throws IOException {
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

        Solution solution = new Solution();
        solution.setTime(DateAndTime.getDateAndTime());
        solution.setAnswerFileType(fileType);
        solution.setResponsiveId(Client.clientUsername);
        solution.setAnswerFileString(text);
        client.getServerController().sendNewSolution(homeWork, solution);
        uploadNotice.setVisible(true);
    }
}
