package network.database;

import network.Client;
import network.ServerController;
import response.Response;
import sharedmodels.department.Course;
import sharedmodels.department.PassedCourse;
import sharedmodels.department.TemporaryCourse;
import sharedmodels.users.SharedMaster;
import sharedmodels.users.SharedStudent;

import java.io.IOException;
import java.util.ArrayList;

public class StudentData {
    public static Client client = ServerController.client;
    public static SharedStudent student;
    public static ArrayList<Course> courses;
    public static ArrayList<SharedMaster> masters;

    public static void updateData() throws IOException {
        Response response = client.getServerController().getAllStudentData(Client.clientUsername);
        student = (SharedStudent) response.getData("user");
        courses = (ArrayList<Course>) response.getData("courses");
        masters = (ArrayList<SharedMaster>) response.getData("masters");

        //get messages for show last 10 messages... by class Chat
    }
}
