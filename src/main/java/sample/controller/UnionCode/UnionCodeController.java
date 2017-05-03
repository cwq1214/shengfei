package sample.controller.UnionCode;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import sample.controller.BaseController;
import sample.entity.Record;
import sample.entity.Table;

/**
 * Created by Bee on 2017/5/3.
 */
public class UnionCodeController extends BaseController {

    private Table nowTable;

    public void setNowTable(Table nowTable) {
        this.nowTable = nowTable;
    }

    @FXML
    private TableView tableView;

    @FXML
    public void replaceSelectBtnClick(){

    }

    @FXML
    public void replaceAllBtnClick(){

    }

    @FXML
    public void refreshBtnClick(){

    }

    @Override
    public void prepareInit() {
        super.prepareInit();

        setupTableView();
    }

    private void setupTableView(){
        TableColumn<Record,String> codeCol = new TableColumn<>("编码");
        TableColumn<Record,String> baseCodeCol = new TableColumn<>("系统编码");
        TableColumn<Record,String> contentCol = new TableColumn<>("条目");
        TableColumn<Record,String> systemContentCol = new TableColumn<>("系统条目");
    }
}
