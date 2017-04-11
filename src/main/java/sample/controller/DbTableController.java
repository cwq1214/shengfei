package sample.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javax.xml.soap.Text;


/**
 * Created by chenweiqi on 2017/4/10.
 */
public class DbTableController extends BaseController {

    //打开表的类型
    public static final int TYPE_HAN_YU_FANG_YAN_ZI_BIAO = 1;
    public static final int TYPE_CI_HUI = 2;
    public static final int TYPE_RI_CHANG_YONG_JU = 3;
    public static final int TYPE_HUA_YU_ZHU_TI = 4;
    public static final int TYPE_HAN_YU_FANG_YAN = 5;
    public static final int TYPE_YUAN_YIN_ZI_FU = 6;
    private int type = 0;

    //VIEW
    @FXML
    ImageView btn_beforeLine;
    @FXML
    ImageView btn_beforePage;
    @FXML
    ImageView btn_nextLine;
    @FXML
    ImageView btn_nextPage;
    @FXML
    Label label_maxNumber;
    @FXML
    TextField input_number;
    @FXML
    Button btn_add;
    @FXML
    Button btn_modify;
    @FXML
    Button btn_del;
    @FXML
    Button btn_search;
    @FXML
    Button btn_replace;
    @FXML
    Button btn_image;
    @FXML
    Button btn_video;
    @FXML
    Button btn_save;
    @FXML
    Button btn_import;
    @FXML
    Button btn_export;
    @FXML
    Button btn_refresh;
    @FXML
    Label label_tableName;
    @FXML
    TableView tableView;
    @FXML
    HBox box_code;
    @FXML
    TextField input_code;
    @FXML
    HBox box_index;
    @FXML
    TextField input_index;
    @FXML
    HBox box_name;
    @FXML
    TextField input_name;
    @FXML
    VBox box_distribution;
    @FXML
    TextArea input_distribution;
    @FXML
    HBox box_singleWord;
    @FXML
    TextField input_singleWord;
    @FXML
    HBox box_entry;
    @FXML
    TextArea input_entry;
    @FXML
    HBox box_level;
    @FXML
    TextField input_level;
    @FXML
    HBox box_alphabet;
    @FXML
    TextField input_alphabet;
    @FXML
    HBox box_english;
    @FXML
    TextArea input_english;
    @FXML
    HBox box_voiceRange;
    @FXML
    TextField input_voiceRange;
    @FXML
    HBox box_notes;
    @FXML
    TextArea input_notes;
    @FXML
    HBox box_chart;
    @FXML
    TextField input_chart;
    @FXML
    Button done;
    @FXML
    Button cancel;
    @FXML
    Button close;
    @FXML
    TabPane tabPane;
    @FXML
    Tab tab_image;
    @FXML
    Tab tab_video;


    @Override
    public void onCreatedView() {
        super.onCreatedView();
        if (type == 0) {
            throw new RuntimeException("type can not be 0");
        }
        switch (type) {
            case TYPE_HAN_YU_FANG_YAN_ZI_BIAO:
                initHanYuFangYanZiBiao();
                break;
            case TYPE_CI_HUI:
                initCiHui();
                break;
            case TYPE_RI_CHANG_YONG_JU:
                initRiChangYongJu();
                break;
            case TYPE_HUA_YU_ZHU_TI:
                initHuaYuZhuTi();
                break;
            case TYPE_HAN_YU_FANG_YAN:
                initHanYuFangYan();
                break;
            case TYPE_YUAN_YIN_ZI_FU:
                initYuanYinZiFu();
                break;
        }
    }

    private void initHanYuFangYanZiBiao() {

    }

    private void initCiHui() {

    }

    private void initRiChangYongJu() {

    }

    private void initHuaYuZhuTi() {

    }

    private void initHanYuFangYan() {

    }

    private void initYuanYinZiFu() {

    }

}
