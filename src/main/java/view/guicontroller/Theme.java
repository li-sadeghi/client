package view.guicontroller;

import config.Config;
import javafx.scene.layout.AnchorPane;

public class Theme {
    public static Config config = Config.getConfig();
    private static String firstBackground = config.getProperty(String.class, "firstBackground");
    private static String secondBackground = config.getProperty(String.class, "secondBackground");;
    private static String thirdBackground = config.getProperty(String.class, "thirdBackground");;


    public static void setTheme(int counter, AnchorPane background){
        if (counter % 3 == 0){
            background.setStyle(firstBackground);
        } else if (counter % 3 == 1){
            background.setStyle(secondBackground);
        }else {
            background.setStyle(thirdBackground);
        }
    }
}
