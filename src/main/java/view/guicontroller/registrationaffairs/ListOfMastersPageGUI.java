package view.guicontroller.registrationaffairs;

import config.Config;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import network.Client;
import sharedmodels.users.MasterRole;
import sharedmodels.users.SharedMaster;
import view.OpenPage;
import view.guicontroller.CheckConnection;
import view.guicontroller.LoginGUI;
import view.guicontroller.Theme;
import view.guicontroller.mainmenu.StudentMainMenuGUI;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ListOfMastersPageGUI implements Initializable {
    public static Client client;
    public static SharedMaster master;
    public static Config config = Config.getConfig();
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
        CheckConnection.checkConnection(refreshButton, connectionLabel);
        if (Client.clientType.equals(config.getProperty(String.class, "masterType"))) {
//            SharedMaster master = MasterMainMenuGUI.master;
            if (master.getMasterRole() == MasterRole.CHAIRMAN) setPage();
        }
        int counter = 0;
        if (LoginGUI.type.equals(config.getProperty(String.class, "studentType"))) {
            counter = StudentMainMenuGUI.counter;
        } else {
            //TODO
//            counter = MasterMainMenuGUI.counter;
        }
        Theme.setTheme(counter, background);
        setMastersList();

    }

    public void setPage() {
        enterLabel.setVisible(true);
        idField.setVisible(true);
        deleteButton.setVisible(true);
        addButton.setVisible(true);
        editButton.setVisible(true);
    }

    public void setMastersList()  {
//        String path = "./src/main/resources/users/allMasters.txt";
//        File mastersFile = new File(path);
//        if (mastersFile.exists()) {
//            Scanner scanner = new Scanner(mastersFile);
//            while (scanner.hasNext()) {
//                LoadUser loadUser = new LoadUser();
//                String username = scanner.nextLine();
//                loadUser.checkIsExistUser(username);
//                Master master = (Master) loadUser.getUser(username);
//                writeMaster(master);
//            }
//        }
    }


    public void logout(ActionEvent actionEvent) throws IOException {
        String page = config.getProperty(String.class, "loginPage");
        OpenPage.openNewPage(actionEvent, page);
    }

    public void backMainMenu(ActionEvent actionEvent) throws IOException {
        String type = LoginGUI.type;
        if (type.equals(config.getProperty(String.class, "studentType"))) {
            String page = config.getProperty(String.class, "studentMainMenu");
            OpenPage.openNewPage(actionEvent, page);
        } else {
            String page = config.getProperty(String.class, "masterMainMenu");
            OpenPage.openNewPage(actionEvent, page);
        }

    }

    public void deleteMaster(ActionEvent actionEvent) throws IOException {
//        String username = idField.getText();
//        Response response = client.getServerController().sendDeleteMasterRequest(username, master.getUsername());
        //TODO
    }

    public void addMasterPage(ActionEvent actionEvent) throws IOException {
        String page = config.getProperty(String.class, "addNewUserPage");
        OpenPage.openNewPage(actionEvent, page);
    }

    public void editMasterPage(ActionEvent actionEvent) throws IOException {
        String page = config.getProperty(String.class, "editMasterPage");
        OpenPage.openNewPage(actionEvent, page);
    }
}
