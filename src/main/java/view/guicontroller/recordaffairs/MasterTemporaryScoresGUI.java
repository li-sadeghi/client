package view.guicontroller.recordaffairs;

import config.Config;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import network.Client;
import network.ServerController;
import view.OpenPage;
import view.guicontroller.LoginGUI;
import view.guicontroller.Theme;
import view.guicontroller.mainmenu.MasterMainMenuGUI;
import view.guicontroller.mainmenu.StudentMainMenuGUI;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MasterTemporaryScoresGUI implements Initializable {
    public static Client client;
    public static String courseId;
    public static Config config = Config.getConfig();
    @FXML
    TextField courseIdField;
    @FXML
    TextField idField;
    @FXML
    TextField markField;
    @FXML
    TextField answerField;
    @FXML
    Label finalNoticeLabel;
    @FXML
    Label markNoticeLabel;
    @FXML
    VBox studentIdVBox;
    @FXML
    VBox markVBox;
    @FXML
    VBox protestVBox;
    @FXML
    VBox answerVBox;
    @FXML
    AnchorPane background;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        client = ServerController.client;
        int counter;
        if (Client.clientType.equals(config.getProperty(String.class, "studentType"))) {
            counter = StudentMainMenuGUI.counter;
        } else counter = MasterMainMenuGUI.counter;
        Theme.setTheme(counter, background);

    }

    public void logout(ActionEvent actionEvent) throws IOException {
        String page = config.getProperty(String.class, "loginPage");

        OpenPage.openNewPage(actionEvent, page);
    }

    public void backMainMenu(ActionEvent actionEvent) throws IOException {
        String page;
        if (LoginGUI.type.equals(config.getProperty(String.class, "studentType"))) {
            page = config.getProperty(String.class, "studentMainMenu");//
        } else page = config.getProperty(String.class, "masterMainMenu");

        OpenPage.openNewPage(actionEvent, page);
    }

    public void showStudents(ActionEvent actionEvent) {
    }

    public void registerMarkAndAnswer(ActionEvent actionEvent) {
    }

    public void finalMarkThisCourse(ActionEvent actionEvent) throws FileNotFoundException {
//        Course course = LoadCourse.loadCourse(courseId);
//        if (course.getTemporaryScoresIds().size() != course.getStudentsHaveCourse().size()) {
//            showFinalNotice("Please enter all Marks! then try");
//        } else {
//            showFinalNotice("all Marks changed to final mark!");
//        }
    }

    public void showFinalNotice(String notice) {
        finalNoticeLabel.setText(notice);
        finalNoticeLabel.setVisible(true);
    }

    public void showMarkNotice(String notice) {
        markNoticeLabel.setText(notice);
        markNoticeLabel.setVisible(true);
    }
}
