package network.database;

import network.Client;
import network.ServerController;
import response.Response;
import sharedmodels.chatroom.Message;
import sharedmodels.users.SharedUser;

import java.io.IOException;
import java.util.ArrayList;

public class AdminData {
    public static Client client = ServerController.client;
    public static SharedUser admin;
    public static ArrayList<Message> messages;


    public static void updateData() throws IOException {
        Response response = client.getServerController().getAllAdminData();
        messages = (ArrayList<Message>) response.getData("messages");
        admin = (SharedUser) response.getData("user");
        System.out.println(admin.getEmailAddress());
    }
}
