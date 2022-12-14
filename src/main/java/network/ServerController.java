package network;

import config.Config;
import javafx.stage.Stage;
import org.codehaus.jackson.map.ObjectMapper;
import request.Request;
import request.RequestType;
import response.Response;
import sharedmodels.chatroom.Message;
import sharedmodels.chatroom.MessageType;
import sharedmodels.cw.EducationalThing;
import sharedmodels.cw.HomeWork;
import sharedmodels.cw.Solution;
import sharedmodels.department.Course;
import sharedmodels.users.SharedMaster;
import sharedmodels.users.SharedStudent;
import util.Jackson;
import view.OpenPage;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ServerController {
    public static Client client;
    private PrintStream printStream;
    private Scanner scanner;

    private final int port;
    private final String hostIp;

    private final ObjectMapper objectMapper;

    public ServerController(int port, String hostIp) {
        this.port = port;
        this.hostIp = hostIp;
        objectMapper = Jackson.getNetworkObjectMapper();
    }

    public void connectToServer() {
        try {
            Socket socket = new Socket(hostIp, port);
            printStream = new PrintStream(socket.getOutputStream());
            scanner = new Scanner(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendRequest(Request request) {
        try {
            String requestString = objectMapper.writeValueAsString(request);
            printStream.println(requestString);
            printStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Response getResponse() throws IOException {
//        Response response = null;
//        try {
//            String input;
//            try {
//                input = scanner.nextLine();
//            }catch (Exception e){
//                input = null;
//            }
//            if (input!= null){
//                response = objectMapper.readValue(input, Response.class);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }catch (NoSuchElementException n){
//            disconnect();
//        }
//        return response;
        Response response = null;
        do {
            try {
                String input = scanner.nextLine();
                response = objectMapper.readValue(input, Response.class);
            } catch (IOException e) {
                e.printStackTrace();
            }catch (NoSuchElementException n){
                disconnect();
                break;
            }catch (Exception e){
//                System.out.println("next line exception");
            }
        }while (response == null);
        return response;
    }

    private void disconnect() throws IOException {
        Client.isConnect = false;
        Client.pingThread.interrupt();
    }
    public static void reconnect() throws IOException {
        String hostIp = Config.getConfig().getProperty(String.class, "hostIp");
        int port = Config.getConfig().getProperty(Integer.class, "serverPort");
        Socket socket = new Socket(hostIp, port);
        Client.isConnect = true;
        client.start();
        socket.close();
    }

    public Response sendCaptchaRequest() throws IOException {
        Request request = new Request(RequestType.CAPTCHA);
        sendRequest(request);
        return getResponse();
    }

    public Response sendLoginRequest(String enteredUsername, String enteredPassword) throws IOException {
        Request request = new Request(RequestType.LOGIN);
        request.addData("username", enteredUsername);
        request.addData("password", enteredPassword);
        sendRequest(request);
        return getResponse();
    }

    public Response sendChangePasswordRequest(String enteredPrePass, String enteredNewPass, String clientUsername) throws IOException {
        Request request = new Request(RequestType.CHANGE_PASS);
        request.addData("prePass", enteredPrePass);
        request.addData("newPass", enteredNewPass);
        request.addData("username", clientUsername);
        sendRequest(request);
        return getResponse();
    }

    public Response getAllAdminData() throws IOException {
        Request request = new Request(RequestType.ADMIN_DATA);
        sendRequest(request);
        return getResponse();
    }

    public void sendNewMessage(String senderUsername, String receiverUsername, String messageText, MessageType messageType) {
        Request request = new Request(RequestType.NEW_MESSAGE);
        request.addData("sender", senderUsername);
        request.addData("receiver", receiverUsername);
        request.addData("text", messageText);
        request.addData("type", messageType.toString());
        sendRequest(request);
    }

    public void checkConnectionRequest() throws IOException {
        Request request = new Request(RequestType.CHECK_CONNECTION);
        sendRequest(request);
        Response response = getResponse();
    }

    public Response getAllStudentData(String clientUsername) throws IOException {
        Request request = new Request(RequestType.STUDENT_DATA);
        request.addData("username", clientUsername);
        sendRequest(request);
        return getResponse();
    }

    public Response getAllMasterData(String clientUsername) throws IOException {
        Request request = new Request(RequestType.MASTER_DATA);
        request.addData("username", clientUsername);
        sendRequest(request);
        return getResponse();
    }

    public Response getAllMohseniData(String clientUsername) throws IOException {
        Request request = new Request(RequestType.MOHSENI_DATA);
        request.addData("username", clientUsername);
        sendRequest(request);
        return getResponse();
    }

    public Response getStudentRequest(String studentUsername) throws IOException {
        Request request = new Request(RequestType.SINGLE_STUDENT);
        request.addData("username", studentUsername);
        sendRequest(request);
        return getResponse();
    }

    public Response sendChangeEmailRequest(String studentUsername, String enteredEmail) throws IOException {
        Request request = new Request(RequestType.CHANGE_EMAIL);
        request.addData("username", studentUsername);
        request.addData("newEmail", enteredEmail);
        sendRequest(request);
        return getResponse();
    }

    public Response sendChangePhoneNumberRequest(String studentUsername, String enteredPhoneNumber) throws IOException {
        Request request = new Request(RequestType.CHANGE_PHONE);
        request.addData("username", studentUsername);
        request.addData("newPhone", enteredPhoneNumber);
        sendRequest(request);
        return getResponse();
    }

    public Response deleteCourse(String courseId) throws IOException {
        Request request = new Request(RequestType.DELETE_COURSE);
        request.addData("courseId", courseId);
        sendRequest(request);
        return getResponse();
    }

    public Response deleteMaster(String usernameSelected, String clientUsername) throws IOException {
        Request request = new Request(RequestType.DELETE_MASTER);
        request.addData("username", clientUsername);
        request.addData("usernameSelected", usernameSelected);
        sendRequest(request);
        return getResponse();
    }

    public Response addCourseRequest(Course newCourse, String departmentId, String masterId, String prerequisiteId, ArrayList<String> studentIDs, ArrayList<String> tAsIds) throws IOException {
        Request request = new Request(RequestType.ADD_COURSE);
        request.addData("course", newCourse);
        request.addData("departmentId", departmentId);
        request.addData("masterId", masterId);
        request.addData("prerequisiteId", prerequisiteId);
        request.addData("studentIDs", studentIDs);
        request.addData("tAsIds", tAsIds);
        sendRequest(request);
        return getResponse();
    }

    public Response editCourseRequest(Course newCourse, String departmentId, String masterId, String prerequisiteId, ArrayList<String> studentIDs, ArrayList<String> tAsIds) throws IOException {
        Request request = new Request(RequestType.EDIT_COURSE);
        request.addData("course", newCourse);
        request.addData("departmentId", departmentId);
        request.addData("masterId", masterId);
        request.addData("prerequisiteId", prerequisiteId);
        request.addData("studentIDs", studentIDs);
        request.addData("tAsIds", tAsIds);
        sendRequest(request);
        return getResponse();
    }

    public Response createNewMasterRequest(SharedMaster newMaster, String password) throws IOException {
        //department : ?????????????? ???????? ???????? ???? ???????? ????????????
        Request request = new Request(RequestType.ADD_MASTER);
        request.addData("editorId", Client.clientUsername);
        request.addData("newMaster", newMaster);
        request.addData("password", password);
        sendRequest(request);
        return getResponse();
    }

    public Response createNewStudentRequest(SharedStudent newStudent, String password, String helperMasterId) throws IOException {
        //department : ?????????????? ???????? ???????? ???? ???????? ????????????
        Request request = new Request(RequestType.ADD_STUDENT);
        request.addData("editorId", Client.clientUsername);
        request.addData("newStudent", newStudent);
        request.addData("password", password);
        request.addData("helperMasterId", helperMasterId);
        sendRequest(request);
        return getResponse();
    }

    public Response editMasterRequest(SharedMaster newMaster, String password, ArrayList<String> coursesIDs) throws IOException {
        Request request = new Request(RequestType.EDIT_MASTER);
        request.addData("newMaster", newMaster);
        request.addData("password", password);
        request.addData("coursesIDs", coursesIDs);
        sendRequest(request);
        return getResponse();
    }

    public Response sendNewProtest(String temporaryId, String protestTex) throws IOException {
        Request request = new Request(RequestType.NEW_PROTEST);
        request.addData("temporaryCourseId", temporaryId);
        request.addData("protestTex", protestTex);
        sendRequest(request);
        return getResponse();
    }

    public void setSelectionTime(String username, String startTime, String endTime) {
        Request request = new Request(RequestType.SET_SELECTION_TIME);
        request.addData("username", username);
        request.addData("startTime", startTime);
        request.addData("endTime", endTime);
        sendRequest(request);
    }

    public void sendNewFileMessage(String senderUsername, String receiverUsername, String text, MessageType file, String fileType) {
        Request request = new Request(RequestType.FILE_MESSAGE);
        Message message = new Message();
        message.setMessageText(text);
        message.setFileType(fileType);
        message.setMessageType(MessageType.FILE);
        message.setSenderId(senderUsername);
        message.setReceiverId(receiverUsername);
        request.addData("message", message);
        sendRequest(request);
    }

    public Response starNewCourse(String clientUsername, String courseId) throws IOException {
        Request request = new Request(RequestType.STAR_COURSE);
        request.addData("username", clientUsername);
        request.addData("courseId", courseId);
        sendRequest(request);
        return getResponse();
    }

    public Response catchNewCourse(String clientUsername, String courseId) throws IOException {
        Request request = new Request(RequestType.CATCH_COURSE);
        request.addData("username", clientUsername);
        request.addData("courseId", courseId);
        sendRequest(request);
        return getResponse();

    }

    public Response removeCourse(String clientUsername, String courseId) throws IOException {
        Request request = new Request(RequestType.REMOVE_COURSE);
        request.addData("username", clientUsername);
        request.addData("courseId", courseId);
        sendRequest(request);
        return getResponse();
    }

    public Response getHomework(Integer id) throws IOException {
        Request request = new Request(RequestType.GET_HOMEWORK);
        request.addData("id", id);
        sendRequest(request);
        return getResponse();
    }

    public Response getAllSolutionsToHomework(int id) throws IOException {
        Request request = new Request(RequestType.GET_SOLUTIONS);
        request.addData("homeworkId", id);
        sendRequest(request);
        return getResponse();
    }

    public void registerMarkForSolution(int id, double mark) {
        Request request = new Request(RequestType.REGISTER_MARK);
        request.addData("solutionId", id);
        request.addData("mark", mark);
        sendRequest(request);
    }

    public Response checkHaveSolutionToHomeWork(int id, String clientUsername) throws IOException {
        Request request = new Request(RequestType.HAVE_SOLUTION);
        request.addData("homeworkId", id);
        request.addData("username", clientUsername);
        sendRequest(request);
        return getResponse();
    }

    public Response getSolutionForHomeWork(int id, String clientUsername) throws IOException {
        Request request = new Request(RequestType.GET_SOLUTION);
        request.addData("homeworkId", id);
        request.addData("username", clientUsername);
        sendRequest(request);
        return getResponse();
    }

    public void sendNewSolution(HomeWork homeWork, Solution solution) throws IOException {
        Request request = new Request(RequestType.NEW_SOLUTION);
        request.addData("solution", solution);
        request.addData("homework", homeWork);
        sendRequest(request);
    }

    public void addNewUserToCourse(String studentId, String courseId) {
        Request request = new Request(RequestType.ADD_USER_TO_COURSE);
        request.addData("studentId", studentId);
        request.addData("courseId", courseId);
        sendRequest(request);
    }

    public Response getEducational(Integer id) throws IOException {
        Request request = new Request(RequestType.GET_EDUCATIONAL);
        request.addData("id", id);
        sendRequest(request);
        return getResponse();
    }

    public void deleteEducational(int id) {
        Request request = new Request(RequestType.DELETE_EDUCATIONAL);
        request.addData("id", id);
        sendRequest(request);
    }

    public void addNewHomeworkToCourse(HomeWork homeWork) {
        Request request = new Request(RequestType.ADD_HOMEWORK);
        request.addData("homework", homeWork);
        sendRequest(request);
    }

    public void addNewEducational(EducationalThing educationalThing, Course course) {
        Request request = new Request(RequestType.ADD_EDUCATIONAL);
        request.addData("educational", educationalThing);
        request.addData("courseId", course.getId());
        sendRequest(request);
    }

    public Response getAllHomeworksOfCourse(String courseId) throws IOException {
        Request request = new Request(RequestType.ALL_HOMEWORKS);
        request.addData("courseId", courseId);
        sendRequest(request);
        return getResponse();
    }

    public void addTARequest(String taId, String courseId) {
        Request request = new Request(RequestType.ADD_TA);
        request.addData("taId", taId);
        request.addData("courseId", courseId);
        sendRequest(request);
    }
}
