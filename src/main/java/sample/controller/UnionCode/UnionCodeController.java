package sample.controller.UnionCode;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import sample.controller.BaseController;
import sample.diycontrol.ProgressView.ProgressViewController;
import sample.entity.CodeBase;
import sample.entity.Record;
import sample.entity.Table;
import sample.util.DbHelper;
import sample.util.ViewUtil;

import javax.swing.text.View;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bee on 2017/5/3.
 */
public class UnionCodeController extends BaseController {

    private Table nowTable;
    private ObservableList<UnionCodeBean> tableDatas;

    public void setNowTable(Table nowTable) {
        this.nowTable = nowTable;
        setupTableView();
    }

    @FXML
    private TableView tableView;

    @FXML
    public void replaceSelectBtnClick(){
        DbHelper.getInstance().updateReplaceCode(replaceSelectCode(false));
        tableView.getItems().removeAll(tableView.getSelectionModel().getSelectedItems());
    }

    @FXML
    public void replaceAllBtnClick(){
        DbHelper.getInstance().updateReplaceCode(replaceSelectCode(true));
        tableView.getItems().clear();
    }

    @FXML
    public void refreshBtnClick(){

    }

    @Override
    public void prepareInit() {
        super.prepareInit();
    }

    public List<Record> replaceSelectCode(boolean isAll){
        List<Record> result = new ArrayList<>();
        if (isAll){
            for (int i = tableDatas.size() - 1 ; i >= 0 ; i--) {
                UnionCodeBean bean = tableDatas.get(i);
                bean.getUnionRecord().setBaseCode(bean.getCodeBase().getCode());
                result.add(bean.getUnionRecord());
            }
        }else{
            ObservableList<Integer> selects = tableView.getSelectionModel().getSelectedIndices();
            for (int j = selects.size() - 1 ; j >= 0 ; j--) {
                int i = selects.get(j);
                UnionCodeBean bean = tableDatas.get(i);
                bean.getUnionRecord().setBaseCode(bean.getCodeBase().getCode());
                result.add(bean.getUnionRecord());
            }
        }
        return result;
    }

    private void setupTableView(){
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        TableColumn<UnionCodeBean,String> codeCol = new TableColumn<>("编码");
        TableColumn<UnionCodeBean,String> baseCodeCol = new TableColumn<>("系统编码");
        TableColumn<UnionCodeBean,String> contentCol = new TableColumn<>("条目");
        TableColumn<UnionCodeBean,String> systemContentCol = new TableColumn<>("系统条目");

        codeCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<UnionCodeBean, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<UnionCodeBean, String> param) {
                return new SimpleStringProperty(param.getValue().getUnionRecord().getInvestCode());
            }
        });
        baseCodeCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<UnionCodeBean, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<UnionCodeBean, String> param) {
                return new SimpleStringProperty(param.getValue().getCodeBase().getCode());
            }
        });
        contentCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<UnionCodeBean, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<UnionCodeBean, String> param) {
                return new SimpleStringProperty(param.getValue().getUnionRecord().getContent());
            }
        });
        systemContentCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<UnionCodeBean, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<UnionCodeBean, String> param) {
                return new SimpleStringProperty(param.getValue().getUnionRecord().getContent());
            }
        });

        ProgressViewController pvc = ViewUtil.getInstance().showProgressView("加载数据中...请稍等");
        new Thread(new Runnable() {
            @Override
            public void run() {
                tableDatas = DbHelper.getInstance().getHasDifferentUnionCodeBean(nowTable);
                tableView.setItems(tableDatas);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        pvc.mStage.close();
                    }
                });
            }
        }).start();



        tableView.getColumns().addAll(codeCol,baseCodeCol,contentCol,systemContentCol);
    }
}
