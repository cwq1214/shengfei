package sample.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

/**
 * Created by Bee on 2017/4/11.
 */
public class PasswordAnalyController extends BaseController{
    @FXML
    public TextField pwdTF;
    
    @FXML
    public void enterBtnClick(){
        System.out.println(pwdTF.getText());
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
