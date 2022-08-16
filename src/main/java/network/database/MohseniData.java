package network.database;

import network.Client;
import network.ServerController;
import response.Response;
import sharedmodels.users.SharedStudent;
import sharedmodels.users.SharedUser;

import java.io.IOException;
import java.util.ArrayList;

public class MohseniData {
    public static Client client = ServerController.client;
    public static ArrayList<SharedStudent> students;
    public static SharedUser mohseni;
    public static void updateData() throws IOException {
        Response response = client.getServerController().getAllMohseniData(Client.clientUsername);
        mohseni = (SharedStudent) response.getData("user");
        students = (ArrayList<SharedStudent>) response.getData("students");
    }
}
