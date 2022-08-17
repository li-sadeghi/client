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
import java.util.ResourceBundle;

public class EditMasterPageGUI implements Initializable {
    public static Client client;
    public static SharedMaster master;
    public static Config config = Config.getConfig();
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
        SharedMaster newMaster = new SharedMaster();
        String id = idField.getText();
        String password = passwordField.getText();
        String fullName = fullNameField.getText();
        String nationalCode = nationalCodeField.getText();
        String phoneNumber = phoneNumberField.getText();
        String email = emailField.getText();
        String roomNumber = roomNumberField.getText();
        // department = editor's department
        String grade = gradeField.getText();
        if (grade.equals("S")) newMaster.setGrade(MasterGrade.ASSISTANT_PROFESSOR);
        else if (grade.equals("C")) newMaster.setGrade(MasterGrade.ASSOCIATE_PROFESSOR);
        else newMaster.setGrade(MasterGrade.FULL_PROFESSOR);
        String newCourseId = courseIdField.getText();
        String role = roleField.getText();
        if (role.equals("E")) newMaster.setMasterRole(MasterRole.EDUCATIONAL_ASSISTANT);
        else newMaster.setMasterRole(MasterRole.MASTER);
        String imageBytes = EncodeDecodeFile.byteArrayToString(byteArrayImage);
        newMaster.setUserImageBytes(imageBytes);

        //TODO
//        Response response = client.getServerController().editMasterRequest(editor, newMaster, password, nationalCode, phoneNumber, newCourseId);
//        String error = response.getErrorMessage();
//        showNotice(error);

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
}
