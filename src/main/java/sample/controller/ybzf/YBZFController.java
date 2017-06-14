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
    private FlowPane yyFlowPane;

    @FXML
    private FlowPane fyFlowPane;

    @FXML
    private FlowPane otherFlowPane;


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
        yyFlowPane.setVgap(1);
        yyFlowPane.setHgap(1);
        fyFlowPane.setVgap(1);
        fyFlowPane.setHgap(1);
        otherFlowPane.setVgap(1);
        otherFlowPane.setHgap(1);

        paneDatas = DbHelper.getInstance().searchAllCodeIPABase();

        setupPaneWithType("1");
        setupPaneWithType("2");
        setupPaneWithType("3");
    }

    public void setupPaneWithType(String type){
        FlowPane flowPane = null;
        if (type.equalsIgnoreCase("1")){
            flowPane = yyFlowPane;
        }else if (type.equalsIgnoreCase("2")){
            flowPane = fyFlowPane;
        }else if (type.equalsIgnoreCase("3")){
            flowPane = otherFlowPane;
        }

        ObservableList<CodeIPABase> temp = paneDatas.filtered(new Predicate<CodeIPABase>() {
            @Override
            public boolean test(CodeIPABase codeIPABase) {
                if (codeIPABase.getType().equalsIgnoreCase(type)){
                    return true;
                }
                return false;
            }
        });

        for (int i = 0; i < temp.size(); i++) {
            CodeIPABase ipaBase = temp.get(i);
            Button tempBtn = new Button(ipaBase.getContent());
            tempBtn.setUserData(ipaBase.getCode());
            tempBtn.setMinSize(50,50);
            tempBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (listener != null){
                        String str = ((String) tempBtn.getUserData());
                        listener.btnClickWithText(unicode2String("\\u"+str));
                    }
                }
            });
            flowPane.getChildren().add(tempBtn);
        }
    }

    public static String unicode2String(String unicode) {

        StringBuffer string = new StringBuffer();

        String[] hex = unicode.split("\\\\u");

        for (int i = 1; i < hex.length; i++) {

            // 转换出每一个代码点
            int data = Integer.parseInt(hex[i], 16);

            // 追加成string
            string.append((char) data);
        }

        return string.toString();
    }
}
