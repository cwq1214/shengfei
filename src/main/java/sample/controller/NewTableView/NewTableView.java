package sample.controller.NewTableView;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;
import sample.controller.BaseController;
import sample.controller.ybzf.YBZFController;
import sample.controller.ybzf.YBZFListener;
import sample.diycontrol.ClickType;
import sample.diycontrol.TableTopControl;
import sample.diycontrol.TableTopCtlListener;
import sample.entity.Record;
import sample.entity.Table;
import sample.util.DbHelper;
import sample.util.ViewUtil;

import java.io.File;
import java.lang.reflect.Field;
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


    private int newType;

    private ContextMenu contextMenu;

    private ObservableList tableDatas;

    private ObservableList originDatas;

    private MyTFCell nowEditCell;

    @FXML
    private TableTopControl tableTopCtl;

    @FXML
    private TableView tableView;


    @Override
    public void prepareInit() {
        super.prepareInit();


        //设置contextMenu内容
        contextMenu = new ContextMenu();
        MenuItem addRecord = new MenuItem("新增记录");
        MenuItem delRecord = new MenuItem("删除记录");
        MenuItem hideRecord = new MenuItem("隐藏记录");
        MenuItem keepRecord = new MenuItem("保留记录");
        MenuItem showYBZF = new MenuItem("音标字符");

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
                YBZFController vc = ((YBZFController) ViewUtil.getInstance().showView("view/ybzfView.fxml", "音标字符", -1, -1, ""));
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
        contextMenu.getItems().addAll(addRecord,delRecord,hideRecord,keepRecord,showYBZF);

        tableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                int nowIndex = tableView.getSelectionModel().getSelectedIndex();
                if (nowIndex != -1){
                    tableTopCtl.setNowIndex(nowIndex);
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
                        tableDatas = FXCollections.observableArrayList(originDatas.filtered(new Predicate<Record>(){
                            @Override
                            public boolean test(Record record) {
                                if (Integer.parseInt(record.hide) == 0){
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
                        tableDatas = FXCollections.observableArrayList(originDatas.filtered(new Predicate<Record>(){
                            @Override
                            public boolean test(Record record) {
                                if (Integer.parseInt(record.hide) == 1){
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
                }
            }
        });
    }

    public void addBtnClick(){
        int nowIndex = tableView.getSelectionModel().getSelectedIndex();
        Record selectR = ((Record) tableDatas.get(nowIndex));
        int oIndex = originDatas.indexOf(selectR);

        String regEx = "^[A-Z0-9]*[a-z]+$";
        Pattern pattern = Pattern.compile(regEx);

        Matcher matcher = pattern.matcher(selectR.getBaseCode());

        if (!matcher.matches()){
            //上面是基础记录
            String newBaseCode = getBiggestBaseCode(selectR.getBaseCode());
            String newInvCode = newBaseCode;
            Record newR = new Record(selectR.getBaseId(),newInvCode,newBaseCode,"0","0","","","","","","","","","","");

            tableDatas.add(nowIndex+1,newR);
            originDatas.add(oIndex+1,newR);
            tableView.refresh();

        }else{
            String nRegEx = "^[A-Z0-9]*";
            Pattern nPattern = Pattern.compile(nRegEx);
            String[] results = nPattern.split(selectR.getBaseCode());

            int suffixBeginIndex = selectR.getBaseCode().length() - results[1].length();

            //上面是新增加的记录
            String biggestBaseCode = getBiggestBaseCode(selectR.getBaseCode().substring(0,suffixBeginIndex));

            Record newR = new Record(selectR.getBaseId(),biggestBaseCode,biggestBaseCode,"0","0","","","","","","","","","","");

            tableDatas.add(nowIndex+1,newR);
            originDatas.add(oIndex+1,newR);
            tableView.refresh();
        }
    }

    public String getBiggestBaseCode(String prefix){
        //最大的basecode
        String biggest = "";
        for (Object o: originDatas) {
            String baseCode = ((Record) o).getBaseCode();
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
        ObservableList postions = tableView.getSelectionModel().getSelectedIndices();
        for (int i = 0 ;i<postions.size();i++) {
            int nowIndex = ((Integer) postions.get(i));
            if (nowIndex != -1) {
                int oIndex = originDatas.indexOf(tableDatas.get(nowIndex));
                tableDatas.remove(nowIndex);
                originDatas.remove(oIndex);
            }
        }
        tableView.refresh();
    }

    public void keepBtnClick(){
        ObservableList postions = tableView.getSelectionModel().getSelectedIndices();
        for (int i = 0 ;i<postions.size();i++){
            int nowIndex = ((Integer) postions.get(i));
            if (nowIndex != -1){
                int oIndex = originDatas.indexOf(tableDatas.get(nowIndex));
                ((Record) tableView.getItems().get(nowIndex)).setHide("0");
                ((Record) originDatas.get(oIndex)).setHide("0");
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
                ((Record) tableView.getItems().get(nowIndex)).setHide("1");
                ((Record) originDatas.get(oIndex)).setHide("1");
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
        }
    }

    public void setNewType(int newType){
        this.newType = newType;

        Callback<TableColumn<Record,String>,TableCell<Record,String>> callback = (TableColumn<Record,String> col) -> new MyTFCell(new DefaultStringConverter());

        TableColumn<Record,String> hideCol = new TableColumn<>("条目筛选");
        TableColumn<Record,String> doneCol = new TableColumn<>("录音状态");
        TableColumn<Record,String> codeCol = new TableColumn<>("编码");
        TableColumn<Record,String> rankCol = new TableColumn<>("分级");
        TableColumn<Record,String> yunCol = new TableColumn<>("音韵");
        TableColumn<Record,String> IPACol = new TableColumn<>("音标注音");
        TableColumn<Record,String> spellCol = new TableColumn<>("拼音");
        TableColumn<Record,String> englishCol = new TableColumn<>("英语");
        TableColumn<Record,String> noteCol = new TableColumn<>("注释");
        TableColumn<Record,String> recordDateCol = new TableColumn<>("录音日期");

        //设置单元格编辑权限
        hideCol.setEditable(false);
        doneCol.setEditable(false);
//        codeCol.setEditable(false);
        recordDateCol.setEditable(false);


        //设置单元格类型
        hideCol.setCellFactory(new Callback<TableColumn<Record, String>, TableCell<Record, String>>() {
            @Override
            public TableCell<Record, String> call(TableColumn<Record, String> param) {
                TextFieldTableCell cell = new TextFieldTableCell();
                cell.setOnMouseClicked((MouseEvent t)->{
                    if (t.getClickCount() == 2){
                        Record temp = ((Record) cell.getTableView().getItems().get(cell.getIndex()));
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
        yunCol.setCellFactory(callback);
        IPACol.setCellFactory(callback);
        spellCol.setCellFactory(callback);
        englishCol.setCellFactory(callback);
        noteCol.setCellFactory(callback);
        recordDateCol.setCellFactory(callback);


        //设置单元格编辑事件
        rankCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Record, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Record, String> event) {
                int nowIndex = event.getTablePosition().getRow();
                int oIndex = originDatas.indexOf(tableDatas.get(nowIndex));
                ((Record) tableDatas.get(nowIndex)).setRank(event.getNewValue());
                ((Record) originDatas.get(oIndex)).setRank(event.getNewValue());
            }
        });
        yunCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Record, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Record, String> event) {
                int nowIndex = event.getTablePosition().getRow();
                int oIndex = originDatas.indexOf(tableDatas.get(nowIndex));
                ((Record) tableDatas.get(nowIndex)).setYun(event.getNewValue());
                ((Record) originDatas.get(oIndex)).setYun(event.getNewValue());
            }
        });
        IPACol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Record, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Record, String> event) {
                int nowIndex = event.getTablePosition().getRow();
                int oIndex = originDatas.indexOf(tableDatas.get(nowIndex));
                ((Record) tableDatas.get(nowIndex)).setIPA(event.getNewValue());
                ((Record) originDatas.get(oIndex)).setIPA(event.getNewValue());
            }
        });
        spellCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Record, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Record, String> event) {
                int nowIndex = event.getTablePosition().getRow();
                int oIndex = originDatas.indexOf(tableDatas.get(nowIndex));
                ((Record) tableDatas.get(nowIndex)).setSpell(event.getNewValue());
                ((Record) originDatas.get(oIndex)).setSpell(event.getNewValue());
            }
        });
        englishCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Record, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Record, String> event) {
                int nowIndex = event.getTablePosition().getRow();
                int oIndex = originDatas.indexOf(tableDatas.get(nowIndex));
                ((Record) tableDatas.get(nowIndex)).setEnglish(event.getNewValue());
                ((Record) originDatas.get(oIndex)).setEnglish(event.getNewValue());
            }
        });
        noteCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Record, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Record, String> event) {
                int nowIndex = event.getTablePosition().getRow();
                int oIndex = originDatas.indexOf(tableDatas.get(nowIndex));
                ((Record) tableDatas.get(nowIndex)).setNote(event.getNewValue());
                ((Record) originDatas.get(oIndex)).setNote(event.getNewValue());
            }
        });

        //设置单元格数据
        hideCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Record, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Record, String> param) {
                if (Integer.parseInt(param.getValue().hide) == 0){
                    return new ReadOnlyStringWrapper("保留");
                }else{
                    return new ReadOnlyStringWrapper("隐藏");
                }
            }
        });
        doneCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Record, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Record, String> param) {
                if (Integer.parseInt(param.getValue().done) == 0){
                    return new ReadOnlyStringWrapper("未录");
                }else{
                    return new ReadOnlyStringWrapper("已录");
                }
            }
        });
        codeCol.setCellValueFactory(new PropertyValueFactory<>("investCode"));
        rankCol.setCellValueFactory(new PropertyValueFactory<>("rank"));
        yunCol.setCellValueFactory(new PropertyValueFactory<>("yun"));
        IPACol.setCellValueFactory(new PropertyValueFactory<>("IPA"));
        spellCol.setCellValueFactory(new PropertyValueFactory<>("spell"));
        englishCol.setCellValueFactory(new PropertyValueFactory<>("english"));
        noteCol.setCellValueFactory(new PropertyValueFactory<>("note"));
        recordDateCol.setCellValueFactory(new PropertyValueFactory<>("createDate"));

        if (newType == NewWordType){
            TableColumn<Record,String> wordCol = new TableColumn<>("单字");

//            wordCol.setEditable(false);
            wordCol.setCellFactory(callback);
            wordCol.setCellValueFactory(new PropertyValueFactory<>("content"));

            originDatas = DbHelper.getInstance().searchTempRecord("0", ((Table) preData).getId());
            tableDatas = FXCollections.observableArrayList(originDatas);
            tableTopCtl.setAllCount(tableDatas.size());
            tableTopCtl.setNowIndex(0);

            tableView.setItems(tableDatas);
            tableView.getColumns().addAll(hideCol,doneCol,codeCol,rankCol,wordCol,yunCol,IPACol,spellCol,englishCol,noteCol,recordDateCol);
        }else if (newType == NewCiType){
            TableColumn<Record,String> ciCol = new TableColumn<>("词条");
            TableColumn<Record,String> mwfyCol = new TableColumn<>("民族文字或方言字");

//            ciCol.setEditable(false);
            ciCol.setCellFactory(callback);
            ciCol.setCellValueFactory(new PropertyValueFactory<>("content"));

            mwfyCol.setCellFactory(callback);
            mwfyCol.setCellValueFactory(new PropertyValueFactory<>("MWFY"));
            mwfyCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Record, String>>() {
                @Override
                public void handle(TableColumn.CellEditEvent<Record, String> event) {
                    int nowIndex = event.getTablePosition().getRow();
                    int oIndex = originDatas.indexOf(tableDatas.get(nowIndex));
                    ((Record) tableDatas.get(nowIndex)).setMWFY(event.getNewValue());
                    ((Record) originDatas.get(oIndex)).setMWFY(event.getNewValue());
                }
            });

            originDatas = DbHelper.getInstance().searchTempRecord("1",((Table) preData).getId());
            tableDatas = FXCollections.observableArrayList(originDatas);
            tableTopCtl.setAllCount(tableDatas.size());
            tableTopCtl.setNowIndex(0);

            tableView.setItems(tableDatas);
            tableView.getColumns().addAll(hideCol,doneCol,codeCol,rankCol,ciCol,mwfyCol,IPACol,spellCol,englishCol,noteCol,recordDateCol);
        }else if (newType == NewSentenceType){
            TableColumn<Record,String> sentenceCol = new TableColumn<>("句子");
            TableColumn<Record,String> mwfyCol = new TableColumn<>("民族文字或方言字");
            TableColumn<Record,String> duiyiCol = new TableColumn<>("普通话词对译");

//            sentenceCol.setEditable(false);

            sentenceCol.setCellFactory(callback);
            mwfyCol.setCellFactory(callback);
            duiyiCol.setCellFactory(callback);

            mwfyCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Record, String>>() {
                @Override
                public void handle(TableColumn.CellEditEvent<Record, String> event) {
                    int nowIndex = event.getTablePosition().getRow();
                    int oIndex = originDatas.indexOf(tableDatas.get(nowIndex));
                    ((Record) tableDatas.get(nowIndex)).setMWFY(event.getNewValue());
                    ((Record) originDatas.get(oIndex)).setMWFY(event.getNewValue());
                }
            });
            duiyiCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Record, String>>() {
                @Override
                public void handle(TableColumn.CellEditEvent<Record, String> event) {
                    int nowIndex = event.getTablePosition().getRow();
                    int oIndex = originDatas.indexOf(tableDatas.get(nowIndex));
                    ((Record) tableDatas.get(nowIndex)).setFree_trans(event.getNewValue());
                    ((Record) originDatas.get(oIndex)).setFree_trans(event.getNewValue());
                }
            });


            sentenceCol.setCellValueFactory(new PropertyValueFactory<>("content"));
            mwfyCol.setCellValueFactory(new PropertyValueFactory<>("MWFY"));
            duiyiCol.setCellValueFactory(new PropertyValueFactory<>("free_trans"));

            originDatas = DbHelper.getInstance().searchTempRecord("2",((Table) preData).getId());
            tableDatas = FXCollections.observableArrayList(originDatas);
            tableTopCtl.setAllCount(tableDatas.size());
            tableTopCtl.setNowIndex(0);

            tableView.setItems(tableDatas);
            tableView.getColumns().addAll(hideCol,doneCol,codeCol,rankCol,sentenceCol,mwfyCol,IPACol,duiyiCol,noteCol,englishCol,recordDateCol);
        }else if (newType == NewHuaYuType){

        }
    }

    @Override
    public void onTabClosed() {
        DbHelper.getInstance().insertRecord(originDatas);
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

                    Record r = ((Record) tableDatas.get(getTableRow().getIndex()));

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
                }
            }
        }
    }
}
