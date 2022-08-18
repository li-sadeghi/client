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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import network.Client;
import network.ServerController;
import network.database.MasterData;
import response.Response;
import sharedmodels.users.SharedMaster;
import sharedmodels.users.SharedStudent;
import util.extra.EncodeDecodeFile;
import view.OpenPage;
import view.guicontroller.CheckConnection;
import view.guicontroller.Theme;
import view.guicontroller.mainmenu.MasterMainMenuGUI;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MasterProfileGUI implements Initializable {
    public static Client client;
    public static SharedMaster master = MasterData.master;
    public static Config config = Config.getConfig();
    @FXML
    Label fullNameLabel;
    @FXML
    Label nationalCodeLabel;
    @FXML
    Label masterIdLabel;
    @FXML
    Label phoneNumberLabel;
    @FXML
    Label emailAddressLabel;
    @FXML
    Label departmentLabel;
    @FXML
    Label gradeLabel;
    @FXML
    Label roomNumberLabel;
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
    Button refreshButton;
    @FXML
    Label connectionLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        int counter = MasterMainMenuGUI.counter;
        Theme.setTheme(counter, background);
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                CheckConnection.checkConnection(refreshButton, connectionLabel);
                master = MasterData.master;
                setProfile(master);
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.playFromStart();
    }

    public void setProfile(SharedMaster master)  {
        fullNameLabel.setText(master.getFullName());
        String nationalCode = master.getNationalCode();
        nationalCodeLabel.setText(nationalCode);
        masterIdLabel.setText(master.getUsername());
        String phoneNumber = master.getPhoneNumber();
        phoneNumberLabel.setText(phoneNumber);
        emailAddressLabel.setText(master.getEmailAddress());
        departmentLabel.setText(master.getDepartment().getName());
        gradeLabel.setText(master.getGrade().toString());
        roomNumberLabel.setText(master.getRoomNumber());
        byte[] decodeImage = EncodeDecodeFile.decode(master.getUserImageBytes());
        Image image = new Image(new ByteArrayInputStream(decodeImage));
        ImageView pic = new ImageView();
        pic.setFitWidth(config.getProperty(Integer.class, "userImageWidthSize"));
        pic.setFitHeight(config.getProperty(Integer.class, "userImageHeightSize"));
        pic.setImage(image);
        userImageVBox.getChildren().add(pic);
    }

    public void logout(ActionEvent actionEvent) throws IOException {
        String page = config.getProperty(String.class, "loginPage");
        OpenPage.openNewPage(actionEvent, page);
    }

    public void backMainMenu(ActionEvent actionEvent) throws IOException {
        String page = config.getProperty(String.class, "masterMainMenu");
        OpenPage.openNewPage(actionEvent, page);
    }

    public void changeEmailAddress(ActionEvent actionEvent) throws IOException {
        String enteredEmail = emailTextField.getText();
        Response response = client.getServerController().sendChangeEmailRequest(master.getUsername(), enteredEmail);
        String error = response.getErrorMessage();
        showError(error);
    }

    public void changePhoneNumber(ActionEvent actionEvent) throws IOException {
        String enteredPhoneNumber = phoneNumberTextField.getText();
        Response response = client.getServerController().sendChangePhoneNumberRequest(master.getUsername(), enteredPhoneNumber);
        String error = response.getErrorMessage();
        showError(error);
    }

    public void showError(String errorName) {
        noticeLabel.setText(errorName);
        noticeLabel.setVisible(true);
        emailTextField.clear();
        phoneNumberTextField.clear();
    }

    public void refresh(ActionEvent actionEvent) throws IOException {
        ServerController.reconnect();
    }
}
