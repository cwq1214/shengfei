package sample.controller.openTable;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import sample.controller.BaseController;
import sample.entity.Table;
import sample.entity.Topic;
import sample.util.DbHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bee on 2017/4/17.
 */
public class OpenTableController extends BaseController {

    @FXML
    private Label tipL;

    @FXML
    private ListView listView;

    @FXML
    private Button openBtn;

    @FXML
    private Button cancelBtn;

    private boolean isOpen;

    private ObservableList<Table> tableDatas;
    private ObservableList<String> tableNameDatas;

    private OpenTableListener listener;

    public void setOpen(boolean open) {
        isOpen = open;
        if (!open){
            openBtn.setText("删除");
            tipL.setText("请选择删除的表：");
        }
    }

    public void setListener(OpenTableListener listener) {
        this.listener = listener;
    }

    @Override
    public void prepareInit() {
        super.prepareInit();

        //设置listview事件
        listView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2){
                    openOrDelTable();
                }
            }
        });

        //设置listview数据
        tableDatas = DbHelper.getInstance().searchAllTable();

        List temp = new ArrayList();
        for (Table t : tableDatas) {
            temp.add(t.getTitle());
        }
        tableNameDatas = FXCollections.observableArrayList(temp);

        listView.setItems(tableNameDatas);

    }

    @FXML
    public void openBtnClick(){
        openOrDelTable();
    }

    @FXML
    public void cancelBtnClick(){
        mStage.close();
    }


    public void openOrDelTable(){
        int nowIndex = listView.getSelectionModel().getSelectedIndex();
        if (isOpen){
            if (nowIndex != -1){
                listener.onOpenTable(tableDatas.get(nowIndex));
                mStage.close();
            }
        }else {
            if (nowIndex != -1){
                DbHelper.getInstance().delTableAndRecord(tableDatas.get(nowIndex));
                tableDatas.remove(nowIndex);
                tableNameDatas.remove(nowIndex);
                listView.refresh();
            }
        }
    }
}
