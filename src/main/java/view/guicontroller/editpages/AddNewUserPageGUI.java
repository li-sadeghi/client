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
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import network.Client;
import network.ServerController;
import network.database.MasterData;
import response.Response;
import sharedmodels.department.Department;
import sharedmodels.users.*;
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

public class AddNewUserPageGUI implements Initializable {
    public static Client client;
    public static SharedMaster master;
    public static Config config = Config.getConfig();
    public static byte[] byteArrayImage;
    @FXML
    TextField masterName;
    @FXML
    TextField masterUsername;
    @FXML
    TextField masterPassword;
    @FXML
    TextField masterNationalCode;
    @FXML
    TextField masterPhone;
    @FXML
    TextField masterEmail;
    @FXML
    TextField masterGrade;
    @FXML
    TextField maserRoomNumber;
    @FXML
    TextField studentName;
    @FXML
    TextField studentUsername;
    @FXML
    TextField studentPassword;
    @FXML
    TextField studentNationalCode;
    @FXML
    TextField studentPhone;
    @FXML
    TextField studentEmail;
    @FXML
    TextField studentGrade;
    @FXML
    TextField enteringYear;
    @FXML
    Label masterNotice;
    @FXML
    Label studentNotice;
    @FXML
    Button addMasterButton;
    @FXML
    AnchorPane background;
    @FXML
    Button refreshButton;
    @FXML
    Label connectionLabel;
    @FXML
    TextField masterRoleField;
    @FXML
    TextField helperMasterIdField;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        int counter = 0;
        master = MasterData.master;
//        int counter = MasterMainMenuGUI.counter;
        if (master.getMasterRole() == MasterRole.CHAIRMAN) setPageForChairman();
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

    public void setPageForChairman() {
        maserRoomNumber.setDisable(true);
        masterEmail.setDisable(true);
        masterGrade.setDisable(true);
        masterName.setDisable(true);
        masterNationalCode.setDisable(true);
        masterUsername.setDisable(true);
        masterPhone.setDisable(true);
        masterPassword.setDisable(true);
        addMasterButton.setDisable(true);
    }

    public void addMaster(ActionEvent actionEvent) throws IOException {
        String name = masterName.getText();
        String username = masterUsername.getText();
        String password = masterPassword.getText();
        String nationalCode = masterNationalCode.getText();
        String phoneNumber = masterPhone.getText();
        String email = masterEmail.getText();
        String grade = masterGrade.getText();
        String roomNumber = maserRoomNumber.getText();
        String masterRole = masterRoleField.getText();
        if (name == "" ||
                username == "" ||
                password == "" ||
                nationalCode == "" ||
                phoneNumber == "" ||
                email == "" ||
                grade == "" ||
                roomNumber == "") {
            showMasterError("Please fill in all the items");
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
            newMaster.setDepartment(new Department());
            newMaster.setCourses(new ArrayList<>());
            newMaster.setMasterRole(MasterRole.MASTER);
            newMaster.setRoomNumber(roomNumber);
            newMaster.setNationalCode(nationalCode);
            newMaster.setPhoneNumber(phoneNumber);
            newMaster.setEmailAddress(email);
            newMaster.setFullName(name);
            String imageBytes = EncodeDecodeFile.byteArrayToString(byteArrayImage);
            newMaster.setUserImageBytes(imageBytes);
            Response response = client.getServerController().createNewMasterRequest(newMaster, password);
            String error = response.getErrorMessage();
            showMasterError(error);
        }
    }

    public void addStudent(ActionEvent actionEvent) throws IOException {
        String name = studentName.getText();
        String username = studentUsername.getText();
        String password = studentPassword.getText();
        String nationalCode = studentNationalCode.getText();
        String phoneNumber = studentPhone.getText();
        String email = studentEmail.getText();
        String grade = studentGrade.getText();
        String entering = enteringYear.getText();
        String helperMasterId = helperMasterIdField.getText();

        if (name == "" ||
                username == "" ||
                password == "" ||
                nationalCode == "" ||
                phoneNumber == "" ||
                email == "" ||
                grade == "" ||
                entering == "") {
            showStudentError("Please fill in all the items");
        } else {
            SharedStudent newStudent = new SharedStudent();
            newStudent.setFullName(name);
            newStudent.setUsername(username);
            newStudent.setNationalCode(nationalCode);
            newStudent.setPhoneNumber(phoneNumber);
            newStudent.setRole(Role.STUDENT);
            String imageBytes = EncodeDecodeFile.byteArrayToString(byteArrayImage);
            newStudent.setUserImageBytes(imageBytes);
            newStudent.setAverage(0);
            newStudent.setUnits(0);
            newStudent.setDepartment(new Department());
            newStudent.setStatus(EducationalStatus.STUDYING);
            newStudent.setCourses(new ArrayList<>());
            newStudent.setPassedCourses(new ArrayList<>());
            newStudent.setTemporaryCourses(new ArrayList<>());
            newStudent.setEmailAddress(email);
            newStudent.setEnteringYear(entering);
            if (grade.equals("U")) {
                newStudent.setGrade(StudentGrade.UNDERGRADUATE);
            } else if (grade.equals("G")) {
                newStudent.setGrade(StudentGrade.GRADUATED);
            } else {
                newStudent.setGrade(StudentGrade.PHD);
            }
            Response response = client.getServerController().createNewStudentRequest(newStudent, password, helperMasterId);
            String error = response.getErrorMessage();
            showStudentError(error);

        }
    }

    public void showStudentError(String errorName) {
        showError(errorName, studentNotice);
    }

    public void showMasterError(String errorName) {
        showError(errorName, masterNotice);
    }

    public void showError(String errorName, Label label) {
        label.setVisible(true);
        label.setText(errorName);
    }

    public void logout(ActionEvent actionEvent) throws IOException {
        String page = config.getProperty(String.class, "loginPage");
        OpenPage.openNewPage(actionEvent, page);
    }

    public void backMainMenu(ActionEvent actionEvent) throws IOException {
        String page = config.getProperty(String.class, "masterMainMenu");
        OpenPage.openNewPage(actionEvent, page);
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

    public void refresh(ActionEvent actionEvent) throws IOException {
        ServerController.reconnect();
    }
}
