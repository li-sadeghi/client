package view.guicontroller.cw;

import config.Config;
import extra.Deadline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import network.Client;
import network.ServerController;
import response.Response;
import sharedmodels.cw.HomeWork;
import sharedmodels.cw.Solution;
import sharedmodels.department.Course;
import time.DateAndTime;
import util.extra.EncodeDecodeFile;
import view.OpenPage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
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



    @Override
    public void initialize(URL location, ResourceBundle resources) {
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
        //TODO



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
        String page = config.getProperty(String.class, "loginPage");
        OpenPage.openNewPage(actionEvent, page);
    }

    public void backMainMenu(ActionEvent actionEvent) throws IOException {
        String page = config.getProperty(String.class, "cwMainPage");
        OpenPage.openNewPage(actionEvent, page);
    }

    public void downloadMedia(ActionEvent actionEvent) throws IOException {
        Solution solution = solutionsSelected.get(0);
        downloadFileAndSave(solution.getAnswerFileString(), solution.getAnswerFileType());
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
}
