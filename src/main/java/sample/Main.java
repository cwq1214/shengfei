package sample;

import javafx.application.Application;
import javafx.stage.Stage;
import sample.util.ViewUtil;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
//        ViewUtil.getInstance().openDbTableView();
        ViewUtil.getInstance().openMainView();

    }

    @Override
    public void stop() throws Exception {
        super.stop();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
