package sample.controller;

import javafx.fxml.FXML;
import sample.util.ExportUtil;

/**
 * Created by chenweiqi on 2017/4/25.
 */
public class CreateHTML4Controller extends BaseController {
    private Html4Listener listener;

    public void setListener(Html4Listener listener) {
        this.listener = listener;
    }

    @FXML
    public void createAndShowBtnClick(){
        listener.onClickCreateAndShow();
    }

    @FXML
    public void createBtnClick(){
        listener.onClickCreate();
    }

    @FXML
    public void cancelBtnClick(){
        listener.onClickCancel();
    }

    public interface Html4Listener{
        public void onClickCreateAndShow();
        public void onClickCreate();
        public void onClickCancel();
    }
}
