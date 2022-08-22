package view.guicontroller;

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
import response.Response;
import response.ResponseStatus;
import sharedmodels.users.Role;
import sharedmodels.users.SharedUser;
import util.extra.EncodeDecodeFile;
import view.OpenPage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginGUI implements Initializable {
    public static Client client = ServerController.client;
    public static String type = "nothing";
    public static Timeline timeline;
    public static Config config = Config.getConfig();
    private static String captchaCode;
    private static String page;
    @FXML
    VBox captchaVBOX;
    @FXML
    Label loginNotice;
    @FXML
    TextField usernameField;
    @FXML
    TextField passwordField;
    @FXML
    TextField captchaNumberField;
    @FXML
    AnchorPane background;
    @FXML
    Button refreshButton;
    @FXML
    Label connectionLabel;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Theme.setTheme(2, background);
        try {
                setCaptchaImage();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                CheckConnection.checkConnection(refreshButton, connectionLabel);
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.playFromStart();
    }

    public void setCaptchaImage() throws IOException {
        Response response = client.getServerController().sendCaptchaRequest();
        byte[] imageBytes = EncodeDecodeFile.decode((String) response.getData("captchaPic"));
        String captchaNumber = (String) response.getData("captchaNumber");
        captchaVBOX.getChildren().clear();
        Image image = new Image(new ByteArrayInputStream(imageBytes));
        ImageView pic = new ImageView();
        pic.setFitWidth(config.getProperty(Integer.class, "captchaPicWidthSize"));
        pic.setFitHeight(config.getProperty(Integer.class, "captchaPicHeightSize"));
        pic.setImage(image);
        captchaVBOX.getChildren().add(pic);
        setCaptchaCode(captchaNumber);
    }

    private void setCaptchaCode(String captchaCode) {
        this.captchaCode = captchaCode;
    }

    public void ChangeCaptchaImage(ActionEvent actionEvent) throws IOException {
        setCaptchaImage();
    }

    public void loginByClick(ActionEvent actionEvent) throws IOException {
        loginUser(actionEvent);
    }

    private void loginUser(ActionEvent actionEvent) throws IOException {
        String enteredCaptcha = captchaNumberField.getText();
        String enteredUsername = usernameField.getText();
        String enteredPassword = passwordField.getText();
        Response response = client.getServerController().sendLoginRequest(enteredUsername, enteredPassword);
        Client.clientUsername = enteredUsername;
        if (!enteredCaptcha.equals(captchaCode)) {
            showError(config.getProperty(String.class, "captchaWrongError"));
        } else {
            if (response.getStatus() == ResponseStatus.OK) {
                SharedUser user = (SharedUser) response.getData("user");
                login(user, actionEvent);
            } else if (response.getStatus() == ResponseStatus.TIME_LIMIT) {
                SharedUser user = (SharedUser) response.getData("user");
                setRole(user);
                timeline.stop();
                openChangePassPage((SharedUser) response.getData("user"), actionEvent);
            } else {
                showError(response.getErrorMessage());
            }
        }
    }

    public void setRole(SharedUser user){
        if (user.getRole() == Role.STUDENT){
            Client.clientType = config.getProperty(String.class, "studentType");;
        }
        else if (user.getRole() == Role.MASTER){
            Client.clientType = config.getProperty(String.class, "masterType");
        }
        else if (user.getRole() == Role.EDU_ADMIN) {
            Client.clientType = config.getProperty(String.class, "adminType");
        }
        else if (user.getRole() == Role.MR_MOHSENI){
            Client.clientType = config.getProperty(String.class, "mohseniType");
        }
    }

    public void login(SharedUser user, ActionEvent actionEvent) throws IOException {
        String page = "";
        if (user.getRole() == Role.STUDENT){
            Client.clientType = config.getProperty(String.class, "studentType");;
            page = config.getProperty(String.class, "studentMainMenu");
        }
        else if (user.getRole() == Role.MASTER){
            Client.clientType = config.getProperty(String.class, "masterType");
            page = config.getProperty(String.class, "masterMainMenu");
        }
        else if (user.getRole() == Role.EDU_ADMIN){
            Client.clientType = config.getProperty(String.class, "adminType");
            page = config.getProperty(String.class, "adminPage");
        }
        else if (user.getRole() == Role.MR_MOHSENI){
            Client.clientType = config.getProperty(String.class, "mohseniType");
            page = config.getProperty(String.class, "mohseniPage");
        }
        timeline.stop();
        OpenPage.openNewPage(actionEvent, page);
    }

    public void showError(String errorName) throws IOException {
        loginNotice.setText(errorName);
        loginNotice.setVisible(true);
        setCaptchaImage();
        usernameField.clear();
        passwordField.clear();
        captchaNumberField.clear();
    }

    public void openChangePassPage(SharedUser user, ActionEvent actionEvent) throws IOException {
        timeline.stop();
        page = config.getProperty(String.class, "changePasswordPage");
        OpenPage.openNewPage(actionEvent, page);
    }

    public static void setClient(Client newClient) {
        client = newClient;
    }
    public void refresh(ActionEvent actionEvent) throws IOException {
        ServerController.reconnect();
    }
}
