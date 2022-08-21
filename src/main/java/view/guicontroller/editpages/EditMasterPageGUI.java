package view.guicontroller.editpages;

import config.Config;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import network.Client;
import network.ServerController;
import network.database.MasterData;
import response.Response;
import sharedmodels.department.Department;
import sharedmodels.users.MasterGrade;
import sharedmodels.users.MasterRole;
import sharedmodels.users.SharedMaster;
import util.extra.EncodeDecodeFile;
import view.OpenPage;
import view.guicontroller.CheckConnection;
import view.guicontroller.Theme;

import javax.naming.SizeLimitExceededException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class EditMasterPageGUI implements Initializable {
    public static Client client;
    public static SharedMaster master;
    public static Config config = Config.getConfig();
    public static ArrayList<String> coursesIDs = new ArrayList<>();
    public static byte[] byteArrayImage;
    @FXML
    ImageView myImageBackground;
    @FXML
    Label editMasterLabel;
    @FXML
    TextField idField;
    @FXML
    TextField passwordField;
    @FXML
    TextField fullNameField;
    @FXML
    TextField nationalCodeField;
    @FXML
    TextField phoneNumberField;
    @FXML
    TextField emailField;
    @FXML
    TextField roomNumberField;
    @FXML
    TextField departmentIdField;
    @FXML
    TextField gradeField;
    @FXML
    TextField courseIdField;
    @FXML
    TextField roleField;
    @FXML
    AnchorPane background;
    @FXML
    Button refreshButton;
    @FXML
    Label connectionLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        client = ServerController.client;
        master = MasterData.master;
        int counter = 0;
//        counter = MasterMainMenuGUI.counter;
        Theme.setTheme(counter, background);
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(6), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                CheckConnection.checkConnection(refreshButton, connectionLabel);
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.playFromStart();
    }

    public void editMaster(ActionEvent actionEvent) throws IOException {
        String name = fullNameField.getText();
        String username = idField.getText();
        String password = passwordField.getText();
        String nationalCode = nationalCodeField.getText();
        String phoneNumber = phoneNumberField.getText();
        String email = emailField.getText();
        String grade = gradeField.getText();
        String roomNumber = roomNumberField.getText();
        String masterRole = roleField.getText();
        if (name == "" ||
                username == "" ||
                password == "" ||
                nationalCode == "" ||
                phoneNumber == "" ||
                email == "" ||
                grade == "" ||
                roomNumber == "") {
            showNotice("Please fill in all the items");
        } else {
            SharedMaster newMaster = new SharedMaster();
            newMaster.setUsername(username);
            if (grade.equals("S")) {
                newMaster.setGrade(MasterGrade.ASSISTANT_PROFESSOR);
            } else if (grade.equals("C")) {
                newMaster.setGrade(MasterGrade.ASSOCIATE_PROFESSOR);
            } else {
                newMaster.setGrade(MasterGrade.FULL_PROFESSOR);
            }
            if (masterRole.equals("M")) newMaster.setMasterRole(MasterRole.MASTER);
            else if (masterRole.equals("E")) newMaster.setMasterRole(MasterRole.EDUCATIONAL_ASSISTANT);
            else newMaster.setMasterRole(MasterRole.CHAIRMAN);
            newMaster.setDepartmentId(departmentIdField.getText());
            newMaster.setMasterRole(MasterRole.MASTER);
            newMaster.setRoomNumber(roomNumber);
            newMaster.setNationalCode(nationalCode);
            newMaster.setPhoneNumber(phoneNumber);
            newMaster.setEmailAddress(email);
            newMaster.setFullName(name);
            String imageBytes = EncodeDecodeFile.byteArrayToString(byteArrayImage);
            newMaster.setUserImageBytes(imageBytes);
            Response response = client.getServerController().editMasterRequest(newMaster, password, coursesIDs);
            String error = response.getErrorMessage();
            showNotice(error);
        }

    }

    private void showNotice(String notice) {
        editMasterLabel.setText(notice);
        editMasterLabel.setVisible(true);
    }

    public void choosePicture(ActionEvent actionEvent) throws SizeLimitExceededException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));

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
        byteArrayImage = byteArray;
    }

    public void logout(ActionEvent actionEvent) throws IOException {
        String page = config.getProperty(String.class, "loginPage");
        OpenPage.openNewPage(actionEvent, page);
    }

    public void backMainMenu(ActionEvent actionEvent) throws IOException {
        String page = config.getProperty(String.class, "masterMainMenu");
        OpenPage.openNewPage(actionEvent, page);
    }

    public void refresh(ActionEvent actionEvent) throws IOException {
        ServerController.reconnect();
    }

    public void addCourse(ActionEvent actionEvent) {
        String newId = courseIdField.getText();
        coursesIDs.add(newId);
        courseIdField.clear();
    }
}
