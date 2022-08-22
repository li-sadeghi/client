package network.database;

import network.Client;
import network.ServerController;
import network.offline.MessageToAdmin;
import response.Response;
import sharedmodels.chatroom.Chat;
import sharedmodels.department.Course;
import sharedmodels.department.Department;
import sharedmodels.department.PassedCourse;
import sharedmodels.department.TemporaryCourse;
import sharedmodels.users.SharedMaster;
import sharedmodels.users.SharedStudent;

import java.io.IOException;
import java.util.ArrayList;

public class MasterData {
    public static Client client = ServerController.client;
    public static SharedMaster master;
    public static Department department;
    public static ArrayList<Course> courses;
    public static ArrayList<SharedMaster> masters;

    public static ArrayList<PassedCourse> passedCourses;
    public static ArrayList<Chat> chats;

    //برای رییس و معاون دانشکده همه ی بچه های دانشکده و برای غیر، دانشجوهایی که استاد راهنمایشان هست
    public static ArrayList<SharedStudent> students;
    public static ArrayList<SharedStudent> allStudents;
    public static ArrayList<TemporaryCourse> temporaryCourses;
    public static ArrayList<Course> coursesHave;

    public static void updateData() throws IOException {
        MessageToAdmin.loadAndSendMessages();
        Response response = client.getServerController().getAllMasterData(Client.clientUsername);
        master = (SharedMaster) response.getData("user");
        courses = (ArrayList<Course>) response.getData("courses");
        masters = (ArrayList<SharedMaster>) response.getData("masters");
        passedCourses = (ArrayList<PassedCourse>) response.getData("passedCourses");
        students = (ArrayList<SharedStudent>) response.getData("students");
        chats = (ArrayList<Chat>) response.getData("chats");
        allStudents = (ArrayList<SharedStudent>) response.getData("allStudents");
        department = (Department) response.getData("department");
        temporaryCourses = (ArrayList<TemporaryCourse>) response.getData("temporaryCourses");
        coursesHave = (ArrayList<Course>) response.getData("coursesHave");
    }
}
