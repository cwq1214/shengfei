package sample.controller.NewTableView;

import com.sun.javafx.scene.control.skin.TableViewSkin;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;
import sample.Main;
import sample.controller.BaseController;
import sample.controller.MainController;
import sample.controller.ReplaceViewController;
import sample.controller.SearchViewController;
import sample.controller.UnionCode.UnionCodeController;
import sample.controller.YBCC.YBCCBean;
import sample.controller.widget.VideoPlayer;
import sample.controller.ybzf.YBZFController;
import sample.controller.ybzf.YBZFListener;
import sample.diycontrol.TableTopCtl.ClickType;
import sample.diycontrol.TableTopCtl.TableTopControl;
import sample.diycontrol.TableTopCtl.TableTopCtlListener;
import sample.entity.Record;
import sample.entity.Table;
import sample.util.*;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by Bee on 2017/4/13.
 */
public class NewTableView extends BaseController {
    public static final int NewWordType = 0;
    public static final int NewCiType =  1;
    public static final int NewSentenceType =  2;
    public static final int NewHuaYuType = 3;


    TableColumn<YBCCBean,String> hideCol = new TableColumn<>("条目筛选");
    TableColumn<YBCCBean,String> doneCol = new TableColumn<>("录音状态");
    TableColumn<YBCCBean,String> codeCol = new TableColumn<>("编码");
    TableColumn<YBCCBean,String> rankCol = new TableColumn<>("分级");
    TableColumn<YBCCBean,String> contentCol = new TableColumn<>("单字");
    TableColumn<YBCCBean,String> yunCol = new TableColumn<>("音韵");
    TableColumn<YBCCBean,String> IPACol = new TableColumn<>("音标注音");
    TableColumn<YBCCBean,String> spellCol = new TableColumn<>("拼音");
    TableColumn<YBCCBean,String> englishCol = new TableColumn<>("英语");
    TableColumn<YBCCBean,String> noteCol = new TableColumn<>("注释");
    TableColumn<YBCCBean,String> recordDateCol = new TableColumn<>("录音日期");
    TableColumn<YBCCBean,String> mwfyCol = new TableColumn<>("民族文字或方言字");
    TableColumn<YBCCBean,String> duiyiCol = new TableColumn<>("普通话词对译");

    private int newType;

    private ContextMenu contextMenu;

    private ObservableList tableDatas;

    private ObservableList originDatas;

    private boolean canZlyyd = false;

    private ObservableList afterAnalyDatas;

    private MyTFCell nowEditCell;

    @FXML
    private VideoPlayer mediaView;

    @FXML
    private ImageView imgView;

    @FXML
    private TableTopControl tableTopCtl;

    @FXML
    private TableView tableView;

    public ObservableList getOriginDatas() {
        return originDatas;
    }

    public void refreshTableView(){
        tableView.refresh();
    }

    public boolean isCanZlyyd() {
        return canZlyyd;
    }

    public void setCanZlyyd(boolean canZlyyd) {
        this.canZlyyd = canZlyyd;
    }

    public ObservableList getAfterAnalyDatas() {
        return afterAnalyDatas;
    }

    public void setAfterAnalyDatas(ObservableList afterAnalyDatas) {
        System.out.println("aaaa:"+afterAnalyDatas.size());
        this.afterAnalyDatas = FXCollections.observableArrayList(afterAnalyDatas);
    }

    public void setSelectWithBean(YBCCBean bean){
        for (int i = 0; i < tableDatas.size(); i++) {
            YBCCBean b = ((YBCCBean) tableDatas.get(i));
            if (b.getRecord().getBaseCode().equalsIgnoreCase(bean.getRecord().getBaseCode())){
                tableView.getSelectionModel().select(i);
                tableView.scrollTo(i);
                break;
            }
        }
    }

    public TableView getTableView() {
        return tableView;
    }

    public TableColumn searchColumn(ContextMenu menu){
        for (int i = 0; i < tableView.getColumns().size(); i++) {
            if (((TableColumn) tableView.getColumns().get(i)).contextMenuProperty().get().equals(menu)){
                return ((TableColumn) tableView.getColumns().get(i));
            }
        }
        return null;
    }

    public ContextMenu setupHeaderMenu(boolean sort,boolean change,boolean hide,boolean search,boolean replace){
        ContextMenu headerMenu = new ContextMenu();
        MenuItem sortItem = new MenuItem("记录排序");
        MenuItem changeItem = new MenuItem("修改列");
        MenuItem hideItem = new MenuItem("隐藏列");
        MenuItem searchItem = new MenuItem("在当前列查找");
        MenuItem replaceItem = new MenuItem("在当前列替换");
        MenuItem showHideItem = new MenuItem("显示隐藏列");

        sortItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                TableColumn c = searchColumn(headerMenu);
                if (tableView.getSortOrder().size() == 0){
                    c.setSortType(TableColumn.SortType.ASCENDING);
                    tableView.getSortOrder().setAll(c);
                }else{
                    if (tableView.getSortOrder().get(0).equals(c)){
                        if (c.getSortType() == TableColumn.SortType.ASCENDING){
                            c.setSortType(TableColumn.SortType.DESCENDING);
                            tableView.getSortOrder().setAll(c);
                        }else {
                            tableView.getSortOrder().clear();
                        }
                    }else{
                        c.setSortType(TableColumn.SortType.ASCENDING);
                        tableView.getSortOrder().setAll(c);
                    }
                }
                tableView.sort();
            }
        });
        changeItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                TableColumn c = searchColumn(headerMenu);
                System.out.println(c.getText());
            }
        });
        hideItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                TableColumn c = searchColumn(headerMenu);
                tableView.getColumns().remove(c);
            }
        });
        searchItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    SearchViewController searchViewController = (SearchViewController) ViewUtil.getInstance().openSearchDialog();
                    List columnsName = getTableColumnNameList();
                    searchViewController.setSearchTableTitleName(columnsName);
                    searchViewController.setSearchTableTitleShowIndex(tableView.getColumns().indexOf(searchColumn(headerMenu)));
                    searchViewController.setOnDoneClickCallback(new SearchViewController.OnDoneClickCallback() {
                        @Override
                        public void onClick(SearchViewController controller) {
                            String inputText = controller.getInputText();
                            boolean isAccurate = controller.isChecked();
                            int index = controller.getSelTableTitleIndex();

                            Pattern pattern = Pattern.compile("[,，;；]");
                            String[] ips = pattern.split(inputText);

                            TableColumn col = ((TableColumn) tableView.getColumns().get(index));

                            List<YBCCBean> temp = new ArrayList<>();

                            for (int i = 0; i < tableDatas.size(); i++) {
                                YBCCBean bean = ((YBCCBean) tableDatas.get(i));
                                String colData = ((StringProperty) col.getCellValueFactory().call(new TableColumn.CellDataFeatures<>(tableView, col, tableDatas.get(i)))).get();
                                if (isAccurate){
                                    for (String s : ips) {
                                        if (colData.contains(s)){
                                            temp.add(bean);
                                            break;
                                        }
                                    }
                                }else{
                                    for (String s : ips) {
                                        Pattern p = Pattern.compile(getMHSearRegEx(s));
                                        if (p.matcher(colData).find()){
                                            temp.add(bean);
                                            break;
                                        }
                                    }
                                }
                            }

                            tableDatas = FXCollections.observableArrayList(temp);
                            tableView.setItems(tableDatas);
                            tableView.refresh();
                            searchViewController.close();
                        }
                    });
                    searchViewController.show();
                }catch (Exception e){

                }

            }
        });
        replaceItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try{
                    ReplaceViewController controller = (ReplaceViewController) ViewUtil.getInstance().openReplaceDialog();
                    controller.setChoiceBoxItems(getTableColumnNameList());
                    controller.setTableNameShowIndex(tableView.getColumns().indexOf(searchColumn(headerMenu)));
                    controller.setOnDoneClickCallback(new ReplaceViewController.OnDoneClickCallback() {
                        @Override
                        public void click(ReplaceViewController controller) {
                            String replaceContent = controller.getInputReplaceContent();
                            String searchContent = controller.getInputSearchContent();
                            boolean isChecked = controller.getIsSelCheckBox();
                            int index = controller.getChoiceBoxSelItemIndex();

                            TableColumn col = ((TableColumn) tableView.getColumns().get(index));

                            for (int i = 0; i < tableDatas.size(); i++) {
                                String colData = ((StringProperty) col.getCellValueFactory().call(new TableColumn.CellDataFeatures<>(tableView, col, tableDatas.get(i)))).get();
                                if (isChecked){
                                    if (!colData.equalsIgnoreCase(searchContent)){
                                        continue;
                                    }
                                }
                                col.getOnEditCommit().handle(new TableColumn.CellEditEvent<>(tableView,new TablePosition<>(tableView,i,col),TableColumn.editCommitEvent() ,colData.replaceAll(searchContent,replaceContent)));
                            }

                            tableView.refresh();

                        }
                    });
                    controller.show();
                }catch (Exception e){

                }
            }
        });
        showHideItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                setupAllColumn();
            }
        });

        sortItem.setDisable(!sort);
        changeItem.setDisable(!change);
        hideItem.setDisable(!hide);
        showHideItem.setDisable(!hide);
        searchItem.setDisable(!search);
        replaceItem.setDisable(!replace);

        headerMenu.getItems().addAll(sortItem,hideItem,searchItem,replaceItem,showHideItem);
        return headerMenu;
    }

    public void setupRowMenu(){
        //设置contextMenu内容
        contextMenu = new ContextMenu();
        MenuItem addRecord = new MenuItem("新增记录");
        MenuItem delRecord = new MenuItem("删除记录");
        MenuItem hideRecord = new MenuItem("隐藏记录");
        MenuItem keepRecord = new MenuItem("保留记录");
        MenuItem showYBZF = new MenuItem("音标字符");
        MenuItem importDemoVideo = new MenuItem("导入演示视频");
        MenuItem importDemoPic = new MenuItem("导入演示图片");
        MenuItem delDemoVideo = new MenuItem("删除演示视频");
        MenuItem delDemoPic = new MenuItem("删除演示图片");

        addRecord.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                addBtnClick();
            }
        });

        delRecord.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                delBtnClick();
            }
        });

        hideRecord.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                disapperBtnClick();
            }
        });

        keepRecord.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                keepBtnClick();
            }
        });

        showYBZF.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                YBZFController vc = ((YBZFController) ViewUtil.getInstance().showView("view/ybzfView.fxml", "音标面板", -1, -1, ""));
                vc.setListener(new YBZFListener() {
                    @Override
                    public void btnClickWithText(String str) {
                        nowEditCell.getTextField().setText(nowEditCell.getTextField().getText()+str);
                    }

                    @Override
                    public void okBtnClick() {
                        nowEditCell.commitEdit(nowEditCell.getTextField().getText());
                    }
                });
                vc.mStage.setResizable(false);
                vc.mStage.show();
            }
        });
        importDemoPic.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                YBCCBean bean = (YBCCBean) tableDatas.get(tableView.getSelectionModel().getSelectedIndex());
                int oIndex = originDatas.indexOf(bean);

                FileChooser fc = new FileChooser();
                fc.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("All Image","*.*"),
                        new FileChooser.ExtensionFilter("JPG","*.jpg"),
                        new FileChooser.ExtensionFilter("PNG","*.png")
                );
                File selectFile = fc.showOpenDialog(mStage);
                File ourFile = copyFile2Path(selectFile, bean.getRecord(),false);
                if (ourFile != null){
                    bean.setDemoPicLoc(ourFile.getAbsolutePath());
                    ((YBCCBean) originDatas.get(oIndex)).setDemoPicLoc(ourFile.getAbsolutePath());

                    try {
                        FileInputStream fip = new FileInputStream(ourFile);Image img = new Image(fip);
                        imgView.setImage(img);
                        fip.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
        importDemoVideo.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                YBCCBean bean = (YBCCBean) tableDatas.get(tableView.getSelectionModel().getSelectedIndex());
                int oIndex = originDatas.indexOf(bean);

                FileChooser fc = new FileChooser();
                fc.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("MP4","*.mp4")
                );
                File selectFile = fc.showOpenDialog(mStage);
                File ourFile = copyFile2Path(selectFile, bean.getRecord(),true);
                if (ourFile != null){
                    bean.setDemoVideoLoc(ourFile.getAbsolutePath());
                    ((YBCCBean) originDatas.get(oIndex)).setDemoVideoLoc(ourFile.getAbsolutePath());

                    mediaView.setMediaPath(ourFile.getAbsolutePath());
                }
            }
        });
        delDemoPic.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                YBCCBean bean = (YBCCBean) tableDatas.get(tableView.getSelectionModel().getSelectedIndex());
                int oIndex = originDatas.indexOf(bean);

                imgView.setImage(null);
                delFile(bean.getDemoPicLoc());

                bean.setDemoPicLoc("");
                ((YBCCBean) originDatas.get(oIndex)).setDemoPicLoc("");
            }
        });
        delDemoVideo.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                YBCCBean bean = (YBCCBean) tableDatas.get(tableView.getSelectionModel().getSelectedIndex());
                int oIndex = originDatas.indexOf(bean);

                mediaView.reset();
                delFile(bean.getDemoVideoLoc());

                bean.setDemoVideoLoc("");
                ((YBCCBean) originDatas.get(oIndex)).setDemoVideoLoc("");
            }
        });
        contextMenu.getItems().addAll(addRecord,delRecord,hideRecord,keepRecord,showYBZF,importDemoPic,importDemoVideo,delDemoPic,delDemoVideo);
    }

    private void delFile(String path){
        File f = new File(path);
        System.out.println(f.delete());
    }

    private File copyFile2Path (File f ,Record r,boolean isVideo){

        String fileType = f.getName().substring(f.getName().lastIndexOf("."));
        String dir = isVideo?Constant.DEMO_Video_DIR:Constant.DEMO_PIC_DIR;

        File dirF = new File(dir);
        if (!dirF.exists()){
            dirF.mkdir();
        }

        String path = dir + "/" + r.getUuid() + fileType;
        FileUtil.fileCopy(f.getAbsolutePath(),path);
        return new File(path);
    }

    @Override
    public void prepareInit() {
        super.prepareInit();

        setupRowMenu();

        tableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                int nowIndex = tableView.getSelectionModel().getSelectedIndex();
                if (nowIndex != -1){
                    tableTopCtl.setNowIndex(nowIndex);

                    String demoP = ((YBCCBean) tableView.getItems().get(nowIndex)).getDemoPicLoc();
                    String demoV = ((YBCCBean) tableView.getItems().get(nowIndex)).getDemoVideoLoc();

                    if (demoP != null && demoP.length() != 0){
                        try {
                            FileInputStream fip = new FileInputStream(demoP);
                            Image img = new Image(fip);
                            imgView.setImage(img);
                            fip.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else {
                        imgView.setImage(null);
                    }
                    if (demoV != null && demoV.length() != 0){
                        mediaView.setMediaPath(demoV);
                    }else {
                        mediaView.setMediaPath("");
                    }

                }
                if (event.getButton() == MouseButton.SECONDARY){
                    setContextMenuContent(false);
                    tableView.setContextMenu(contextMenu);
                }
            }
        });

        tableView.setEditable(true);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        tableTopCtl.setBtnClickListener(new TableTopCtlListener() {
            @Override
            public void onBtnClick(ClickType ct) {
                switch (ct){
                    case FirstBtnClick:
                        tableView.getSelectionModel().clearSelection();
                        tableView.getSelectionModel().select(0);
                        tableView.scrollTo(tableView.getSelectionModel().getSelectedIndex());
                        tableTopCtl.setNowIndex(tableView.getSelectionModel().getSelectedIndex());
                        break;
                    case PreBtnClick: {
                        int nowIndex = tableView.getSelectionModel().getSelectedIndex();
                        tableView.getSelectionModel().clearSelection();
                        if (nowIndex <= 0) {
                            tableView.getSelectionModel().select(0);
                        } else {
                            tableView.getSelectionModel().select(nowIndex - 1);
                        }
                        tableView.scrollTo(tableView.getSelectionModel().getSelectedIndex());
                        tableTopCtl.setNowIndex(tableView.getSelectionModel().getSelectedIndex());
                        break;
                    }
                    case NextBtnClick: {
                        int nowIndex = tableView.getSelectionModel().getSelectedIndex();
                        tableView.getSelectionModel().clearSelection();
                        if (nowIndex >= tableDatas.size() - 1) {
                            tableView.getSelectionModel().select(tableDatas.size() - 1);
                        } else {
                            tableView.getSelectionModel().select(nowIndex+1);
                        }
                        tableView.scrollTo(tableView.getSelectionModel().getSelectedIndex());
                        tableTopCtl.setNowIndex(tableView.getSelectionModel().getSelectedIndex());
                        break;
                    }
                    case LastBtnClick:
                        tableView.getSelectionModel().clearSelection();
                        tableView.getSelectionModel().select(tableDatas.size()-1);
                        tableView.scrollTo(tableView.getSelectionModel().getSelectedIndex());
                        tableTopCtl.setNowIndex(tableView.getSelectionModel().getSelectedIndex());
                        break;
                    case RefreshBtnClick:
                        tableDatas = FXCollections.observableArrayList(originDatas);
                        tableView.setItems(tableDatas);
                        tableView.refresh();
                        break;
                    case SelectAllBtnClick:
                        tableView.getSelectionModel().selectAll();
                        break;
                    case SelectAnotherBtnClick:{
                        ObservableList postions = tableView.getSelectionModel().getSelectedIndices();
                        for (int i = 0;i<tableDatas.size();i++){
                            if (postions.indexOf(i) != -1){
                                tableView.getSelectionModel().clearSelection(i);
                            }else{
                                tableView.getSelectionModel().select(i);
                            }
                        }
                        break;
                    }
                    case KeepBtnClick: {
                        keepBtnClick();
                        break;
                    }
                    case DisappearBtnClick: {
                        disapperBtnClick();
                        break;
                    }
                    case DelBtnClick: {
                        delBtnClick();
                        break;
                    }
                    case ShowAllBtnClick:
                        tableDatas = FXCollections.observableArrayList(originDatas);
                        tableView.setItems(tableDatas);
                        tableView.getSelectionModel().clearSelection();
                        tableView.getSelectionModel().select(0);
                        tableTopCtl.setNowIndex(0);
                        tableView.refresh();
                        break;
                    case ShowOnlyKeepBtnClick:
                        tableDatas = FXCollections.observableArrayList(originDatas.filtered(new Predicate<YBCCBean>(){
                            @Override
                            public boolean test(YBCCBean bean) {
                                if (Integer.parseInt(bean.getRecord().hide) == 0){
                                    return true;
                                }
                                return false;
                            }
                        }));
                        tableView.setItems(tableDatas);
                        tableView.getSelectionModel().clearSelection();
                        tableView.getSelectionModel().select(0);
                        tableTopCtl.setNowIndex(0);
                        tableView.refresh();
                        break;
                    case ShowOnlyDisappearBtnClick:
                        tableDatas = FXCollections.observableArrayList(originDatas.filtered(new Predicate<YBCCBean>(){
                            @Override
                            public boolean test(YBCCBean bean) {
                                if (Integer.parseInt(bean.getRecord().hide) == 1){
                                    return true;
                                }
                                return false;
                            }
                        }));
                        tableView.setItems(tableDatas);
                        tableView.getSelectionModel().clearSelection();
                        tableView.getSelectionModel().select(0);
                        tableTopCtl.setNowIndex(0);
                        tableView.refresh();
                        break;
                    case SaveBtnClick:
                        saveBtnClick();
                        break;
                    case UnionBtnClick:
                        saveBtnClick();
                        unionBtnClick();
                        break;
                }
            }
        });
    }

    public void unionBtnClick(){
        MainController mainVC = ((MainController) mStage.getUserData());
        Table t = ((Table) preData);

        UnionCodeController vc = ((UnionCodeController) ViewUtil.getInstance().showView("view/unionCodeView.fxml", "", -1, -1, ""));
        vc.setNowTable(t);

        mainVC.contentPane.getTabs().remove(mainVC.contentPane.getSelectionModel().getSelectedIndex());

        Tab tab = WidgetUtil.createNewTab(t.getTitle()+"-统一编码", vc.getmParent());
        WidgetUtil.addTabToTabPane(mainVC.contentPane, tab,true,vc);
        WidgetUtil.selectTab(tab);
    }

    public void saveBtnClick(){
        DbHelper.getInstance().insertRecord(originDatas);
    }

    public void addBtnClick(){
        int nowIndex = tableView.getSelectionModel().getSelectedIndex();
        YBCCBean ybccBean = (YBCCBean) tableDatas.get(nowIndex);
        Record selectR = ybccBean.getRecord();
        int oIndex = originDatas.indexOf(ybccBean);

        String regEx = "^[A-Z0-9]*[a-z]+$";
        Pattern pattern = Pattern.compile(regEx);

        Matcher matcher = pattern.matcher(selectR.getBaseCode());

        if (!matcher.matches()){
            //上面是基础记录
            String newBaseCode = getBiggestBaseCode(selectR.getBaseCode());
            String newInvCode = newBaseCode;
            Record newR = new Record(selectR.getBaseId(),newInvCode,newBaseCode,"0","0","","","","","","","","","","");
            YBCCBean newBean = new YBCCBean(newR);

            tableDatas.add(nowIndex+1,newBean);
            originDatas.add(oIndex+1,newBean);
            tableView.refresh();

        }else{
            String nRegEx = "^[A-Z0-9]*";
            Pattern nPattern = Pattern.compile(nRegEx);
            String[] results = nPattern.split(selectR.getBaseCode());

            int suffixBeginIndex = selectR.getBaseCode().length() - results[1].length();

            //上面是新增加的记录
            String biggestBaseCode = getBiggestBaseCode(selectR.getBaseCode().substring(0,suffixBeginIndex));

            Record newR = new Record(selectR.getBaseId(),biggestBaseCode,biggestBaseCode,"0","0","","","","","","","","","","");
            YBCCBean newBean = new YBCCBean(newR);

            tableDatas.add(nowIndex+1,newBean);
            originDatas.add(oIndex+1,newBean);
            tableView.refresh();
        }
    }

    public String getBiggestBaseCode(String prefix){
        //最大的basecode
        String biggest = "";
        for (Object o: originDatas) {
            Record temp = ((YBCCBean) o).getRecord();
            String baseCode = temp.getBaseCode();
            //假如basecode包含prefix这个前缀
            if (baseCode.contains(prefix)){
                //得到他的后缀
                String suffix = baseCode.substring(prefix.length());
                if (suffix.compareTo(biggest)>=0){
                    biggest = suffix;
                }
            }
        }

        if (biggest.length()!=0) {
            Character t = biggest.charAt(biggest.length()-1);


            //最后字母在a-z之间，不需要增加一个
            if (t.compareTo('a')>=0 && t.compareTo('z')<0){
                t = ((char) (((int) t) + 1));
                biggest = biggest.substring(0,biggest.length()-1) + t;
                biggest = prefix + biggest;
            }else{
                biggest = prefix + biggest + 'a';
            }
        }else
        {
            biggest = prefix + 'a';
        }

        return biggest;
    }

    public void delBtnClick(){
        DbHelper.getInstance().delRecord(tableView.getSelectionModel().getSelectedItems());
        originDatas.removeAll(tableView.getSelectionModel().getSelectedItems());
        tableView.getItems().removeAll(tableView.getSelectionModel().getSelectedItems());
        tableView.refresh();
    }

    public void keepBtnClick(){
        ObservableList postions = tableView.getSelectionModel().getSelectedIndices();
        for (int i = 0 ;i<postions.size();i++){
            int nowIndex = ((Integer) postions.get(i));
            if (nowIndex != -1){
                int oIndex = originDatas.indexOf(tableDatas.get(nowIndex));
                ((YBCCBean) tableView.getItems().get(nowIndex)).getRecord().setHide("0");
                ((YBCCBean) originDatas.get(oIndex)).getRecord().setHide("0");
            }
        }
        tableView.refresh();
    }

    public void disapperBtnClick(){
        ObservableList postions = tableView.getSelectionModel().getSelectedIndices();
        for (int i = 0 ;i<postions.size();i++){
            int nowIndex = ((Integer) postions.get(i));
            if (nowIndex != -1){
                int oIndex = originDatas.indexOf(tableDatas.get(nowIndex));
                ((YBCCBean) tableView.getItems().get(nowIndex)).getRecord().setHide("1");
                ((YBCCBean) originDatas.get(oIndex)).getRecord().setHide("1");
            }
        }
        tableView.refresh();
    }

    public void setContextMenuContent(boolean isTextField){
        contextMenu.getItems().get(0).setDisable(isTextField);
        contextMenu.getItems().get(1).setDisable(isTextField);
        contextMenu.getItems().get(2).setDisable(isTextField);
        contextMenu.getItems().get(3).setDisable(isTextField);
        contextMenu.getItems().get(4).setDisable(!isTextField);
        if (tableView.getSelectionModel().getSelectedIndices().size() != 1){
            contextMenu.getItems().get(0).setDisable(true);
        }else{
            String demoV = ((YBCCBean) tableDatas.get(tableView.getSelectionModel().getSelectedIndex())).getDemoVideoLoc();
            String demoP = ((YBCCBean) tableDatas.get(tableView.getSelectionModel().getSelectedIndex())).getDemoPicLoc();

//            contextMenu.getItems().get(5).setDisable(!(demoV == null || demoV.length() == 0));
//            contextMenu.getItems().get(6).setDisable(!(demoV == null || demoV.length() == 0));
            contextMenu.getItems().get(7).setDisable(demoP == null || demoP.length() == 0);
            contextMenu.getItems().get(8).setDisable(demoV == null || demoV.length() == 0);
        }
    }

    public int getNewType() {
        return newType;
    }

    public void setNewType(int newType){
        this.newType = newType;

        Callback<TableColumn<YBCCBean,String>,TableCell<YBCCBean,String>> callback = (TableColumn<YBCCBean,String> col) -> new MyTFCell(new DefaultStringConverter());


        IPACol.setId("test");


        //设置header contextMenu
        hideCol.setContextMenu(setupHeaderMenu(true,true,true,true,true));
        doneCol.setContextMenu(setupHeaderMenu(true,false,true,false,false));
        codeCol.setContextMenu(setupHeaderMenu(true,true,true,true,true));
        rankCol.setContextMenu(setupHeaderMenu(true,true,true,true,true));
        contentCol.setContextMenu(setupHeaderMenu(true,true,true,true,true));
        yunCol.setContextMenu(setupHeaderMenu(true,true,true,true,true));
        IPACol.setContextMenu(setupHeaderMenu(true,true,true,true,true));
        spellCol.setContextMenu(setupHeaderMenu(true,true,true,true,true));
        englishCol.setContextMenu(setupHeaderMenu(true,true,true,true,true));
        noteCol.setContextMenu(setupHeaderMenu(true,true,true,true,true));
        recordDateCol.setContextMenu(setupHeaderMenu(true,true,true,true,true));
        mwfyCol.setContextMenu(setupHeaderMenu(true,true,true,true,true));
        duiyiCol.setContextMenu(setupHeaderMenu(true,true,true,true,true));


        //设置单元格编辑权限
        hideCol.setEditable(false);
        doneCol.setEditable(false);
//        codeCol.setEditable(false);
        recordDateCol.setEditable(false);

        //设置单元格类型
        hideCol.setCellFactory(new Callback<TableColumn<YBCCBean, String>, TableCell<YBCCBean, String>>() {
            @Override
            public TableCell<YBCCBean, String> call(TableColumn<YBCCBean, String> param) {
                TextFieldTableCell cell = new TextFieldTableCell();
                cell.setOnMouseClicked((MouseEvent t)->{
                    if (t.getClickCount() == 2){
                        Record temp = ((YBCCBean) cell.getTableView().getItems().get(cell.getIndex())).getRecord();
                        temp.setHide(temp.hide.equalsIgnoreCase("0")?"1":"0");
                        cell.getTableView().refresh();
                    }
                });
                return cell;
            }
        });
        doneCol.setCellFactory(callback);
        codeCol.setCellFactory(callback);
        rankCol.setCellFactory(callback);
        contentCol.setCellFactory(callback);
        yunCol.setCellFactory(callback);
        IPACol.setCellFactory(callback);
        spellCol.setCellFactory(callback);
        englishCol.setCellFactory(callback);
        noteCol.setCellFactory(callback);
        recordDateCol.setCellFactory(callback);
        mwfyCol.setCellFactory(callback);
        duiyiCol.setCellFactory(callback);


        //设置单元格编辑事件
        codeCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<YBCCBean, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<YBCCBean, String> event) {
                int nowIndex = event.getTablePosition().getRow();
                int oIndex = originDatas.indexOf(tableDatas.get(nowIndex));
                ((YBCCBean) tableDatas.get(nowIndex)).getRecord().setInvestCode(event.getNewValue());
                ((YBCCBean) originDatas.get(oIndex)).getRecord().setInvestCode(event.getNewValue());

                editNext(event.getTableColumn(),nowIndex + 1);
            }
        });

        rankCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<YBCCBean, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<YBCCBean, String> event) {
                int nowIndex = event.getTablePosition().getRow();
                int oIndex = originDatas.indexOf(tableDatas.get(nowIndex));
                ((YBCCBean) tableDatas.get(nowIndex)).getRecord().setRank(event.getNewValue());
                ((YBCCBean) originDatas.get(oIndex)).getRecord().setRank(event.getNewValue());

                editNext(event.getTableColumn(),nowIndex + 1);
            }
        });
        contentCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<YBCCBean, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<YBCCBean, String> event) {
                int nowIndex = event.getTablePosition().getRow();
                int oIndex = originDatas.indexOf(tableDatas.get(nowIndex));
                ((YBCCBean) tableDatas.get(nowIndex)).getRecord().setContent(event.getNewValue());
                ((YBCCBean) originDatas.get(oIndex)).getRecord().setContent(event.getNewValue());

                editNext(event.getTableColumn(),nowIndex + 1);
            }
        });
        yunCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<YBCCBean, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<YBCCBean, String> event) {
                int nowIndex = event.getTablePosition().getRow();
                int oIndex = originDatas.indexOf(tableDatas.get(nowIndex));
                ((YBCCBean) tableDatas.get(nowIndex)).getRecord().setYun(event.getNewValue());
                ((YBCCBean) originDatas.get(oIndex)).getRecord().setYun(event.getNewValue());

                editNext(event.getTableColumn(),nowIndex + 1);
            }
        });
        IPACol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<YBCCBean, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<YBCCBean, String> event) {
                int nowIndex = event.getTablePosition().getRow();
                int oIndex = originDatas.indexOf(tableDatas.get(nowIndex));
                ((YBCCBean) tableDatas.get(nowIndex)).getRecord().setIPA(event.getNewValue());
                ((YBCCBean) originDatas.get(oIndex)).getRecord().setIPA(event.getNewValue());

                editNext(event.getTableColumn(),nowIndex + 1);
            }
        });
        spellCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<YBCCBean, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<YBCCBean, String> event) {
                int nowIndex = event.getTablePosition().getRow();
                int oIndex = originDatas.indexOf(tableDatas.get(nowIndex));
                ((YBCCBean) tableDatas.get(nowIndex)).getRecord().setSpell(event.getNewValue());
                ((YBCCBean) originDatas.get(oIndex)).getRecord().setSpell(event.getNewValue());

                editNext(event.getTableColumn(),nowIndex + 1);
            }
        });
        englishCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<YBCCBean, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<YBCCBean, String> event) {
                int nowIndex = event.getTablePosition().getRow();
                int oIndex = originDatas.indexOf(tableDatas.get(nowIndex));
                ((YBCCBean) tableDatas.get(nowIndex)).getRecord().setEnglish(event.getNewValue());
                ((YBCCBean) originDatas.get(oIndex)).getRecord().setEnglish(event.getNewValue());

                editNext(event.getTableColumn(),nowIndex + 1);
            }
        });
        noteCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<YBCCBean, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<YBCCBean, String> event) {
                int nowIndex = event.getTablePosition().getRow();
                int oIndex = originDatas.indexOf(tableDatas.get(nowIndex));
                ((YBCCBean) tableDatas.get(nowIndex)).getRecord().setNote(event.getNewValue());
                ((YBCCBean) originDatas.get(oIndex)).getRecord().setNote(event.getNewValue());

                editNext(event.getTableColumn(),nowIndex + 1);
            }
        });
        mwfyCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<YBCCBean, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<YBCCBean, String> event) {
                int nowIndex = event.getTablePosition().getRow();
                int oIndex = originDatas.indexOf(tableDatas.get(nowIndex));
                ((YBCCBean) tableDatas.get(nowIndex)).getRecord().setMWFY(event.getNewValue());
                ((YBCCBean) originDatas.get(oIndex)).getRecord().setMWFY(event.getNewValue());

                editNext(event.getTableColumn(),nowIndex + 1);
            }
        });
        duiyiCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<YBCCBean, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<YBCCBean, String> event) {
                int nowIndex = event.getTablePosition().getRow();
                int oIndex = originDatas.indexOf(tableDatas.get(nowIndex));
                ((YBCCBean) tableDatas.get(nowIndex)).getRecord().setFree_trans(event.getNewValue());
                ((YBCCBean) originDatas.get(oIndex)).getRecord().setFree_trans(event.getNewValue());

                editNext(event.getTableColumn(),nowIndex + 1);
            }
        });


        //设置单元格数据
        hideCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<YBCCBean, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<YBCCBean, String> param) {
                if (Integer.parseInt(param.getValue().getRecord().hide) == 0){
                    return new ReadOnlyStringWrapper("保留");
                }else{
                    return new ReadOnlyStringWrapper("隐藏");
                }
            }
        });
        doneCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<YBCCBean, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<YBCCBean, String> param) {
                if (Integer.parseInt(param.getValue().getRecord().done) == 0){
                    return new ReadOnlyStringWrapper("未录");
                }else{
                    return new ReadOnlyStringWrapper("已录");
                }
            }
        });
        codeCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<YBCCBean, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<YBCCBean, String> param) {
                return new SimpleStringProperty(param.getValue().getRecord().getInvestCode());
            }
        });
        contentCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<YBCCBean, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<YBCCBean, String> param) {
                return new SimpleStringProperty(param.getValue().getRecord().getContent());
            }
        });
        rankCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<YBCCBean, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<YBCCBean, String> param) {
                return new SimpleStringProperty(param.getValue().getRecord().getRank());
            }
        });
        yunCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<YBCCBean, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<YBCCBean, String> param) {
                return new SimpleStringProperty(param.getValue().getRecord().getYun());
            }
        });
        IPACol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<YBCCBean, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<YBCCBean, String> param) {
                return new SimpleStringProperty(param.getValue().getRecord().getIPA());
            }
        });
        spellCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<YBCCBean, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<YBCCBean, String> param) {
                return new SimpleStringProperty(param.getValue().getRecord().getSpell());
            }
        });
        englishCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<YBCCBean, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<YBCCBean, String> param) {
                return new SimpleStringProperty(param.getValue().getRecord().getEnglish());
            }
        });
        noteCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<YBCCBean, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<YBCCBean, String> param) {
                return new SimpleStringProperty(param.getValue().getRecord().getNote());
            }
        });
        recordDateCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<YBCCBean, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<YBCCBean, String> param) {
                return new SimpleStringProperty(param.getValue().getRecord().getCreateDate());
            }
        });
        mwfyCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<YBCCBean, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<YBCCBean, String> param) {
                return new SimpleStringProperty(param.getValue().getRecord().getMWFY());
            }
        });
        duiyiCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<YBCCBean, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<YBCCBean, String> param) {
                return new SimpleStringProperty(param.getValue().getRecord().getFree_trans());
            }
        });

        originDatas = DbHelper.getInstance().searchTempRecord2YBCCBean(((Table) preData).getDatatype(), ((Table) preData).getId());

        setupAllColumn();

        tableDatas = FXCollections.observableArrayList(originDatas);
        tableTopCtl.setAllCount(tableDatas.size());
        tableTopCtl.setNowIndex(0);

        tableView.setItems(tableDatas);
    }

    public void setupAllColumn(){
        tableView.getColumns().clear();
        if (newType == NewWordType){
            contentCol.setText("单字");
            tableView.getColumns().addAll(hideCol,doneCol,codeCol,rankCol,contentCol,yunCol,IPACol,spellCol,englishCol,noteCol,recordDateCol);
        }else if (newType == NewCiType){
            contentCol.setText("词条");
            tableView.getColumns().addAll(hideCol,doneCol,codeCol,rankCol,contentCol,mwfyCol,IPACol,spellCol,englishCol,noteCol,recordDateCol);
        }else if (newType == NewSentenceType){
            contentCol.setText("句子");
            tableView.getColumns().addAll(hideCol,doneCol,codeCol,rankCol,contentCol,mwfyCol,IPACol,duiyiCol,noteCol,englishCol,recordDateCol);
        }else if (newType == NewHuaYuType){

        }
    }

    @Override
    public void onTabClosed() {

    }

    private void editNext(TableColumn column,int rowIndex){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (rowIndex  <= tableDatas.size() - 1){
                    tableView.scrollTo(rowIndex);
                    tableView.edit(rowIndex, column);
                }
            }
        });
    }
    

    class MyTFCell extends TextFieldTableCell{
        private TextField textField;

        public MyTFCell(StringConverter converter) {
            super(converter);
        }

        public TextField getTextField() {
            return textField;
        }


        @Override
        public void startEdit() {
            super.startEdit();
            if (isEditable()){
                try {
                    Field field = TextFieldTableCell.class.getDeclaredField("textField");
                    field.setAccessible(true);
                    textField = (TextField) field.get(this);
                    setContextMenuContent(true);
                    textField.setContextMenu(contextMenu);

                    nowEditCell = this;
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void updateItem(Object item, boolean empty) {
            super.updateItem(item, empty);

            if (empty && getIndex()<0){

            }else {
                if (getTableRow() != null){
                    int colIndex = getTableView().getColumns().indexOf(getTableColumn());

                    try {
                        YBCCBean bean = (YBCCBean) tableDatas.get(getTableRow().getIndex());
                        Record r = bean.getRecord();

                        if (bean.getWrongReason() == null || bean.getWrongReason().equals("")){
                            getTableRow().setStyle("");
                        }else {
                            getTableRow().setStyle("-fx-background-color: deepskyblue");
                        }

                        if (getTableColumn().getText().equalsIgnoreCase("录音状态")){
                            if (bean.getRecord().getDone().equalsIgnoreCase("0")){
                                setStyle("-fx-text-fill: #ff0000");
                            }else{
                                setStyle("-fx-text-fill: #000000");
                            }
                        }

                        String regEx = "^[A-Z0-9]*$";
                        Pattern pattern = Pattern.compile(regEx);
                        Matcher matcher = pattern.matcher(r.getBaseCode());
                        boolean isUserCreate = !matcher.matches();

                        if (newType == NewTableView.NewWordType){
                            if (colIndex == 0 || colIndex == 1 || colIndex == 4 || colIndex == 10){
                                setEditable(isUserCreate);
                            }else {
                                setEditable(true);
                            }
                        }else if (newType == NewTableView.NewCiType){
                            if (colIndex == 0 || colIndex == 1 || colIndex == 4 || colIndex == 10){
                                setEditable(isUserCreate);
                            }else {
                                setEditable(true);
                            }
                        }else if (newType == NewTableView.NewSentenceType){
                            if (colIndex == 0 || colIndex == 1 || colIndex == 4 || colIndex == 10){
                                setEditable(isUserCreate);
                            }else {
                                setEditable(true);
                            }
                        }
                    }catch (Exception e){

                    }
                }
            }
        }
    }

    private List getTableColumnNameList() {
        ObservableList<TableColumn> tableColumns = tableView.getColumns();
        List<String> columnsName = new ArrayList<>();
        for (TableColumn tableColumn :
                tableColumns) {
            columnsName.add(tableColumn.getText());
        }
        return columnsName;
    }

    //获取模糊搜索匹配规则
    private String getMHSearRegEx(String str){
        StringBuilder sb = new StringBuilder(str);
        for (int i = str.length() - 1; i > 0; i--) {
            sb.insert(i,".*");
        }
        return sb.toString();
    }
}
