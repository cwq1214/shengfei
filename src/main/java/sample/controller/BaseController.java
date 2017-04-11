package sample.controller;

import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by chenweiqi on 2017/3/29.
 */
public class BaseController implements Initializable {
    public Stage mStage;
    public Parent mParent;

    public Object preData;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    //Stage OnShowing
    public void onCreatedView(){
        System.out.println("onCreatedView");
    }
    //Stage OnCloseRequest
    public void onStop(){
        System.out.println("onStop");
    }

    public void show() {
        if (mStage != null)
            mStage.show();
    }

    public void close() {
        if (mStage != null)
            mStage.close();
    }

    public Stage getmStage() {
        return mStage;
    }

    public void setPreData(Object preData) {
        this.preData = preData;
    }

    public void setmStage(Stage mStage) {
        this.mStage = mStage;
    }

    public Parent getmParent() {
        return mParent;
    }

    public void setmParent(Parent mParent) {
        this.mParent = mParent;
    }

    /**
     * 在传递完数据preData后回调用这个函数
     */
    public void prepareInit() {
    }

}