package network.database;

import network.Client;
import network.ServerController;
import response.Response;
import sharedmodels.department.Course;
import sharedmodels.department.PassedCourse;
import sharedmodels.users.SharedMaster;
import sharedmodels.users.SharedStudent;

import java.io.IOException;
import java.util.ArrayList;

public class MasterData {
    public static Client client = ServerController.client;
    public static SharedMaster master;
    public static ArrayList<Course> courses;
    public static ArrayList<SharedMaster> masters;

    public static ArrayList<PassedCourse> passedCourses;
    public static void updateData() throws IOException {
        Response response = client.getServerController().getAllMasterData(Client.clientUsername);
        master = (SharedMaster) response.getData("user");
        courses = (ArrayList<Course>) response.getData("courses");
        masters = (ArrayList<SharedMaster>) response.getData("masters");
        passedCourses = (ArrayList<PassedCourse>) response.getData("passedCourses");
    }
}
