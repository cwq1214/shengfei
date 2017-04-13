package sample.util;

import javafx.stage.Stage;
import sample.view.Toast;

/**
 * Created by chenweiqi on 2017/4/13.
 */
public class ToastUtil {

    public static void show(String message) {
        Toast.makeText(new Stage(), message, 2000, 500, 500);
    }
}
