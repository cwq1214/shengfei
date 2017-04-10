package sample.util;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import sample.Main;
import sample.controller.AboutController;
import sample.controller.BaseController;
import sample.controller.MainController;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by chenweiqi on 2017/3/29.
 */
public class ViewUtil {

    static ViewUtil util = new ViewUtil();

    Locale mLocale;
    public static String currentLanguage = "中文";

    public Locale getmLocale() {
        if (mLocale == null){
            mLocale = Locale.SIMPLIFIED_CHINESE;
        }
        return mLocale;
    }

    public static ViewUtil getInstance(){
        return util;
    }

    private ViewUtil(){

    }

    public void setLocal(Locale local){
        mLocale = local;
    }

    public void openMainView() throws Exception {
        showView("view/main.fxml","首页",800,600);
    }

    public void showView (String resourcePath,String title,double width,double height,Object preData){
        try {
            Stage stage = new Stage();

            FXMLLoader loader = new FXMLLoader(Main.class.getResource(resourcePath),ResourceBundle.getBundle("i18n",getmLocale()));
            Parent root = loader.load();
            Scene scene = new Scene(root,width,height);
            BaseController controller = loader.getController();
            controller.setmParent(root);
            controller.setmStage(stage);
            if (preData != null){
                controller.setPreData(preData);
                controller.prepareInit();
            }

            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void openAboutDialog() throws IOException {
         showView("view/about.fxml","关于",-1,-1);

    }

    private Object showView(String resourcePath) throws IOException {
        return showView(resourcePath,"");
    }
    private Object showView(String resourcePath,String title) throws IOException {
        return showView(resourcePath,title,-1,-1);
    }
    private Object showView(String resourcePath,String title,double width ,double height) throws IOException {
        Locale locale;
        if (mLocale!=null){
            locale = mLocale;
        }else {
            locale = Locale.getDefault();
        }
        ResourceBundle bundle = ResourceBundle.getBundle("i18n",locale);
        FXMLLoader loader = new FXMLLoader(Main.class.getResource(resourcePath),bundle);
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        Parent root = loader.load();
        final BaseController controller = loader.getController();


        Stage primaryStage = new Stage();

        primaryStage.setOnShowing(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent event) {
                controller.onCreatedView();
            }
        });

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent event) {
                controller.onStop();
            }
        });

        primaryStage.setTitle(title);
        Scene scene = new Scene(root, width, height);
        controller.setmParent(root);
        controller.setmStage(primaryStage);
        primaryStage.getIcons().add(new Image(
                Main.class.getResourceAsStream("/sample/resource/img/shengfei.png")));

        primaryStage.setScene(scene);
        primaryStage.show();


        return controller;

    }
}
