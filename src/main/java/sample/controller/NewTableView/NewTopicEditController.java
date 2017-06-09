package sample.controller.NewTableView;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import sample.controller.BaseController;
import sample.diycontrol.TopicSpeak.TopicSpeakControl;
import sample.diycontrol.TopicSpeak.TopicSpeakCtlListener;
import sample.entity.Record;
import sample.entity.Topic;
import sample.util.DbHelper;
import sample.util.ExportUtil;
import sample.util.TextUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Bee on 2017/4/28.
 */
public class NewTopicEditController extends BaseController {
    private Record record;
    private String kindCode;
    private int baseId;

    private ObservableList<Topic> topics;
    private TextArea nowEditTF;
    private Matcher searchM;

    private String biaoDian = "。，、：∶；‘’“”〝〞ˆˇ﹕︰﹔﹖﹑·¨.¸,.´？！!～—｜‖＂〃｀@﹫¡¿﹏﹋︴々﹟#﹩$﹠&﹪%﹡﹢×﹦‐￣¯―﹨˜﹍﹎＿~（）〈〉‹›﹛﹜『』〖〗［］《》〔〕{}「」【】︵︷︿︹︽_︶︸﹀︺︾ˉ﹂﹄︼﹁﹃︻▲●□…→";

    @FXML
    private ScrollPane scrollView;

    @FXML
    private VBox scrollVBox;

    @FXML
    private TextArea topTextArea;

    @FXML
    private TextField searchKeyWordTF;

    @FXML
    private TextField replaceKeyWordTF;

    @FXML
    private TextField replaceWordTF;


    private ContextMenu topMenu;

    @FXML
    public void searchBtnClick(){
        if (searchKeyWordTF.getText().length() != 0){
            String regEx = searchKeyWordTF.getText();
            Pattern pattern = Pattern.compile(regEx);
            searchM = pattern.matcher(topTextArea.getText());
            if (searchM.find()){
                topTextArea.selectRange(searchM.start(), searchM.end());
            }
        }
    }

    @FXML
    public void searchNextBtnClick(){
        if (searchM != null && searchM.find()){
            topTextArea.selectRange(searchM.start(), searchM.end());
        }
    }

    @FXML
    public void replaceBtnClick(){
        replaceAction(false);
    }

    @FXML
    public void replaceAllBtnClick(){
        replaceAction(true);
    }

    private void replaceAction(boolean isAll){
        if (replaceKeyWordTF.getText().length() != 0 && replaceWordTF.getText().length() !=0){
            String regEx = replaceKeyWordTF.getText();
            Pattern pattern = Pattern.compile(regEx);
            Matcher m = pattern.matcher(topTextArea.getText());
            if (isAll){
                while (m.find()){
                    topTextArea.replaceText(m.start(),m.end(),replaceWordTF.getText());
                }
            }else{
                if (m.find()){
                    topTextArea.replaceText(m.start(),m.end(),replaceWordTF.getText());
                }
            }
        }
    }

    @FXML
    public void addSpeakerBtnClick(){
        Topic t = new Topic(kindCode,baseId);

        if (scrollVBox.getChildren().size() != 0){
            scrollVBox.getChildren().add(new Separator());
        }

        scrollVBox.getChildren().add(getTSC(t));
        topics.add(t);
    }

    @FXML
    public void saveBtnClick(){
        int realIndex = 0;
        for (int i = 0; i < scrollVBox.getChildren().size(); i++) {
            Node n = scrollVBox.getChildren().get(i);
            if (n instanceof TopicSpeakControl){
                ((TopicSpeakControl) scrollVBox.getChildren().get(i)).makeTopicMsg(topics.get(realIndex++));
            }
        }
        DbHelper.getInstance().saveTopics(topics);
    }

    @Override
    public void prepareInit() {
        super.prepareInit();
    }


    public void setBaseMsg(String kindCode,int baseId){
        this.kindCode = kindCode;
        this.baseId = baseId;
        topics = DbHelper.getInstance().searchTopicData(kindCode,baseId);

        setupTopMenu();
        setupTopTextArea();
        setupScrollViewContent();
    }

    private void setupTopMenu(){
        MenuItem nowBreak = new MenuItem("此处断句");
        MenuItem pointBreak = new MenuItem("标点断句");
        nowBreak.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int index = topTextArea.getCaretPosition();
                if (index != -1){
                    StringBuilder sb = new StringBuilder(topTextArea.getText());
                    sb.insert(index,"╟");
                    topTextArea.setText(sb.toString());
                }
            }
        });
        pointBreak.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                StringBuilder sb = new StringBuilder(topTextArea.getText());
                for (int i = topTextArea.getText().length() - 1; i >= 0 ; i--) {
                    Character c = topTextArea.getText().charAt(i);
                    if (biaoDian.contains(c.toString())){
                        sb.insert(i+1,"\r\n╟");
                    }
                }
                topTextArea.setText(sb.toString());
            }
        });

        topMenu = new ContextMenu(nowBreak,pointBreak);
        topTextArea.setContextMenu(topMenu);
    }

    private void setupTopTextArea(){
        topTextArea.setWrapText(true);
        topTextArea.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                topTextArea.setEditable(nowEditTF != null);
            }
        });
        topTextArea.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                System.out.println(newValue);
                nowEditTF.setText(newValue);
            }
        });
    }

    private void setupScrollViewContent(){
        for (Topic t : topics) {
            if (scrollVBox.getChildren().size() != 0){
                scrollVBox.getChildren().add(new Separator());
            }
            scrollVBox.getChildren().add(getTSC(t));
        }
    }

    private TopicSpeakControl getTSC(Topic t){
        String spkId;

        if (TextUtil.isEmpty(t.speakerId)) {
            if (topics.size() != 0) {
                String lastTopicSpId = topics.get(topics.size() - 1).speakerId;
                if (TextUtil.isEmpty(lastTopicSpId)) {
                    spkId = "SPK1";
                } else {
                    String index = lastTopicSpId.split("SPK")[1];
                    spkId = "SPK" + (Integer.valueOf(index) + 1);
                }
            } else {
                spkId = "SPK1";
            }
            t.speakerId = spkId;
        }
        TopicSpeakControl ctl = new TopicSpeakControl();
        ctl.setupTextFieldContent(t);
        ctl.setUserData(t);
        ctl.setListener(new TopicSpeakCtlListener() {
            @Override
            public void textFieldActive(TextArea tf, TextFieldType type) {
                nowEditTF = tf;
                topTextArea.setText(tf.getText());
            }

            @Override
            public void textFieldChange(TextArea tf, TextFieldType type) {
                nowEditTF = tf;
                topTextArea.setText(tf.getText());
            }

            @Override
            public void addSpeakerClick() {
                addSpeakerBtnClick();
            }

            @Override
            public void delSpeakerClick(TopicSpeakControl ctl) {
                topics.remove(ctl.getUserData());

                //删除speackControl 和sepator
                int ctlIndex = scrollVBox.getChildren().indexOf(ctl);

                if (scrollVBox.getChildren().size() > 1){
                    if (ctlIndex == 0){
                        scrollVBox.getChildren().remove(1);
                    }else {
                        scrollVBox.getChildren().remove(ctlIndex - 1);
                    }
                }
                scrollVBox.getChildren().remove(ctl);
            }

            @Override
            public void tqjzClick(TopicSpeakControl ctl) {
                int ctlIndex = scrollVBox.getChildren().indexOf(ctl);
                ExportUtil.exportTQJz(getTopics().get(ctlIndex));
            }

            @Override
            public void tqchClick(TopicSpeakControl ctl) {
                int ctlIndex = scrollVBox.getChildren().indexOf(ctl);
                ExportUtil.exportTQCh(getTopics().get(ctlIndex));
            }
        });
        return ctl;
    }

    public List<Topic> getTopics(){
        int realIndex = 0;
        for (int i = 0; i < scrollVBox.getChildren().size(); i++) {
            Node n = scrollVBox.getChildren().get(i);
            if (n instanceof TopicSpeakControl){
                ((TopicSpeakControl) scrollVBox.getChildren().get(i)).makeTopicMsg(topics.get(realIndex++));
            }
        }
        return topics;
    }

}
