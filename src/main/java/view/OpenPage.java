package view;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class OpenPage {
    public static void openNewPage(Stage stage, String page) throws IOException {
        FXMLLoader loader = new FXMLLoader(OpenPage.class.getClassLoader().getResource(page));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public static void openNewPage(ActionEvent actionEvent, String page) throws IOException {
        Stage stage = (Stage) ((((Node) (actionEvent.getSource())).getScene()).getWindow());
        FXMLLoader loader = new FXMLLoader(OpenPage.class.getClassLoader().getResource(page));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public static void openNewPage(MouseEvent mouseEvent, String page) throws IOException{
        Stage stage = (Stage) ((((Node) (mouseEvent.getSource())).getScene()).getWindow());
        FXMLLoader loader = new FXMLLoader(OpenPage.class.getClassLoader().getResource(page));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
