package network;

import config.Config;
import javafx.stage.Stage;
import org.codehaus.jackson.map.ObjectMapper;
import request.Request;
import request.RequestType;
import response.Response;
import sharedmodels.chatroom.MessageType;
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
        Response response = null;
        try {
            String input = scanner.nextLine();
            response = objectMapper.readValue(input, Response.class);
        } catch (IOException e) {
            e.printStackTrace();
        }catch (NoSuchElementException n){
            disconnect();
        }
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
        //department : دانشکده همون فردی که داره میسازه
        Request request = new Request(RequestType.ADD_MASTER);
        request.addData("editorId", Client.clientUsername);
        request.addData("newMaster", newMaster);
        request.addData("password", password);
        sendRequest(request);
        return getResponse();
    }

    public Response createNewStudentRequest(SharedStudent newStudent, String password, String helperMasterId) throws IOException {
        //department : دانشکده همون فردی که داره میسازه
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
}
