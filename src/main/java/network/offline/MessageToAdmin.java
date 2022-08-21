package network.offline;

import network.Client;
import network.ServerController;
import sharedmodels.chatroom.MessageType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MessageToAdmin {
    private static String username = Client.clientUsername;
    private static final Client client = ServerController.client;
    public static void createNewMessageAndSave(String text, String username){
        OfflineMessage newMessage = new OfflineMessage();
        newMessage.setText(text);
        newMessage.setSenderUsername(username);
        SaveMessage.save(newMessage);
    }
    public static void loadAndSendMessages(){
        List<OfflineMessage> messages = LoadMessage.fetchAll(OfflineMessage.class);
        for (OfflineMessage message : messages) {
            if (message.getSenderUsername().equals(username)){
                client.getServerController().sendNewMessage(username, "1", message.getText(), MessageType.TEXT);
                LoadMessage.delete(message);
            }
        }
    }
}
