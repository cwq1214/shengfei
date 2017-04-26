package sample.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import sample.controller.NewTableView.NewTableView;
import sample.controller.YBCC.YBCCController;
import sample.controller.openTable.OpenTableController;
import sample.controller.openTable.OpenTableListener;
import sample.entity.Table;
import sample.util.*;

import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;

public class MainController extends BaseController {
    @FXML
    public MenuItem tutorial;
    @FXML
    public TabPane contentPane;
    @FXML
    public Label changeLanguage;


    @FXML
    public void ybccClick(){
        int tabIndex = contentPane.getSelectionModel().getSelectedIndex();
        if (tabIndex != -1){
            Tab t = contentPane.getTabs().get(tabIndex);
            if (t.getUserData() !=  null && (t.getUserData() instanceof NewTableView)){
                NewTableView vc = ((NewTableView) t.getUserData());

                YBCCController yvc = ((YBCCController) ViewUtil.getInstance().showView("view/YBCCView.fxml", "音标查错", -1, -1, ""));
                yvc.setAnalyDatas(vc.getOriginDatas());
                yvc.mStage.setResizable(false);
                yvc.mStage.show();

            }
        }
    }

    @FXML
    public void delTableClick(){
        OpenTableController vc = ((OpenTableController) ViewUtil.getInstance().showView("view/openTable.fxml", "删除表", -1, -1, ""));
        vc.setOpen(false);

        vc.mStage.setResizable(false);
        vc.mStage.initModality(Modality.APPLICATION_MODAL);
        vc.mStage.show();
    }

    @FXML
    public void openTableClick(){
        OpenTableController vc = ((OpenTableController) ViewUtil.getInstance().showView("view/openTable.fxml", "打开表", -1, -1, ""));
        vc.setListener(new OpenTableListener() {
            @Override
            public void onOpenTable(Table t) {
                NewTableView vc = ((NewTableView) ViewUtil.getInstance().showView("view/newTableView.fxml", "", -1, -1, t));
                if (t.datatype.equals("0")){
                    vc.setNewType(NewTableView.NewWordType);
                }else if (t.datatype.equals("1")){
                    vc.setNewType(NewTableView.NewCiType);
                }else if (t.datatype.equals("2")){
                    vc.setNewType(NewTableView.NewSentenceType);
                }

                Tab tab = WidgetUtil.createNewTab(t.getTitle(), vc.getmParent());
                tab.setOnClosed(new EventHandler<Event>() {
                    @Override
                    public void handle(Event event) {
                        vc.onTabClosed();
                    }
                });
                WidgetUtil.addTabToTabPane(contentPane, tab,true,vc);
                WidgetUtil.selectTab(tab);
            }
        });
        vc.setOpen(true);

        vc.mStage.setResizable(false);
        vc.mStage.initModality(Modality.APPLICATION_MODAL);
        vc.mStage.show();
    }

    @FXML
    public void newWordTableClick(){
        Table t = new Table("","0","","","","","","","","","","","","","","","");
        DbHelper.getInstance().insertNewTable(t);

        NewTableView vc = ((NewTableView) ViewUtil.getInstance().showView("view/newTableView.fxml", "", -1, -1, t));
        vc.setNewType(NewTableView.NewWordType);

        Tab tab = WidgetUtil.createNewTab(t.getTitle(), vc.getmParent());
        tab.setOnClosed(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                vc.onTabClosed();
            }
        });
        WidgetUtil.addTabToTabPane(contentPane, tab,true,vc);
        WidgetUtil.selectTab(tab);
    }

    @FXML
    public void newCiTableClick(){
        Table t = new Table("","1","","","","","","","","","","","","","","","");
        DbHelper.getInstance().insertNewTable(t);

        NewTableView vc = ((NewTableView) ViewUtil.getInstance().showView("view/newTableView.fxml", "", -1, -1, t));
        vc.setNewType(NewTableView.NewCiType);

        Tab tab = WidgetUtil.createNewTab(t.getTitle(), vc.getmParent());
        tab.setOnClosed(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                vc.onTabClosed();
            }
        });
        WidgetUtil.addTabToTabPane(contentPane, tab,true,vc);
        WidgetUtil.selectTab(tab);
    }


    @FXML
    public void newSentenceTableClick(){
        Table t = new Table("","2","","","","","","","","","","","","","","","");
        DbHelper.getInstance().insertNewTable(t);

        NewTableView vc = ((NewTableView) ViewUtil.getInstance().showView("view/newTableView.fxml", "", -1, -1, t));
        vc.setNewType(NewTableView.NewSentenceType);

        Tab tab = WidgetUtil.createNewTab(t.getTitle(), vc.getmParent());
        tab.setOnClosed(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                vc.onTabClosed();
            }
        });
        WidgetUtil.addTabToTabPane(contentPane, tab,true,vc);
        WidgetUtil.selectTab(tab);
    }

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
            String osName = System.getProperty("os.name");
            File file = new File(Constant.ROOT_FILE_DIR + "/English.xls");
            if (AppCache.getInstance().getOsType()!=0) {
                Runtime.getRuntime().exec("open " + file.getAbsolutePath());
            } else {
                Runtime.getRuntime().exec("cmd /c start " + Constant.ROOT_FILE_DIR + "/English.xls");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onPasswordAnalyClick(){
        BaseController ctl = ((BaseController) ViewUtil.getInstance().showView("view/passwordAnaly.fxml", "密码验证", -1, -1,null));
        ctl.mStage.initModality(Modality.APPLICATION_MODAL);
        ctl.mStage.setResizable(false);
        ctl.mStage.show();
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

        WidgetUtil.addTabToTabPane(contentPane, tab, true);
        WidgetUtil.selectTab(tab);
    }

    public void onCHDCBClick(){
        BaseController controller = null;
        controller = ViewUtil.getInstance().openCHDCBTable();

        Tab tab = WidgetUtil.createNewTab("词汇调查表", controller.getmParent());

        WidgetUtil.addTabToTabPane(contentPane, tab, true);
        WidgetUtil.selectTab(tab);
    }

    public void onJZDCBClick(){
        BaseController controller = null;
        controller = ViewUtil.getInstance().openYFLJRCYJDCB();

        Tab tab = WidgetUtil.createNewTab("句子调查表", controller.getmParent());

        WidgetUtil.addTabToTabPane(contentPane, tab, true);
        WidgetUtil.selectTab(tab);
    }

    public void onHYDCBClick(){
        BaseController controller = null;
        controller = ViewUtil.getInstance().openHYZTBTable();

        Tab tab = WidgetUtil.createNewTab("话语调查表", controller.getmParent());

        WidgetUtil.addTabToTabPane(contentPane, tab, true);
        WidgetUtil.selectTab(tab);
    }

    public void onZGYYDMClick(){
        BaseController controller = null;
        controller = ViewUtil.getInstance().openYYZFDMBTable();

        Tab tab = WidgetUtil.createNewTab("中国语言代码", controller.getmParent());

        WidgetUtil.addTabToTabPane(contentPane, tab, true);
        WidgetUtil.selectTab(tab);
    }

    public void onYYYBZFClick(){
        BaseController controller = null;
        controller = ViewUtil.getInstance().openYYYBTable();

        Tab tab = WidgetUtil.createNewTab("元音音标字符", controller.getmParent());

        WidgetUtil.addTabToTabPane(contentPane, tab, true);
        WidgetUtil.selectTab(tab);
    }

    //说话人列表
    @FXML
    private void onSpeakerInfoTableClick() throws IOException {
        SpeakerInfoTableController speakerInfoTableController = ViewUtil.getInstance().openSpeakerInoTableView();

        Tab tab = WidgetUtil.createNewTab("说话人列表", speakerInfoTableController.getmParent());
        WidgetUtil.addTabToTabPane(contentPane, tab, true);
        WidgetUtil.selectTab(tab);

    }

    //调查表元数据信息
    @FXML
    private void onTableMetadataInfoClick() throws IOException {
        TableMetadataInfoController controller = ViewUtil.getInstance().openTableMetadataInfoView();

        Tab tab = WidgetUtil.createNewTab("调查表元数据信息", controller.getmParent());
        WidgetUtil.addTabToTabPane(contentPane, tab, true);
        WidgetUtil.selectTab(tab);
    }

    //导出网页
    @FXML
    private void onCreateHTMLFileClick()throws IOException{
        CreateHTMLFileController controller = ViewUtil.getInstance().openCreateHTMLFileView();
        controller.getmStage().setAlwaysOnTop(true);
        controller.show();
//        Tab tab = WidgetUtil.createNewTab("调查表元数据信息", controller.getmParent());
//        WidgetUtil.addTabToTabPane(contentPane, tab, true);
//        WidgetUtil.selectTab(tab);
    }

    //录音模式
    @FXML
    private void onRecordModeClick() throws IOException {
        RecordTabController controller = (RecordTabController) ViewUtil.getInstance().openRecordTab();
        Tab tab = WidgetUtil.createNewTab("录制", controller.getmParent());

        tab.setOnClosed(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                System.out.println("close");
                controller.stopPreview();
            }
        });

        WidgetUtil.addTabToTabPane(contentPane, tab, true);
        WidgetUtil.selectTab(tab);

        controller.startPreview();

    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location,resources);
        changeLanguage.setText(ViewUtil.currentLanguage);
    }


    @Override
    public void onStop() {
        super.onStop();

        //关闭窗口时，调用所有tab的onClose方法
        if (contentPane.getTabs().size()!=0){
            for (Tab tab :
                    contentPane.getTabs()) {
                if (tab.getOnClosed()!=null)
                    tab.getOnClosed().handle(null);
            }
        }
    }
}
