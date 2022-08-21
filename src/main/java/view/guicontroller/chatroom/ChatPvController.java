package view.guicontroller.chatroom;

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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import network.Client;
import network.ServerController;
import network.database.MasterData;
import network.database.StudentData;
import sharedmodels.chatroom.Chat;
import sharedmodels.chatroom.Message;
import sharedmodels.chatroom.MessageType;
import util.extra.EncodeDecodeFile;
import view.OpenPage;
import view.guicontroller.CheckConnection;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Random;
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
    HBox imageHBox;
    @FXML
    Label nameLabel;
    @FXML
    TextField newText;
    @FXML
    TextArea chatBox;
    @FXML
    Button refreshButton;
    @FXML
    Label connectionLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        receiverUsername = thisChat.getReceiverId();
        senderUsername = thisChat.getSenderId();

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(6), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                CheckConnection.checkConnection(refreshButton, connectionLabel);
                if (Client.clientType.equals(config.getProperty(String.class, "masterType"))){
                    thisChat = MasterData.chats.get(indexOfChat);
                }else {
                    thisChat = StudentData.chats.get(indexOfChat);
                }
                setProfile();
                setPage(thisChat.getMessages());

            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.playFromStart();

    }

    private void setProfile() {
        imageHBox.getChildren().clear();
        byte[] decodeImage = EncodeDecodeFile.decode(thisChat.getReceiverImageByes());
        Image image = new Image(new ByteArrayInputStream(decodeImage));
        ImageView pic = new ImageView();
        pic.setFitWidth(config.getProperty(Integer.class, "imageWidthInChat"));
        pic.setFitHeight(config.getProperty(Integer.class, "imageHeightInChat"));
        pic.setImage(image);
        imageHBox.getChildren().add(pic);
        nameLabel.setText(thisChat.getReceiverName());
    }

    private void setPage(ArrayList<Message> messages) {
        chatBox.clear();
        String text = "";
        for (Message message : messages) {
            text  += getMessage(message);
        }
        chatBox.setText(text);
    }

    private String getMessage(Message message){
        String text = "";
        if (message.getSenderId().equals(senderUsername)){
            text += "YOU";
        }else text += message.getSenderId();
        text += ":\n";
        text += message.getTime();
        text += "\n";
        if (message.getMessageType() == MessageType.FILE){
            text += ("YOU HAVE A FILE!" + "\n" + "DOWNLOAD IT.");
        }else {
            text += message.getMessageText();
        }
        text += "\n";
        return text;
    }

    public void sendText(ActionEvent actionEvent) throws IOException {
        String text = newText.getText();
        client.getServerController().sendNewMessage(thisChat.getSenderId(), thisChat.getReceiverId(), text, MessageType.TEXT);
        newText.clear();
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
        String fileType = EncodeDecodeFile.getFormat(String.valueOf(selectedFile.toPath()));

        client.getServerController().sendNewFileMessage(senderUsername, receiverUsername, text, MessageType.FILE, fileType);
    }

    public void downloadAllMedia(ActionEvent actionEvent) throws IOException {
        ArrayList<Message> messages = thisChat.getMessages();
        for (int i = 0; i < messages.size(); i++) {
            Message message = messages.get(i);
            if (message.getMessageType() == MessageType.FILE) {
                downloadFileAndSave(message.getMessageText(), message.getFileType());
            }
        }

    }

    private void downloadFileAndSave(String encoded, String fileType) throws IOException {
        byte[] decoded = EncodeDecodeFile.decode(encoded);
        String path = "./src/main/resources/downloadedfiles/" + new Random().nextLong() + "." + fileType ;
        File file = new File(path);
        file.createNewFile();
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(decoded);
        fileOutputStream.close();
        fileOutputStream.flush();
    }

    public void refresh(ActionEvent actionEvent) throws IOException {
        ServerController.reconnect();
    }

    public void backEduGram(ActionEvent actionEvent) throws IOException {
        String page = config.getProperty(String.class, "eduGramPage");
        OpenPage.openNewPage(actionEvent, page);
    }

    public void logout(ActionEvent actionEvent) throws IOException {
        String page = config.getProperty(String.class, "loginPage");
        OpenPage.openNewPage(actionEvent, page);
    }
}
