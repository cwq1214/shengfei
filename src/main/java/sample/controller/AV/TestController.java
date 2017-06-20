package sample.controller.AV;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import org.bytedeco.javacv.*;
import sample.controller.BaseController;
import sample.entity.Record;
import sample.util.Constant;
import sample.util.DbHelper;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Bee on 2017/6/5.
 */
public class TestController extends BaseController {
    @FXML
    private TableView tableView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        TableColumn<Record,String> doneCol = new TableColumn<>("录音状态");
        TableColumn<Record,String> videoDoneCol = new TableColumn<>("录像状态");
        TableColumn<Record,String> codeCol = new TableColumn<>("编码");
        TableColumn<Record,String> rankCol = new TableColumn<>("分级");
        TableColumn<Record,String> yunCol = new TableColumn<>("音韵");
        TableColumn<Record,String> IPACol = new TableColumn<>("音标注音");
        TableColumn<Record,String> spellCol = new TableColumn<>("拼音");
        TableColumn<Record,String> englishCol = new TableColumn<>("英语");
        TableColumn<Record,String> noteCol = new TableColumn<>("注释");
        TableColumn<Record,String> recordDateCol = new TableColumn<>("录音日期");
        TableColumn<Record,String> contentCol = new TableColumn<>("内容");
        TableColumn<Record,String> mwfyCol = new TableColumn<>("民族文字或方言字");
        TableColumn<Record,String> freeTran = new TableColumn<>("普通话词对译");
//
//        IPACol.setId("test");

        doneCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Record, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Record, String> param) {
                if (param.getValue().getDone().equals("0")){
                    return new ReadOnlyStringWrapper("未录");
                }
                return new ReadOnlyStringWrapper("已录");
            }
        });
        videoDoneCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Record, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Record, String> param) {
                File f = new File(Constant.ROOT_FILE_DIR+"/video/"+"9/"+param.getValue().getUuid()+".mp4");
                if (f.exists()){
                    return new ReadOnlyStringWrapper("已录");
                }
                return new ReadOnlyStringWrapper("未录");
            }
        });

        codeCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Record, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Record, String> param) {
                return new SimpleStringProperty(param.getValue().getInvestCode());
            }
        });
//        codeCol.setCellValueFactory(new PropertyValueFactory<>("investCode"));
//        rankCol.setCellValueFactory(new PropertyValueFactory<>("rank"));
//        yunCol.setCellValueFactory(new PropertyValueFactory<>("yun"));
//        IPACol.setCellValueFactory(new PropertyValueFactory<>("IPA"));
//        spellCol.setCellValueFactory(new PropertyValueFactory<>("spell"));
//        englishCol.setCellValueFactory(new PropertyValueFactory<>("english"));
//        noteCol.setCellValueFactory(new PropertyValueFactory<>("note"));
//        recordDateCol.setCellValueFactory(new PropertyValueFactory<>("createDate"));
//        contentCol.setCellValueFactory(new PropertyValueFactory<>("content"));
//        mwfyCol.setCellValueFactory(new PropertyValueFactory<>("MWFY"));
//        freeTran.setCellValueFactory(new PropertyValueFactory<>("free_trans"));

        ObservableList<Record> rs = FXCollections.observableArrayList(DbHelper.getInstance().searchTempRecordKeep("",1));

        tableView.setItems(rs);

        tableView.getColumns().addAll(codeCol, doneCol,videoDoneCol, contentCol, englishCol, yunCol, noteCol, rankCol, spellCol, IPACol, recordDateCol);
    }
}
