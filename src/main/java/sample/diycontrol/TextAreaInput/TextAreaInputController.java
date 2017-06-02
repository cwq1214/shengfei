package sample.diycontrol.TextAreaInput;

import com.sun.corba.se.impl.oa.toa.TOA;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import sample.controller.BaseController;
import sample.util.ToastUtil;

/**
 * Created by Bee on 2017/6/1.
 */
public class TextAreaInputController extends BaseController {
    private TextAreaInputListener listener;

    public void setListener(TextAreaInputListener listener) {
        this.listener = listener;
    }

    @FXML
    private TextArea textArea;

    @FXML
    public void okBtnClick(){
        if (textArea.getText().length() != 0){
            listener.onFinishInput(textArea.getText());
            mStage.close();
        }else{
            ToastUtil.show("请输入内容");
        }
    }

    @FXML
    public void cancelBtnClick(){
        mStage.close();
    }

    public interface TextAreaInputListener{
        public void onFinishInput(String str);
    }
}
