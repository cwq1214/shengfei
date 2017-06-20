package sample;

import javafx.application.Application;
import javafx.stage.Stage;
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
//        TestController vc = ((TestController) ViewUtil.getInstance().showView("view/testView.fxml", "测试", -1, -1, ""));
//        vc.mStage.show();

        ViewUtil.getInstance().openMainView();
        DbHelper.getInstance();

    }

    @Override
    public void stop() throws Exception {
        super.stop();
        DbHelper.getInstance().closeDBHelper();

        File tempDir = new File(Constant.TEMP_DIR);
        File[] files = tempDir.listFiles();
        for (int i = 0;i < files.length;i ++) {
            files[i].deleteOnExit();
        }
        tempDir.delete();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
