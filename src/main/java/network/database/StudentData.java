package network.database;

import network.Client;
import network.ServerController;
import response.Response;
import sharedmodels.users.SharedStudent;

import java.io.IOException;

public class StudentData {
    public static Client client = ServerController.client;
    public static SharedStudent student;

    public static void updateData() throws IOException {
        Response response = client.getServerController().getAllStudentData(Client.clientUsername);
        student = (SharedStudent) response.getData("user");
    }
}
