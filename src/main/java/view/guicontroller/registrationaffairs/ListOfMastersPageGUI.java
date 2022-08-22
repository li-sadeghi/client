package view.guicontroller.registrationaffairs;

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
import network.database.MasterData;
import network.database.StudentData;
import response.Response;
import sharedmodels.users.MasterRole;
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
import java.util.List;
import java.util.ResourceBundle;

public class ListOfMastersPageGUI implements Initializable {
    public static Client client;
    public static SharedMaster master;
    public static Config config = Config.getConfig();

    public static TableView<SharedMaster> mastersTable;
    public static List<SharedMaster> mastersSelected;
    private Timeline timeline;

    @FXML
    Label errorLabel;
    @FXML
    Label enterLabel;
    @FXML
    TextField idField;
    @FXML
    Button deleteButton;
    @FXML
    Button addButton;
    @FXML
    Button editButton;
    @FXML
    TextField gradeField;
    @FXML
    TextField roomNumberField;
    @FXML
    TextField nameField;
    @FXML
    AnchorPane background;
    @FXML
    Button refreshButton;
    @FXML
    Label connectionLabel;
    @FXML
    VBox mastersVBox;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        client = ServerController.client;
        timeline = new Timeline(new KeyFrame(Duration.seconds(6), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                CheckConnection.checkConnection(refreshButton, connectionLabel);
                if (Client.clientType.equals(config.getProperty(String.class, "masterType"))) {
                    SharedMaster master = MasterData.master;
                    if (master.getMasterRole() == MasterRole.CHAIRMAN) setPage();
                }
                int counter;
                if (LoginGUI.type.equals(config.getProperty(String.class, "studentType"))) {
                    counter = StudentMainMenuGUI.counter;
                } else {
                    counter = MasterMainMenuGUI.counter;
                }
                Theme.setTheme(counter, background);
                setMastersList();
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.playFromStart();

    }

    public void setPage() {
        enterLabel.setVisible(true);
        idField.setVisible(true);
        deleteButton.setVisible(true);
        addButton.setVisible(true);
        editButton.setVisible(true);
    }

    public void setMastersList()  {
        ArrayList<SharedMaster> masters;
        if (Client.clientType.equals(config.getProperty(String.class, "masterType"))) {
            masters = MasterData.masters;
        }else {
            masters = StudentData.masters;
        }
        mastersVBox.getChildren().clear();
        mastersTable = new TableView<>();

        TableColumn<SharedMaster, String> nameColumn = new TableColumn<>("master name");
        nameColumn.setPrefWidth(200);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        TableColumn<SharedMaster, String> idColumn = new TableColumn<>("master id");
        idColumn.setPrefWidth(100);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        TableColumn<SharedMaster, Enum> gradeCol = new TableColumn<>("Grade");
        gradeCol.setPrefWidth(250);
        gradeCol.setCellValueFactory(new PropertyValueFactory<>("grade"));
        TableColumn<SharedMaster, String> roomNumberCol = new TableColumn<>("room number");
        roomNumberCol.setPrefWidth(250);
        roomNumberCol.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        TableColumn<SharedMaster, Enum> role = new TableColumn<>("role");
        role.setPrefWidth(100);
        role.setCellValueFactory(new PropertyValueFactory<>("masterRole"));
        mastersTable.setPrefWidth(900);
        mastersTable.setPrefHeight(400);
        mastersTable.getColumns().addAll(nameColumn, idColumn, gradeCol, roomNumberCol, role);


        for (SharedMaster sharedMaster : masters) {
            String grade = sharedMaster.getGrade().toString();
            String name = sharedMaster.getFullName();
            String roomNumber = String.valueOf(sharedMaster.getRoomNumber());
            boolean nameOk = StringMatcher.isOk(nameField.getText(), name);
            boolean roomNumberOk = StringMatcher.isOk(roomNumberField.getText(), roomNumber);;
            boolean gradeOk = StringMatcher.isOk(gradeField.getText(), grade);;
            if (nameOk && roomNumberOk && gradeOk) mastersTable.getItems().add(sharedMaster);
        }

        mastersVBox.getChildren().add(mastersTable);
        mastersTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        mastersTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mastersSelected = mastersTable.getSelectionModel().getSelectedItems();
            }
        });
    }


    public void logout(ActionEvent actionEvent) throws IOException {
        timeline.stop();
        String page = config.getProperty(String.class, "loginPage");
        OpenPage.openNewPage(actionEvent, page);
    }

    public void backMainMenu(ActionEvent actionEvent) throws IOException {
        timeline.stop();
        String type = Client.clientType;
        if (type.equals(config.getProperty(String.class, "studentType"))) {
            String page = config.getProperty(String.class, "studentMainMenu");
            OpenPage.openNewPage(actionEvent, page);
        } else {
            String page = config.getProperty(String.class, "masterMainMenu");
            OpenPage.openNewPage(actionEvent, page);
        }

    }

    public void deleteMaster(ActionEvent actionEvent) throws IOException {
        String usernameSelected = mastersSelected.get(0).getUsername();
        Response response = client.getServerController().deleteMaster(usernameSelected, Client.clientUsername);
        String error = response.getErrorMessage();
        errorLabel.setVisible(true);
        errorLabel.setText(error);
    }

    public void addMasterPage(ActionEvent actionEvent) throws IOException {
        timeline.stop();
        String page = config.getProperty(String.class, "addNewUserPage");
        OpenPage.openNewPage(actionEvent, page);
    }

    public void editMasterPage(ActionEvent actionEvent) throws IOException {
        timeline.stop();
        String page = config.getProperty(String.class, "editMasterPage");
        OpenPage.openNewPage(actionEvent, page);
    }

    public void refresh(ActionEvent actionEvent) throws IOException {
        ServerController.reconnect();
    }
}
