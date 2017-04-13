package sample.diycontrol;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import sample.Main;

import java.io.IOException;

/**
 * Created by Bee on 2017/4/13.
 */
public class TableTopControl extends FlowPane{
    @FXML
    private Button firstBtn;
    @FXML
    private Button preBtn;
    @FXML
    private Button nextBtn;
    @FXML
    private Button lastBtn;

    @FXML
    private TextField nowIndexTF;
    @FXML
    private Label allCountL;

    @FXML
    private Button refreshBtn;
    @FXML
    private Button selectAllBtn;
    @FXML
    private Button selectAnotherBtn;
    @FXML
    private Button keepBtn;
    @FXML
    private Button disappearBtn;
    @FXML
    private Button delBtn;
    @FXML
    private Button showAllBtn;
    @FXML
    private Button showOnlyKeepBtn;
    @FXML
    private Button showOnlyDisappearBtn;


    private TableTopCtlListener btnClickListener;

    public TableTopControl() {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("view/control/tableTopControl.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置监听器
     * @param btnClickListener
     */
    public void setBtnClickListener(TableTopCtlListener btnClickListener) {
        this.btnClickListener = btnClickListener;
    }

    /**
     * 设置当前下标
     * @param index
     */
    public void setNowIndex(String index){
        nowIndexTF.setText(index);
    }

    /**
     * 获取当前下标
     * @return
     */
    public int getNowIndex(){
        return Integer.parseInt(nowIndexTF.getText());
    }

    /**
     * 设置数据总条数
     * @param allCount
     */
    public void setAllCount(int allCount){
        allCountL.setText(Integer.toString(allCount));
    }


    //按钮事件
    @FXML
    public void firstBtnClick(){
        System.out.println("firstBtnClick");
        btnClickListener.onBtnClick(ClickType.FirstBtnClick);
    }

    @FXML
    public void preBtnClick(){
        System.out.println("preBtnClick");
        btnClickListener.onBtnClick(ClickType.PreBtnClick);
    }

    @FXML
    public void nextBtnClick(){
        System.out.println("nextBtnClick");
        btnClickListener.onBtnClick(ClickType.NextBtnClick);
    }

    @FXML
    public void lastBtnClick(){
        System.out.println("lastBtnClick");
        btnClickListener.onBtnClick(ClickType.LastBtnClick);
    }

    @FXML
    public void refreshBtnClick(){
        System.out.println("refreshBtnClick");
        btnClickListener.onBtnClick(ClickType.RefreshBtnClick);
    }

    @FXML
    public void selectAllBtnClick(){
        System.out.println("selectAllBtnClick");
        btnClickListener.onBtnClick(ClickType.SelectAllBtnClick);
    }

    @FXML
    public void selectAnotherBtnClick(){
        System.out.println("SelectAnotherBtnClick");
        btnClickListener.onBtnClick(ClickType.SelectAnotherBtnClick);
    }

    @FXML
    public void keepBtnClick(){
        System.out.println("keepBtnClick");
        btnClickListener.onBtnClick(ClickType.KeepBtnClick);
    }

    @FXML
    public void disappearBtnClick(){
        System.out.println("disappearBtnClick");
        btnClickListener.onBtnClick(ClickType.DisappearBtnClick);
    }

    @FXML
    public void delBtnClick(){
        System.out.println("delBtnClick");
        btnClickListener.onBtnClick(ClickType.DelBtnClick);
    }

    @FXML
    public void showAllBtnClick(){
        System.out.println("showAllBtnClick");
        btnClickListener.onBtnClick(ClickType.ShowAllBtnClick);
    }

    @FXML
    public void showOnlyKeepBtnClick(){
        System.out.println("showOnlyKeepBtnClick");
        btnClickListener.onBtnClick(ClickType.ShowOnlyKeepBtnClick);
    }

    @FXML
    public void showOnlyDisappearBtnClick(){
        System.out.println("showOnlyDisappearBtnClick");
        btnClickListener.onBtnClick(ClickType.ShowOnlyDisappearBtnClick);
    }
}
