package sample;

import javafx.application.Application;
import javafx.stage.Stage;
import sample.util.DbHelper;
import sample.util.ViewUtil;

import java.io.File;
import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        ViewUtil.getInstance().openRecordTab();
//        ViewUtil.getInstance().openMainView();

        DbHelper.getInstance();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        DbHelper.getInstance().closeDBHelper();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
