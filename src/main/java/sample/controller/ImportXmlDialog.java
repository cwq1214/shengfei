package sample.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;
import sample.entity.BindResult;
import sample.util.*;

import java.io.File;
import java.net.URL;
import java.util.*;

/**
 * Created by chenweiqi on 2017/5/16.
 */
public class ImportXmlDialog extends BaseController {
    @FXML
    RadioButton toZB;
    @FXML
    RadioButton toCB;
    @FXML
    RadioButton toJB1;
    @FXML
    RadioButton toJB2;
    @FXML
    Button tab1_next;
    @FXML
    Button tab1_cancel;
    @FXML
    Label tab2_title;
    @FXML
    Label tab2_name;
    @FXML
    Button btn_getXml;
    @FXML
    TextField input_xmlPath;
    @FXML
    Button btn_getWav;
    @FXML
    TextField input_wavPath;
    @FXML
    Button tab2_done;
    @FXML
    Button tab2_cancel;
    @FXML
    Label tab3_title;
    @FXML
    TableView tb_xmlName;
    @FXML
    TableView tb_sfName;
    @FXML
    TableView tb_bindName;
    @FXML
    Button tab3_done;
    @FXML
    Button tab3_cancel;
    @FXML
    TabPane tb_tabPane;
    @FXML
    ToggleGroup tg_type;


    /*
       0 eaf
       1 exb
       2 xml
       3 ac
    */
    int importType;

    File xmlFilePath;
    File wavFilePath;
    TableColumn xmlTC;
    TableColumn sfTC;
    TableColumn bindTC;
    public ImportXmlDialog(){

    }

    public void setImportType(int importType){
        this.importType = importType;

        String tab2Title = "选择一个%s文件和媒体文件";
        String tab2FileName = "%s  file";
        String tab3Title = "请匹配%s层名与sonicfield字段名";

        switch (importType){
            case 0:
                tab2_title.setText(String.format(tab2Title,"eaf"));
                tab2_name.setText(String.format(tab2FileName,"eaf"));
                tab3_title.setText(String.format(tab3Title,"eaf"));
                xmlTC = new TableColumn("EAF");
                break;
            case 1:
                tab2_title.setText(String.format(tab2Title,"exb"));
                tab2_name.setText(String.format(tab2FileName,"exb"));
                tab3_title.setText(String.format(tab3Title,"exb"));
                xmlTC = new TableColumn("EXB");

                break;
            case 2:
                tab2_title.setText(String.format(tab2Title,"xml"));
                tab2_name.setText(String.format(tab2FileName,"xml"));
                tab3_title.setText(String.format(tab3Title,"xml"));
                break;
            case 3:
                tab2_title.setText(String.format(tab2Title,"ac"));
                tab2_name.setText(String.format(tab2FileName,"ac"));
                tab3_title.setText(String.format(tab3Title,"ac"));
                xmlTC = new TableColumn("AC");

                break;
        }
        xmlTC.setCellValueFactory(new Callback<TableColumn.CellDataFeatures, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures param) {
                return new SimpleStringProperty((String) param.getValue());
            }
        });
        tb_xmlName.getColumns().add(xmlTC);

    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);


        bindTC = new TableColumn("绑定");
        bindTC.setCellValueFactory(new Callback<TableColumn.CellDataFeatures, ObservableValue>() {
            @Override
            public ObservableValue call(TableColumn.CellDataFeatures param) {
                BindResult o = ((BindResult) param.getValue());
                return new SimpleStringProperty(o.key1+" - "+o.key2.recordName);
            }
        });
        tb_bindName.getColumns().add(bindTC);

        sfTC = new TableColumn("sf");
        sfTC.setCellValueFactory(new Callback<TableColumn.CellDataFeatures, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures param) {
                return new SimpleStringProperty(((BindResult.RecordMapper) param.getValue()).recordName);
            }
        });
        tb_sfName.getColumns().add(sfTC);


        tg_type.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {

                int selType = getSelType();
                List<BindResult.RecordMapper> mappers = new ArrayList<>();
                if (selType == 0){
                    mappers.add(new BindResult.RecordMapper("编码","baseCode"));
                    mappers.add(new BindResult.RecordMapper("条目","content"));
                    mappers.add(new BindResult.RecordMapper("音标注音","IPA"));
                    mappers.add(new BindResult.RecordMapper("音韵","yun"));
                    mappers.add(new BindResult.RecordMapper("注释","note"));
                    mappers.add(new BindResult.RecordMapper("拼音","spell"));
                    mappers.add(new BindResult.RecordMapper("英文","english"));
                }else if (selType == 1){
                    mappers.add(new BindResult.RecordMapper("编码","baseCode"));
                    mappers.add(new BindResult.RecordMapper("条目","content"));
                    mappers.add(new BindResult.RecordMapper("音标注音","IPA"));
                    mappers.add(new BindResult.RecordMapper("民文或方言字","MWFY"));
                    mappers.add(new BindResult.RecordMapper("注释","note"));
                    mappers.add(new BindResult.RecordMapper("拼音","spell"));
                    mappers.add(new BindResult.RecordMapper("英文","english"));

                }else if (selType == 2){
                    mappers.add(new BindResult.RecordMapper("编码","baseCode"));
                    mappers.add(new BindResult.RecordMapper("条目","content"));
                    mappers.add(new BindResult.RecordMapper("音标注音","IPA"));
                    mappers.add(new BindResult.RecordMapper("民文或方言字","MWFY"));
                    mappers.add(new BindResult.RecordMapper("注释","note"));
                    mappers.add(new BindResult.RecordMapper("普通话词对译","free_trans"));
                    mappers.add(new BindResult.RecordMapper("英文","english"));

                }

                tb_sfName.getItems().clear();
                tb_sfName.getItems().addAll(mappers);
            }
        });

    }

    private int getSelType(){
        if (toZB.isSelected()){
            return 0;
        }else
            if (toCB.isSelected()){
                return 1;
            }     else
                if (toJB1.isSelected()){
                    return 2;
                }else
                    if (toJB2.isSelected()){
                        return 2;
                    }
        return -1;
    }

    @FXML
    private void onNextClick(){
        tb_tabPane.getSelectionModel().selectNext();
    }
    @FXML
    private void onCancelClick(){
        close();
    }

    @FXML
    private void onDoneClick(){
        int selType = getSelType();
        if (selType == -1){
            ToastUtil.show("请先选择导入类型");
            return;
        }else
        if (xmlFilePath==null){
            ToastUtil.show("请先选择导入文件");
            return;
        }else
//        if (wavFilePath==null){
//            ToastUtil.show("请先选择导入音频文件");
//            return;
//        }

        if (selType == 0){//字

        }else if (selType == 1){//词

        }else if (selType == 2){//句

        }

        if(importType==0){//eaf
            EAFHelper.getInstance().deco(xmlFilePath.getPath(),wavFilePath != null ?wavFilePath.getPath():null,tb_bindName.getItems(),selType);
        }else if (importType==1){//exb
            EXBHelper.getInstance().deco(xmlFilePath.getPath(),wavFilePath != null ?wavFilePath.getPath():null,tb_bindName.getItems(),selType);
        }else if (importType==3){//ac
            AudoCityHelper.getInstance().deco(xmlFilePath.getPath(),wavFilePath != null ?wavFilePath.getPath():null,tb_bindName.getItems(),selType);
        }
        this.mStage.close();
    }

    @FXML
    private void getXmlFileClick(){
        int selFileType = -1;
        if (importType==0) {
            selFileType = DialogUtil.FILE_CHOOSE_TYPE_EAF;
        }else if (importType == 1){
            selFileType = DialogUtil.FILE_CHOOSE_TYPE_EXB;
        }else if (importType == 3){
            selFileType = DialogUtil.FILE_CHOOSE_TYPE_AC;
        }

        xmlFilePath = DialogUtil.fileChoiceDialog("选择文件",selFileType);
        if (xmlFilePath==null){
            return;
        }
        input_xmlPath.setText(xmlFilePath.getAbsolutePath());

        if (importType==0) {
            List<String> xml = EAFHelper.getInstance().readXmlAttrTitle(xmlFilePath.getPath());
            System.out.println(Arrays.toString(xml.toArray()));
            tb_xmlName.getItems().clear();
            tb_xmlName.getItems().addAll(xml);
        }else if (importType == 1){
            List<String> xml = EXBHelper.getInstance().readXmlAttrTitle(xmlFilePath.getPath());
            System.out.println(Arrays.toString(xml.toArray()));
            tb_xmlName.getItems().clear();
            tb_xmlName.getItems().addAll(xml);
        }else if (importType ==3 ){
            List<String> xml = AudoCityHelper.getInstance().readAttrTitle(xmlFilePath.getPath());
            System.out.println(Arrays.toString(xml.toArray()));
            tb_xmlName.getItems().clear();
            tb_xmlName.getItems().addAll(xml);
        }
    }

    @FXML
    private void getWavFileClick(){
        wavFilePath = DialogUtil.fileChoiceDialog("选择文件",DialogUtil.FILE_CHOOSE_TYPE_WAV);
        if (wavFilePath == null){
            return;
        }
        input_wavPath.setText(wavFilePath.getAbsolutePath());
    }

    @FXML
    private void addBindClick(){
        if (tb_sfName.getSelectionModel().getSelectedItem()!=null
                &&tb_xmlName.getSelectionModel().getSelectedItem()!=null){
            String xmlItem = (String) tb_xmlName.getSelectionModel().getSelectedItem();
            BindResult.RecordMapper sfItem = (BindResult.RecordMapper) tb_sfName.getSelectionModel().getSelectedItem();

            tb_xmlName.getItems().remove(xmlItem);
            tb_sfName.getItems().remove(sfItem);

            BindResult bindResult = new BindResult();
            bindResult.key1 = xmlItem;
            bindResult.key2 = sfItem;

            tb_bindName.getItems().add(bindResult);

        }
    }
    @FXML
    private void delBindClick(){
        if (tb_bindName.getSelectionModel().getSelectedItem()!=null){
            BindResult result = (BindResult) tb_bindName.getSelectionModel().getSelectedItem();
            tb_xmlName.getItems().add(result.key1);
            tb_sfName.getItems().add(result.key2);

            tb_bindName.getItems().remove(result);
        }
    }
    @FXML
    private void delAllBindClick(){
        List list = tb_bindName.getItems();
        if (list!=null&&list.size()!=0){
            for (int i=0,max = list.size();i<max;i++){
                BindResult result = ((BindResult) list.get(i));
                tb_xmlName.getItems().add(result.key1);
                tb_sfName.getItems().add(result.key2);
            }

            list.clear();
        }
    }





}
