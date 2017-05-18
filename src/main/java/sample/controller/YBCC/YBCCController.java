package sample.controller.YBCC;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import sample.controller.BaseController;
import sample.controller.NewTableView.NewTableView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Bee on 2017/4/20.
 */
public class YBCCController extends BaseController {

    public NewTableView newTableViewVC;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private TableView tableView;


    private ObservableList<YBCCBean> analyDatas;
    private ObservableList<YBCCBean> showDatas;

    private NewTableView tbVC;


    private String biaoDian = "。，、：∶；‘’“”〝〞ˆˇ﹕︰﹔﹖﹑·¨.¸´？！～—｜‖＂〃｀@﹫¡¿﹏﹋︴々﹟#﹩$﹠&﹪%﹡﹢×﹦‐￣¯―﹨˜﹍﹎＿~（）〈〉‹›﹛﹜『』〖〗［］《》〔〕{}「」【】︵︷︿︹︽_︶︸﹀︺︾ˉ﹂﹄︼﹁﹃︻▲●□…→";
    private String baseYuan = "əɛɿʮʅʯiyɪʏeøᴇɛεɛœæaɶɑɒʌɔɤoɷʊƜɯuɨʉɘɵəǝɚɜɝɞɐᴀɩϊàáâãäåḁèéêëḙḛẽìíîïòóôõöùúûüṵṷāăēĕěĩīĭōŏőũūŭůűǎǐǒǔǖǘǚǜǣȁȅȇȉȋȍȗ";
    private String doubleYuan = "ai ei ui ao ou iu ie ue";
    private String wrongSD = "111 222 333 444 555 123 124 125 234 235 345 543 542 541 432 431 321";
    private String currectZeroSD = "01 02 03 04 05";

    public ObservableList<YBCCBean> getAnalyDatas() {
        return analyDatas;
    }

    public void setTbVC(NewTableView tbVC) {
        this.tbVC = tbVC;
    }

    public void setAnalyDatas(ObservableList<YBCCBean> analyDatas) {
        this.analyDatas = analyDatas;
        this.analyDatas = FXCollections.observableArrayList(analyDatas.filtered(new Predicate<YBCCBean>() {
            @Override
            public boolean test(YBCCBean bean) {
                if (bean.getRecord().getIPA() == null || bean.getRecord().getIPA().length() == 0){
                    return false;
                }
                return true;
            }
        }));
        System.out.println("analy datas count:"+this.analyDatas.size());
        showDatas = FXCollections.observableArrayList();
    }

    @Override
    public void prepareInit() {
        super.prepareInit();

        TableColumn<YBCCBean,String> codeCol = new TableColumn<>("编码");
        TableColumn<YBCCBean,String> ipaCol = new TableColumn<>("音标注音");
        TableColumn<YBCCBean,String> reasonCol = new TableColumn<>("错误原因");

        ipaCol.setMinWidth(150);
        reasonCol.setMinWidth(200);

        codeCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<YBCCBean, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<YBCCBean, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getRecord().getBaseCode());
            }
        });
        ipaCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<YBCCBean, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<YBCCBean, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getRecord().getIPA());
            }
        });
        reasonCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<YBCCBean, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<YBCCBean, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getWrongReason());
            }
        });

        tableView.getColumns().addAll(codeCol,ipaCol,reasonCol);
    }


    public int wrongTipAlert(String p_header,String p_message){
//        按钮部分可以使用预设的也可以像这样自己 new 一个
        Alert _alert = new Alert(Alert.AlertType.NONE,p_message,new ButtonType("继续查错"),new ButtonType("忽略本条"),
                new ButtonType("忽略全部"),new ButtonType("取消"));
//        设置窗口的标题
        _alert.setTitle("确认");
        _alert.setHeaderText(p_header);
//        设置对话框的 icon 图标，参数是主窗口的 stage
        _alert.initOwner(mStage);
//        showAndWait() 将在对话框消失以前不会执行之后的代码
        Optional<ButtonType> _buttonType = _alert.showAndWait();
//        根据点击结果返回
        if(_buttonType.get().getText().equals("继续查错")){
            return 0;
        }
        else if(_buttonType.get().getText().equals("忽略本条")){
            return 1;
        }
        else if(_buttonType.get().getText().equals("忽略全部")){
            return 2;
        }
        else if(_buttonType.get().getText().equals("取消")){
            return 3;
        }
        return -1;
    }


    public boolean isContainWrongBiaoDian(String str){
        for (int i = 0; i < biaoDian.length(); i++) {
            Character temp = biaoDian.charAt(i);
            if (str.contains(temp.toString())){
                return true;
            }
        }
        return false;
    }

    public String[] splitWithFenHao(String str){
        return str.split(";");
    }

    public ArrayList splitIPA(String str){
        ArrayList result = new ArrayList();

        String regEx = "[^\\d-;]+[\\d]+(-\\d+)?";
        Pattern pattern = Pattern.compile(regEx);
        Matcher m = pattern.matcher(str);
        while (m.find()){
            result.add(m.group(0));
            System.out.println("m:"+m.group(0));
        }
        return result;
    }

    public boolean checkLength(String originStr,List<String> afterSplit){
        int length = 0;
        for (String str : afterSplit) {
            length += str.length();
        }
        System.out.println("check length:"+(length == originStr.length()));
        return length == originStr.length();
    }

    public boolean checkSDNum(String sd){
        //判断是否0-5
        if (sd.matches("^[0-5]{2,3}$")){
            //判断是否必错声调
            if (wrongSD.contains(" "+sd+" ")){
                return false;
            }else {
                //假如包含0的话，检测是否对的情况
                if (sd.contains("0") && !currectZeroSD.contains(" "+sd+" ")){
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    public boolean checkShengMuYunMuShengDiao(YBCCBean bean,ArrayList<String> voiceList){
        String regEx = "[^\\d-]+";
        Pattern pattern = Pattern.compile(regEx);

        //声母 韵母 声调
        String sm = "";
        String ym = "";
        String sd = "";

        for (String s : voiceList) {
            sm = "";
            ym = "";
            sd = "";

            //分割声韵与声调
            Matcher matcher = pattern.matcher(s);
            matcher.find();

            String sy = matcher.group();
            System.out.println("声韵:"+sy);

            boolean isFindSm = false;

            //记录最长连续元音
            int maxSMCount = 0;
            int tempSMCount = 0;

            for (int i = 0; i < sy.length(); i++) {
                Character c = sy.charAt(i);
                //找出声母韵母
                if (baseYuan.contains(c.toString())){
                    tempSMCount++;

                    //判断找到声母没有
                    if (!isFindSm){
                        //元音开头
                        if (i == 0){
                            sm = "";
                            ym = sy;
                        }else {
                            sm = sy.substring(0,i);
                            ym = sy.substring(i);
                        }
                        isFindSm = true;
                    }

                    //判断连续元音
                    for (int j = i + 1 ;j < sy.length(); j++){
                        Character afterC = sy.charAt(j);
                        String tempStr = sy.substring(i,j);
                        if (baseYuan.contains(afterC.toString()) && !tempStr.matches("[0-5]+")){
                            //判断两个原因字母是否相连,且为复韵母
                            if(i + 1 == j && doubleYuan.contains(tempStr)){
                                continue;
                            }

                            bean.setWrongReason("声调错漏");
                            return false;
                        }
                    }
                }else {
                    if (tempSMCount > maxSMCount){
                        maxSMCount = tempSMCount;
                    }
                    tempSMCount = 0;
                }

            }

            if (tempSMCount > maxSMCount){
                maxSMCount = tempSMCount;
            }
            tempSMCount = 0;

            //判断连续元音 长度为三有可能错，超过三一定错
            if (maxSMCount == 3){
                bean.setWrongReason("出现连续三个元音字符，有可能出错");
                return false;
            }

            if (maxSMCount > 3){
                bean.setWrongReason("出现超过连续三个元音字符，声韵错漏");
                return false;
            }

            //声调
            sd = s.substring(matcher.end());

            System.out.println("声母:"+sm);
            System.out.println("韵母:"+ym);
            System.out.println("声调:"+sd);


            //声韵为空
            if (sm.equals("") && ym.equals("")){
                bean.setWrongReason("声韵错漏");
                return false;
            }



            //声调为空
            if (sd.equals("")){
                bean.setWrongReason("声调错漏");
                return false;
            }


            //声调存在变调的情况
            if(sd.contains("-")){
                String[] sds = sd.split("-");
                if (checkSDNum(sds[0]) && checkSDNum(sds[1])){

                }else{
                    bean.setWrongReason("声调错漏");
                    return false;
                }
            }else{
                if (!checkSDNum(sd)){
                    bean.setWrongReason("声调错漏");
                    return false;
                }
            }

            bean.addSm(sm);
            bean.addYm(ym);
            bean.addSd(sd);
        }
        return true;
    }

    public boolean hasWrong(YBCCBean bean){
        String ipa = bean.getRecord().getIPA();
        ipa = ipa.replaceAll(" ","");

        System.out.println("****************************");
        System.out.println("IPA:"+ipa);

        if (!ipa.matches(".*[0-5]+.*")){
            //判断有无0-5数字
            bean.setWrongReason("声调错漏");
            return true;
        }else if (isContainWrongBiaoDian(ipa)){
            //是否包含非法标点符号
            bean.setWrongReason("包含非法标点符号");
            return true;
        }else if(!ipa.substring(ipa.length()-1).matches("[1-5]+")){
            bean.setWrongReason("声调错误");
            return true;
        }else{
            //通过分号分割
            String[] differentVoice = splitWithFenHao(ipa);
            //遍历每种读音是否存在不正确情况
            for (String voice : differentVoice) {
                System.out.println("voice:"+voice);

                //分割出单个字的声韵母声调
                ArrayList<String> result = splitIPA(voice);

                //判断格式是否正确xxx55-55，这种格式
                if (!checkLength(voice,result)){
                    bean.setWrongReason("声调格式错误");
                    return true;
                }else{
                    //分析每个发音的声母，韵母，声调
                    if (!checkShengMuYunMuShengDiao(bean,result)){
                        return true;
                    }
                }
            }

        }
        return false;
    }

    public void resetAllWrongReason(){
        for (YBCCBean bean : analyDatas) {
            bean.setWrongReason("");
        }
    }

    @Override
    public void onCreatedView() {
        super.onCreatedView();

        mStage.setOnShown(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                boolean isPassAll = false;
                for (int i = 0; i < analyDatas.size(); i++) {
                    YBCCBean bean = analyDatas.get(i);

                    if (bean.getSmList()!=null) bean.getSmList().clear();
                    if (bean.getYmList()!=null) bean.getYmList().clear();
                    if (bean.getSdList()!=null) bean.getSdList().clear();

                    //分析得出有可能错误
                    if (hasWrong(bean)){
                        //错误出现声母变为空 声调变为空  韵母为IPA
                        bean.addSm("无");
                        bean.addYm(bean.getRecord().getIPA());
                        bean.addSd("无");

                        if (!isPassAll){
                            int result = wrongTipAlert("",bean.getRecord().getInvestCode()+":"+bean.getWrongReason()+"\n"+"条目音标:"+bean.getRecord().getIPA()+"\n\n");
                            if (result == 0){
                                showDatas.add(bean);
                            }else if (result == 1){
                                bean.setWrongReason("");
                                continue;
                            }else if (result == 2){
                                bean.setWrongReason("");
                                isPassAll = true;
                            }else if (result == 3){
                                resetAllWrongReason();
                                break;
                            }else if (result == -1){

                            }
                        }

                        tableView.setItems(showDatas);
                        tableView.refresh();
                    }
                    progressBar.setProgress(( i + 1 )*1.0/analyDatas.size());
                }
                tbVC.refreshTableView();
                newTableViewVC.setAfterAnalyDatas(analyDatas);
            }
        });
    }
}
