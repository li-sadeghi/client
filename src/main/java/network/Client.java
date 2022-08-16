package network;

import config.Config;
import network.database.AdminData;
import network.database.MasterData;
import network.database.MohseniData;
import network.database.StudentData;
import view.guicontroller.LoginGUI;

import java.io.IOException;
import java.util.Scanner;

public class Client {
    public static Config config = Config.getConfig();
    public static String clientType = "";
    public static String clientUsername = "";
    public static Thread pingThread;
    private final Scanner scanner = new Scanner(System.in);
    private final int port;
    private final String hostIp;
    public static boolean isConnect;
    private final int ping;

    public int getPing() {
        return ping;
    }

    private ServerController serverController;

    public Client(int port, String hostIp ,int ping) {
        this.port = port;
        this.hostIp = hostIp;
        this.ping = ping;
        isConnect = true;
    }

    public void start() {
        serverController = new ServerController(port, hostIp);
        serverController.connectToServer();
        ServerController.client = this;
        String masterType = config.getProperty(String.class, "masterType");;
        String studentType = config.getProperty(String.class, "studentType");;
        String adminType = config.getProperty(String.class, "adminType");;
        String mohseniType = config.getProperty(String.class, "mohseniType");
        pingThread = new Thread(() -> {
            while (true){
                if (isConnect){
                    if (clientType.equals(studentType)){
                        try {
                            StudentData.updateData();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }else if (clientType.equals(masterType)){
                        try {
                            MasterData.updateData();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }else if (clientType.equals(adminType)){
                        try {
                            AdminData.updateData();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                    }else if (clientType.equals(mohseniType)){
                        try {
                            MohseniData.updateData();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
//                    else {
//                        try {
//                            serverController.checkConnectionRequest();
//                        } catch (IOException e) {
//                            throw new RuntimeException(e);
//                        }
//                    }
                }
                System.out.println(isConnect);
                try {
                    Thread.sleep(ping);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        pingThread.setDaemon(true);
        pingThread.start();

    }

    public ServerController getServerController() {
        return serverController;
    }

}
