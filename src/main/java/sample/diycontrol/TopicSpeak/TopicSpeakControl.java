package sample.diycontrol.TopicSpeak;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import sample.Main;
import sample.entity.Topic;
import sample.util.DialogUtil;


import java.io.*;

/**
 * Created by Bee on 2017/4/28.
 */
public class TopicSpeakControl extends VBox {

    @FXML
    private TextArea ipaTF;

    @FXML
    private TextArea mwfyTF;

    @FXML
    private TextArea spellTF;

    @FXML
    private TextArea wordTranTF;

    @FXML
    private TextArea freeTranTF;

    @FXML
    private TextArea noteTF;

    @FXML
    private TextArea englishTF;
    @FXML
    Label ipaLB;
    @FXML
    Label mwfyLB;
    @FXML
    Label spellLB;
    @FXML
    Label wordTranLB;
    @FXML
    Label freeTranLB;
    @FXML
    Label noteLB;
    @FXML
    Label englishLB;


    private TextArea lastActive;
    private ContextMenu contextMenu;
    private TopicSpeakCtlListener listener;

    public void setListener(TopicSpeakCtlListener listener) {
        this.listener = listener;
    }

    public TopicSpeakControl() {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("view/control/topicSpeakControl.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        setupContextMenu();
        setupTextFieldContextMenu();
        setupTextFieldChange();
        setupTextFieldActive();
    }

    private void setupTextFieldContextMenu(){
        ipaTF.setContextMenu(contextMenu);
        mwfyTF.setContextMenu(contextMenu);
        spellTF.setContextMenu(contextMenu);
        wordTranTF.setContextMenu(contextMenu);
        freeTranTF.setContextMenu(contextMenu);
        noteTF.setContextMenu(contextMenu);
        englishTF.setContextMenu(contextMenu);
    }

    private void setupContextMenu(){
        contextMenu = new ContextMenu();

        MenuItem addSpeaker = new MenuItem("添加说话人");
        addSpeaker.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (listener != null){
                    listener.addSpeakerClick();
                }
            }
        });

        MenuItem delSpeaker = new MenuItem("删除说话人");
        delSpeaker.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                listener.delSpeakerClick(TopicSpeakControl.this);
            }
        });

        MenuItem tqjzItem = new MenuItem("提取此层句子");
        tqjzItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                listener.tqjzClick(TopicSpeakControl.this);
            }
        });

        MenuItem tqchItem = new MenuItem("提取此层词汇");
        tqchItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                listener.tqchClick(TopicSpeakControl.this);
            }
        });

        MenuItem impTxt = new MenuItem("导入文本");
        impTxt.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                File f = DialogUtil.fileChoiceDialog("选择文件",DialogUtil.FILE_CHOOSE_TYPE_TXT);
                if (f != null){
                    StringBuilder sb = new StringBuilder();
                    try {
                        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f),"utf-8"));
                        String line = null;
                        while ((line = br.readLine()) != null){
                            sb.append(line+"\r\n");
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    lastActive.setText(sb.toString());
                }
            }
        });

        contextMenu.getItems().addAll(addSpeaker,delSpeaker,impTxt,tqjzItem,tqchItem);
        setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
            @Override
            public void handle(ContextMenuEvent event) {
                contextMenu.show(TopicSpeakControl.this,event.getScreenX(),event.getScreenY());
            }
        });
    }

    public void setupTextFieldContent(Topic t){
        ipaTF.setText(t.getIpa());
        mwfyTF.setText(t.getMwfy());
        spellTF.setText(t.getSpell());
        wordTranTF.setText(t.getWord_trans());
        freeTranTF.setText(t.getFree_trans());
        noteTF.setText(t.getNote());
        englishTF.setText(t.getEnglish());

        ipaLB.setText("("+t.speakerId+")音标注音");
        mwfyLB.setText("("+t.speakerId+")民文或方言");
        spellLB.setText("("+t.speakerId+")拼音");
        wordTranLB.setText("("+t.speakerId+")普通话词对译");
        freeTranLB.setText("("+t.speakerId+")普通话意译");
        noteLB.setText("("+t.speakerId+")注释");
        englishLB.setText("("+t.speakerId+")英语");
    }

    private void setupTextFieldActive(){
        ipaTF.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                lastActive = ipaTF;
                listener.textFieldActive(ipaTF, TopicSpeakCtlListener.TextFieldType.IPATF);
            }
        });
        mwfyTF.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                lastActive = mwfyTF;
                listener.textFieldActive(mwfyTF, TopicSpeakCtlListener.TextFieldType.MWFYTF);
            }
        });
        spellTF.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                lastActive = spellTF;
                listener.textFieldActive(spellTF, TopicSpeakCtlListener.TextFieldType.SPELLTF);
            }
        });
        wordTranTF.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                lastActive = wordTranTF;
                listener.textFieldActive(wordTranTF, TopicSpeakCtlListener.TextFieldType.WORDTRANTF);
            }
        });
        freeTranTF.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                lastActive = freeTranTF;
                listener.textFieldActive(freeTranTF, TopicSpeakCtlListener.TextFieldType.FREETRANTF);
            }
        });
        noteTF.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                lastActive = noteTF;
                listener.textFieldActive(noteTF, TopicSpeakCtlListener.TextFieldType.NOTETF);
            }
        });
        englishTF.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                lastActive = englishTF;
                listener.textFieldActive(englishTF, TopicSpeakCtlListener.TextFieldType.ENGLISHTF);
            }
        });
    }

    private void setupTextFieldChange(){
        ipaTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (listener != null){
                    listener.textFieldChange(ipaTF, TopicSpeakCtlListener.TextFieldType.IPATF);
                }
            }
        });
        mwfyTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (listener != null){
                    listener.textFieldChange(mwfyTF, TopicSpeakCtlListener.TextFieldType.MWFYTF);
                }
            }
        });
        spellTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (listener != null){
                    listener.textFieldChange(spellTF, TopicSpeakCtlListener.TextFieldType.SPELLTF);
                }
            }
        });
        wordTranTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (listener != null){
                    listener.textFieldChange(wordTranTF, TopicSpeakCtlListener.TextFieldType.WORDTRANTF);
                }
            }
        });
        freeTranTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (listener != null){
                    listener.textFieldChange(freeTranTF, TopicSpeakCtlListener.TextFieldType.FREETRANTF);
                }
            }
        });
        noteTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (listener != null){
                    listener.textFieldChange(noteTF, TopicSpeakCtlListener.TextFieldType.NOTETF);
                }
            }
        });
        englishTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (listener != null){
                    listener.textFieldChange(englishTF, TopicSpeakCtlListener.TextFieldType.ENGLISHTF);
                }
            }
        });
    }

    public Topic getInputData(){
        Topic topic = new Topic();
        topic.setIpa(ipaTF.getText());
        topic.setMwfy(mwfyTF.getText());
        topic.setSpell(spellTF.getText());
        topic.setWord_trans(wordTranTF.getText());
        topic.setFree_trans(freeTranTF.getText());
        topic.setNote(noteTF.getText());
        topic.setEnglish(englishTF.getText());
        return topic;
    }

    public Topic makeTopicMsg(Topic t){
        t.setIpa(ipaTF.getText() == null?"":ipaTF.getText());
        t.setMwfy(mwfyTF.getText()== null?"":mwfyTF.getText());
        t.setSpell(spellTF.getText()== null?"":spellTF.getText());
        t.setWord_trans(wordTranTF.getText()== null?"":wordTranTF.getText());
        t.setFree_trans(freeTranTF.getText()== null?"":freeTranTF.getText());
        t.setNote(noteTF.getText()== null?"":noteTF.getText());
        t.setEnglish(englishTF.getText()== null?"":englishTF.getText());
        return t;
    }
}
