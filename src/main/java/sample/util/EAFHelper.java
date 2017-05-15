package sample.util;

import javafx.scene.control.TableView;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameRecorder;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXWriter;
import org.dom4j.io.XMLWriter;
import sample.controller.YBCC.YBCCBean;
import sample.entity.Record;
import sample.entity.Table;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.bytedeco.javacpp.*;
/**
 * Created by chenweiqi on 2017/5/11.
 */
public class EAFHelper {

    static EAFHelper eafHelper;

    private EAFHelper(){

    }

    public static synchronized EAFHelper getInstance(){
        if (eafHelper==null){
            eafHelper = new EAFHelper();
        }
        return eafHelper;
    }


    public void writeToEaf(String filePath, TableView tableView, Table t){
        File eafFilePath = new File(filePath);

        int tableType = Integer.parseInt(t.getDatatype());

        List content = tableView.getItems();

        List<String> audioFilPaths = new ArrayList<>();
        for (int i=0;i<content.size();i++){
            YBCCBean bean = ((YBCCBean) tableView.getItems().get(i));
            Record r = bean.getRecord();
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
            WAVUtil.getInstance().join(filePath.replace(".eaf",Constant.AUDIO_SUFFIX),audioFilPaths);
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

        createXML(filePath,filePath.replace(".eaf",Constant.AUDIO_SUFFIX),records);

        if (tableType == 0){//字表

        }else if (tableType == 1){//词

        }else if (tableType == 2){//句

        }
    }



    private void createXML(String savePath,String media_url,List<Record> records){
        Document doc = DocumentHelper.createDocument();
        Element rootElement = doc.addElement("ANNOTATION_DOCUMENT");
        rootElement.addAttribute("DATE","");
        rootElement.addAttribute("AUTHOR","");
        rootElement.addAttribute("VERSION","2.1");
        rootElement.addAttribute("FORMAT","2.1");

        Element header = rootElement.addElement("HEADER");
        header.addAttribute("MEDIA_FILE","");
        header.addAttribute("TIME_UNITS","milliseconds");

        Element media_descriptor = header.addElement("MEDIA_DESCRIPTOR");
        media_descriptor.addAttribute("MEDIA_URL",media_url);
        media_descriptor.addAttribute("MIME_TYPE","audio/x-wav");

        Element time_order = rootElement.addElement("TIME_ORDER");
        long timeLine=0;
        for (int i=0,max = records.size();i<max;i++){
            Element time_slot = time_order.addElement("TIME_SLOT");
            time_slot.addAttribute("TIME_SLOT_ID","T"+i);
            time_slot.addAttribute("TIME_VALUE",""+timeLine);
            Record record = records.get(i);
            String path = Constant.getAudioPath(record.baseId+"",record.uuid);
            System.out.println(new File(path).exists());
            System.out.println(WAVUtil.getInstance().getAudioTimeLine(path));
            timeLine += WAVUtil.getInstance().getAudioTimeLine(path);
        }

        String[] tierRefType = {"SN","CH","IPA","MWFY","EN","Pinyin","Note"};
        String[] annotation_value_key = {"baseCode","content","IPA","MWFY","english","spell","note"};

        for (int i=0,max1 = tierRefType.length;i<max1;i++){
            Element tier = rootElement.addElement("TIER");
            tier.addAttribute("TIER_ID","X[SN] (TIE"+i+")");
            tier.addAttribute("PARTICIPANT","SPK0");
            tier.addAttribute("LINGUISTIC_TYPE_REF",tierRefType[i]);
            tier.addAttribute("DEFAULT_LOCALE","en");
            if (i!=0){
                tier.addAttribute("PARENT_REF","X[SN] (TIE0)");
            }
            for (int j = 0,max2 = records.size();j<max2;j++){
                Element annotation = tier.addElement("ANNOTATION");
                Element alignable_annotation = annotation.addElement("ALIGNABLE_ANNOTATION");
                alignable_annotation.addAttribute("ANNOTATION_ID","TIE"+i+"_"+j);
                alignable_annotation.addAttribute("TIME_SLOT_REF1","T"+j);
                alignable_annotation.addAttribute("TIME_SLOT_REF2","T"+(j+1));
                Element annotation_value = annotation.addElement("ANNOTATION_VALUE");
                Record record = records.get(j);
                try {
                     Field field= record.getClass().getDeclaredField(annotation_value_key[i]);
                     String value = field.get(record)==null?"":field.get(record).toString();
                    annotation_value.addText(value);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        for (int i=0,max1 = tierRefType.length;i<max1;i++){
            Element linguistic_type = rootElement.addElement("LINGUISTIC_TYPE");
            linguistic_type.addAttribute("CONSTRAINTS","Time_Subdivision");
            linguistic_type.addAttribute("GRAPHIC_REFERENCES","false");
            linguistic_type.addAttribute("LINGUISTIC_TYPE_ID",tierRefType[i]);
            linguistic_type.addAttribute("TIME_ALIGNABLE","true");

        }

        Element locale = rootElement.addElement("LOCALE");
        locale.addAttribute("LANGUAGE_CODE","en");
        locale.addAttribute("COUNTRY_CODE","EN");


        Element constraint1 = rootElement.addElement("CONSTRAINT");
        constraint1.addAttribute("STEREOTYPE","Time_Subdivision");
        constraint1.addAttribute("DESCRIPTION","Time subdivision of parent annotation's time interval, no time gaps allowed within this interval");

        Element constraint2 = rootElement.addElement("CONSTRAINT");
        constraint2.addAttribute("STEREOTYPE","Symbolic_Subdivision");
        constraint2.addAttribute("DESCRIPTION","Symbolic subdivision of a parent annotation. Annotations refering to the same parent are ordered");

        Element constraint3 = rootElement.addElement("CONSTRAINT");
        constraint3.addAttribute("STEREOTYPE","Symbolic_Association");
        constraint3.addAttribute("DESCRIPTION","1-1 association with a parent annotation");



        try {
            XMLWriter xmlWriter = new XMLWriter(new FileOutputStream(new File(savePath),false), OutputFormat.createPrettyPrint());
            xmlWriter.write(doc);
            xmlWriter.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
