package sample.controller.ZlyydView;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import javafx.util.StringConverter;
import sample.controller.BaseController;
import sample.controller.YBCC.YBCCBean;
import sample.entity.Record;
import sample.entity.Table;
import sample.util.ExportUtil;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

/**
 * Created by Bee on 2017/5/4.
 */
public class ZlyydViewController extends BaseController {

    //整理音韵调
    public boolean isZLYYD;

    public boolean isZLYYD() {
        return isZLYYD;
    }

    public void setZLYYD(boolean ZLYYD) {
        isZLYYD = ZLYYD;
        setSomethingDisable();
    }

    private ObservableList<YBCCBean> originDatas;
    private ObservableList<CollectBean> smDatas;
    private ObservableList<CollectBean> ymDatas;
    private ObservableList<CollectBean> sdDatas;
    private List<String> presetList = new ArrayList<>(Arrays.asList("p", "pʰ", "b", "ɓ", "m", "m̥", "f", "v", "t", "tʰ", "d", "ɗ", "n", "n̥", "l", "l̥", "ɬ", "ɮ", "ʦ", "ʦʰ", "ʣ", "s", "z", "ȶ", "ȶʰ", "ȡ", "ȵ", "ȵ̊", "ʧ", "ʧʰ", "ʤ", "ʃ", "ʒ", "ʈ", "ʈʰ", "ɖ", "ɳ", "ɳ̊", "tʂ", "tʂʰ", "dʐ", "ʂ", "ʐ", "ʨ", "ʨʰ", "ʥ", "ɕ", "ʑ", "ç", "ʝ", "c", "cʰ", "ɟ", "ɲ", "ɲ̊", "k", "kʰ", "ɡ", "ɠ", "ŋ̊", "x", "ɣ", "q", "qʰ", "ɢ", "ɴ", "ɴ̥", "χ", "ʁ", "h", "ɦ", "pl", "pf", "pʰf", "pʰl", "bl", "ml", "tl", "tʰl", "dl", "kl", "kʰl", "ɡl", "gl", "sl", "pj", "pʰj", "bj", "mj", "fj", "tj", "tʰj", "dj", "nj", "lj", "pr", "pʰr", "br", "mr", "kr", "kʰr", "gr", "ɡr", "sr", "r", "ʀ", "ŋ","Ø","自成音节" ));


    @FXML
    private ListView smList;

    @FXML
    private ListView ymList;

    @FXML
    private ListView sdList;

    @FXML
    private Label msgTipL;

    @FXML
    private RadioButton vBtn;

    @FXML
    private RadioButton hBtn;

    @FXML
    private ChoiceBox lineSmCount;

    @FXML
    private ChoiceBox lineYmCount;

    @FXML
    private ChoiceBox lineSdCount;

    @FXML
    private ChoiceBox demoWordCount;

    @FXML
    private ChoiceBox sortType;

    @FXML
    private ChoiceBox fileFormat;

    @FXML
    private Button smFB;

    @FXML
    private Button ymFB;

    @FXML
    private Button sdFB;

    @FXML
    private Button smPB;

    @FXML
    private Button ymPB;

    @FXML
    private Button sdPB;

    @FXML
    private Button smAB;

    @FXML
    private Button ymAB;

    @FXML
    private Button sdAB;

    @FXML
    private Button smLB;

    @FXML
    private Button ymLB;

    @FXML
    private Button sdLB;

    @FXML
    private Button smPSB;

    @FXML
    private Button ymPSB;

    @FXML
    private Button sdPSB;

    @FXML
    private Button smAs;

    @FXML
    private Button ymAs;

    @FXML
    private Button sdAs;

    @FXML
    private Button smAns;

    @FXML
    private Button ymAns;

    @FXML
    private Button sdAns;
    @FXML
    public void radioBtnClick(){
        lineSmCount.setDisable(vBtn.isSelected());
        lineYmCount.setDisable(vBtn.isSelected());
        lineSdCount.setDisable(vBtn.isSelected());
    }

    public List<SameIPABean> analySameIpa(){
        List<CollectBean> temp = new ArrayList<>();
        temp.addAll(smDatas.filtered(new Predicate<CollectBean>() {
            @Override
            public boolean test(CollectBean collectBean) {
                return collectBean.isIsChecked();
            }
        }));
        temp.addAll(ymDatas.filtered(new Predicate<CollectBean>() {
            @Override
            public boolean test(CollectBean collectBean) {
                return collectBean.isIsChecked();
            }
        }));
        temp.addAll(sdDatas.filtered(new Predicate<CollectBean>() {
            @Override
            public boolean test(CollectBean collectBean) {
                return collectBean.isIsChecked();
            }
        }));

        List<SameIPABean> result = new ArrayList<>();

        for (CollectBean c : temp) {
            for (Record r : c.getDemoRecordList()) {
                int i = 0;
                for (i = 0;i<result.size();i++){
                    SameIPABean s = result.get(i);
                    if (s.getIpa().equals(r.getIPA())){
                        if (!s.getBaseCode().equals(r.getBaseCode())){
                            s.addSameRecords(r);
                            s.setCount(s.getCount() + 1);
                        }
                        break;
                    }
                }
                if (i == result.size()){
                    SameIPABean sameIPABean = new SameIPABean(r.getIPA(),1,r.getBaseCode());
                    sameIPABean.addSameRecords(r);
                    result.add(sameIPABean);
                }
            }
        }

        return result;
    }

    @FXML
    public void okBtnClick(){
        if (!isZLYYD){
            if (fileFormat.getSelectionModel().getSelectedIndex() == 0){
                ExportUtil.exportTYZHCHExcel(mStage,
                        analySameIpa(),
                        fileFormat.getSelectionModel().getSelectedIndex());
            }else{
                ExportUtil.exportTYZHCHHtml(mStage,
                        analySameIpa(),
                        fileFormat.getSelectionModel().getSelectedIndex(),
                        ((Table) preData));
            }
        }else {
            if (fileFormat.getSelectionModel().getSelectedIndex() == 0){
                ExportUtil.exportYYDExcel(mStage,
                        hBtn.isSelected(),
                        Integer.parseInt(demoWordCount.getSelectionModel().getSelectedItem().toString()),
                        Integer.parseInt(lineSmCount.getSelectionModel().getSelectedItem().toString()),
                        Integer.parseInt(lineYmCount.getSelectionModel().getSelectedItem().toString()),
                        Integer.parseInt(lineSdCount.getSelectionModel().getSelectedItem().toString()),
                        fileFormat.getSelectionModel().getSelectedIndex(),
                        sortType.getSelectionModel().getSelectedIndex() == 0,
                        smDatas.filtered(new Predicate<CollectBean>() {
                            @Override
                            public boolean test(CollectBean collectBean) {
                                return collectBean.isIsChecked();
                            }
                        }),
                        ymDatas.filtered(new Predicate<CollectBean>() {
                            @Override
                            public boolean test(CollectBean collectBean) {
                                return collectBean.isIsChecked();
                            }
                        }),
                        sdDatas.filtered(new Predicate<CollectBean>() {
                            @Override
                            public boolean test(CollectBean collectBean) {
                                return collectBean.isIsChecked();
                            }
                        }));
            }else{
                ExportUtil.exportYYD(mStage,
                        hBtn.isSelected(),
                        Integer.parseInt(demoWordCount.getSelectionModel().getSelectedItem().toString()),
                        Integer.parseInt(lineSmCount.getSelectionModel().getSelectedItem().toString()),
                        Integer.parseInt(lineYmCount.getSelectionModel().getSelectedItem().toString()),
                        Integer.parseInt(lineSdCount.getSelectionModel().getSelectedItem().toString()),
                        fileFormat.getSelectionModel().getSelectedIndex(),
                        sortType.getSelectionModel().getSelectedIndex() == 0,
                        smDatas.filtered(new Predicate<CollectBean>() {
                            @Override
                            public boolean test(CollectBean collectBean) {
                                return collectBean.isIsChecked();
                            }
                        }),
                        ymDatas.filtered(new Predicate<CollectBean>() {
                            @Override
                            public boolean test(CollectBean collectBean) {
                                return collectBean.isIsChecked();
                            }
                        }),
                        sdDatas.filtered(new Predicate<CollectBean>() {
                            @Override
                            public boolean test(CollectBean collectBean) {
                                return collectBean.isIsChecked();
                            }
                        }),
                        ((Table) preData));
            }
        }
    }

    @FXML
    public void cancelBtnClick(){
        mStage.close();
    }

    @Override
    public void prepareInit() {
        super.prepareInit();
        setupBtnAction();
        setupChoiceBoxContent();
    }

    public void setSomethingDisable(){
        vBtn.setDisable(!isZLYYD);
        hBtn.setDisable(!isZLYYD);
        demoWordCount.setDisable(!isZLYYD);
        sortType.setDisable(!isZLYYD);
    }

    public void setupBtnAction(){
        smFB.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                firstBtnClick(1);
            }
        });
        ymFB.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                firstBtnClick(2);
            }
        });
        sdFB.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                firstBtnClick(3);
            }
        });

        smPB.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                preBtnClick(1);
            }
        });
        ymPB.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                preBtnClick(2);
            }
        });
        sdPB.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                preBtnClick(3);
            }
        });

        smAB.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                afterBtnClick(1);
            }
        });
        ymAB.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                afterBtnClick(2);
            }
        });
        sdAB.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                afterBtnClick(3);
            }
        });

        smLB.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                lastBtnClick(1);
            }
        });
        ymLB.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                lastBtnClick(2);
            }
        });
        sdLB.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                lastBtnClick(3);
            }
        });

        smPSB.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                preSetDatas(1);
            }
        });
        ymPSB.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                preSetDatas(2);
            }
        });
        sdPSB.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                preSetDatas(3);
            }
        });
        smAs.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                allSelect(1);
            }
        });
        ymAs.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                allSelect(2);
            }
        });
        sdAs.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                allSelect(3);
            }
        });
        smAns.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                allUnSelect(1);
            }
        });
        ymAns.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                allUnSelect(2);
            }
        });
        sdAns.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                allUnSelect(3);
            }
        });
    }

    public void allUnSelect(int type){
        ListView temp = null;
        if (type == 1){
            temp = smList;
        }else if (type == 2){
            temp = ymList;
        }else if (type == 3){
            temp = sdList;
        }
        for (int i = 0; i < temp.getItems().size(); i++) {
            CollectBean bean = ((CollectBean) temp.getItems().get(i));
            bean.setIsChecked(false);
        }
    }

    public void allSelect(int type){
        ListView temp = null;
        if (type == 1){
            temp = smList;
        }else if (type == 2){
            temp = ymList;
        }else if (type == 3){
            temp = sdList;
        }
        for (int i = 0; i < temp.getItems().size(); i++) {
            CollectBean bean = ((CollectBean) temp.getItems().get(i));
            bean.setIsChecked(true);
        }
    }

    public void doAction(ListView temp,int nowIndex,int newIndex){
        if (nowIndex != -1){
            ObservableList tempDatas = temp.getItems();
            Object tempBean = tempDatas.get(nowIndex);
            tempDatas.remove(nowIndex);
            tempDatas.add(newIndex,tempBean);
            temp.setItems(tempDatas);

            temp.getSelectionModel().select(newIndex);

            temp.refresh();
        }
    }

    public void firstBtnClick(int type){
        ListView temp = null;
        if (type == 1){
            temp = smList;
        }else if (type == 2){
            temp = ymList;
        }else if (type == 3){
            temp = sdList;
        }

        int nowIndex = temp.getSelectionModel().getSelectedIndex();
        int newIndex = 0;
        doAction(temp,nowIndex,newIndex);
    }

    public void preBtnClick(int type){
        ListView temp = null;
        if (type == 1){
            temp = smList;
        }else if (type == 2){
            temp = ymList;
        }else if (type == 3){
            temp = sdList;
        }

        int nowIndex = temp.getSelectionModel().getSelectedIndex();
        int newIndex = nowIndex - 1;
        newIndex = newIndex>=0?newIndex:0;
        doAction(temp,nowIndex,newIndex);
    }

    public void afterBtnClick(int type){
        ListView temp = null;
        if (type == 1){
            temp = smList;
        }else if (type == 2){
            temp = ymList;
        }else if (type == 3){
            temp = sdList;
        }

        int nowIndex = temp.getSelectionModel().getSelectedIndex();
        int newIndex = nowIndex + 1;
        newIndex = newIndex<=temp.getItems().size() - 1?newIndex:temp.getItems().size() - 1;
        doAction(temp,nowIndex,newIndex);
    }

    public void lastBtnClick(int type){
        ListView temp = null;
        if (type == 1){
            temp = smList;
        }else if (type == 2){
            temp = ymList;
        }else if (type == 3){
            temp = sdList;
        }

        int nowIndex = temp.getSelectionModel().getSelectedIndex();
        int newIndex = temp.getItems().size() - 1;
        doAction(temp,nowIndex,newIndex);
    }

    public ObservableList<YBCCBean> getOriginDatas() {
        return originDatas;
    }

    public void setOriginDatas(ObservableList<YBCCBean> originDatas) {
        this.originDatas = originDatas;

        smDatas = FXCollections.observableArrayList();
        ymDatas = FXCollections.observableArrayList();
        sdDatas = FXCollections.observableArrayList();

        analyOriginDatas();

        int bdCount = calculateBD();
        msgTipL.setText("信息提示统计：声母"+smDatas.size()+"个，韵母："+ymDatas.size()+"个，声调："+(sdDatas.size() - bdCount)+"个，变调：" + bdCount + "个");
        preSetDatas(-1);
        setupListContent();
    }

    public int calculateBD(){
        int count = 0;
        for (CollectBean b : sdDatas) {
            if (b.getKey().contains("-")){
                count++;
            }
        }
        return count;
    }

    public void preSetDatas(int type){
        if (type == -1){
            preSetDatas(1);
            preSetDatas(2);
            preSetDatas(3);
        }else {
            if (type == 1){
                smDatas.sort(new Comparator<CollectBean>() {
                    @Override
                    public int compare(CollectBean o1, CollectBean o2) {
                        int i1 = presetList.indexOf(o1.getKey());
                        int i2 = presetList.indexOf(o2.getKey());
                        return i1-i2;
                    }
                });
            }else if (type == 2){
                ymDatas.sort(new Comparator<CollectBean>() {
                    @Override
                    public int compare(CollectBean o1, CollectBean o2) {
                        return o1.getKey().compareTo(o2.getKey());
                    }
                });
            }else if (type == 3){
                sdDatas.sort(new Comparator<CollectBean>() {
                    @Override
                    public int compare(CollectBean o1, CollectBean o2) {
                        return o1.getKey().compareTo(o2.getKey());
                    }
                });
            }
        }
    }

    public void setupListContent(){
        smList.setCellFactory(CheckBoxListCell.forListView(new Callback<CollectBean, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(CollectBean param) {
                return param.isCheckedProperty();
            }
        }, new StringConverter<CollectBean>() {
            @Override
            public String toString(CollectBean object) {
                return object.getKey()+"("+object.getCount()+")";
            }

            @Override
            public CollectBean fromString(String string) {
                return null;
            }
        }));
        smList.setItems(smDatas);

        ymList.setCellFactory(CheckBoxListCell.forListView(new Callback<CollectBean, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(CollectBean param) {
                return param.isCheckedProperty();
            }
        }, new StringConverter<CollectBean>() {
            @Override
            public String toString(CollectBean object) {
                return object.getKey()+"("+object.getCount()+")";
            }

            @Override
            public CollectBean fromString(String string) {
                return null;
            }
        }));
        ymList.setItems(ymDatas);

        sdList.setCellFactory(CheckBoxListCell.forListView(new Callback<CollectBean, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(CollectBean param) {
                return param.isCheckedProperty();
            }
        }, new StringConverter<CollectBean>() {
            @Override
            public String toString(CollectBean object) {
                return object.getKey()+"("+object.getCount()+")";
            }

            @Override
            public CollectBean fromString(String string) {
                return null;
            }
        }));
        sdList.setItems(sdDatas);
    }

    public void analyOriginDatas (){
        for (YBCCBean bean: originDatas){
            analyWithKeyWord(1,bean);
            analyWithKeyWord(2,bean);
            analyWithKeyWord(3,bean);
        }
    }

    public void analyWithKeyWord(int type,YBCCBean cBean){
        ObservableList<CollectBean> temp = null;
        List<String> keyWordList = null;
        if (type == 1){
            temp = smDatas;
            keyWordList = cBean.getSmList();
        }else if (type == 2){
            temp = ymDatas;
            keyWordList = cBean.getYmList();
        }else if (type == 3){
            temp = sdDatas;
            keyWordList = cBean.getSdList();
        }

        //判断kewordlist是否为空，keywordlist为内容如下：例如bean的记录是duo215;ta88  ymList为d,t
        if (keyWordList != null){
            for (String keyWord : keyWordList) {
                int i = 0;
                for (i = 0; i < temp.size(); i++) {
                    CollectBean bean = temp.get(i);
                    if (bean.getKey().equalsIgnoreCase(keyWord)){
                        bean.setCount(bean.getCount()+1);
                        bean.addDemoReocrd(cBean.getRecord());
                        break;
                    }
                }
                if (i == temp.size()){
                    CollectBean bean = new CollectBean(keyWord,1,type);
                    bean.addDemoReocrd(cBean.getRecord());
                    bean.setIsChecked(true);
                    temp.add(bean);
                }
            }
        }
    }

    public void setupChoiceBoxContent(){
        lineSmCount.setItems(FXCollections.observableArrayList("2","3","4","5","6"));
        lineYmCount.setItems(FXCollections.observableArrayList("2","3","4","5","6"));
        lineSdCount.setItems(FXCollections.observableArrayList("2","3","4","5","6"));
        demoWordCount.setItems(FXCollections.observableArrayList("2","3","4","5","6"));
        sortType.setItems(FXCollections.observableArrayList("音标+中文","中文+音标"));
        fileFormat.setItems(FXCollections.observableArrayList("Excel","单个网页","带音频的网页"));

        lineSmCount.getSelectionModel().select(0);
        lineYmCount.getSelectionModel().select(0);
        lineSdCount.getSelectionModel().select(0);
        demoWordCount.getSelectionModel().select(0);
        sortType.getSelectionModel().select(0);
        fileFormat.getSelectionModel().select(0);
    }
}
