package sample.controller;

import com.sun.javafx.scene.control.skin.TextFieldSkin;
import com.sun.javafx.scene.control.skin.TextInputControlSkin;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.InputMethodRequests;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import sample.diycontrol.TextAreaInput.TextAreaInputController;
import sample.util.DialogUtil;
import sample.util.ExportUtil;
import sample.util.ViewUtil;

import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Created by chenweiqi on 2017/4/25.
 */
public class CreateHTML3Controller extends BaseController {
    @FXML
    public TextField gkTF;
    @FXML
    public TextField zbTF;
    @FXML
    public TextField chTF;
    @FXML
    public TextField jzTF;
    @FXML
    public TextField yxTF;
    @FXML
    public TextField dzdzTF;
    @FXML
    public TextField chdzTF;
    @FXML
    public TextField jzdzTF;
    @FXML
    public TextField wbwyTF;

    @FXML
    public void gkTextBtnClick(){
        TextAreaInputController vc = ((TextAreaInputController) ViewUtil.getInstance().showView("view/textAreaInput.fxml", "请输入概况", -1, -1, ""));
        vc.setListener(new TextAreaInputController.TextAreaInputListener() {
            @Override
            public void onFinishInput(String str) {
                String tempPath = ExportUtil.export2DescHtml(str);
                gkTF.setText(gkTF.getText().length() == 0?"":gkTF.getText() + ";");
                gkTF.setText(gkTF.getText() + tempPath);
                gkTF.setUserData(gkTF.getText());
            }
        });

        vc.mStage.initModality(Modality.APPLICATION_MODAL);
        vc.mStage.setResizable(false);
        vc.mStage.show();
    }

    private void setTfContent(TextField tf){
        File f = DialogUtil.fileChoiceDialog("选择文件",DialogUtil.FILE_CHOOSE_TYPE_HTML);
        if (f != null){
            tf.setText(tf.getText().length() == 0?"":tf.getText() + ";");
            tf.setText(tf.getText() + f.getAbsolutePath());
        }
    }

    @FXML
    public void gkHtmlBtnClick(){
        setTfContent(gkTF);
    }

    @FXML
    public void zbHtmlClick(){
        setTfContent(zbTF);
    }

    @FXML
    public void chHtmlClick(){
        setTfContent(chTF);
    }

    @FXML
    public void jzHtmlClick(){
        setTfContent(jzTF);
    }

    @FXML
    public void yxHtmlClick(){
        setTfContent(yxTF);
    }

    @FXML
    public void dzdzHtmlClick(){
        setTfContent(dzdzTF);
    }

    @FXML
    public void chdzHtmlClick(){
        setTfContent(chdzTF);
    }

    @FXML
    public void jzdzHtmlClick(){
        setTfContent(jzdzTF);
    }

    @FXML
    public void wbwyHtmlClick(){
        setTfContent(wbwyTF);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
    }
}
