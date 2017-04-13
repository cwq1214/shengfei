package sample.util;

import javafx.scene.control.Alert;

/**
 * Created by chenweiqi on 2017/4/13.
 */
public class DialogUtil {

    public static Alert showDialog(String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, null);
        alert.setHeaderText(content);
        alert.show();
        return alert;
    }
}
