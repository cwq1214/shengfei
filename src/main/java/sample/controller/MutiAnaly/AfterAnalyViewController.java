package sample.controller.MutiAnaly;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import sample.controller.BaseController;
import sample.entity.Record;
import sample.entity.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bee on 2017/5/16.
 */
public class AfterAnalyViewController extends BaseController {
    @FXML
    private TableView tableView;

    public TableView getTableView() {
        return tableView;
    }

    private ObservableList<MutiAnalyBean> resultDatas;

    public void setResultDatas(ObservableList resultDatas, List<Table> tbls,int type) {
        this.resultDatas = resultDatas;
        System.out.println(resultDatas.size());
        if (type == 0){
            setupTableViewHor(tbls);
        }else{
            setupTableViewVe(tbls);
        }

    }

    private void setupTableViewVe(List<Table> tbls){
        List<VetBean> showDatas = new ArrayList<>();
        for (int i = 0;i < resultDatas.size() ; i++){
            MutiAnalyBean bean = resultDatas.get(i);
            for (int j = bean.getOtherIPA().size() - 1; j >= 0; j--) {
                Record r = bean.getOtherIPA().get(j);

                int index = i*5*(j + 1);

                showDatas.add(index,new VetBean(tbls.get(j+1).getTitle(),r.getMWFY()));
                showDatas.add(index,new VetBean(tbls.get(j+1).getTitle(),r.getFree_trans()));
                showDatas.add(index,new VetBean(tbls.get(j+1).getTitle(),r.getIPA()));
                showDatas.add(index,new VetBean(tbls.get(j+1).getTitle(),r.getContent()));
                showDatas.add(index,new VetBean(tbls.get(j+1).getTitle(),r.getBaseCode()));
            }

            Record baseR = bean.getBaseRecord();
            int index = i*5;
            showDatas.add(index,new VetBean(tbls.get(0).getTitle(),baseR.getMWFY()));
            showDatas.add(index,new VetBean(tbls.get(0).getTitle(),baseR.getFree_trans()));
            showDatas.add(index,new VetBean(tbls.get(0).getTitle(),baseR.getIPA()));
            showDatas.add(index,new VetBean(tbls.get(0).getTitle(),baseR.getContent()));
            showDatas.add(index,new VetBean(tbls.get(0).getTitle(),baseR.getBaseCode()));
        }

        TableColumn<VetBean,String> tblName = new TableColumn<>("表名");
        TableColumn<VetBean,String> tblContent = new TableColumn<>("内容");

        tblName.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<VetBean, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<VetBean, String> param) {
                return new SimpleStringProperty(param.getValue().getTblName());
            }
        });
        tblContent.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<VetBean, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<VetBean, String> param) {
                return new SimpleStringProperty(param.getValue().getContent());
            }
        });

        tableView.getColumns().addAll(tblName,tblContent);

        tableView.setItems(FXCollections.observableArrayList(showDatas));

    }

    private void setupTableViewHor(List<Table> tbls){
        TableColumn<MutiAnalyBean,String> codeCol = new TableColumn<>("编码");
        TableColumn<MutiAnalyBean,String> contentCol = new TableColumn<>("条目");

        codeCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<MutiAnalyBean, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<MutiAnalyBean, String> param) {
                return new SimpleStringProperty(param.getValue().getBaseRecord().getBaseCode());
            }
        });

        contentCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<MutiAnalyBean, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<MutiAnalyBean, String> param) {
                return new SimpleStringProperty(param.getValue().getBaseRecord().getContent());
            }
        });

        tableView.getColumns().addAll(codeCol,contentCol);

        for (int i = 0; i< tbls.size();i++) {
            Table t = tbls.get(i);
            TableColumn<MutiAnalyBean,String> col = new TableColumn<>(t.getTitle());
            col.setUserData(i);
            col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<MutiAnalyBean, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<MutiAnalyBean, String> param) {
                    int index = ((Integer) param.getTableColumn().getUserData());
                    if (index == 0){
                        return new SimpleStringProperty(param.getValue().getBaseRecord().getIPA());
                    }
                    return new SimpleStringProperty(param.getValue().getOtherIPA().get(index - 1).getIPA());
                }
            });
            tableView.getColumns().add(col);
        }

        tableView.setItems(resultDatas);

    }

    public class VetBean {
        private String tblName;
        private String content;

        public VetBean(String tblName, String content) {
            this.tblName = tblName;
            this.content = content;
        }

        public String getTblName() {
            return tblName;
        }

        public void setTblName(String tblName) {
            this.tblName = tblName;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
