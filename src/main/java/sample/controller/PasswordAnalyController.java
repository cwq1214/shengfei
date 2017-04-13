package sample.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sample.util.*;
import sample.view.Toast;

/**
 * Created by Bee on 2017/4/11.
 */
public class PasswordAnalyController extends BaseController{
    @FXML
    public TextField pwdTF;
    
    @FXML
    public void enterBtnClick(){

        String inputText = pwdTF.getText();

        if (!TextUtil.isEmpty(inputText) && inputText.equals(Constant.PASSWORD)) {
            AppCache.getInstance().setCertificate(true);
            ToastUtil.show("验证成功");
            this.close();
        } else {
            AppCache.getInstance().setCertificate(false);
            DialogUtil.showDialog("密码错误");
        }
    }
    
    @FXML
    public void cancelBtnClick(){
        close();
    }
    
    @Override
    public void prepareInit() {
        super.prepareInit();

    }
}
