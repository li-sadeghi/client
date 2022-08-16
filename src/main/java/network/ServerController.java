package network;

import config.Config;
import javafx.stage.Stage;
import org.codehaus.jackson.map.ObjectMapper;
import request.Request;
import request.RequestType;
import response.Response;
import sharedmodels.chatroom.MessageType;
import util.Jackson;
import view.OpenPage;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
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
}
