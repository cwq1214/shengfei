package sample.controller.NewTableView;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;
import sample.controller.YBCC.YBCCBean;
import sample.entity.Record;
import sample.entity.Table;
import sample.util.DbHelper;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Bee on 2017/5/20.
 */
public class TempTableView {
    private TableView tableView;
    private ObservableList<YBCCBean> originDatas;
    private int newType;
    private int tblId;

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

    public TempTableView(int newType,int tblId) {
        this.newType = newType;
        this.tblId = tblId;
        tableView = new TableView();
        setupTableView();
    }

    private void setupTableView(){
        Callback<TableColumn<YBCCBean,String>,TableCell<YBCCBean,String>> callback = (TableColumn<YBCCBean,String> col) -> new MyTFCell(new DefaultStringConverter());


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

        originDatas = DbHelper.getInstance().searchTempRecord2YBCCBean(Integer.toString(newType), tblId);

        setupAllColumn();

        tableView.setItems(originDatas);
    }

    public void setupAllColumn(){
        tableView.getColumns().clear();
        if (newType == 0){
            contentCol.setText("单字");
            tableView.getColumns().addAll(hideCol,doneCol,codeCol,rankCol,contentCol,yunCol,IPACol,spellCol,englishCol,noteCol,recordDateCol);
        }else if (newType == 1){
            contentCol.setText("词条");
            tableView.getColumns().addAll(hideCol,doneCol,codeCol,rankCol,contentCol,mwfyCol,IPACol,spellCol,englishCol,noteCol,recordDateCol);
        }else if (newType == 2){
            contentCol.setText("句子");
            tableView.getColumns().addAll(hideCol,doneCol,codeCol,rankCol,contentCol,mwfyCol,IPACol,duiyiCol,noteCol,englishCol,recordDateCol);
        }else if (newType == 3){

        }
    }

    public TableView getTableView() {
        return tableView;
    }

    public void setTableView(TableView tableView) {
        this.tableView = tableView;
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
                        YBCCBean bean = (YBCCBean) originDatas.get(getTableRow().getIndex());
                        Record r = bean.getRecord();

                        if (bean.getWrongReason() == null || bean.getWrongReason().equals("")){
                            getTableRow().setStyle("");
                        }else {
                            getTableRow().setStyle("-fx-background-color: deepskyblue");
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
}
