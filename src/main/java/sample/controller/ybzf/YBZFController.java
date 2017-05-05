package sample.controller.ybzf;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import sample.controller.BaseController;
import sample.entity.CodeIPABase;
import sample.util.DbHelper;

import java.util.function.Predicate;

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
    private ChoiceBox typeChoice;

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
        setupChoiceBox();
    }

    public void setupChoiceBox(){
        typeChoice.setItems(FXCollections.observableArrayList("元音字符","辅音字符","其他字符"));
        typeChoice.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                setupPaneWithType(typeChoice.getSelectionModel().getSelectedIndex() + 1 + "");
            }
        });
        typeChoice.getSelectionModel().select(0);
    }

    public void setupPaneWithType(String type){
        ObservableList<CodeIPABase> temp = paneDatas.filtered(new Predicate<CodeIPABase>() {
            @Override
            public boolean test(CodeIPABase codeIPABase) {
                if (codeIPABase.getType().equalsIgnoreCase(type)){
                    return true;
                }
                return false;
            }
        });

        flowPane.getChildren().clear();

        for (int i = 0; i < temp.size(); i++) {
            CodeIPABase ipaBase = temp.get(i);
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
