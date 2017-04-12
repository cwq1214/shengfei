package sample.controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import sample.entity.CodeBase;
import sample.entity.CodeIPABase;
import sample.entity.CodeLangHanYu;
import sample.util.DbHelper;

import java.util.ArrayList;
import java.util.List;


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
    FlowPane btnPane;

    private ObservableList<CodeBaseItem> codeBaseDatas;
    private ObservableList<ChineseLangItem> chineseLangDatas;
    private ObservableList<CodeIPAItem> codeIPADatas;

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
    }

    private void removeUnVisibleChild(Pane parent){
        ObservableList<Node> children = parent.getChildren();
        for(int i=children.size()-1;i>=0;i--){
            if (!children.get(i).isVisible()){
                children.remove(i);
            }
        }
    }

    private ObservableList<CodeBaseItem> codeBaselist2Obserlist(List<CodeBase> dataList){
        List<CodeBaseItem> datas = new ArrayList<CodeBaseItem>();

        for (CodeBase cb: dataList) {
            datas.add(new CodeBaseItem(cb.code,cb.codeType,cb.content,cb.rank,cb.yun,cb.spell,cb.IPA,cb.english,cb.note,cb.mwfy));
        }

        return FXCollections.observableList(datas);
    }

    private ObservableList<ChineseLangItem> chineseLanglist2Obserlist(List<CodeLangHanYu> dataList){
        List<ChineseLangItem> datas = new ArrayList<ChineseLangItem>();

        for (CodeLangHanYu item: dataList) {
            datas.add(new ChineseLangItem(item.code,item.name,item.region));
        }

        return FXCollections.observableList(datas);
    }

    private ObservableList<CodeIPAItem> codeIPAList2Obserlist(List<CodeIPABase> dataList){
        List<CodeIPAItem> datas = new ArrayList<CodeIPAItem>();

        for (CodeIPABase item: dataList) {
            datas.add(new CodeIPAItem(item.code,item.content,item.type));
        }

        return FXCollections.observableList(datas);
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

        codeBaseDatas = codeBaselist2Obserlist(DbHelper.getInstance().searchCodeBaseWithCode(0));

        TableColumn<CodeBaseItem,String> codeCol = new TableColumn<>("编码");
//        TableColumn<CodeBaseItem,Integer> codeTypeCol = new TableColumn<>("类型");
        TableColumn<CodeBaseItem,String> contentCol = new TableColumn<>("单字");
        TableColumn<CodeBaseItem,String> rankCol = new TableColumn<>("分级");
        TableColumn<CodeBaseItem,String> yunCol = new TableColumn<CodeBaseItem, String>("声韵");
        TableColumn<CodeBaseItem,String> spellCol = new TableColumn<>("拼音");
        TableColumn<CodeBaseItem,String> IPACol = new TableColumn<CodeBaseItem, String>("IPA");
        TableColumn<CodeBaseItem,String> englishCol = new TableColumn<CodeBaseItem, String>("英语");
        TableColumn<CodeBaseItem,String> noteCol = new TableColumn<CodeBaseItem, String>("注释");
//        TableColumn<CodeBaseItem,String> mwfyCol = new TableColumn<CodeBaseItem, String>("明文方言");

        codeCol.setCellFactory(TextFieldTableCell.<CodeBaseItem>forTableColumn());
        codeCol.setCellValueFactory(new PropertyValueFactory<>("code"));;

        contentCol.setCellFactory(TextFieldTableCell.<CodeBaseItem>forTableColumn());
        contentCol.setCellValueFactory(new PropertyValueFactory<CodeBaseItem, String>("content"));

        rankCol.setCellFactory(TextFieldTableCell.<CodeBaseItem>forTableColumn());
        rankCol.setCellValueFactory(new PropertyValueFactory<CodeBaseItem, String>("rank"));

        yunCol.setCellFactory(TextFieldTableCell.<CodeBaseItem>forTableColumn());
        yunCol.setCellValueFactory(new PropertyValueFactory<CodeBaseItem, String>("yun"));

        spellCol.setCellFactory(TextFieldTableCell.<CodeBaseItem>forTableColumn());
        spellCol.setCellValueFactory(new PropertyValueFactory<CodeBaseItem,String>("spell"));

        IPACol.setCellFactory(TextFieldTableCell.<CodeBaseItem>forTableColumn());
        IPACol.setCellValueFactory(new PropertyValueFactory<CodeBaseItem, String>("IPA"));

        englishCol.setCellFactory(TextFieldTableCell.<CodeBaseItem>forTableColumn());
        englishCol.setCellValueFactory(new PropertyValueFactory<CodeBaseItem, String>("english"));

        noteCol.setCellFactory(TextFieldTableCell.<CodeBaseItem>forTableColumn());
        noteCol.setCellValueFactory(new PropertyValueFactory<CodeBaseItem, String>("note"));

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

        codeBaseDatas = codeBaselist2Obserlist(DbHelper.getInstance().searchCodeBaseWithCode(1));


        TableColumn<CodeBaseItem,String> codeCol = new TableColumn<>("编码");
//        TableColumn<CodeBaseItem,Integer> codeTypeCol = new TableColumn<>("类型");
        TableColumn<CodeBaseItem,String> contentCol = new TableColumn<>("词条");
        TableColumn<CodeBaseItem,String> rankCol = new TableColumn<>("分级");
        TableColumn<CodeBaseItem,String> yunCol = new TableColumn<>("声韵");
        TableColumn<CodeBaseItem,String> spellCol = new TableColumn<>("拼音");
        TableColumn<CodeBaseItem,String> IPACol = new TableColumn<CodeBaseItem, String>("IPA");
        TableColumn<CodeBaseItem,String> englishCol = new TableColumn<CodeBaseItem, String>("英语");
        TableColumn<CodeBaseItem,String> noteCol = new TableColumn<CodeBaseItem, String>("注释");
//        TableColumn<CodeBaseItem,String> mwfyCol = new TableColumn<CodeBaseItem, String>("明文方言");

        codeCol.setCellFactory(TextFieldTableCell.<CodeBaseItem>forTableColumn());
        codeCol.setCellValueFactory(new PropertyValueFactory<>("code"));;

        contentCol.setCellFactory(TextFieldTableCell.<CodeBaseItem>forTableColumn());
        contentCol.setCellValueFactory(new PropertyValueFactory<CodeBaseItem, String>("content"));

        rankCol.setCellFactory(TextFieldTableCell.<CodeBaseItem>forTableColumn());
        rankCol.setCellValueFactory(new PropertyValueFactory<CodeBaseItem, String>("rank"));

        yunCol.setCellFactory(TextFieldTableCell.<CodeBaseItem>forTableColumn());
        yunCol.setCellValueFactory(new PropertyValueFactory<CodeBaseItem, String>("yun"));

        spellCol.setCellFactory(TextFieldTableCell.<CodeBaseItem>forTableColumn());
        spellCol.setCellValueFactory(new PropertyValueFactory<CodeBaseItem, String>("spell"));

        IPACol.setCellFactory(TextFieldTableCell.<CodeBaseItem>forTableColumn());
        IPACol.setCellValueFactory(new PropertyValueFactory<CodeBaseItem, String>("IPA"));

        englishCol.setCellFactory(TextFieldTableCell.<CodeBaseItem>forTableColumn());
        englishCol.setCellValueFactory(new PropertyValueFactory<CodeBaseItem, String>("english"));

        noteCol.setCellFactory(TextFieldTableCell.<CodeBaseItem>forTableColumn());
        noteCol.setCellValueFactory(new PropertyValueFactory<CodeBaseItem, String>("note"));

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

        codeBaseDatas = codeBaselist2Obserlist(DbHelper.getInstance().searchCodeBaseWithCode(2));


        TableColumn<CodeBaseItem,String> codeCol = new TableColumn<>("编码");
//        TableColumn<CodeBaseItem,Integer> codeTypeCol = new TableColumn<>("类型");
        TableColumn<CodeBaseItem,String> contentCol = new TableColumn<>("句子");
        TableColumn<CodeBaseItem,String> rankCol = new TableColumn<>("分级");
        TableColumn<CodeBaseItem,String> yunCol = new TableColumn<>("声韵");
        TableColumn<CodeBaseItem,String> spellCol = new TableColumn<>("拼音");
        TableColumn<CodeBaseItem,String> IPACol = new TableColumn<CodeBaseItem, String>("IPA");
        TableColumn<CodeBaseItem,String> englishCol = new TableColumn<CodeBaseItem, String>("英语");
        TableColumn<CodeBaseItem,String> noteCol = new TableColumn<CodeBaseItem, String>("注释");
//        TableColumn<CodeBaseItem,String> mwfyCol = new TableColumn<CodeBaseItem, String>("明文方言");

        codeCol.setCellFactory(TextFieldTableCell.<CodeBaseItem>forTableColumn());
        codeCol.setCellValueFactory(new PropertyValueFactory<>("code"));;

        contentCol.setCellFactory(TextFieldTableCell.<CodeBaseItem>forTableColumn());
        contentCol.setCellValueFactory(new PropertyValueFactory<CodeBaseItem, String>("content"));

        rankCol.setCellFactory(TextFieldTableCell.<CodeBaseItem>forTableColumn());
        rankCol.setCellValueFactory(new PropertyValueFactory<CodeBaseItem, String>("rank"));

        yunCol.setCellFactory(TextFieldTableCell.<CodeBaseItem>forTableColumn());
        yunCol.setCellValueFactory(new PropertyValueFactory<CodeBaseItem, String>("yun"));

        spellCol.setCellFactory(TextFieldTableCell.<CodeBaseItem>forTableColumn());
        spellCol.setCellValueFactory(new PropertyValueFactory<CodeBaseItem, String>("spell"));

        IPACol.setCellFactory(TextFieldTableCell.<CodeBaseItem>forTableColumn());
        IPACol.setCellValueFactory(new PropertyValueFactory<CodeBaseItem, String>("IPA"));

        englishCol.setCellFactory(TextFieldTableCell.<CodeBaseItem>forTableColumn());
        englishCol.setCellValueFactory(new PropertyValueFactory<CodeBaseItem, String>("english"));

        noteCol.setCellFactory(TextFieldTableCell.<CodeBaseItem>forTableColumn());
        noteCol.setCellValueFactory(new PropertyValueFactory<CodeBaseItem, String>("note"));

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


        codeBaseDatas = codeBaselist2Obserlist(DbHelper.getInstance().searchCodeBaseWithCode(3));


        TableColumn<CodeBaseItem,String> codeCol = new TableColumn<>("编码");
//        TableColumn<CodeBaseItem,Integer> codeTypeCol = new TableColumn<>("类型");
        TableColumn<CodeBaseItem,String> contentCol = new TableColumn<>("词条");
        TableColumn<CodeBaseItem,String> rankCol = new TableColumn<>("分级");
        TableColumn<CodeBaseItem,String> yunCol = new TableColumn<>("声韵");
        TableColumn<CodeBaseItem,String> spellCol = new TableColumn<>("拼音");
        TableColumn<CodeBaseItem,String> IPACol = new TableColumn<CodeBaseItem, String>("IPA");
        TableColumn<CodeBaseItem,String> englishCol = new TableColumn<CodeBaseItem, String>("英语");
        TableColumn<CodeBaseItem,String> noteCol = new TableColumn<CodeBaseItem, String>("注释");
//        TableColumn<CodeBaseItem,String> mwfyCol = new TableColumn<CodeBaseItem, String>("明文方言");

        codeCol.setCellFactory(TextFieldTableCell.<CodeBaseItem>forTableColumn());
        codeCol.setCellValueFactory(new PropertyValueFactory<>("code"));;

        contentCol.setCellFactory(TextFieldTableCell.<CodeBaseItem>forTableColumn());
        contentCol.setCellValueFactory(new PropertyValueFactory<CodeBaseItem, String>("content"));

        rankCol.setCellFactory(TextFieldTableCell.<CodeBaseItem>forTableColumn());
        rankCol.setCellValueFactory(new PropertyValueFactory<CodeBaseItem, String>("rank"));

        yunCol.setCellFactory(TextFieldTableCell.<CodeBaseItem>forTableColumn());
        yunCol.setCellValueFactory(new PropertyValueFactory<CodeBaseItem, String>("yun"));

        spellCol.setCellFactory(TextFieldTableCell.<CodeBaseItem>forTableColumn());
        spellCol.setCellValueFactory(new PropertyValueFactory<CodeBaseItem, String>("spell"));

        IPACol.setCellFactory(TextFieldTableCell.<CodeBaseItem>forTableColumn());
        IPACol.setCellValueFactory(new PropertyValueFactory<CodeBaseItem, String>("IPA"));

        englishCol.setCellFactory(TextFieldTableCell.<CodeBaseItem>forTableColumn());
        englishCol.setCellValueFactory(new PropertyValueFactory<CodeBaseItem, String>("english"));

        noteCol.setCellFactory(TextFieldTableCell.<CodeBaseItem>forTableColumn());
        noteCol.setCellValueFactory(new PropertyValueFactory<CodeBaseItem, String>("note"));

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

        chineseLangDatas = chineseLanglist2Obserlist(DbHelper.getInstance().searchAllCodeLangHanYu());

        TableColumn<ChineseLangItem,String> codeCol = new TableColumn<>("序号");
        TableColumn<ChineseLangItem,String> nameCol = new TableColumn<>("名称");
        TableColumn<ChineseLangItem,String> regionCol = new TableColumn<>("主要分布地");

        codeCol.setCellFactory(TextFieldTableCell.<ChineseLangItem>forTableColumn());
        codeCol.setCellValueFactory(new PropertyValueFactory<>("code"));

        nameCol.setCellFactory(TextFieldTableCell.<ChineseLangItem>forTableColumn());
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        regionCol.setCellFactory(TextFieldTableCell.<ChineseLangItem>forTableColumn());
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

        codeIPADatas = codeIPAList2Obserlist(DbHelper.getInstance().searchAllCodeIPABase());

        TableColumn<ChineseLangItem,String> codeCol = new TableColumn<>("编码");
        TableColumn<ChineseLangItem,String> contentCol = new TableColumn<>("元音字符");

        codeCol.setCellFactory(TextFieldTableCell.<ChineseLangItem>forTableColumn());
        codeCol.setCellValueFactory(new PropertyValueFactory<>("code"));

        contentCol.setCellFactory(TextFieldTableCell.<ChineseLangItem>forTableColumn());
        contentCol.setCellValueFactory(new PropertyValueFactory<>("content"));

        tableView.setItems(codeIPADatas);
        tableView.getColumns().addAll(codeCol,contentCol);
    }


    /**
     * 方言调查字表、词表、句表、话语表table数据Item
     */
    public static class CodeBaseItem{
        private final SimpleStringProperty code;
        private final SimpleIntegerProperty codeType;
        private final SimpleStringProperty content;
        private final SimpleStringProperty rank;
        private final SimpleStringProperty yun;
        private final SimpleStringProperty spell;
        private final SimpleStringProperty IPA;
        private final SimpleStringProperty english;
        private final SimpleStringProperty note;
        private final SimpleStringProperty mwfy;

        private CodeBaseItem(String code, Integer codeType, String content, String rank, String yun,String spell, String IPA, String english, String note, String mwfy) {
            this.code = new SimpleStringProperty(code);
            this.codeType = new SimpleIntegerProperty(codeType);
            this.content = new SimpleStringProperty(content);
            this.rank = new SimpleStringProperty(rank);
            this.yun = new SimpleStringProperty(yun);
            this.spell = new SimpleStringProperty(spell);
            this.IPA = new SimpleStringProperty(IPA);
            this.english = new SimpleStringProperty(english);
            this.note = new SimpleStringProperty(note);
            this.mwfy = new SimpleStringProperty(mwfy);
        }

        public String getCode() {
            return code.get();
        }

        public void setCode(String code) {
            this.code.set(code);
        }

        public int getCodeType() {
            return codeType.get();
        }

        public void setCodeType(int codeType) {
            this.codeType.set(codeType);
        }

        public String getContent() {
            return content.get();
        }

        public void setContent(String content) {
            this.content.set(content);
        }

        public String getRank() {
            return rank.get();
        }

        public void setRank(String rank) {
            this.rank.set(rank);
        }

        public String getYun() {
            return yun.get();
        }

        public void setYun(String yun) {
            this.yun.set(yun);
        }

        public String getSpell() {
            return spell.get();
        }

        public void setSpell(String spell) {
            this.spell.set(spell);
        }

        public String getIPA() {
            return IPA.get();
        }

        public void setIPA(String IPA) {
            this.IPA.set(IPA);
        }

        public String getEnglish() {
            return english.get();
        }

        public void setEnglish(String english) {
            this.english.set(english);
        }

        public String getNote() {
            return note.get();
        }

        public void setNote(String note) {
            this.note.set(note);
        }

        public String getMwfy() {
            return mwfy.get();
        }

        public void setMwfy(String mwfy) {
            this.mwfy.set(mwfy);
        }
    }

    /**
     * 中国语言代码Item
     */
    public static class ChineseLangItem{
        private final SimpleStringProperty code;
        private final SimpleStringProperty name;
        private final SimpleStringProperty region;

        public ChineseLangItem(String code, String name, String  region) {
            this.code = new SimpleStringProperty(code);
            this.name = new SimpleStringProperty(name);
            this.region = new SimpleStringProperty(region);
        }

        public String getCode() {
            return code.get();
        }

        public void setCode(String code) {
            this.code.set(code);
        }

        public String getName() {
            return name.get();
        }

        public void setName(String name) {
            this.name.set(name);
        }

        public String getRegion() {
            return region.get();
        }

        public void setRegion(String region) {
            this.region.set(region);
        }
    }

    public static class CodeIPAItem{
        private final SimpleStringProperty code;
        private final SimpleStringProperty content;
        private final SimpleStringProperty type;

        public CodeIPAItem(String code, String content, String type) {
            this.code = new SimpleStringProperty(code);
            this.content = new SimpleStringProperty(content);
            this.type = new SimpleStringProperty(type);
        }

        public String getCode() {
            return code.get();
        }

        public void setCode(String code) {
            this.code.set(code);
        }

        public String getContent() {
            return content.get();
        }

        public void setContent(String content) {
            this.content.set(content);
        }

        public String getType() {
            return type.get();
        }

        public void setType(String type) {
            this.type.set(type);
        }
    }
}

