package sample.util;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import sample.Main;
import sample.controller.*;

import java.io.IOException;
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
        showView("view/main.fxml", "首页", 800, 600, true);
    }


    /**
     *
     * @param resourcePath
     * @param title
     * @param width
     * @param height
     * @param preData controller传值使用
     * @return
     */
    public Object showView (String resourcePath,String title,double width,double height,Object preData){
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

            stage.setOnShowing(new EventHandler<WindowEvent>() {
                public void handle(WindowEvent event) {
                    controller.onCreatedView();
                }
            });

            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                public void handle(WindowEvent event) {
                    controller.onStop();
                }
            });

            stage.setTitle(title);
            stage.setScene(scene);
            return controller;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public BaseController openAboutDialog() throws IOException {
        return (BaseController) showView("view/about.fxml", "关于", -1, -1, true);

    }

    public BaseController openRecordTab()throws IOException{
        return (BaseController) showView("view/recordTabView.fxml", "录制", -1, -1, false);

    }


    public BaseController openHYFYDCZBTable() {
        DbTableController controller = null;
        try {
            controller = (DbTableController) showView("view/dbTableView.fxml", "表", 800, 600, false);
            controller.setType(DbTableController.TYPE_HAN_YU_FANG_YAN_ZI_BIAO);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return controller;
    }

    public BaseController openCHDCBTable() {
        DbTableController controller = null;
        try {
            controller = (DbTableController) showView("view/dbTableView.fxml", "表", 800, 600, false);
            controller.setType(DbTableController.TYPE_CI_HUI);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return controller;
    }

    public BaseController openYFLJRCYJDCB() {
        DbTableController controller = null;
        try {
            controller = (DbTableController) showView("view/dbTableView.fxml", "表", 800, 600, false);
            controller.setType(DbTableController.TYPE_RI_CHANG_YONG_JU);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return controller;
    }

    public BaseController openHYZTBTable() {
        DbTableController controller = null;
        try {
            controller = (DbTableController) showView("view/dbTableView.fxml", "表", 800, 600, false);
            controller.setType(DbTableController.TYPE_HUA_YU_ZHU_TI);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return controller;
    }

    public BaseController openYYZFDMBTable() {
        DbTableController controller = null;
        try {
            controller = (DbTableController) showView("view/dbTableView.fxml", "表", 800, 600, false);
            controller.setType(DbTableController.TYPE_HAN_YU_FANG_YAN);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return controller;
    }

    public BaseController openYYYBTable() {
        DbTableController controller = null;
        try {
            controller = (DbTableController) showView("view/dbTableView.fxml", "表", 800, 600, false);
            controller.setType(DbTableController.TYPE_YUAN_YIN_ZI_FU);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return controller;
    }

    private BaseController openDbTableView() throws IOException {
        return (BaseController) showView("view/dbTableView.fxml", "表", 800, 600, true);

    }


    public BaseController openSearchDialog() throws IOException {
        return (BaseController) showView("view/searchView.fxml", "查找", -1, -1, false);
    }

    public BaseController openReplaceDialog() throws IOException {
        return (BaseController) showView("view/replaceView.fxml", "替换", -1, -1, false);
    }

    public CreateHTMLFileController openCreateHTMLFileView() throws IOException {
        return (CreateHTMLFileController) showView("view/createHTMLFileView.fxml", "", -1, -1, false);

    }

    public CreateHTML1Controller openCreateHTML1View() throws IOException {
        return (CreateHTML1Controller) showView("view/createHTML1View.fxml", "", -1, -1, false);

    }
    public CreateHTML2Controller openCreateHTML2View() throws IOException {
        return (CreateHTML2Controller) showView("view/createHTML2View.fxml", "", -1, -1, false);

    }

    public CreateHTML3Controller openCreateHTML3View() throws IOException {
        return (CreateHTML3Controller) showView("view/createHTML3View.fxml", "", -1, -1, false);

    }

    public CreateHTML4Controller openCreateHTML4View() throws IOException {
        return (CreateHTML4Controller) showView("view/createHTML4View.fxml", "", -1, -1, false);

    }

    //说话人列表
    public SpeakerInfoTableController openSpeakerInoTableView() throws IOException {
        return (SpeakerInfoTableController) showView("view/speakerInfoTableView.fxml", "", -1, -1, false);

    }

    //调查表元数据信息
    public TableMetadataInfoController openTableMetadataInfoView()throws IOException{
        return (TableMetadataInfoController) showView("view/tableMetadataInfoView.fxml", "", -1, -1, false);

    }





    private Object showView(String resourcePath) throws IOException {
        return showView(resourcePath,"");
    }
    private Object showView(String resourcePath,String title) throws IOException {
        return showView(resourcePath, title, -1, -1, true);
    }

    private Object showView(String resourcePath, String title, double width, double height, boolean open) throws IOException {
        Locale locale;
        if (mLocale!=null){
            locale = mLocale;
        }else {
            locale = Locale.SIMPLIFIED_CHINESE;
        }
        ResourceBundle bundle = ResourceBundle.getBundle("i18n",locale);
        FXMLLoader loader = new FXMLLoader(Main.class.getResource(resourcePath),bundle);
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        Parent root = loader.load();
        final BaseController controller = loader.getController();


        Stage primaryStage = new Stage();

        Scene scene = new Scene(root, width, height);
        if (controller != null) {
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
            controller.setmParent(root);
            controller.setmStage(primaryStage);
        }
        primaryStage.getIcons().add(new Image(
                Main.class.getResourceAsStream("/sample/resource/img/shengfei.png")));

        primaryStage.setScene(scene);
        if (open)
            primaryStage.show();


        return controller;
    }
}
