package sample.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import sample.util.TextUtil;

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
