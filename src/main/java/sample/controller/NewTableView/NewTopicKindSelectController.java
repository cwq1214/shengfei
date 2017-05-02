package sample.controller.NewTableView;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import sample.controller.BaseController;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * Created by Bee on 2017/4/28.
 */
public class NewTopicKindSelectController extends BaseController {

    @FXML
    private TableView tableView;

    private NewTopicKindSelectListener listener;

    public NewTopicKindSelectListener getListener() {
        return listener;
    }

    public void setListener(NewTopicKindSelectListener listener) {
        this.listener = listener;
    }

    public static ObservableList topicKind = FXCollections.observableArrayList(
            new TopicBean("3","对话","dialogue"),
            new TopicBean("4","故事","story"),
            new TopicBean("5","儿童","child"),
            new TopicBean("6","音乐","music"),
            new TopicBean("7","民歌","folk song"),
            new TopicBean("8","技艺","skill"),
            new TopicBean("9","娱乐","entertainment"),
            new TopicBean("10","图像","image"),
            new TopicBean("11","研究","study"),
            new TopicBean("12","购物","shopping"));

    @FXML
    public void nextStepBtnClick(){
        selectNowIndex();
    }

    @FXML
    public void cancelBtnClick(){
        mStage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        TableColumn<TopicBean,String> codeCol = new TableColumn<>("编码");
        TableColumn<TopicBean,String> topicCol = new TableColumn<>("话题");
        TableColumn<TopicBean,String> englishCol = new TableColumn<>("英语");

        codeCol.setCellValueFactory(new PropertyValueFactory<>("code"));
        topicCol.setCellValueFactory(new PropertyValueFactory<>("topic"));
        englishCol.setCellValueFactory(new PropertyValueFactory<>("english"));

        tableView.getColumns().addAll(codeCol,topicCol,englishCol);
        tableView.setItems(topicKind);

        tableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2){
                    selectNowIndex();
                }
            }
        });
    }

    public void selectNowIndex(){
        int nowIndex = tableView.getSelectionModel().getSelectedIndex();
        if (nowIndex != -1){
            listener.onClickNextStep(((TopicBean) topicKind.get(nowIndex)));
            mStage.close();
        }
    }

}
