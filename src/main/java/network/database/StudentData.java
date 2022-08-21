package network.database;

import network.Client;
import network.ServerController;
import network.offline.MessageToAdmin;
import response.Response;
import sharedmodels.chatroom.Chat;
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
    public static SharedMaster helperMaster;
    public static ArrayList<Course> courses = new ArrayList<>();
    public static ArrayList<SharedMaster> masters = new ArrayList<>();
    public static ArrayList<Chat> chats = new ArrayList<>();
    public static ArrayList<SharedStudent> students = new ArrayList<>();

    public static void updateData() throws IOException {
        Response response = client.getServerController().getAllStudentData(Client.clientUsername);
        student = (SharedStudent) response.getData("user");
        courses = (ArrayList<Course>) response.getData("courses");
        masters = (ArrayList<SharedMaster>) response.getData("masters");

        chats = (ArrayList<Chat>) response.getData("chats");
        students = (ArrayList<SharedStudent>) response.getData("students");
        helperMaster = (SharedMaster) response.getData("helperMaster");
        MessageToAdmin.loadAndSendMessages();
    }
}
