package sample.controller.ybzf;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import sample.controller.BaseController;
import sample.entity.CodeIPABase;
import sample.util.DbHelper;

/**
 * Created by Bee on 2017/4/19.
 */
public class YBZFController extends BaseController {

    private ObservableList<CodeIPABase> paneDatas;
    private YBZFListener listener;

    public void setListener(YBZFListener listener) {
        this.listener = listener;
    }

    @FXML
    private FlowPane flowPane;

    @FXML
    public void okBtnClick(){
        listener.okBtnClick();
    }

    @FXML
    public void cancelBtnClick(){
        mStage.close();
    }

    @Override
    public void prepareInit() {
        super.prepareInit();
        flowPane.setVgap(1);
        flowPane.setHgap(1);

        paneDatas = DbHelper.getInstance().searchAllCodeIPABase();

        for (int i = 0; i < paneDatas.size(); i++) {
            CodeIPABase ipaBase = paneDatas.get(i);
            Button tempBtn = new Button(ipaBase.getContent());
            tempBtn.setMinSize(50,50);
            tempBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (listener != null){
                        listener.btnClickWithText(tempBtn.getText());
                    }
                }
            });
            flowPane.getChildren().add(tempBtn);
        }


    }
}
