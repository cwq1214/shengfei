package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sample.controller.AV.TestController;
import sample.controller.RecordTabController;
import sample.entity.Record;
import sample.util.Constant;
import sample.util.DbHelper;
import sample.util.ViewUtil;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

//        RecordTabController controller = (RecordTabController) ViewUtil.getInstance().openRecordTab();
//        controller.startPreview();
//        controller.show();
//
//

        System.out.println("");

        ViewUtil.getInstance().openMainView();
        DbHelper.getInstance();

    }

    @Override
    public void stop() throws Exception {
        super.stop();
        DbHelper.getInstance().closeDBHelper();

        File tempDir = new File(Constant.TEMP_DIR);
        if (tempDir.exists()){
            File[] files = tempDir.listFiles();
            for (int i = 0;i < files.length;i ++) {
                files[i].deleteOnExit();
            }
            tempDir.delete();
        }
    }

    public static void main(String[] args) {
        Font isaFont = Font.loadFont(Main.class.getResource("resource/bee.ttf").toString(), 20);
        launch(args);
    }
}
