package sample.controller;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import sample.entity.CodeBase;
import sample.entity.CodeIPABase;
import sample.entity.CodeLangHanYu;
import sample.util.DbHelper;
import sample.util.TextUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


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
    VBox rightBox;

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
    @FXML
    HBox rightBtnBox;
    @FXML
    FlowPane btnPane;

    private ObservableList<CodeBase> codeBaseDatas;
    private ObservableList<CodeLangHanYu> chineseLangDatas;
    private ObservableList<CodeIPABase> codeIPADatas;

    Object selItem;
    boolean autoScroll = false;
    boolean tableViewScroll = false;

    public void setType(int type) {
        this.type = type;
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


        sameInit();
    }

    private void sameInit() {
        removeUnVisibleChild(rightBtnBox);
        setTableEvent();
        setInputNumberEvent();
        if (tableView.getItems() != null) {
            int allSize = tableView.getItems().size();
            label_maxNumber.setText(allSize + "");
        }

    }

    private void removeUnVisibleChild(Pane parent){
        ObservableList<Node> children = parent.getChildren();
        for(int i=children.size()-1;i>=0;i--){
            if (!children.get(i).isVisible()){
                children.remove(i);
            }
        }
    }

    private void initHanYuFangYanZiBiao() {
        label_tableName.setText("汉语方言调查字表");

        btn_add.setVisible(true);
        btn_modify.setVisible(true);
        btn_del.setVisible(true);
        btn_search.setVisible(true);
        btn_replace.setVisible(true);
        btn_image.setVisible(true);
        btn_video.setVisible(true);
        btn_save.setVisible(true);
        btn_import.setVisible(true);
        btn_export.setVisible(true);
        btn_refresh.setVisible(true);

        removeUnVisibleChild(btnPane);

        box_code.setVisible(true);
        box_singleWord.setVisible(true);
        box_level.setVisible(true);
        box_alphabet.setVisible(true);
        box_english.setVisible(true);
        box_voiceRange.setVisible(true);
        box_notes.setVisible(true);
        done.setVisible(true);
        cancel.setVisible(true);
        tabPane.setVisible(true);

        removeUnVisibleChild(rightBox);

        codeBaseDatas = DbHelper.getInstance().searchCodeBaseWithCode(0);

        TableColumn<CodeBase,String> codeCol = new TableColumn<>("编码");
        TableColumn<CodeBase,String> contentCol = new TableColumn<>("单字");
        TableColumn<CodeBase,String> rankCol = new TableColumn<>("分级");
        TableColumn<CodeBase,String> yunCol = new TableColumn<>("声韵");
        TableColumn<CodeBase,String> spellCol = new TableColumn<>("拼音");
        TableColumn<CodeBase,String> IPACol = new TableColumn<>("IPA");
        TableColumn<CodeBase,String> englishCol = new TableColumn<>("英语");
        TableColumn<CodeBase,String> noteCol = new TableColumn<>("注释");

        codeCol.setCellValueFactory(new PropertyValueFactory<>("code"));
        contentCol.setCellValueFactory(new PropertyValueFactory<>("content"));
        rankCol.setCellValueFactory(new PropertyValueFactory<>("rank"));
        yunCol.setCellValueFactory(new PropertyValueFactory<>("yun"));
        spellCol.setCellValueFactory(new PropertyValueFactory<>("spell"));
        IPACol.setCellValueFactory(new PropertyValueFactory<>("IPA"));
        englishCol.setCellValueFactory(new PropertyValueFactory<>("english"));
        noteCol.setCellValueFactory(new PropertyValueFactory<>("note"));

        tableView.setItems(codeBaseDatas);
        tableView.getColumns().addAll(codeCol,contentCol,yunCol,rankCol,spellCol,englishCol,noteCol);
    }

    private void initCiHui() {
        label_tableName.setText("词汇调查表");

        btn_add.setVisible(true);
        btn_modify.setVisible(true);
        btn_del.setVisible(true);
        btn_search.setVisible(true);
        btn_replace.setVisible(true);
        btn_image.setVisible(true);
        btn_video.setVisible(true);
        btn_save.setVisible(true);
        btn_import.setVisible(true);
        btn_export.setVisible(true);
        btn_refresh.setVisible(true);

        removeUnVisibleChild(btnPane);

        box_code.setVisible(true);
        box_singleWord.setVisible(true);
        box_level.setVisible(true);
        box_alphabet.setVisible(true);
        box_english.setVisible(true);
        box_voiceRange.setVisible(true);
        box_notes.setVisible(true);
        done.setVisible(true);
        cancel.setVisible(true);
        tabPane.setVisible(true);

        removeUnVisibleChild(rightBox);

        codeBaseDatas = DbHelper.getInstance().searchCodeBaseWithCode(1);


        TableColumn codeCol = new TableColumn<>("编码");
        TableColumn contentCol = new TableColumn<>("词条");
        TableColumn rankCol = new TableColumn<>("分级");
        TableColumn yunCol = new TableColumn<>("声韵");
        TableColumn spellCol = new TableColumn<>("拼音");
        TableColumn IPACol = new TableColumn<>("IPA");
        TableColumn englishCol = new TableColumn<>("英语");
        TableColumn noteCol = new TableColumn<>("注释");

        codeCol.setCellValueFactory(new PropertyValueFactory<>("code"));
        contentCol.setCellValueFactory(new PropertyValueFactory<>("content"));
        rankCol.setCellValueFactory(new PropertyValueFactory<>("rank"));
        yunCol.setCellValueFactory(new PropertyValueFactory<>("yun"));
        spellCol.setCellValueFactory(new PropertyValueFactory<>("spell"));
        IPACol.setCellValueFactory(new PropertyValueFactory<>("IPA"));
        englishCol.setCellValueFactory(new PropertyValueFactory<>("english"));
        noteCol.setCellValueFactory(new PropertyValueFactory<>("note"));

        tableView.setItems(codeBaseDatas);
        tableView.getColumns().addAll(codeCol,contentCol,rankCol,spellCol,englishCol,noteCol);

    }

    private void initRiChangYongJu() {
        label_tableName.setText("语法例句日常用句调查表");

        btn_add.setVisible(true);
        btn_modify.setVisible(true);
        btn_del.setVisible(true);
        btn_search.setVisible(true);
        btn_replace.setVisible(true);
        btn_image.setVisible(true);
        btn_video.setVisible(true);
        btn_save.setVisible(true);
        btn_import.setVisible(true);
        btn_export.setVisible(true);
        btn_refresh.setVisible(true);

        removeUnVisibleChild(btnPane);

        box_code.setVisible(true);
        box_level.setVisible(true);
        box_english.setVisible(true);
        box_notes.setVisible(true);
        done.setVisible(true);
        cancel.setVisible(true);
        tabPane.setVisible(true);

        removeUnVisibleChild(rightBox);

        codeBaseDatas = DbHelper.getInstance().searchCodeBaseWithCode(2);


        TableColumn codeCol = new TableColumn<>("编码");
        TableColumn contentCol = new TableColumn<>("句子");
        TableColumn rankCol = new TableColumn<>("分级");
        TableColumn yunCol = new TableColumn<>("声韵");
        TableColumn spellCol = new TableColumn<>("拼音");
        TableColumn IPACol = new TableColumn<>("IPA");
        TableColumn englishCol = new TableColumn<>("英语");
        TableColumn noteCol = new TableColumn<>("注释");

        codeCol.setCellValueFactory(new PropertyValueFactory<>("code"));
        contentCol.setCellValueFactory(new PropertyValueFactory<>("content"));
        rankCol.setCellValueFactory(new PropertyValueFactory<>("rank"));
        yunCol.setCellValueFactory(new PropertyValueFactory<>("yun"));
        spellCol.setCellValueFactory(new PropertyValueFactory<>("spell"));
        IPACol.setCellValueFactory(new PropertyValueFactory<>("IPA"));
        englishCol.setCellValueFactory(new PropertyValueFactory<>("english"));
        noteCol.setCellValueFactory(new PropertyValueFactory<>("note"));

        tableView.setItems(codeBaseDatas);
        tableView.getColumns().addAll(codeCol,contentCol,rankCol,englishCol,noteCol);
    }

    private void initHuaYuZhuTi() {
        label_tableName.setText("话语主题表");

        btn_add.setVisible(true);
        btn_modify.setVisible(true);
        btn_del.setVisible(true);
        btn_search.setVisible(true);
        btn_replace.setVisible(true);
        btn_image.setVisible(true);
        btn_video.setVisible(true);
        btn_save.setVisible(true);
        btn_import.setVisible(true);
        btn_export.setVisible(true);
        btn_refresh.setVisible(true);

        removeUnVisibleChild(btnPane);

        box_code.setVisible(true);
        box_level.setVisible(true);
        box_english.setVisible(true);
        box_notes.setVisible(true);
        done.setVisible(true);
        cancel.setVisible(true);
        tabPane.setVisible(true);

        removeUnVisibleChild(rightBox);


        codeBaseDatas = DbHelper.getInstance().searchCodeBaseWithCode(3);


        TableColumn codeCol = new TableColumn<>("编码");
        TableColumn contentCol = new TableColumn<>("词条");
        TableColumn rankCol = new TableColumn<>("分级");
        TableColumn yunCol = new TableColumn<>("声韵");
        TableColumn spellCol = new TableColumn<>("拼音");
        TableColumn IPACol = new TableColumn<>("IPA");
        TableColumn englishCol = new TableColumn<>("英语");
        TableColumn noteCol = new TableColumn<>("注释");

        codeCol.setCellValueFactory(new PropertyValueFactory<>("code"));
        contentCol.setCellValueFactory(new PropertyValueFactory<>("content"));
        rankCol.setCellValueFactory(new PropertyValueFactory<>("rank"));
        yunCol.setCellValueFactory(new PropertyValueFactory<>("yun"));
        spellCol.setCellValueFactory(new PropertyValueFactory<>("spell"));
        IPACol.setCellValueFactory(new PropertyValueFactory<>("IPA"));
        englishCol.setCellValueFactory(new PropertyValueFactory<>("english"));
        noteCol.setCellValueFactory(new PropertyValueFactory<>("note"));

        tableView.setItems(codeBaseDatas);
        tableView.getColumns().addAll(codeCol,contentCol,rankCol,englishCol,noteCol);
    }

    private void initHanYuFangYan() {
        label_tableName.setText("汉语方言代码");

        btn_add.setVisible(true);
        btn_modify.setVisible(true);
        btn_del.setVisible(true);
        btn_search.setVisible(true);
        btn_replace.setVisible(true);
        btn_image.setVisible(true);
        btn_video.setVisible(true);
        btn_save.setVisible(true);
        btn_import.setVisible(true);
        btn_export.setVisible(true);
        btn_refresh.setVisible(true);

        removeUnVisibleChild(btnPane);

        box_index.setVisible(true);
        box_name.setVisible(true);
        box_distribution.setVisible(true);
        done.setVisible(true);
        cancel.setVisible(true);

        removeUnVisibleChild(rightBox);

        chineseLangDatas = DbHelper.getInstance().searchAllCodeLangHanYu();

        TableColumn codeCol = new TableColumn<>("序号");
        TableColumn nameCol = new TableColumn<>("名称");
        TableColumn regionCol = new TableColumn<>("主要分布地");

        codeCol.setCellValueFactory(new PropertyValueFactory<>("code"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        regionCol.setCellValueFactory(new PropertyValueFactory<>("region"));

        tableView.setItems(chineseLangDatas);
        tableView.getColumns().addAll(codeCol,nameCol,regionCol);
    }

    private void initYuanYinZiFu() {
        label_tableName.setText("元音字符代码表");

        btn_add.setVisible(true);
        btn_modify.setVisible(true);
        btn_del.setVisible(true);
        btn_search.setVisible(true);
        btn_replace.setVisible(true);
        btn_save.setVisible(true);
        btn_import.setVisible(true);
        btn_export.setVisible(true);
        btn_refresh.setVisible(true);

        removeUnVisibleChild(btnPane);

        box_code.setVisible(true);
        box_chart.setVisible(true);
        close.setVisible(true);

        removeUnVisibleChild(rightBox);

        codeIPADatas = DbHelper.getInstance().searchAllCodeIPABase();

        TableColumn<CodeIPABase,String> codeCol = new TableColumn<>("编码");
        TableColumn<CodeIPABase,String> contentCol = new TableColumn<>("元音字符");

        codeCol.setCellValueFactory(new PropertyValueFactory<>("code"));

        contentCol.setCellValueFactory(new PropertyValueFactory<>("content"));

        tableView.setItems(codeIPADatas);
        tableView.getColumns().addAll(codeCol,contentCol);
    }

    private void setInputNumberEvent() {

        input_number.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (TextUtil.isEmpty(newValue)) {
                    return;
                }
                Pattern pattern = Pattern.compile("\\d+");
                String text = input_number.getText();
                if (!pattern.matcher(text).matches()) {
                    return;
                }
                System.out.println("input_number " + text);
                tableView.getSelectionModel().select(Integer.valueOf(text));
//                tableView.getSelectionModel().select(Integer.valueOf(newValue));

            }
        });
        input_number.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                tableViewScroll = false;
            }
        });
    }

    private void setTableEvent() {
        tableView.setOnTouchPressed(new EventHandler<TouchEvent>() {
            @Override
            public void handle(TouchEvent event) {
                System.out.println("setOnTouchPressed");
            }
        });

        tableView.onKeyPressedProperty().addListener(new ChangeListener<EventHandler<? super KeyEvent>>() {
            @Override
            public void changed(ObservableValue<? extends EventHandler<? super KeyEvent>> observable, EventHandler<? super KeyEvent> oldValue, EventHandler<? super KeyEvent> newValue) {
                System.out.println("ChangeListener");
            }
        });
        tableView.onMousePressedProperty().addListener(new ChangeListener<EventHandler<? super MouseEvent>>() {
            @Override
            public void changed(ObservableValue<? extends EventHandler<? super MouseEvent>> observable, EventHandler<? super MouseEvent> oldValue, EventHandler<? super MouseEvent> newValue) {
                System.out.println("ChangeListener");
            }
        });
        tableView.onTouchPressedProperty().addListener(new ChangeListener<EventHandler<? super TouchEvent>>() {
            @Override
            public void changed(ObservableValue<? extends EventHandler<? super TouchEvent>> observable, EventHandler<? super TouchEvent> oldValue, EventHandler<? super TouchEvent> newValue) {
                System.out.println("ChangeListener");
            }
        });
        tableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("tableView setOnMouseClicked");
                selItem = tableView.getSelectionModel().getSelectedItem();
                autoScroll = false;
                tableViewScroll = true;
            }

        });

        tableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if (newValue != null && newValue != oldValue) {
                    selItem = tableView.getSelectionModel().getSelectedItem();
                    if (selItem != null) {
                        showSelItemToEditPane();
                        if (!tableViewScroll) {
                            System.out.println("!tableViewScroll");
                            return;
                        }
                        System.out.println("tableViewScroll");

                        int selIndex = tableView.getSelectionModel().getFocusedIndex();

                        if (autoScroll)
                            tableView.scrollToColumnIndex(selIndex);


                        int objIndex = tableView.getItems().indexOf(newValue);
                        System.out.println(objIndex);
                        input_number.textProperty().set(objIndex + 1 + "");
                    }
                }
            }
        });
    }

    private void showSelItemToEditPane() {
        if (selItem instanceof CodeBase) {
            input_code.setText(((CodeBase) selItem).getCode());
            input_singleWord.setText(((CodeBase) selItem).getContent());
            input_level.setText(((CodeBase) selItem).getRank());
            input_alphabet.setText(((CodeBase) selItem).getSpell());
            input_english.setText(((CodeBase) selItem).getEnglish());
            input_voiceRange.setText(((CodeBase) selItem).getYun());
            input_notes.setText(((CodeBase) selItem).getNote());
        } else if (selItem instanceof CodeLangHanYu) {
            input_index.setText(((CodeLangHanYu) selItem).getCode());
            input_name.setText(((CodeLangHanYu) selItem).getName());
            input_distribution.setText(((CodeLangHanYu) selItem).getRegion());
        } else if (selItem instanceof CodeIPABase) {
            input_code.setText(((CodeIPABase) selItem).getCode());
            input_chart.setText(((CodeIPABase) selItem).getContent());
        }
    }

    //右侧编辑框 确定按钮
    public void onEditPaneDoneClick() {
        //TODO
    }

    //右侧编辑框 取消按钮
    public void onEditPaneCancelClick() {
        showSelItemToEditPane();
    }

    //右侧编辑框 关闭按钮
    public void onEditPaneCloseClick() {
        //TODO
    }

    //顶部 上一页按钮
    public void onTopToolsBeforePageClick() {
        autoScroll = true;
        tableViewScroll = true;
        tableView.getSelectionModel().selectFirst();
    }

    //顶部 上一行按钮
    public void onTopToolsBeforeLineClick() {
        autoScroll = true;
        tableViewScroll = true;
        tableView.getSelectionModel().selectPrevious();
    }

    //顶部 下一页按钮
    public void onTopToolsNextPageClick() {
        autoScroll = true;
        tableViewScroll = true;
        tableView.getSelectionModel().selectLast();
    }

    //顶部 下一行按钮
    public void onTopToolsNextLineClick() {
        autoScroll = true;
        tableViewScroll = true;
        tableView.getSelectionModel().selectNext();
    }

    //顶部 新添按钮
    public void onTopToolsAddClick() {

    }

    //顶部 修改按钮
    public void onTopToolsModifyClick() {

    }

    //顶部 删除按钮
    public void onTopToolsDelClick() {

    }

    //顶部 查找按钮
    public void onTopToolsSearchClick() {

    }

    //顶部 替换按钮
    public void onTopToolsReplaceClick() {

    }

    //顶部 图像按钮
    public void onTopToolsImageClick() {

    }

    //顶部 视频按钮
    public void onTopToolsVideoClick() {

    }

    //顶部 保存按钮
    public void onTopToolsSaveClick() {

    }

    //顶部 导入按钮
    public void onTopToolsImportClick() {

    }

    //顶部 导出按钮
    public void onTopToolsExportClick() {

    }

    //顶部 刷新按钮
    public void onTopToolsRefreshClick() {
        setType(type);
    }





}

