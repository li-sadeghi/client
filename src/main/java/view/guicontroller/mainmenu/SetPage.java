package view.guicontroller.mainmenu;

import config.Config;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import sharedmodels.users.SharedUser;
import util.extra.EncodeDecodeFile;

import java.io.ByteArrayInputStream;

public class SetPage {
    public static Config config = Config.getConfig();
    public static void setPage(VBox userImageVBox, Label nameLabel, Label emailAddressLabel, Label lastLoginTimeLabel, VBox userImageVBox1, SharedUser user) {
        userImageVBox.getChildren().clear();
        nameLabel.setText(user.getFullName());
        emailAddressLabel.setText(user.getEmailAddress());
        lastLoginTimeLabel.setText(user.getLastLogin());
        byte[] decodeImage = EncodeDecodeFile.decode(user.getUserImageBytes());
        Image image = new Image(new ByteArrayInputStream(decodeImage));
        ImageView pic = new ImageView();
        pic.setFitWidth(config.getProperty(Integer.class, "userImageWidthSize"));
        pic.setFitHeight(config.getProperty(Integer.class, "userImageHeightSize"));
        pic.setImage(image);
        userImageVBox.getChildren().add(pic);
    }
}
