package sample.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import sample.controller.ImportExcel.ImportExcelBindViewController;
import sample.controller.MutiAnaly.AfterAnalyViewController;
import sample.controller.MutiAnaly.MutiAnalyBean;
import sample.controller.MutiAnaly.MutiAnalySelectFileController;
import sample.controller.NewTableView.*;
import sample.controller.RewriteView.RewriteViewController;
import sample.controller.YBCC.YBCCBean;
import sample.controller.YBCC.YBCCController;
import sample.controller.ZlyydView.ZlyydViewController;
import sample.controller.openTable.OpenTableController;
import sample.controller.openTable.OpenTableListener;
import sample.entity.Record;
import sample.entity.Table;
import sample.entity.Topic;
import sample.util.*;

import javax.swing.text.View;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.function.Predicate;

public class MainController extends BaseController {
    @FXML
    public MenuItem tutorial;
    @FXML
    public TabPane contentPane;
    @FXML
    public Label changeLanguage;

    @FXML
    public void tqjzchClick(){
        int index = contentPane.getSelectionModel().getSelectedIndex();
        if (index != -1){
            Tab tab = contentPane.getTabs().get(index);
            if (tab.getUserData() !=  null && tab.getUserData() instanceof NewTableView){
                NewTableView vc = ((NewTableView) tab.getUserData());
                Table t = ((Table) vc.preData);

                if (t.getDatatype().equalsIgnoreCase("2")){
                    TqjzchViewController tvc = ((TqjzchViewController) ViewUtil.getInstance().showView("view/tqjzchView.fxml", "选择格式", -1, -1, ""));
                    tvc.setListener(new TqjzchViewController.TqjzchListener() {
                        @Override
                        public void exportExcel(int format) {
                            ExportUtil.exportSentenceJZCH(t,true,format);
                        }

                        @Override
                        public void exportHtml() {
                            ExportUtil.exportSentenceJZCH(t,false,-1);
                        }
                    });

                    tvc.mStage.initModality(Modality.APPLICATION_MODAL);
                    tvc.mStage.setResizable(false);
                    tvc.mStage.show();
                }
            }
        }
    }

    @FXML
    public void tqjzClick(){

    }

    @FXML
    public void tqchClick(){

    }

    public void openImpExcelBindWithType(int type){
        ImportExcelBindViewController vc = ((ImportExcelBindViewController) ViewUtil.getInstance().showView("view/impExcelBindView.fxml", "导入数据绑定", -1, -1, this));
        vc.setExcelType(type);
        vc.mStage.initModality(Modality.APPLICATION_MODAL);
        vc.mStage.setResizable(false);
        vc.mStage.show();
    }

    @FXML
    public void changeTableNameClick(){
        int nIndex = contentPane.getSelectionModel().getSelectedIndex();
        if (nIndex != -1 ){
            Tab tab = contentPane.getTabs().get(nIndex);
            if (tab.getUserData() != null && (tab.getUserData() instanceof NewTableView)) {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("修改表名");
                dialog.setHeaderText("");
                dialog.setContentText("请输入新表名：");
                Optional<String> result = dialog.showAndWait();
                if (result.isPresent()){
                    Table t = ((Table) ((NewTableView) tab.getUserData()).preData);
                    if (result.get().length()!=0){
                        t.setTitle(result.get());
                        t.setProjectname(result.get());

                        DbHelper.getInstance().addOrUpdateTable(t);

                        tab.setText(result.get());
                    }else {
                        ToastUtil.show("表名不可为空");
                    }
                }
            }
        }
    }

    @FXML
    public void onTableModeClick(){
        int nowIndex = contentPane.getSelectionModel().getSelectedIndex();
        if (nowIndex != -1) {
            Tab t = contentPane.getTabs().get(nowIndex);
            if (t.getUserData() != null && (t.getUserData() instanceof RewriteViewController || t.getUserData() instanceof RecordTabController)) {

                NewTableView nvc = null;
                Table table = null;

                if (t.getUserData() instanceof  RewriteViewController){
                    RewriteViewController vc = ((RewriteViewController) t.getUserData());
                    vc.saveBtnClick();
                    table = ((Table) vc.preData);
                    nvc = ((NewTableView) ViewUtil.getInstance().showView("view/newTableView.fxml", "", -1, -1, vc.preData));
                }else {
                    RecordTabController vc = ((RecordTabController) t.getUserData());
                    table = vc.t;
                    nvc = ((NewTableView) ViewUtil.getInstance().showView("view/newTableView.fxml", "", -1, -1, vc.t));
                    contentPane.getTabs().get(nowIndex).getOnClosed().handle(new Event(Tab.CLOSED_EVENT));
                }

                if (table.datatype.equalsIgnoreCase("0")){
                    nvc.setNewType(NewTableView.NewWordType);
                }else if (table.datatype.equalsIgnoreCase("1")){
                    nvc.setNewType(NewTableView.NewCiType);
                }else if (table.datatype.equalsIgnoreCase("2")){
                    nvc.setNewType(NewTableView.NewSentenceType);
                }

                contentPane.getTabs().remove(nowIndex);

                Tab tab = WidgetUtil.createNewTab(table.title, nvc.getmParent());
                WidgetUtil.addTabToTabPane(contentPane, tab,true,nvc);
                WidgetUtil.selectTab(tab);
            }
        }
    }

    @FXML
    public void onRewriteModeClick(){
        int nowIndex = contentPane.getSelectionModel().getSelectedIndex();
        if (nowIndex != -1) {
            Tab t = contentPane.getTabs().get(nowIndex);
            if (t.getUserData() != null && (t.getUserData() instanceof NewTableView || t.getUserData() instanceof RecordTabController)) {

                RewriteViewController rvc = null;

                if (t.getUserData() instanceof  NewTableView){
                    NewTableView vc = ((NewTableView) t.getUserData());
                    vc.saveBtnClick();
                    rvc = ((RewriteViewController) ViewUtil.getInstance().showView("view/rewriteView.fxml", "", -1, -1, vc.preData));
                }else {
                    RecordTabController vc = ((RecordTabController) t.getUserData());
                    rvc = ((RewriteViewController) ViewUtil.getInstance().showView("view/rewriteView.fxml", "", -1, -1, vc.t));
                    contentPane.getTabs().get(nowIndex).getOnClosed().handle(new Event(Tab.CLOSED_EVENT));
                }

                contentPane.getTabs().remove(nowIndex);

                Tab tab = WidgetUtil.createNewTab("转写模式", rvc.getmParent());
                WidgetUtil.addTabToTabPane(contentPane, tab,true,rvc);
                WidgetUtil.selectTab(tab);
            }
        }

    }

    @FXML
    public void impExlWordClick(){
        openImpExcelBindWithType(0);
    }

    @FXML
    public void impExlCiClick(){
        openImpExcelBindWithType(1);
    }

    @FXML
    public void impSentenceClick(){
        openImpExcelBindWithType(2);
    }

    public void openTable(Table t){
        NewTableView vc = ((NewTableView) ViewUtil.getInstance().showView("view/newTableView.fxml", "", -1, -1, t));
        vc.getmStage().setUserData(MainController.this);
        if (t.datatype.equals("0")){
            vc.setNewType(NewTableView.NewWordType);
        }else if (t.datatype.equals("1")){
            vc.setNewType(NewTableView.NewCiType);
        }else if (t.datatype.equals("2")){
            vc.setNewType(NewTableView.NewSentenceType);
        }

        Tab tab = WidgetUtil.createNewTab(t.getTitle(), vc.getmParent());
        rightClickWithNewTableTab(tab);
        tab.setOnClosed(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                vc.onTabClosed();
            }
        });
        WidgetUtil.addTabToTabPane(contentPane, tab,true,vc);
        WidgetUtil.selectTab(tab);
    }

    private void rightClickWithNewTableTab(Tab tab){
        ContextMenu menu = new ContextMenu();
        MenuItem changeName = new MenuItem("修改表名");
        changeName.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("修改表名");
                dialog.setHeaderText("");
                dialog.setContentText("请输入新表名：");
                Optional<String> result = dialog.showAndWait();
                if (result.isPresent()){
                    Table t = ((Table) ((NewTableView) tab.getUserData()).preData);
                    if (result.get().length()!=0){
                        t.setTitle(result.get());
                        t.setProjectname(result.get());

                        DbHelper.getInstance().addOrUpdateTable(t);

                        tab.setText(result.get());
                    }else {
                        ToastUtil.show("表名不可为空");
                    }
                }
            }
        });
        menu.getItems().add(changeName);
        tab.setContextMenu(menu);
    }

    @FXML
    public void impYbCi(){
        ImportUtil.importYbWithType(1,this);
    }

    @FXML
    public void impYbWord(){
        ImportUtil.importYbWithType(0,this);
    }

    @FXML
    public void impYbSentence(){
        ImportUtil.importYbWithType(2,this);
    }

    //.exb
    @FXML
    public void impExma(){
        try {
            ImportXmlDialog dialog = ViewUtil.getInstance().openImpotXmlDialog("exma");
            dialog.setImportType(1);
            dialog.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //.eaf
    @FXML
    public void impElan(){
        try {
            ImportXmlDialog dialog = ViewUtil.getInstance().openImpotXmlDialog("elan");
            dialog.setImportType(0);
            dialog.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void impAudacity(){
        try {
            ImportXmlDialog dialog = ViewUtil.getInstance().openImpotXmlDialog("Audacity");
            dialog.setImportType(3);
            dialog.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //.xml
    @FXML
    public void impSfXml(){
        try {
            ImportXmlDialog2 dialog = ViewUtil.getInstance().openImportXmlDialog2();
            dialog.show();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @FXML
    public void impTalkTxt(){

    }

    @FXML
    public void dbdzClick(){
        MutiAnalySelectFileController vc = ((MutiAnalySelectFileController) ViewUtil.getInstance().showView("view/mutiTableAnaly.fxml", "多表对照", -1, -1, ""));
        vc.setListener(new MutiAnalySelectFileController.AnalyListener() {
            @Override
            public void finishAnaly(List<MutiAnalyBean> result, List<Table> tbls, int type) {
                AfterAnalyViewController avc = ((AfterAnalyViewController) ViewUtil.getInstance().showView("view/afterAnalyView.fxml", "", -1, -1, ""));
                avc.setResultDatas(FXCollections.observableArrayList(result),tbls,type);

                Tab tab = WidgetUtil.createNewTab("多表对照", avc.getmParent());
                WidgetUtil.addTabToTabPane(contentPane, tab,true,avc);
                WidgetUtil.selectTab(tab);
            }
        });
        vc.mStage.initModality(Modality.APPLICATION_MODAL);
        vc.mStage.setResizable(false);
        vc.mStage.show();
    }

    @FXML
    public void fileExportClick(){
        int tabIndex = contentPane.getSelectionModel().getSelectedIndex();
        if (tabIndex != -1) {
            Tab t = contentPane.getTabs().get(tabIndex);
            if (t.getUserData() != null && (t.getUserData() instanceof NewTableView || t.getUserData() instanceof RecordTabController || t.getUserData() instanceof RewriteViewController)) {
                BaseController vc = ((BaseController) t.getUserData());
                ExportUtil.exportTable(((Table) vc.preData));
            }else if (t.getUserData()!=null&&t.getUserData() instanceof NewTopicEditController){
                NewTopicEditController newTopicEditController  = (NewTopicEditController) t.getUserData();
                ExportUtil.exportTopic(newTopicEditController.getTopics());
            }
        }
    }

    @FXML
    public void newDiscourceClick(){
        NewTopicKindSelectController vc = ((NewTopicKindSelectController) ViewUtil.getInstance().showView("view/newTopicKindSelect.fxml", "选择一个话题", -1, -1, ""));
        vc.setListener(new NewTopicKindSelectListener() {
            @Override
            public void onClickNextStep(TopicBean bean) {
                Table t = new Table("",bean.getCode(),"","","","","","","","","","","","","","","");
                DbHelper.getInstance().insertNewTable(t);

                NewTopicEditController editVC = ((NewTopicEditController) ViewUtil.getInstance().showView("view/newTopicEditView.fxml", "话题表", -1, -1, ""));
                editVC.setBaseMsg(bean.getCode(),t.getId());


                Tab tab = WidgetUtil.createNewTab(t.getTitle(), editVC.getmParent());
                tab.setOnClosed(new EventHandler<Event>() {
                    @Override
                    public void handle(Event event) {
                        vc.onTabClosed();
                    }
                });
                WidgetUtil.addTabToTabPane(contentPane, tab,true,editVC);
                WidgetUtil.selectTab(tab);
            }
        });

        vc.mStage.setResizable(false);
        vc.mStage.initModality(Modality.APPLICATION_MODAL);
        vc.mStage.show();
    }

    @FXML
    public void tyzhchClick(){
        int tabIndex = contentPane.getSelectionModel().getSelectedIndex();
        if (tabIndex != -1){
            Tab t = contentPane.getTabs().get(tabIndex);
            if (t.getUserData() !=  null && (t.getUserData() instanceof NewTableView)){
                NewTableView vc = ((NewTableView) t.getUserData());
                if (vc.getNewType() == NewTableView.NewSentenceType || vc.getNewType()== NewTableView.NewHuaYuType){
                    return;
                }

                //已经进行过音标差错
                if (vc.isCanZlyyd()){
                    System.out.println("can zlyyd");
                    ZlyydViewController zVc = ((ZlyydViewController) ViewUtil.getInstance().showView("view/zlyydView.fxml", "整理音韵调", -1, -1, vc.preData));
                    zVc.setZLYYD(false);
                    zVc.setOriginDatas(vc.getAfterAnalyDatas());
                    zVc.mStage.initModality(Modality.APPLICATION_MODAL);
                    zVc.mStage.setResizable(false);
                    zVc.mStage.show();
                }else{
                    DialogUtil.showDialog("请先执行音标差错！");
                }
            }
        }
    }

    @FXML
    public void zlyydClick(){
        int tabIndex = contentPane.getSelectionModel().getSelectedIndex();
        if (tabIndex != -1){
            Tab t = contentPane.getTabs().get(tabIndex);
            if (t.getUserData() !=  null && (t.getUserData() instanceof NewTableView)){
                NewTableView vc = ((NewTableView) t.getUserData());

                //已经进行过音标差错
                if (vc.isCanZlyyd()){
                    System.out.println("can zlyyd");
                    ZlyydViewController zVc = ((ZlyydViewController) ViewUtil.getInstance().showView("view/zlyydView.fxml", "整理音韵调", -1, -1, vc.preData));
                    zVc.setZLYYD(true);
                    zVc.setOriginDatas(vc.getAfterAnalyDatas());
                    zVc.mStage.initModality(Modality.APPLICATION_MODAL);
                    zVc.mStage.setResizable(false);
                    zVc.mStage.show();
                }else{
                    DialogUtil.showDialog("请先执行音标差错！");
                }
            }
        }
    }

    @FXML
    public void ybccClick(){
        int tabIndex = contentPane.getSelectionModel().getSelectedIndex();
        if (tabIndex != -1){
            Tab t = contentPane.getTabs().get(tabIndex);
            if (t.getUserData() !=  null && (t.getUserData() instanceof NewTableView)){
                NewTableView vc = ((NewTableView) t.getUserData());
                vc.saveBtnClick();
                vc.setCanZlyyd(true);

                YBCCController yvc = ((YBCCController) ViewUtil.getInstance().showView("view/YBCCView.fxml", "音标查错", -1, -1, ""));
                yvc.newTableViewVC = vc;
                yvc.setTbVC(vc);
                yvc.setAnalyDatas(vc.getOriginDatas());
                yvc.mStage.setResizable(false);
                yvc.mStage.initModality(Modality.APPLICATION_MODAL);
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
                //判断是否话题表
                if (Integer.parseInt(t.datatype) >= 3){
                    NewTopicEditController editVC = ((NewTopicEditController) ViewUtil.getInstance().showView("view/newTopicEditView.fxml", "话题表", -1, -1, ""));
                    editVC.setBaseMsg(t.datatype,t.getId());

                    Tab tab = WidgetUtil.createNewTab(t.getTitle(), editVC.getmParent());
                    tab.setOnClosed(new EventHandler<Event>() {
                        @Override
                        public void handle(Event event) {
                            vc.onTabClosed();
                        }
                    });
                    WidgetUtil.addTabToTabPane(contentPane, tab,true,editVC);
                    WidgetUtil.selectTab(tab);
                }else {
                    NewTableView vc = ((NewTableView) ViewUtil.getInstance().showView("view/newTableView.fxml", "", -1, -1, t));
                    vc.getmStage().setUserData(MainController.this);
                    if (t.datatype.equals("0")){
                        vc.setNewType(NewTableView.NewWordType);
                    }else if (t.datatype.equals("1")){
                        vc.setNewType(NewTableView.NewCiType);
                    }else if (t.datatype.equals("2")){
                        vc.setNewType(NewTableView.NewSentenceType);
                    }

                    Tab tab = WidgetUtil.createNewTab(t.getTitle(), vc.getmParent());
                    rightClickWithNewTableTab(tab);
                    tab.setOnClosed(new EventHandler<Event>() {
                        @Override
                        public void handle(Event event) {
                            vc.onTabClosed();
                        }
                    });
                    WidgetUtil.addTabToTabPane(contentPane, tab,true,vc);
                    WidgetUtil.selectTab(tab);
                }
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
        vc.getmStage().setUserData(MainController.this);
        vc.setNewType(NewTableView.NewWordType);

        Tab tab = WidgetUtil.createNewTab(t.getTitle(), vc.getmParent());
        rightClickWithNewTableTab(tab);
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
        vc.getmStage().setUserData(MainController.this);
        vc.setNewType(NewTableView.NewCiType);

        Tab tab = WidgetUtil.createNewTab(t.getTitle(), vc.getmParent());
        rightClickWithNewTableTab(tab);
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
        vc.getmStage().setUserData(MainController.this);
        vc.setNewType(NewTableView.NewSentenceType);

        Tab tab = WidgetUtil.createNewTab(t.getTitle(), vc.getmParent());
        rightClickWithNewTableTab(tab);
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
        controller.mStage.initModality(Modality.APPLICATION_MODAL);
        controller.show();
//        Tab tab = WidgetUtil.createNewTab("调查表元数据信息", controller.getmParent());
//        WidgetUtil.addTabToTabPane(contentPane, tab, true);
//        WidgetUtil.selectTab(tab);
    }

    //录音模式
    @FXML
    private void onRecordModeClick() throws IOException {
        int nowIndex = contentPane.getSelectionModel().getSelectedIndex();
        if (nowIndex != -1){
            Tab t = contentPane.getTabs().get(nowIndex);
            if (t.getUserData() !=  null && ((t.getUserData() instanceof NewTableView) || t.getUserData() instanceof RewriteViewController)) {

                RecordTabController controller = (RecordTabController) ViewUtil.getInstance().openRecordTab();

                if (t.getUserData() instanceof NewTableView){
                    //获取controller，先保存数据，然后关闭
                    NewTableView vc = ((NewTableView) t.getUserData());
                    vc.saveBtnClick();

                    controller.t = ((Table) vc.preData);
                    controller.tableType = ((Table) vc.preData).getDatatype();
                }else{
                    RewriteViewController vc = ((RewriteViewController) t.getUserData());
                    vc.saveBtnClick();

                    controller.t = ((Table) vc.preData);
                    controller.tableType = ((Table) vc.preData).getDatatype();
                }

                contentPane.getTabs().remove(nowIndex);

                Tab tab = WidgetUtil.createNewTab("录制", controller.getmParent());

                tab.setOnClosed(new EventHandler<Event>() {
                    @Override
                    public void handle(Event event) {
                        System.out.println("close");
                        controller.stopPreview();
                    }
                });

                WidgetUtil.addTabToTabPane(contentPane, tab, true,controller);
                WidgetUtil.selectTab(tab);

                controller.startPreview();
            }

        }
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
