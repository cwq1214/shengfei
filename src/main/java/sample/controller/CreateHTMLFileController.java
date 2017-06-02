package sample.controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import sample.util.ExportUtil;
import sample.util.ViewUtil;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


/**
 * Created by chenweiqi on 2017/4/25.
 */
public class CreateHTMLFileController extends BaseController {
    @FXML
    Button btn_previous;
    @FXML
    Button btn_next;
    @FXML
    Button btn_close;
    @FXML
    Label label_1;
    @FXML
    Label label_2;
    @FXML
    Label label_3;
    @FXML
    Label label_4;
    @FXML
    BorderPane borderPane;
    @FXML
    VBox pane_labelBox;

    CreateHTML1Controller createHTML1Controller;
    CreateHTML2Controller createHTML2Controller;
    CreateHTML3Controller createHTML3Controller;
    CreateHTML4Controller createHTML4Controller;

    int currentPage = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);


        try {
            createHTML1Controller = ViewUtil.getInstance().openCreateHTML1View();
            createHTML2Controller = ViewUtil.getInstance().openCreateHTML2View();
            createHTML3Controller = ViewUtil.getInstance().openCreateHTML3View();
            createHTML4Controller = ViewUtil.getInstance().openCreateHTML4View();
            createHTML4Controller.setListener(new CreateHTML4Controller.Html4Listener() {
                @Override
                public void onClickCreateAndShow() {
                    createYD();
                }

                @Override
                public void onClickCreate() {
                    createYD();
                }

                @Override
                public void onClickCancel() {
                    mStage.close();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        borderPane.setCenter(createHTML1Controller.getmParent());
    }

    private void createYD(){
        ExportUtil.exportYD(createHTML1Controller.ydbh.getText(),
                createHTML1Controller.ydmc.getText(),
                createHTML1Controller.cjr.getText(),
                createHTML1Controller.gxr.getText(),
                createHTML1Controller.cjrq.getText(),
                createHTML1Controller.cjdd.getText(),
                createHTML1Controller.rjgj.getText(),
                createHTML1Controller.gcjg.getText(),
                createHTML1Controller.urlLink.getText(),
                createHTML1Controller.zyms.getText(),
                createHTML3Controller.gkTF.getText(),
                createHTML3Controller.zbTF.getText(),
                createHTML3Controller.chTF.getText(),
                createHTML3Controller.jzTF.getText(),
                createHTML3Controller.yxTF.getText(),
                createHTML3Controller.dzdzTF.getText(),
                createHTML3Controller.chdzTF.getText(),
                createHTML3Controller.jzdzTF.getText(),
                createHTML3Controller.wbwyTF.getText());
    }

    @FXML
    private void onNextClick(){

        switch (currentPage){
            case 0:
                borderPane.setCenter(createHTML2Controller.getmParent());
                break;
            case 1:
                borderPane.setCenter(createHTML3Controller.getmParent());
                break;
            case 2:
                borderPane.setCenter(createHTML4Controller.getmParent());
                break;
            case 3:
                break;
        }
        currentPage++;
        btn_previous.setVisible(!(currentPage==0));
        btn_next.setVisible(!(currentPage==3));
        setLabelSel();

    }

    @FXML
    private void onPrevClick(){

        switch (currentPage){
            case 0:
                break;
            case 1:
                borderPane.setCenter(createHTML1Controller.getmParent());
                break;
            case 2:
                borderPane.setCenter(createHTML2Controller.getmParent());
                break;
            case 3:
                borderPane.setCenter(createHTML3Controller.getmParent());
                break;
        }
        currentPage--;
        btn_previous.setVisible(!(currentPage==0));
        btn_next.setVisible(!(currentPage==3));
        setLabelSel();


    }
    @FXML
    private void onCloseClick(){
        close();
    }

    private void setLabelSel(){
        ObservableList<Node> children = pane_labelBox.getChildren();

        for (int i=0,max = children.size();i<max;i++){
            if (currentPage==i){
                children.get(i).getStyleClass().add("sel");

            }else {
                if (children.get(i).getStyleClass().contains("sel"))
                    children.get(i).getStyleClass().remove("sel");

            }
        }
    }


}
