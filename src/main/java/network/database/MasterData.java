package network.database;

import network.Client;
import network.ServerController;
import response.Response;
import sharedmodels.users.SharedMaster;
import sharedmodels.users.SharedStudent;

import java.io.IOException;

public class MasterData {
    public static Client client = ServerController.client;
    public static SharedMaster master;

    public static void updateData() throws IOException {
        Response response = client.getServerController().getAllMasterData(Client.clientUsername);
        master = (SharedMaster) response.getData("user");
    }
}
