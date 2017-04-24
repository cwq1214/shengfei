package sample.controller.YBCC;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import sample.controller.BaseController;
import sample.entity.Record;
import sample.entity.Table;

import java.util.function.Predicate;

/**
 * Created by Bee on 2017/4/20.
 */
public class YBCCController extends BaseController {
    @FXML
    private ProgressBar progressBar;

    @FXML
    private TableView tableView;


    private ObservableList<YBCCBean> analyDatas;

    public ObservableList<YBCCBean> getAnalyDatas() {
        return analyDatas;
    }

    public void setAnalyDatas(ObservableList<YBCCBean> analyDatas) {
        this.analyDatas = analyDatas;
//        this.analyDatas = FXCollections.observableArrayList(analyDatas.filtered(new Predicate<YBCCBean>() {
//            @Override
//            public boolean test(YBCCBean bean) {
//                if (bean.getRecord().getIPA() == null || bean.getRecord().getIPA().length() == 0){
//                    return false;
//                }
//                return true;
//            }
//        }));
        tableView.setItems(this.analyDatas);
        tableView.refresh();
    }

    @Override
    public void prepareInit() {
        super.prepareInit();

        TableColumn<YBCCBean,String> codeCol = new TableColumn<>("编码");
        TableColumn<YBCCBean,String> ipaCol = new TableColumn<>("音标注音");
        TableColumn<YBCCBean,String> reasonCol = new TableColumn<>("错误原因");

        codeCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<YBCCBean, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<YBCCBean, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getRecord().getBaseCode());
            }
        });
        ipaCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<YBCCBean, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<YBCCBean, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getRecord().getIPA());
            }
        });
        reasonCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<YBCCBean, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<YBCCBean, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getWrongReason());
            }
        });

        tableView.getColumns().addAll(codeCol,ipaCol,reasonCol);
    }


    @Override
    public void onCreatedView() {
        super.onCreatedView();
        for (int i = 0; i < analyDatas.size(); i++) {
            progressBar.setProgress(i*1.0/analyDatas.size());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
