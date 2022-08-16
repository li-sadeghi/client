import config.Config;
import javafx.application.Application;
import javafx.stage.Stage;
import network.Client;
import view.OpenPage;
import view.guicontroller.LoginGUI;

public class Main extends Application {
    public static final Config config = Config.getConfig();

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Integer port = config.getProperty(Integer.class, "serverPort");
        String hostIp = config.getProperty(String.class, "hostIp");
        Integer ping = config.getProperty(Integer.class, "ping");
        Client client = new Client(port, hostIp, ping);
        client.start();
        LoginGUI.setClient(client);
        String page = config.getProperty(String.class, "loginPage");
        OpenPage.openNewPage(new Stage(), page);
    }
}
