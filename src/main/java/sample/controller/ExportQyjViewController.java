package sample.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.util.StringConverter;
import sample.entity.ExportTemplate;
import sample.entity.Table;
import sample.util.DbHelper;
import sample.util.DialogUtil;
import sample.util.ExportUtil;
import sample.util.ToastUtil;
import sample.view.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Created by Bee on 2017/5/27.
 */
public class ExportQyjViewController extends BaseController {
    @FXML
    private ListView tblList;
    @FXML
    private ChoiceBox learnCB;
    @FXML
    private TextField learnTF;
    @FXML
    private ChoiceBox appCB;
    @FXML
    private TextField appTF;
    @FXML
    private ChoiceBox mediaCB;
    @FXML
    private TextField mediaTF;
    @FXML
    private ChoiceBox diffCB;
    @FXML
    private TextField diffTF;
    @FXML
    private ChoiceBox lessonCB;
    @FXML
    private TextField lessonTF;
    @FXML
    private ChoiceBox fDirCB;
    @FXML
    private TextField fDirTF;
    @FXML
    private ChoiceBox sDirCB;
    @FXML
    private TextField sDirTF;
    @FXML
    private ListView templateList;

    private ObservableList<Table> tbls;
    private ObservableList<ExportTemplate> tmps;

    private ObservableList<ObservableList> normalCBDatas = FXCollections.observableArrayList(FXCollections.observableArrayList("tha","thb","thc","其他"),
            FXCollections.observableArrayList("旅游","生活","其他"),
            FXCollections.observableArrayList("音频","其他"),
            FXCollections.observableArrayList("初级","中级","高级","其他"),
            FXCollections.observableArrayList("地道粤语","地道闽南语","其他"),
            FXCollections.observableArrayList("交通出行","生活","便民","其他"),
            FXCollections.observableArrayList(FXCollections.observableArrayList("问路","导航","其他"),
                    FXCollections.observableArrayList("购物","吃饭","其他"),
                    FXCollections.observableArrayList("自动售货机","编不下去了","其他"))
    );

    private boolean canNextStep(){
        if (!itemCanNextStep(learnCB,learnTF))return false;
        if (!itemCanNextStep(appCB,appTF))return false;
        if (!itemCanNextStep(mediaCB,mediaTF))return false;
        if (!itemCanNextStep(diffCB,diffTF))return false;
        if (!itemCanNextStep(lessonCB,lessonTF))return false;
        if (!itemCanNextStep(fDirCB,fDirTF))return false;
        if (!itemCanNextStep(sDirCB,sDirTF))return false;
        return true;
    }

    private boolean itemCanNextStep(ChoiceBox cb,TextField tf){
        if (cb.getSelectionModel().getSelectedIndex() != cb.getItems().size() - 1){
            return true;
        }else{
            return tf.getText().length() != 0;
        }
    }

    private String itemStr(ChoiceBox cb,TextField tf){
        if (cb.getSelectionModel().getSelectedIndex() != cb.getItems().size() - 1){
            return cb.getSelectionModel().getSelectedItem().toString();
        }else{
            return tf.getText();
        }
    }

    @FXML
    public void saveTemplateClick(){
        if (canNextStep()){
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("提示");
            dialog.setHeaderText("");
            dialog.setContentText("请输入导出模板名称：");
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                String nameStr = result.get();
                String learnStr = itemStr(learnCB,learnTF);
                String appStr = itemStr(appCB,appTF);
                String mediaStr = itemStr(mediaCB,mediaTF);
                String diffStr = itemStr(diffCB,diffTF);
                String lessonStr = itemStr(lessonCB,lessonTF);
                String fDirStr = itemStr(fDirCB,fDirTF);
                String sDirStr = itemStr(sDirCB,sDirTF);

                ExportTemplate t = new ExportTemplate(nameStr,learnStr,appStr,mediaStr,diffStr,lessonStr,fDirStr,sDirStr);
                DbHelper.getInstance().insertTmp(t);
                tmps.add(t);
            }
        }else {
            ToastUtil.show("请填写内容完整！");
        }
    }

    @FXML
    public void exportBtnClick(){
        if (tblList.getSelectionModel().getSelectedItems().size() != 0){
            if (canNextStep()){
                String learnStr = itemStr(learnCB,learnTF);
                String appStr = itemStr(appCB,appTF);
                String mediaStr = itemStr(mediaCB,mediaTF);
                String diffStr = itemStr(diffCB,diffTF);
                String lessonStr = itemStr(lessonCB,lessonTF);
                String fDirStr = itemStr(fDirCB,fDirTF);
                String sDirStr = itemStr(sDirCB,sDirTF);

                ExportTemplate t = new ExportTemplate("",learnStr,appStr,mediaStr,diffStr,lessonStr,fDirStr,sDirStr);
                ExportUtil.exportQYJ(tblList.getSelectionModel().getSelectedItems(),t);
            }else {
                ToastUtil.show("请填写内容完整！");
            }
        }else {
            ToastUtil.show("请选择导出表格！");
        }
    }

    @FXML
    public void cancelBtnClick(){
        mStage.close();
    }


    @Override
    public void prepareInit() {
        super.prepareInit();
        setupCBContent();
        setupTblList();
        setupTmpList();
    }

    private void setupTmpList(){
        templateList.setCellFactory(TextFieldListCell.forListView(new StringConverter<ExportTemplate>() {
            @Override
            public String toString(ExportTemplate object) {
                return object.getName();
            }

            @Override
            public ExportTemplate fromString(String string) {
                return null;
            }
        }));

        tmps = DbHelper.getInstance().searchAllTemplate();
        templateList.setItems(tmps);
    }

    private void setupTblList(){
        tblList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tblList.setCellFactory(TextFieldListCell.forListView(new StringConverter<Table>() {
            @Override
            public Table fromString(String string) {
                return null;
            }

            @Override
            public String toString(Table object) {
                return object.getTitle();
            }
        }));

        tbls = DbHelper.getInstance().searchAllWithoutTopicTable();
        tblList.setItems(tbls);
    }

    private void setupCBContent(){
        learnCB.setItems(normalCBDatas.get(0));
        appCB.setItems(normalCBDatas.get(1));
        mediaCB.setItems(normalCBDatas.get(2));
        diffCB.setItems(normalCBDatas.get(3));
        lessonCB.setItems(normalCBDatas.get(4));
        fDirCB.setItems(normalCBDatas.get(5));
        sDirCB.setItems(((ObservableList) normalCBDatas.get(6).get(0)));

        learnCB.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                learnTF.setDisable(newValue.intValue() != learnCB.getItems().size() - 1);
            }
        });

        appCB.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                appTF.setDisable(newValue.intValue() != appCB.getItems().size() - 1);
            }
        });
        mediaCB.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                mediaTF.setDisable(newValue.intValue() != mediaCB.getItems().size() - 1);
            }
        });
        diffCB.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                diffTF.setDisable(newValue.intValue() != diffCB.getItems().size() - 1);
            }
        });
        lessonCB.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                lessonTF.setDisable(newValue.intValue() != lessonCB.getItems().size() - 1);
            }
        });
        fDirCB.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                fDirTF.setDisable(newValue.intValue() != fDirCB.getItems().size() - 1);
                if (!fDirTF.isDisable()) {
                    sDirCB.getSelectionModel().select(sDirCB.getItems().size() - 1);
                    sDirCB.setDisable(true);
                }else{
                    sDirCB.setDisable(false);
                }
            }
        });
        sDirCB.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                sDirTF.setDisable(newValue.intValue() != sDirCB.getItems().size() - 1);
            }
        });

        learnCB.getSelectionModel().select(0);
        appCB.getSelectionModel().select(0);
        mediaCB.getSelectionModel().select(0);
        diffCB.getSelectionModel().select(0);
        lessonCB.getSelectionModel().select(0);
        fDirCB.getSelectionModel().select(0);
        sDirCB.getSelectionModel().select(0);
    }
}
