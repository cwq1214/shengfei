package sample.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

import java.util.List;


/**
 * Created by chenweiqi on 2017/4/14.
 */
public class ReplaceViewController extends BaseController {
    @FXML
    TextField input_searchContent;
    @FXML
    TextField input_replaceContent;
    @FXML
    CheckBox checkBox;
    @FXML
    ChoiceBox choiceBox;
    @FXML
    Button btn_replace;
    @FXML
    Button btn_close;

    OnDoneClickCallback onDoneClickCallback;

    @FXML
    private void onReplaceBtnClick() {
        if (onDoneClickCallback != null) {
            onDoneClickCallback.click(this);
        }
    }

    @FXML
    private void onCloseBtnClick() {
        close();
    }

    public void setOnDoneClickCallback(OnDoneClickCallback onDoneClickCallback) {
        this.onDoneClickCallback = onDoneClickCallback;
    }

    public void setChoiceBoxItems(List items) {
        if (items != null) {
            choiceBox.setItems(FXCollections.observableArrayList(items));
        }
        if (choiceBox.getItems().size() > 0) {
            choiceBox.getSelectionModel().select(0);
        }
    }

    public int getChoiceBoxSelItemIndex() {
        return choiceBox.getSelectionModel().getSelectedIndex();
    }

    public boolean getIsSelCheckBox() {
        return checkBox.isSelected();
    }

    public String getInputSearchContent() {
        return input_searchContent.getText();
    }

    public String getInputReplaceContent() {
        return input_replaceContent.getText();
    }

    public interface OnDoneClickCallback {
        void click(ReplaceViewController controller);
    }
}
