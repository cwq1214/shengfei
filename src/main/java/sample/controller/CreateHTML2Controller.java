package sample.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.util.StringConverter;
import sample.entity.Table;
import sample.util.DbHelper;
import sample.util.ExportUtil;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by chenweiqi on 2017/4/25.
 */
public class CreateHTML2Controller extends BaseController {
    @FXML
    private ListView tableList;

    @FXML
    private Button b1;

    @FXML
    private Button b2;

    @FXML
    private Button b3;

    @FXML
    private Button b4;

    @FXML
    private ListView tableSelectList;

    @FXML
    private CheckBox showSpeaker;

    @FXML
    private CheckBox showMeta;

    @FXML
    private RadioButton noSplit;

    @FXML
    private RadioButton split;

    @FXML
    private ChoiceBox lineCB;

    @FXML
    private ChoiceBox flCB;

    @FXML
    private ChoiceBox alignCB;

    @FXML
    private RadioButton senVRBtn;

    @FXML
    private RadioButton senHRBtn;

    public List<Map> outFiles;

    @FXML
    public void b1Click(){
        int index = tableList.getSelectionModel().getSelectedIndex();
        if (index != -1){
            selectTable.add(allTable.get(index));
            allTable.remove(index);
        }
    }

    @FXML
    public void b2Click(){
        int index = tableSelectList.getSelectionModel().getSelectedIndex();
        if (index != -1){
            allTable.add(selectTable.get(index));
            selectTable.remove(index);
        }
    }

    @FXML
    public void b3Click(){
        selectTable.addAll(allTable);
        allTable.clear();
    }

    @FXML
    public void b4Click(){
        allTable.addAll(selectTable);
        selectTable.clear();
    }

    @FXML
    public void outHtmlClick(){
        if (selectTable.size()!=0){
            outFiles = ExportUtil.exportTableHtml(selectTable,showSpeaker.isSelected(),showMeta.isSelected(),split.isSelected(),Integer.parseInt(lineCB.getValue().toString()),Integer.parseInt(flCB.getValue().toString()),alignCB.getSelectionModel().getSelectedIndex(),senVRBtn.isSelected());
        }
    }

    private ObservableList<Table> allTable;
    private ObservableList<Table> selectTable = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        allTable = FXCollections.observableArrayList(DbHelper.getInstance().searchAllWithoutTopicTable());

        alignCB.getSelectionModel().select(0);
        flCB.getSelectionModel().select(0);
        lineCB.getSelectionModel().select(0);

        b1.setText(">");
        b2.setText("<");
        b3.setText(">>");
        b4.setText("<<");

        tableList.setCellFactory(TextFieldListCell.forListView(new StringConverter<Table>() {
            @Override
            public String toString(Table object) {
                return object.getTitle();
            }

            @Override
            public Table fromString(String string) {
                return null;
            }
        }));

        tableSelectList.setCellFactory(TextFieldListCell.forListView(new StringConverter<Table>() {
            @Override
            public String toString(Table object) {
                return object.getTitle();
            }

            @Override
            public Table fromString(String string) {
                return null;
            }
        }));

        tableList.setItems(allTable);
        tableSelectList.setItems(selectTable);
    }
}
