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
    @FXML
    private ProgressBar progressBar;

    @FXML
    private TableView tableView;


    private ObservableList<YBCCBean> analyDatas;
    private ObservableList<YBCCBean> showDatas;

    private String biaoDian = "。，、：∶；‘’“”〝〞ˆˇ﹕︰﹔﹖﹑·¨.¸´？！～—｜‖＂〃｀@﹫¡¿﹏﹋︴々﹟#﹩$﹠&﹪%﹡﹢×﹦‐￣¯―﹨˜﹍﹎＿~（）〈〉‹›﹛﹜『』〖〗［］《》〔〕{}「」【】︵︷︿︹︽_︶︸﹀︺︾ˉ﹂﹄︼﹁﹃︻▲●□…→";
    private String baseYuan = "aᴀɑɒøoɔeᴇɛæəɤiɯuyptkbdɡqʔˀŋ";

    public ObservableList<YBCCBean> getAnalyDatas() {
        return analyDatas;
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

        showDatas = FXCollections.observableArrayList();
    }

    @Override
    public void prepareInit() {
        super.prepareInit();

        TableColumn<YBCCBean,String> codeCol = new TableColumn<>("编码");
        TableColumn<YBCCBean,String> ipaCol = new TableColumn<>("音标注音");
        TableColumn<YBCCBean,String> reasonCol = new TableColumn<>("错误原因");

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

        String regEx = "[^0-5-;]+[0-5]+(-[0-5]+)?";
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

    public boolean checkShengMuYunMuShengDiao(YBCCBean bean,ArrayList<String> voiceList){
        for (String s : voiceList) {

        }
        return false;
    }

    public boolean hasWrong(YBCCBean bean){
        String ipa = bean.getRecord().getIPA();
        ipa.replace(" ","");

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
        }else if(!ipa.substring(ipa.length()-2).matches("[1-5]+")){
            bean.setWrongReason("缺少音韵调");
            return true;
        }else{
            //通过分号分割
            String[] differentVoice = splitWithFenHao(ipa);
            //遍历每种读音是否存在不正确情况
            for (String voice : differentVoice) {
                System.out.println("voice:"+voice);
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

    @Override
    public void onCreatedView() {
        super.onCreatedView();

        mStage.setOnShown(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                for (int i = 0; i < analyDatas.size(); i++) {
                    YBCCBean bean = analyDatas.get(i);
                    //分析得出有可能错误
                    if (hasWrong(bean)){
                        int result = wrongTipAlert("",bean.getRecord().getInvestCode()+":"+bean.getWrongReason()+"\n\n");
                        if (result == 0){

                        }else if (result == 1){

                        }else if (result == 2){

                        }else if (result == 3){

                        }else if (result == -1){

                        }
                        showDatas.add(bean);
                        tableView.setItems(showDatas);
                        tableView.refresh();
                    }
                    progressBar.setProgress(( i + 1 )*1.0/analyDatas.size());
                }
            }
        });
    }
}
