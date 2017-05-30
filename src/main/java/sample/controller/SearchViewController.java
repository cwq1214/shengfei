package sample.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import sample.controller.ybzf.YBZFController;
import sample.controller.ybzf.YBZFListener;
import sample.util.TextUtil;
import sample.util.ViewUtil;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by chenweiqi on 2017/4/13.
 */
public class SearchViewController extends BaseController {

    @FXML
    ChoiceBox choiceBox;
    @FXML
    Button done;
    @FXML
    Button cancel;
    @FXML
    CheckBox checkBox;
    @FXML
    TextField input_content;

    private OnDoneClickCallback onDoneClickCallback;


    List<String> tableTitles;

    @FXML
    public void ybmbClick(){
        YBZFController vc = ((YBZFController) ViewUtil.getInstance().showView("view/ybzfView.fxml", "音标面板", -1, -1, ""));
        vc.setListener(new YBZFListener() {
            @Override
            public void btnClickWithText(String str) {
                input_content.setText(input_content.getText() + str);
            }

            @Override
            public void okBtnClick() {

            }
        });
        vc.mStage.setResizable(false);
        vc.mStage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

    }

    public void setSearchTableTitleName(List<String> tableTitles) {
        this.tableTitles = tableTitles;

        choiceBox.getItems().clear();
        choiceBox.setItems(FXCollections.observableArrayList(tableTitles));
        if (tableTitles != null && tableTitles.size() != 0) {
            choiceBox.getSelectionModel().select(0);
        }
    }

    public void setSearchTableTitleShowIndex(int index){
        choiceBox.getSelectionModel().select(index);
    }

    public void setOnDoneClickCallback(OnDoneClickCallback onDoneClickCallback) {
        this.onDoneClickCallback = onDoneClickCallback;
    }

    public int getSelTableTitleIndex() {
        return choiceBox.getSelectionModel().getSelectedIndex();
    }

    public String getSelTableTitleString() {
        return (String) choiceBox.getSelectionModel().getSelectedItem();

    }

    @FXML
    private void onDoneClick() {
        String inputContent = input_content.getText();
        if (TextUtil.isEmpty(inputContent)) {
            return;
        }
        onDoneClickCallback.onClick(this);
    }

    @FXML
    private void onCancelClick() {
        close();
    }


    public String getInputText() {
        String text = input_content.getText();
        if (TextUtil.isEmpty(text))
            return text;
        return text.trim();
    }

    public boolean isChecked() {
        return checkBox.isSelected();
    }

    public interface OnDoneClickCallback {
        void onClick(SearchViewController controller);
    }
}
