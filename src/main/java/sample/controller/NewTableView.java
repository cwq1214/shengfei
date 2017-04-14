package sample.controller;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import sample.diycontrol.ClickType;
import sample.diycontrol.TableTopControl;
import sample.diycontrol.TableTopCtlListener;
import sample.entity.Record;
import sample.entity.Table;
import sample.util.DbHelper;

import java.util.function.Predicate;


/**
 * Created by Bee on 2017/4/13.
 */
public class NewTableView extends BaseController{
    public static final int NewWordType = 0;
    public static final int NewCiType =  1;
    public static final int NewSentenceType =  2;
    public static final int NewHuaYuType = 3;

    private ObservableList tableDatas;
    private ObservableList originDatas;
    @FXML
    private TableTopControl tableTopCtl;

    @FXML
    private TableView tableView;

    @Override
    public void prepareInit() {
        super.prepareInit();

        tableView.setEditable(true);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        tableTopCtl.setBtnClickListener(new TableTopCtlListener() {
            @Override
            public void onBtnClick(ClickType ct) {
                switch (ct){
                    case FirstBtnClick:
                        tableView.getSelectionModel().select(0);
                        tableView.scrollTo(tableView.getSelectionModel().getSelectedIndex());
                        tableTopCtl.setNowIndex(tableView.getSelectionModel().getSelectedIndex());
                        break;
                    case PreBtnClick: {
                        int nowIndex = tableView.getSelectionModel().getSelectedIndex();
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
                        tableView.getSelectionModel().select(tableDatas.size()-1);
                        tableView.scrollTo(tableView.getSelectionModel().getSelectedIndex());
                        tableTopCtl.setNowIndex(tableView.getSelectionModel().getSelectedIndex());
                        break;
                    case RefreshBtnClick:
                        tableDatas = originDatas;
                        tableView.setItems(tableDatas);
                        tableView.refresh();
                        break;
                    case SelectAllBtnClick:
                        tableView.getSelectionModel().selectAll();
                        break;
                    case SelectAnotherBtnClick:
                        break;
                    case KeepBtnClick: {
                        int nowIndex = tableView.getSelectionModel().getSelectedIndex();
                        if (nowIndex != -1){
                            ((Record) tableView.getItems().get(nowIndex)).setHide("0");
                            ((Record) originDatas.get(nowIndex)).setHide("0");
                            tableView.refresh();
                        }
                        break;
                    }
                    case DisappearBtnClick: {
                        int nowIndex = tableView.getSelectionModel().getSelectedIndex();
                        if (nowIndex != -1) {
                            ((Record) tableView.getItems().get(nowIndex)).setHide("1");
                            ((Record) originDatas.get(nowIndex)).setHide("1");
                            tableView.refresh();
                        }
                        break;
                    }
                    case DelBtnClick: {
                        int nowIndex = tableView.getSelectionModel().getSelectedIndex();
                        if (nowIndex != -1) {
//                            originDatas.remove(tableView.getItems().get(nowIndex));
                            tableView.getItems().remove(nowIndex);
                            tableView.refresh();
                        }
                        break;
                    }
                    case ShowAllBtnClick:
                        tableDatas = originDatas;
                        tableView.setItems(tableDatas);
                        tableView.refresh();
                        break;
                    case ShowOnlyKeepBtnClick:
                        tableDatas = originDatas.filtered(new Predicate<Record>(){
                            @Override
                            public boolean test(Record record) {
                                if (Integer.parseInt(record.hide) == 0){
                                    return true;
                                }
                                return false;
                            }
                        });
                        tableView.setItems(tableDatas);
                        tableView.refresh();
                        break;
                    case ShowOnlyDisappearBtnClick:
                        tableDatas = originDatas.filtered(new Predicate<Record>(){
                            @Override
                            public boolean test(Record record) {
                                if (Integer.parseInt(record.hide) == 1){
                                    return true;
                                }
                                return false;
                            }
                        });
                        System.out.println(tableDatas.size());
                        tableView.setItems(tableDatas);
                        tableView.refresh();
                        break;
                }
            }
        });
    }

    public void setNewType(int newType){
        if (newType == NewWordType){
            TableColumn<Record,String> hideCol = new TableColumn<>("条目筛选");
            TableColumn<Record,String> doneCol = new TableColumn<>("录音状态");
            TableColumn<Record,String> codeCol = new TableColumn<>("编码");
            TableColumn<Record,String> rankCol = new TableColumn<>("分级");
            TableColumn<Record,String> wordCol = new TableColumn<>("单字");
            TableColumn<Record,String> yunCol = new TableColumn<>("音韵");
            TableColumn<Record,String> IPACol = new TableColumn<>("音标注音");
            TableColumn<Record,String> spellCol = new TableColumn<>("拼音");
            TableColumn<Record,String> englishCol = new TableColumn<>("英语");
            TableColumn<Record,String> noteCol = new TableColumn<>("注释");
            TableColumn<Record,String> recordDateCol = new TableColumn<>("录音日期");

            hideCol.setEditable(false);
            doneCol.setEditable(false);

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
            doneCol.setCellFactory(TextFieldTableCell.forTableColumn());
            codeCol.setCellFactory(TextFieldTableCell.forTableColumn());
            rankCol.setCellFactory(TextFieldTableCell.forTableColumn());
            wordCol.setCellFactory(TextFieldTableCell.forTableColumn());
            yunCol.setCellFactory(TextFieldTableCell.forTableColumn());
            IPACol.setCellFactory(TextFieldTableCell.forTableColumn());
            spellCol.setCellFactory(TextFieldTableCell.forTableColumn());
            englishCol.setCellFactory(TextFieldTableCell.forTableColumn());
            noteCol.setCellFactory(TextFieldTableCell.forTableColumn());
            recordDateCol.setCellFactory(TextFieldTableCell.forTableColumn());

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
            wordCol.setCellValueFactory(new PropertyValueFactory<>("content"));
            yunCol.setCellValueFactory(new PropertyValueFactory<>("yun"));
            IPACol.setCellValueFactory(new PropertyValueFactory<>("IPA"));
            spellCol.setCellValueFactory(new PropertyValueFactory<>("spell"));
            englishCol.setCellValueFactory(new PropertyValueFactory<>("english"));
            noteCol.setCellValueFactory(new PropertyValueFactory<>("note"));
            recordDateCol.setCellValueFactory(new PropertyValueFactory<>("createDate"));

            originDatas = DbHelper.getInstance().searchTempRecord("0");
            tableDatas = originDatas;
            tableTopCtl.setAllCount(tableDatas.size());
            tableTopCtl.setNowIndex(0);

            tableView.setItems(tableDatas);
            tableView.getColumns().addAll(hideCol,doneCol,codeCol,rankCol,wordCol,yunCol,IPACol,spellCol,englishCol,noteCol,recordDateCol);
        }else if (newType == NewCiType){
            TableColumn<Record,String> hideCol = new TableColumn<>("条目筛选");
            TableColumn<Record,String> doneCol = new TableColumn<>("录音状态");
            TableColumn<Record,String> codeCol = new TableColumn<>("编码");
            TableColumn<Record,String> rankCol = new TableColumn<>("分级");
            TableColumn<Record,String> ciCol = new TableColumn<>("词条");
            TableColumn<Record,String> mwfyCol = new TableColumn<>("民族文字或方言字");
            TableColumn<Record,String> IPACol = new TableColumn<>("音标注音");
            TableColumn<Record,String> spellCol = new TableColumn<>("拼音");
            TableColumn<Record,String> englishCol = new TableColumn<>("英语");
            TableColumn<Record,String> noteCol = new TableColumn<>("注释");
            TableColumn<Record,String> recordDateCol = new TableColumn<>("录音日期");

            hideCol.setEditable(false);
            doneCol.setEditable(false);

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
            doneCol.setCellFactory(TextFieldTableCell.forTableColumn());
            codeCol.setCellFactory(TextFieldTableCell.forTableColumn());
            rankCol.setCellFactory(TextFieldTableCell.forTableColumn());
            ciCol.setCellFactory(TextFieldTableCell.forTableColumn());
            mwfyCol.setCellFactory(TextFieldTableCell.forTableColumn());
            IPACol.setCellFactory(TextFieldTableCell.forTableColumn());
            spellCol.setCellFactory(TextFieldTableCell.forTableColumn());
            englishCol.setCellFactory(TextFieldTableCell.forTableColumn());
            noteCol.setCellFactory(TextFieldTableCell.forTableColumn());
            recordDateCol.setCellFactory(TextFieldTableCell.forTableColumn());

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
            ciCol.setCellValueFactory(new PropertyValueFactory<>("content"));
            mwfyCol.setCellValueFactory(new PropertyValueFactory<>("MWFY"));
            IPACol.setCellValueFactory(new PropertyValueFactory<>("IPA"));
            spellCol.setCellValueFactory(new PropertyValueFactory<>("spell"));
            englishCol.setCellValueFactory(new PropertyValueFactory<>("english"));
            noteCol.setCellValueFactory(new PropertyValueFactory<>("note"));
            recordDateCol.setCellValueFactory(new PropertyValueFactory<>("createDate"));

            originDatas = DbHelper.getInstance().searchTempRecord("0");
            tableDatas = originDatas;
            tableTopCtl.setAllCount(tableDatas.size());
            tableTopCtl.setNowIndex(0);

            tableView.setItems(tableDatas);
            tableView.getColumns().addAll(hideCol,doneCol,codeCol,rankCol,ciCol,mwfyCol,IPACol,spellCol,englishCol,noteCol,recordDateCol);
        }else if (newType == NewSentenceType){
            TableColumn<Record,String> hideCol = new TableColumn<>("条目筛选");
            TableColumn<Record,String> doneCol = new TableColumn<>("录音状态");
            TableColumn<Record,String> codeCol = new TableColumn<>("编码");
            TableColumn<Record,String> rankCol = new TableColumn<>("分级");
            TableColumn<Record,String> sentenceCol = new TableColumn<>("句子");
            TableColumn<Record,String> mwfyCol = new TableColumn<>("民族文字或方言字");
            TableColumn<Record,String> IPACol = new TableColumn<>("音标注音");
            TableColumn<Record,String> duiyiCol = new TableColumn<>("普通话词对译");
            TableColumn<Record,String> englishCol = new TableColumn<>("英语");
            TableColumn<Record,String> noteCol = new TableColumn<>("注释");
            TableColumn<Record,String> recordDateCol = new TableColumn<>("录音日期");

            hideCol.setEditable(false);
            doneCol.setEditable(false);

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
            doneCol.setCellFactory(TextFieldTableCell.forTableColumn());
            codeCol.setCellFactory(TextFieldTableCell.forTableColumn());
            rankCol.setCellFactory(TextFieldTableCell.forTableColumn());
            sentenceCol.setCellFactory(TextFieldTableCell.forTableColumn());
            mwfyCol.setCellFactory(TextFieldTableCell.forTableColumn());
            IPACol.setCellFactory(TextFieldTableCell.forTableColumn());
//            spellCol.setCellFactory(TextFieldTableCell.forTableColumn());
            englishCol.setCellFactory(TextFieldTableCell.forTableColumn());
            noteCol.setCellFactory(TextFieldTableCell.forTableColumn());
            recordDateCol.setCellFactory(TextFieldTableCell.forTableColumn());

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
            sentenceCol.setCellValueFactory(new PropertyValueFactory<>("content"));
            mwfyCol.setCellValueFactory(new PropertyValueFactory<>("MWFY"));
            IPACol.setCellValueFactory(new PropertyValueFactory<>("IPA"));
//            duiyiCol.setCellValueFactory(new PropertyValueFactory<>("spell"));tbl_record表缺少普通话对译字段
            englishCol.setCellValueFactory(new PropertyValueFactory<>("english"));
            noteCol.setCellValueFactory(new PropertyValueFactory<>("note"));
            recordDateCol.setCellValueFactory(new PropertyValueFactory<>("createDate"));

            originDatas = DbHelper.getInstance().searchTempRecord("0");
            tableDatas = originDatas;
            tableTopCtl.setAllCount(tableDatas.size());
            tableTopCtl.setNowIndex(0);

            tableView.setItems(tableDatas);
            tableView.getColumns().addAll(hideCol,doneCol,codeCol,rankCol,sentenceCol,mwfyCol,IPACol,duiyiCol,noteCol,englishCol,recordDateCol);
        }else if (newType == NewHuaYuType){

        }
    }
}
