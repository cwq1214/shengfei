package sample.diycontrol.TopicSpeak;

import javafx.scene.control.TextField;

/**
 * Created by Bee on 2017/5/2.
 */
public interface TopicSpeakCtlListener {

    public static enum TextFieldType {
      IPATF, MWFYTF,SPELLTF,WORDTRANTF,FREETRANTF,NOTETF,ENGLISHTF
    };

    public void textFieldActive(TextField tf,TextFieldType type);
    public void textFieldChange(TextField tf,TextFieldType type);

    public void addSpeakerClick();
    public void delSpeakerClick(TopicSpeakControl ctl);
}
