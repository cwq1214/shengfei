package sample.diycontrol.ProgressView;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import sample.controller.BaseController;

/**
 * Created by Bee on 2017/5/4.
 */
public class ProgressViewController extends BaseController{

    @FXML
    private Label tipL;

    @FXML
    private ProgressIndicator progressIndi;


    public void setTip(String tip){
        tipL.setText(tip);
    }
}
