package sample.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.web.WebView;
import sample.util.ViewUtil;
import sample.util.WidgetUtil;

import javax.swing.text.View;
import java.io.*;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
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
        ViewUtil.getInstance().showView("view/about.fxml","帮助",-1,-1,"abc");
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location,resources);
        System.out.println(location.toString());
        System.out.println(Arrays.toString(resources.keySet().toArray()));

        changeLanguage.setText(ViewUtil.currentLanguage);
    }
}
