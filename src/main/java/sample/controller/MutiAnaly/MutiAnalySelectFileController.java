package sample.controller.MutiAnaly;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
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
import sample.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Created by Bee on 2017/5/9.
 */
public class MutiAnalySelectFileController extends BaseController{

    private ObservableList<Table> baseTable;
    private ObservableList<Table> analyTable;

    private ObservableList<ObservableList<Record>> analyDatas = FXCollections.observableArrayList();

    private AnalyListener listener;

    public void setListener(AnalyListener listener) {
        this.listener = listener;
    }

    @FXML
    private ListView baseListView;

    @FXML
    private ListView afterListView;

    @FXML
    public void okBtnClick(){
        if (baseListView.getSelectionModel().getSelectedIndex() != -1){
            Table baseT = ((Table) baseListView.getSelectionModel().getSelectedItem());
            if (afterListView.getSelectionModel().getSelectedIndices().size() != 0){
                if (baseT.datatype.equalsIgnoreCase("2")){
                    if (afterListView.getSelectionModel().getSelectedIndices().size() > 2){
                        ToastUtil.show("对照表最多只能选择2个");
                    }else{
                        ObservableList temp = FXCollections.observableArrayList(baseT);
                        temp.addAll(afterListView.getSelectionModel().getSelectedItems());
                        analyData(baseT,temp,1);
                    }
                }else {
                    if (afterListView.getSelectionModel().getSelectedIndices().size()>9){
                        ToastUtil.show("对照表最多只能选择9个");
                    }else{
                        ObservableList temp = FXCollections.observableArrayList(baseT);
                        temp.addAll(afterListView.getSelectionModel().getSelectedItems());
                        analyData(baseT,temp,0);
                    }
                }
            }else {
                ToastUtil.show("请选择对照表");
            }
        }else{
            ToastUtil.show("请选择基础表");
        }
        mStage.close();
    }


    private void analyData(Table selectT,ObservableList<Table> tbls,int type){
        ObservableList<Record> baseRecords = DbHelper.getInstance().searchTempRecordKeep("",selectT.getId());
        for (int i = 1;i<tbls.size();i++) {
            Table t = tbls.get(i);
            analyDatas.add(FXCollections.observableArrayList(DbHelper.getInstance().searchTempRecordKeep("",t.getId())));
        }

        List<MutiAnalyBean> result = new ArrayList<>();

        for (Record r : baseRecords) {
            MutiAnalyBean mutiBean = new MutiAnalyBean();
            mutiBean.setBaseRecord(r);

            //遍历每一张对照表
            for (ObservableList<Record> oRecords : analyDatas) {
                //遍历每一张表的内容
                for (Record tempR :oRecords) {
                    if (tempR.getContent().equalsIgnoreCase(r.getContent())){
                        mutiBean.addOtherIPA(tempR);
                        oRecords.remove(tempR);
                        break;
                    }
                }
            }
            if (mutiBean.getOtherIPA().size() == tbls.size() - 1) {
                result.add(mutiBean);
            }
        }
        listener.finishAnaly(result,tbls,type);
        mStage.close();
    }

    @FXML
    public void cancelBtnClick(){
        mStage.close();
    }

    @Override
    public void prepareInit() {
        super.prepareInit();
        baseTable = DbHelper.getInstance().searchAllWithoutTopicTable();

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

    public interface AnalyListener{
        public void finishAnaly(List<MutiAnalyBean> result,List<Table> tbls,int type);
    }
}
