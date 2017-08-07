package sample.util;

import javafx.application.Platform;
import javafx.scene.control.TableView;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameRecorder;
import sample.controller.MainController;
import sample.controller.YBCC.YBCCBean;
import sample.entity.BindResult;
import sample.entity.Record;
import sample.entity.Table;

import java.io.*;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by chenweiqi on 2017/5/18.
 */
public class AudoCityHelper {
    static AudoCityHelper audioCityHelper;

    private AudoCityHelper() {
    }

    public static synchronized AudoCityHelper getInstance(){
        synchronized (AudoCityHelper.class) {
            if (audioCityHelper == null) {
                audioCityHelper = new AudoCityHelper();
            }
        }
        return audioCityHelper;
    }

    public void writeToAc(String filePath, TableView tableView, Table t){
        int tableType = Integer.parseInt(t.getDatatype());

        List content = tableView.getItems();

        List<String> audioFilPaths = new ArrayList<>();
        for (int i=0;i<content.size();i++){
            Record r;
            Object object = tableView.getItems().get(i);
            if (object instanceof  Record){
                r = (Record) object;
            }else if (object instanceof YBCCBean){
                r = ((YBCCBean) object).getRecord();
            }else {
                return;
            }
            if (TextUtil.isEmpty(r.createDate)){
                System.out.println(r.uuid);
                ToastUtil.show("有未录音条目，请录音后再重试");
                return;
            }
            String audioPath = Constant.getAudioPath(r.baseId+"",r.uuid);
            if (TextUtil.isEmpty(audioPath)){
                ToastUtil.show("有未录音条目，请录音后再重试");
                return;
            }else if (!new File(audioPath).exists()){
                ToastUtil.show("有条目丢失音频文件，请核实后再试");
                System.out.println("diu shi "+audioPath);
                return;
            }
            audioFilPaths.add(audioPath);
        }

        //合成一个文件
        try {
            WAVUtil.getInstance().join(filePath.replace(".txt",Constant.AUDIO_SUFFIX),audioFilPaths);
        } catch (FrameRecorder.Exception e) {
            e.printStackTrace();
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
        }
        List<Record> records = new ArrayList<>();
        for (Object bean:
                tableView.getItems()) {
            records.add(((YBCCBean) bean).getRecord());
        }

        String[] field_keys =null;
        if (tableType==0||
                tableType==1){
            field_keys   = new String[]{"investCode","content","IPA","MWFY","english","spell","note"};

        }else if (tableType==2){
            field_keys   = new String[]{"investCode","content","IPA","MWFY","english","note","free_trans"};

        }

        List<WAVUtil.AudioAttr> audioAttrs = new ArrayList<>();
        double startTime = 0.0;
        for (int i=0,max = records.size();i<max;i++){
            Record record = records.get(i);
            WAVUtil.AudioAttr audioAttr = new WAVUtil.AudioAttr();
            audioAttr.start = startTime+"";

            double endTime = WAVUtil.getInstance().getAudioTimeLine(Constant.getAudioPath(record.baseId+"",record.uuid));
            endTime/=1000;
            audioAttr.end = (startTime+endTime)+"";

            startTime += endTime;
            audioAttrs.add(audioAttr);
        }

        File file = new File(filePath);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));

            for (int j = 0 , max1 = field_keys.length; j <max1;j++){
                for (int i=0,max = records.size();i<max;i++){
                    Record record = records.get(i);

                    Field field = Record.class.getDeclaredField(field_keys[j]);
                    Object value = field.get(record);
                    String text = value==null?"":value.toString();
                    if (!(i==0&&j==0)){
                        writer.write("\r\n");
                    }
                    writer.write(audioAttrs.get(i).start + "\t" + audioAttrs.get(i).end + "\t" + text);


                }
            }
            writer.close();

            ToastUtil.show("导出成功");
        }catch (Exception e){
            ToastUtil.show("导出失败");
        }
    }

    public List<String> readAttrTitle(String path){
        File file = new File(path);
        if (!file.exists()){
            return null;
        }
        List<String> strings = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String tempLine;
            int i=0;
            while ((tempLine= reader.readLine())!=null){
                System.out.println("line" + tempLine);
                if (i==0||tempLine.replaceAll(" ","").startsWith("0.0")){
                    System.out.println("start "+tempLine);
                    i++;
                    String[] contents = tempLine.split("\t");
                    strings.add("第"+i+"层"+"\n"+contents[contents.length - 1]);
                }
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return strings;
    }

    public void deco(String txtPath, String audioPath, List<BindResult> bindResults, int tableType){
        File file = new File(txtPath);
        if (!file.exists()){
            return;
        }
        List<Record>  records = new ArrayList<>();
        List<WAVUtil.AudioAttr> audioAttrs = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String temp = null;
            int i = 0;//行数

            int j = 0;//层数
            String key = null;

            while ((temp = reader.readLine())!= null){
                String[] contents = temp.split("\t");
                 if (i==0||temp.startsWith("0.0")){
                     key = "第"+(i+1)+"层"+"\n"+contents[contents.length - 1];
                     i++;

                     j=0;
                 }
                if (key.contains("第1层")){
                     records.add(new Record());
                }


                 Record record = records.get(j);
                 //赋值
                String fieldName = BindResult.getKey2ByKey1(bindResults,key);
                String[] line = temp.split("\t");
                try {
                    Field field = Record.class.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    if (line.length==3) {
                        field.set(record, line[2]);
                    }else {
                        field.set(record,"");
                    }
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                if (i==2){
                    WAVUtil.AudioAttr audioAttr = new WAVUtil.AudioAttr();
                    audioAttr.start = ((long)(Double.valueOf(line[0])*1000))+ "";
                    audioAttrs.add(audioAttr);
                }

                j++;
            }

            Table table = new Table(tableType+"");
            DbHelper.getInstance().insertNewTable(table);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            for (int k=0,max = records.size();k<max;k++){
                Record record = records.get(k);
                record.uuid = UUID.randomUUID().toString();
                record.baseId = table.id;
                if (!TextUtil.isEmpty(record.investCode)){
                    record.baseCode = record.investCode;

                }else if (!TextUtil.isEmpty(record.baseCode)){
                    record.investCode = record.baseCode;

                }else {
                    record.investCode = "A"+String.format("%0" + 5 + "d", k+1);
                    record.baseCode = record.investCode;
                }

                if (audioPath != null){
                    record.createDate =simpleDateFormat.format(new Date());
                    record.done = "1";
                }
                record.autoSupple();

                WAVUtil.AudioAttr audioAttr = audioAttrs.get(k);
                audioAttr.path = Constant.getAudioPath(record.baseId+"",record.uuid);

            }

            WAVUtil.getInstance().deco(audioAttrs,audioPath);


            DbHelper.getInstance().insertOrUpdateRecord(records);

            System.out.println(records);

            ToastUtil.show("导入成功");

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    MainController.getMainC().openTable(table);
                }
            });
        }catch (Exception e){
            ToastUtil.show("导入失败");
            e.printStackTrace();
        }
    }
}
