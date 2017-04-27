package sample.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import sample.entity.MetaData;
import sample.util.TextUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by chenweiqi on 2017/4/27.
 */
public class AddMetadataController extends BaseController {


    @FXML
    TextField input_key1;
    @FXML
    TextField input_key2;
    @FXML
    TextField input_key3;
    @FXML
    TextField input_key4;
    @FXML
    TextField input_key5;
    @FXML
    TextField input_value1;
    @FXML
    TextField input_value2;
    @FXML
    TextField input_value3;
    @FXML
    TextField input_value4;
    @FXML
    TextField input_value5;
    @FXML
    GridPane gridPane;

    OnDoneClickListener onDoneClickListener;


    public void setMetaData(String json){
        List<MetaData> metaData = new Gson().fromJson(json,new TypeToken<List<MetaData>>(){}.getType());

        showMetaData(metaData);
    }

    public void setMetaData(List<MetaData> metaDataList){
        showMetaData(metaDataList);

    }

    public void setOnDoneClickListener(OnDoneClickListener onDoneClickListener) {
        this.onDoneClickListener = onDoneClickListener;
    }

    @FXML
    private void onDoneClick(){
        if (onDoneClickListener!=null){
            onDoneClickListener.onClick(this);
        }
    }

    @FXML
    private void onCancelClick(){
        close();
    }

    public List<MetaData> getMetaData(){
        List<MetaData> metaDatas = new ArrayList<>();

        MetaData metaData1 = new MetaData();
        MetaData metaData2 = new MetaData();
        MetaData metaData3 = new MetaData();
        MetaData metaData4 = new MetaData();
        MetaData metaData5 = new MetaData();

        metaData1.key = input_key1.getText();
        metaData2.key = input_key2.getText();
        metaData3.key = input_key3.getText();
        metaData4.key = input_key4.getText();
        metaData5.key = input_key5.getText();


        metaData1.value = input_value1.getText();
        metaData2.value = input_value2.getText();
        metaData3.value = input_value3.getText();
        metaData4.value = input_value4.getText();
        metaData5.value = input_value5.getText();

        metaDatas.add(metaData1);
        metaDatas.add(metaData2);
        metaDatas.add(metaData3);
        metaDatas.add(metaData4);
        metaDatas.add(metaData5);


        return metaDatas;
    }


    public interface OnDoneClickListener{
        void onClick(AddMetadataController controller);
    }

    private void showMetaData(List<MetaData> metaData){
        input_key1.setText(metaData.get(0).key);
        input_key2.setText(metaData.get(1).key);
        input_key3.setText(metaData.get(2).key);
        input_key4.setText(metaData.get(3).key);
        input_key5.setText(metaData.get(4).key);

        input_value1.setText(metaData.get(0).value);
        input_value2.setText(metaData.get(1).value);
        input_value3.setText(metaData.get(2).value);
        input_value4.setText(metaData.get(3).value);
        input_value5.setText(metaData.get(4).value);
    }
}
