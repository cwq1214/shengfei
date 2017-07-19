package sample.diycontrol.TopicSpeak;

import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * Created by Bee on 2017/5/2.
 */
public interface TopicSpeakCtlListener {

    public static enum TextFieldType {
      IPATF, MWFYTF,SPELLTF,WORDTRANTF,FREETRANTF,NOTETF,ENGLISHTF
    };

    public void textFieldActive(TextArea tf, TextFieldType type);
    public void textFieldChange(TextArea tf,TextFieldType type);

    public void addSpeakerClick();
    public void delSpeakerClick(TopicSpeakControl ctl);
    public void tqjzClick(TopicSpeakControl ctl,String title,String content);
    public void tqchClick(TopicSpeakControl ctl,String title,String content);
}
