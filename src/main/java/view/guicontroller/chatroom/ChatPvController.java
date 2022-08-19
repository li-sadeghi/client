package view.guicontroller.chatroom;

import config.Config;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import network.Client;
import network.ServerController;
import response.Response;
import sharedmodels.chatroom.Chat;
import util.extra.EncodeDecodeFile;
import view.guicontroller.LoginGUI;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;

public class ChatPvController implements Initializable {
    public static Config config = Config.getConfig();
    public static Client client = ServerController.client;
    public static String senderUsername;
    public static String receiverUsername;
    public static Chat thisChat;
    public static int indexOfChat;
    @FXML
    Button downloadMediaButton;
    @FXML
    VBox imageHBox;
    @FXML
    Label nameLabel;
    @FXML
    TextField newText;
    @FXML
    TextArea chatBox;
    @FXML
    Button refreshButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    private void setPage(ArrayList<Message> messages) throws IOException {

    }

    public void sendText(ActionEvent actionEvent) throws IOException {
        String text = newText.getText();
        Message message = new Message(text, MessageType.TEXT, senderUsername, receiverUsername, FileType.NOTHING);
        Response response = client.getServerController().sendNewMessage(message);
    }

    public void sendMedia(ActionEvent actionEvent) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf"),
                new FileChooser.ExtensionFilter("AUDIO FILES", "*.mp3"),
                new FileChooser.ExtensionFilter("IMAGES", "*.jpg"),
                new FileChooser.ExtensionFilter("VIDEOS", "*.mp4"));


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
        String text = EncodeDecodeFile.byteArrayToString(byteArray);
        FileType fileType = EncodeDecodeFile.getFormat(String.valueOf(selectedFile.toPath()));
        Message message = new Message(text, MessageType.FILE, senderUsername, receiverUsername, fileType);
        Response response = client.getServerController().sendNewMessage(message);
    }

    public void downloadNewMedia(ActionEvent actionEvent) throws IOException {
        Response response = client.getServerController().sendGetAllMessagesRequest(senderUsername, receiverUsername);
        ArrayList<Message> messages = (ArrayList<Message>) response.getData("messages");
        for (int i = 0; i < messages.size(); i++) {
            Message message = messages.get(i);
            if (message.getType() == MessageType.FILE) {
                EncodeDecodeFile.downloadFileAndSave(message.getMessage(), message.getFileType());
            }
        }

    }

    public void refresh(ActionEvent actionEvent) throws IOException {
        ServerController.reconnect();
    }
}
