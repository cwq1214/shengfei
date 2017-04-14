package sample.util;

import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

/**
 * Created by chenweiqi on 2017/4/13.
 */
public class DialogUtil {

    public static final int FILE_CHOOSE_TYPE_IMAGE = 0;
    public static final int FILE_CHOOSE_TYPE_VIDEO = 1;
    public static final int FILE_CHOOSE_TYPE_AUDIO = 2;

    public static Alert showDialog(String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, null);
        alert.setHeaderText(content);
        alert.show();
        return alert;
    }

    public static File fileChoiceDialog(String title, int fileType) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);

        if (fileType == FILE_CHOOSE_TYPE_IMAGE) {
            fileChooser.getExtensionFilters().addAll(
//                    new FileChooser.ExtensionFilter("All Images", "*.*"),
                    new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                    new FileChooser.ExtensionFilter("PNG", "*.png"),
                    new FileChooser.ExtensionFilter("GIF", "*.gif")
            );
        } else if (fileType == FILE_CHOOSE_TYPE_VIDEO) {
            fileChooser.getExtensionFilters().addAll(
//                    new FileChooser.ExtensionFilter("All Video", "*.*"),
                    new FileChooser.ExtensionFilter("AVI", "*.avi"),
                    new FileChooser.ExtensionFilter("WMV", "*.wmv")
            );
        }
        Stage stage = new Stage();
        return fileChooser.showOpenDialog(stage);
    }


    public static File saveFileDialog(String title) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(title);
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("excel2007", "*.xlsx"));
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("excel2003", "*.xls"));
        chooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        Stage stage = new Stage();
        return chooser.showSaveDialog(stage);
    }
}
