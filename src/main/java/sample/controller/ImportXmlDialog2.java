package sample.controller;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import sample.Main;
import sample.entity.Record;
import sample.entity.Table;
import sample.util.DbHelper;
import sample.util.DialogUtil;
import sample.util.ToastUtil;
import sample.util.XMLHelper;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.UUID;

/**
 * Created by chenweiqi on 2017/5/16.
 */
public class ImportXmlDialog2  extends BaseController {
    @FXML
    TabPane tp_tabPane;
    @FXML
    RadioButton rb_toZB;
    @FXML
    RadioButton rb_toCB;
    @FXML
    RadioButton rb_toJB;
    @FXML
    RadioButton rb_toHYB;
    @FXML
    Button btn_next;
    @FXML
    Button btn_cancel;
    @FXML
    Button btn_fileChose;
    @FXML
    TextField input_filePath;
    @FXML
    Button btn_done;
    @FXML
    Button btn_cancel2;
    @FXML
    ToggleGroup tg_type;



    File saveFile;

    @FXML
    private void onNextClick(){

        tp_tabPane.getSelectionModel().select(1);
    }

    @FXML
    private void onCancelClick(){
        close();
    }

    @FXML
    private void onCancel2Click(){
        close();
    }

    @FXML
    private void onDoneClick(){
        if (getType()==-1){
            ToastUtil.show("请选择导入类型");
            return;
        }else if (saveFile==null){
            ToastUtil.show("请选择文件");
            return;
        }

        List<Record> records = XMLHelper.getInstance().readXml(saveFile.getAbsolutePath(),getType());
        createNewTableAndSaveRecords(records);
        ToastUtil.show("导入成功");

    }

    @FXML
    private void onFileChoseClick(){
        saveFile = DialogUtil.fileChoiceDialog("选择xml",DialogUtil.FILE_CHOOSE_TYPE_XML);
        if (saveFile!=null){
            input_filePath.setText(saveFile.getAbsolutePath());
        }
    }

    private void createNewTableAndSaveRecords( List<Record> records){
        Table table = new Table(getType()+"");
        DbHelper.getInstance().insertNewTable(table);

        for (Record r :
                records) {
            r.baseId = table.id;
            r.uuid = UUID.randomUUID().toString();
        }

        DbHelper.getInstance().insertOrUpdateRecord(records);
    }

    private int getType(){
        if (rb_toZB.isSelected())
            return 0;
        if (rb_toCB.isSelected())
            return 1;
        if (rb_toJB.isSelected())
            return 2;
        if (rb_toHYB.isSelected())
            return 3;

        return -1;
    }

}
