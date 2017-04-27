package sample.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import sample.entity.MetaData;
import sample.entity.Table;
import sample.util.DbHelper;
import sample.util.TextUtil;
import sample.util.ToastUtil;
import sample.util.ViewUtil;

import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by chenweiqi on 2017/4/25.
 */
public class TableMetadataInfoController extends BaseController {
    @FXML
    ListView<Table> lv_table;
    @FXML
    TextField input_title;
    @FXML
    ChoiceBox cb_datatype;
    @FXML
    TextField input_datades;
    @FXML
    TextField input_projectname;
    @FXML
    TextField input_speaker;
    @FXML
    TextField input_creator;
    @FXML
    TextField input_contributor;
    @FXML
    TextField input_recordingdate;
    @FXML
    TextField input_recordingplace;
    @FXML
    TextField input_languageplace;
    @FXML
    TextField input_language;
    @FXML
    TextField input_languagecode;
    @FXML
    TextField input_equipment;
    @FXML
    TextField input_software;
    @FXML
    TextField input_rightl;
    @FXML
    TextArea input_snote;


    Table table;

    boolean isAsc;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        init();

        getTableListAndShow();


    }

    private void init(){
        lv_table.setCellFactory(new Callback<ListView<Table>, ListCell<Table>>() {
            @Override
            public ListCell call(ListView param) {
                return new ListCell(){
                    @Override
                    protected void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item!=null&&!empty)
                            this.setText(((Table) item).getTitle());
                    }
                };
            }
        });



        lv_table.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Table>() {
            @Override
            public void changed(ObservableValue<? extends Table> observable, Table oldValue, Table newValue) {
                table = newValue;
                if (table!=null)
                showTableInfo(table);
            }
        });

    }

    private void getTableListAndShow(){
        List<Table> tables = DbHelper.getInstance().getAllTables();
        if (tables!=null) {
            lv_table.setItems(FXCollections.observableArrayList(tables));
            if (tables.size()!=0){
                lv_table.getSelectionModel().select(0);
            }
        }
        table = null;


    }

    //调查表排序
    @FXML
    private void onSortTableClick(){
        isAsc = !isAsc;
        lv_table.getItems().sort(new Comparator<Table>() {
            @Override
            public int compare(Table o1, Table o2) {
                if (isAsc){
                    return o1.title.compareTo(o2.title);
                }else {
                    return o2.title.compareTo(o1.title);
                }
            }
        });

    }

    //添加元数据
    @FXML
    private void onAddTableClick(){
        try {
            AddMetadataController controller = ViewUtil.getInstance().openAddMetaDataView();
            Table table = lv_table.getSelectionModel().getSelectedItem();
            if (!TextUtil.isEmpty(table.custom)){
                controller.setMetaData((List<MetaData>) new Gson().fromJson(table.custom,new TypeToken<List<MetaData>>(){}.getType()));
            }
            controller.setOnDoneClickListener(new AddMetadataController.OnDoneClickListener() {
                @Override
                public void onClick(AddMetadataController controller) {
                    List<MetaData> metaDataList = controller.getMetaData();
                    Table table = lv_table.getSelectionModel().getSelectedItem();
                    table.custom = new Gson().toJson(metaDataList);
                    DbHelper.getInstance().addOrUpdateTable(table);
                    getTableListAndShow();
                    controller.close();

                }
            });
            controller.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //删除元数据
    @FXML
    private void onDelTableClick(){
        Table table = lv_table.getSelectionModel().getSelectedItem();
        table.custom = null;
        DbHelper.getInstance().addOrUpdateTable(table);
        getTableListAndShow();
    }

    //确定
    @FXML
    private void onDoneClick(){

        if (!validateInput()){
            return;
        }
        inputToTableBean(table);
        DbHelper.getInstance().addOrUpdateTable(table);
        clearInput();
        getTableListAndShow();
    }

    //关闭
    @FXML
    private void onCloseClick(){

    }

    private void showTableInfo(Table table){
        input_contributor.setText(table.contributor);
        input_creator.setText(table.creator);
        input_datades.setText(table.datades);
        input_equipment.setText(table.equipment);
        input_language.setText(table.language);
        input_languagecode.setText(table.languagecode);
        input_languageplace.setText(table.languageplace);
        input_projectname.setText(table.projectname);
        input_recordingdate.setText(table.recordingdate);
        input_recordingplace.setText(table.recordingplace);
        input_rightl.setText(table.rightl);
        input_snote.setText(table.snote);
        input_software.setText(table.software);
        input_speaker.setText(table.speaker);
        input_title.setText(table.title);

        int index = Integer.valueOf(table.datatype);
        cb_datatype.getSelectionModel().select(index);
    }

    private Table inputToTableBean(Table table){
        if (table==null)
            table = new Table();

        table.contributor = input_contributor.getText();
        table.creator = input_creator.getText();
        table.datades = input_datades.getText();
        table.equipment = input_equipment.getText();
        table.language = input_language.getText();
        table.languagecode = input_languagecode.getText();
        table.languageplace = input_languageplace.getText();
        table.projectname = input_projectname.getText();
        table.recordingdate = input_recordingdate.getText();
        table.recordingplace = input_recordingplace.getText();
        table.rightl = input_rightl.getText();
        table.snote = input_snote.getText();
        table.software = input_software.getText();
        table.speaker = input_speaker.getText();
        table.title = input_title.getText();

        table.datatype = String.valueOf(cb_datatype.getSelectionModel().getSelectedIndex());

        return table;

    }


    private void clearInput(){
        input_contributor.setText(null);
        input_creator.setText(null);
        input_datades.setText(null);
        input_equipment.setText(null);
        input_language.setText(null);
        input_languagecode.setText(null);
        input_languageplace.setText(null);
        input_projectname.setText(null);
        input_recordingdate.setText(null);
        input_recordingplace.setText(null);
        input_rightl.setText(null);
        input_snote.setText(null);
        input_software.setText(null);
        input_speaker.setText(null);
        input_title.setText(null);
        cb_datatype.getSelectionModel().select(0);

    }


    private boolean validateInput(){
        boolean pass = true;
        if (
                TextUtil.isEmpty(input_speaker.getText())
                ||TextUtil.isEmpty(input_creator.getText())
                ||TextUtil.isEmpty(input_recordingdate.getText())
                ||TextUtil.isEmpty(input_recordingplace.getText())
                ||TextUtil.isEmpty(input_languageplace.getText())
                ||TextUtil.isEmpty(input_language.getText())
                ){

            ToastUtil.show("带**的为必填项");
            pass = false;
        }

        return pass;
    }


}
