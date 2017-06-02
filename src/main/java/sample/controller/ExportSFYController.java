package sample.controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.util.StringConverter;
import sample.entity.Table;
import sample.util.DbHelper;
import sample.util.ExportUtil;
import sample.util.ToastUtil;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Bee on 2017/6/2.
 */
public class ExportSFYController extends BaseController {
    @FXML
    private ListView listView;
    @FXML
    private TextField accountTF;

    private ObservableList<Table> tbls;

    @FXML
    public void okBtnClick(){
        if (accountTF.getText().length() == 0){
            ToastUtil.show("请输入声飞云账号！");
            return;
        }
        if (listView.getSelectionModel().getSelectedItems().size() == 0){
            ToastUtil.show("请选择表格！");
            return;
        }

        ExportUtil.exportSFY(listView.getSelectionModel().getSelectedItems(),accountTF.getText());

    }

    @FXML
    public void cancelBtnClick(){
        mStage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        listView.setCellFactory(TextFieldListCell.forListView(new StringConverter<Table>() {
            @Override
            public String toString(Table object) {
                return object.getTitle();
            }

            @Override
            public Table fromString(String string) {
                return null;
            }
        }));
        tbls = DbHelper.getInstance().searchAllWithoutTopicTable();
        listView.setItems(tbls);
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }
}
