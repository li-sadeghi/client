package view.guicontroller.profile;

import config.Config;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import network.Client;
import network.ServerController;
import response.Response;
import response.ResponseStatus;
import sharedmodels.users.SharedStudent;
import util.extra.EncodeDecodeFile;
import view.OpenPage;
import view.guicontroller.Theme;
import view.guicontroller.mainmenu.MohseniPageController;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class StudentProfileGUI implements Initializable {
    public static Client client;
    public static boolean isMohseni = false;
    public static String studentUsername;
    public static Config config = Config.getConfig();
    public static int counter = 0;

    @FXML
    Label fullNameLabel;
    @FXML
    Label nationalCodeLabel;
    @FXML
    Label studentIdLabel;
    @FXML
    Label phoneNumberLabel;
    @FXML
    Label emailAddressLabel;
    @FXML
    Label averageLabel;
    @FXML
    Label departmentLabel;
    @FXML
    Label helperMasterLabel;
    @FXML
    Label enteringYearLabel;
    @FXML
    Label gradeLabel;
    @FXML
    Label educationalStatusLabel;
    @FXML
    TextField emailTextField;
    @FXML
    TextField phoneNumberTextField;
    @FXML
    Label noticeLabel;
    @FXML
    AnchorPane background;
    @FXML
    VBox userImageVBox;
    @FXML
    Label changeEmailLabel;
    @FXML
    Label changePhoneLabel;
    @FXML
    Button emailButton;
    @FXML
    Button phoneButton;
    @FXML
    Hyperlink exitHyperLink;
    @FXML
    Hyperlink backMainMenuHyper;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        client = ServerController.client;
        if (isMohseni) {
            counter = MohseniPageController.counter;
            exitHyperLink.setVisible(false);
            backMainMenuHyper.setVisible(false);
            changeEmailLabel.setVisible(false);
            changePhoneLabel.setVisible(false);
            emailTextField.setVisible(false);
            phoneNumberTextField.setVisible(false);
            emailButton.setVisible(false);
            phoneButton.setVisible(false);
        }
        Theme.setTheme(counter, background);
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Response response = null;
                try {
                    response = client.getServerController().getStudentRequest(studentUsername);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                SharedStudent student = (SharedStudent) response.getData("user");
                try {
                    setProfile(student);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.playFromStart();
    }


    public void setProfile(SharedStudent student) throws IOException {
        fullNameLabel.setText(student.getFullName());
        String nationalCode = student.getNationalCode();
        nationalCodeLabel.setText(nationalCode);
        studentIdLabel.setText(student.getUsername());
        String phoneNumber = student.getPhoneNumber();
        phoneNumberLabel.setText(phoneNumber);
        emailAddressLabel.setText(student.getEmailAddress());
        String average = Double.toString(student.getAverage());
        averageLabel.setText(average);
        departmentLabel.setText(student.getDepartment().getName());
        helperMasterLabel.setText(student.getHelperMaster().getFullName());
        String enteringYear = student.getEnteringYear();
        enteringYearLabel.setText(enteringYear);
        gradeLabel.setText(student.getGrade().toString());
        educationalStatusLabel.setText(student.getStatus().toString());
        byte[] decodeImage = EncodeDecodeFile.decode(student.getUserImageBytes());
        Image image = new Image(new ByteArrayInputStream(decodeImage));
        ImageView pic = new ImageView();
        pic.setFitWidth(config.getProperty(Integer.class, "userImageWidthSize"));
        pic.setFitHeight(config.getProperty(Integer.class, "userImageHeightSize"));
        pic.setImage(image);
        userImageVBox.getChildren().add(pic);
    }

    public void logout(ActionEvent actionEvent) throws IOException {
        String page = "Login.fxml";
        OpenPage.openNewPage(actionEvent, page);
    }

    public void backMainMenu(ActionEvent actionEvent) throws IOException {
        String page = config.getProperty(String.class, "studentMainMenu");
        OpenPage.openNewPage(actionEvent, page);
    }

    public void changeEmailAddress(ActionEvent actionEvent) throws IOException {
//        String enteredEmail = emailTextField.getText();
//        Response response = client.getServerController().sendChangeEmailRequest(student.getUsername(), enteredEmail);
//        String error = response.getErrorMessage();
//        showError(error);
//        if (response.getStatus() == ResponseStatus.OK) {
//            emailAddressLabel.setText(enteredEmail);
//        }
    }

    public void changePhoneNumber(ActionEvent actionEvent) throws IOException {
//        String enteredPhoneNumber = phoneNumberTextField.getText();
//        Response response = client.getServerController().sendChangePhoneNumberRequest(student.getUsername(), enteredPhoneNumber);
//        String error = response.getErrorMessage();
//        showError(error);
//        if (response.getStatus() == ResponseStatus.OK) {
//            phoneNumberLabel.setText(enteredPhoneNumber);
//        }
    }

    public void showError(String errorName) {
        noticeLabel.setText(errorName);
        noticeLabel.setVisible(true);
        emailTextField.clear();
        phoneNumberTextField.clear();
    }
}