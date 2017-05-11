package sample.controller.MutiAnaly;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.util.Callback;
import javafx.util.StringConverter;
import sample.controller.BaseController;
import sample.entity.Record;
import sample.entity.Table;
import sample.util.DbHelper;

import java.util.List;
import java.util.function.Predicate;

/**
 * Created by Bee on 2017/5/9.
 */
public class MutiAnalySelectFileController extends BaseController{

    private ObservableList<Table> baseTable;
    private ObservableList<Table> analyTable;

    @FXML
    private ListView baseListView;

    @FXML
    private ListView afterListView;

    @FXML
    public void okBtnClick(){
        mStage.close();
    }

    @FXML
    public void cancelBtnClick(){
        mStage.close();
    }

    @Override
    public void prepareInit() {
        super.prepareInit();
        baseTable = DbHelper.getInstance().searchAllTable();

        baseListView.setCellFactory(TextFieldListCell.forListView(new StringConverter<Table>() {
            @Override
            public String toString(Table t) {
                return t.getTitle();
            }

            @Override
            public Table fromString(String string) {
                return null;
            }
        }));

        baseListView.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                Table newSelectT = baseTable.get(newValue.intValue());
                analyTable = baseTable.filtered(new Predicate<Table>() {
                    @Override
                    public boolean test(Table table) {
                        if (table!=newSelectT && table.datatype.equals(newSelectT.datatype)){
                            return true;
                        }
                        return false;
                    }
                });
                afterListView.setItems(analyTable);
            }
        });
        baseListView.setItems(baseTable);

        afterListView.setCellFactory(TextFieldListCell.forListView(new StringConverter<Table>() {
            @Override
            public String toString(Table t) {
                return t.getTitle();
            }

            @Override
            public Table fromString(String string) {
                return null;
            }
        }));
        afterListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);





    }
}
