package sample.controller;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Window;
import sample.entity.CodeCounty;
import sample.util.Constant;
import sample.util.ViewUtil;
import sample.util.WidgetUtil;

import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;

public class MainController extends BaseController{
    @FXML
    public MenuItem tutorial;
    @FXML
    public TabPane contentPane;
    @FXML
    public Label changeLanguage;

    @FXML
    public void onTutorialClick() throws IOException {
        WebView webView = new WebView();
        StringBuffer stringBuffer = new StringBuffer();
        BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/sample/resource/html/tutorial.html")));
        String line;
        while ((line =reader.readLine())!=null){
            stringBuffer.append(line);
        }
        webView.getEngine().loadContent(stringBuffer.toString());

        Tab tab = WidgetUtil.createNewTab("声飞教程",webView);
//        tab.setText(webView.getEngine().getTitle()+"jiaocheng");
//        tab.setContent(webView);


        WidgetUtil.addTabToTabPane(contentPane,tab);
        WidgetUtil.selectTab(tab);

    }

    @FXML
    public void onEnglish2ChineseClick(){
        try {
            Runtime.getRuntime().exec("cmd /c start "+Constant.ROOT_FILE_DIR+"/English.xls");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onPasswordAnalyClick(){
//        BaseController ctl = ((BaseController) ViewUtil.getInstance().showView("view/passwordAnaly.fxml", "密码验证", -1, -1, null, false));
    }

    @FXML
    public void onNetClick(){
        String netDoMain = "http://www.clarc.cn/";
        WebView webView = new WebView();

        webView.getEngine().load(netDoMain);

        Tab tab = WidgetUtil.createNewTab("语言资源联盟网",webView);

        WidgetUtil.addTabToTabPane(contentPane,tab);
        WidgetUtil.selectTab(tab);
    }

    @FXML
    public void onAboutClick() throws IOException {
//        ViewUtil.getInstance().openAboutDialog();
//        ViewUtil.getInstance().showView("view/about.fxml","帮助",-1,-1,null,true);
    }

    @FXML
    public void onChangeLanguageClick() throws Exception {
        System.out.println("in");
        Locale locale = null;
        if (!changeLanguage.getText().equals("English")){
            locale = Locale.US;
            changeLanguage.setText("English");
            ViewUtil.currentLanguage="English";
            ViewUtil.getInstance().setLocal(locale);
        }else {
            locale = Locale.SIMPLIFIED_CHINESE;
            ViewUtil.getInstance().setLocal(locale);
            changeLanguage.setText("中文");
            ViewUtil.currentLanguage="中文";
        }
        changeLanguage.getScene().getWindow().hide();
        ViewUtil.getInstance().openMainView();
    }

    public void onFYDCZBClick() {
        BaseController controller = null;
        controller = ViewUtil.getInstance().openHYFYDCZBTable();

        Tab tab = WidgetUtil.createNewTab("方言调查字表", controller.getmParent());

        WidgetUtil.addTabToTabPane(contentPane, tab);
        WidgetUtil.selectTab(tab);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location,resources);
        System.out.println(location.toString());
        System.out.println(Arrays.toString(resources.keySet().toArray()));

        changeLanguage.setText(ViewUtil.currentLanguage);
    }
}
