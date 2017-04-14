package sample.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import sample.diycontrol.ClickType;
import sample.diycontrol.TableTopControl;
import sample.diycontrol.TableTopCtlListener;
import sample.entity.Record;
import sample.entity.Table;
import sample.util.DbHelper;


/**
 * Created by Bee on 2017/4/13.
 */
public class NewTableView extends BaseController{
    public static final int NewWordType = 0;
    public static final int NewCiType =  1;
    public static final int NewSentenceType =  2;
    public static final int NewHuaYuType = 3;

    @FXML
    private TableTopControl tableTopCtl;

    @FXML
    private TableView tableView;

    @Override
    public void prepareInit() {
        super.prepareInit();

        System.out.println(((Table) preData).getTitle());

        tableTopCtl.setBtnClickListener(new TableTopCtlListener() {
            @Override
            public void onBtnClick(ClickType ct) {
                switch (ct){
                    case FirstBtnClick:
                        break;
                    case PreBtnClick:
                        break;
                    case NextBtnClick:
                        break;
                    case LastBtnClick:
                        break;
                    case RefreshBtnClick:
                        break;
                    case SelectAllBtnClick:
                        break;
                    case SelectAnotherBtnClick:
                        break;
                    case KeepBtnClick:
                        break;
                    case DisappearBtnClick:
                        break;
                    case DelBtnClick:
                        break;
                    case ShowAllBtnClick:
                        break;
                    case ShowOnlyKeepBtnClick:
                        break;
                    case ShowOnlyDisappearBtnClick:
                        break;
                }
            }
        });
    }

    public void setNewType(int newType){
        if (newType == NewWordType){
            TableColumn<Record,String> hideCol = new TableColumn<>("条目筛选");
            TableColumn<Record,String> doneCol = new TableColumn<>("录音状态");
            TableColumn<Record,String> codeCol = new TableColumn<>("编码");
            TableColumn<Record,String> rankCol = new TableColumn<>("分级");
            TableColumn<Record,String> wordCol = new TableColumn<>("单字");
            TableColumn<Record,String> yunCol = new TableColumn<>("音韵");
            TableColumn<Record,String> IPACol = new TableColumn<>("音标注音");
            TableColumn<Record,String> spellCol = new TableColumn<>("拼音");
            TableColumn<Record,String> englishCol = new TableColumn<>("英语");
            TableColumn<Record,String> noteCol = new TableColumn<>("注释");
            TableColumn<Record,String> recordDateCol = new TableColumn<>("录音日期");


            tableView.getColumns().addAll(hideCol,doneCol,codeCol,rankCol,wordCol,yunCol,IPACol,spellCol,englishCol,noteCol,recordDateCol);
        }else if (newType == NewCiType){
            TableColumn<Record,String> hideCol = new TableColumn<>("条目筛选");
            TableColumn<Record,String> doneCol = new TableColumn<>("录音状态");
            TableColumn<Record,String> codeCol = new TableColumn<>("编码");
            TableColumn<Record,String> rankCol = new TableColumn<>("分级");
            TableColumn<Record,String> ciCol = new TableColumn<>("词条");
            TableColumn<Record,String> mwfyCol = new TableColumn<>("民族文字或方言字");
            TableColumn<Record,String> IPACol = new TableColumn<>("音标注音");
            TableColumn<Record,String> spellCol = new TableColumn<>("拼音");
            TableColumn<Record,String> englishCol = new TableColumn<>("英语");
            TableColumn<Record,String> noteCol = new TableColumn<>("注释");
            TableColumn<Record,String> recordDateCol = new TableColumn<>("录音日期");


            tableView.getColumns().addAll(hideCol,doneCol,codeCol,rankCol,ciCol,mwfyCol,IPACol,spellCol,englishCol,noteCol,recordDateCol);
        }else if (newType == NewSentenceType){
            TableColumn<Record,String> hideCol = new TableColumn<>("条目筛选");
            TableColumn<Record,String> doneCol = new TableColumn<>("录音状态");
            TableColumn<Record,String> codeCol = new TableColumn<>("编码");
            TableColumn<Record,String> rankCol = new TableColumn<>("分级");
            TableColumn<Record,String> sentenceCol = new TableColumn<>("句子");
            TableColumn<Record,String> mwfyCol = new TableColumn<>("民族文字或方言字");
            TableColumn<Record,String> IPACol = new TableColumn<>("音标注音");
            TableColumn<Record,String> duiyiCol = new TableColumn<>("普通话词对译");
            TableColumn<Record,String> englishCol = new TableColumn<>("英语");
            TableColumn<Record,String> noteCol = new TableColumn<>("注释");
            TableColumn<Record,String> recordDateCol = new TableColumn<>("录音日期");


            tableView.getColumns().addAll(hideCol,doneCol,codeCol,rankCol,sentenceCol,mwfyCol,IPACol,duiyiCol,noteCol,englishCol,recordDateCol);
        }else if (newType == NewHuaYuType){

        }
    }
}
