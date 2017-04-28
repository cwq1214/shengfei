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

    private ObservableList topicKind = FXCollections.observableArrayList(
            new TopicBean("TA","情景对话","Situational dialogues"),
            new TopicBean("TB","个人口述","Monologic discourse"),
            new TopicBean("TC","仪式套话","Ritually formulaic discourse"),
            new TopicBean("TD","表演歌唱","Dramas and songs"),
            new TopicBean("TE","童谣游戏","Children rhythm and language plays"),
            new TopicBean("TF","广播电视","Broadcast and TV"),
            new TopicBean("TG","行话黑话","Jargon, argot and secret language"));

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
