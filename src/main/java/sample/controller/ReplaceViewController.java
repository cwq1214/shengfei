package sample.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import sample.controller.ybzf.YBZFController;
import sample.controller.ybzf.YBZFListener;
import sample.util.ViewUtil;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;


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

    TextField nowInput;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        input_searchContent.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue){
                    nowInput = input_searchContent;
                }
            }
        });
        input_replaceContent.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue){
                    nowInput = input_replaceContent;
                }
            }
        });
    }

    @FXML
    public void ybmbClick(){
        YBZFController vc = ((YBZFController) ViewUtil.getInstance().showView("view/ybzfView.fxml", "音标面板", -1, -1, ""));
        vc.setListener(new YBZFListener() {
            @Override
            public void btnClickWithText(String str) {
                nowInput.setText(nowInput.getText() + str);
            }

            @Override
            public void okBtnClick() {

            }
        });
        vc.mStage.setResizable(false);
        vc.mStage.show();
    }

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

    public void setTableNameShowIndex(int index){
        choiceBox.getSelectionModel().select(index);
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
