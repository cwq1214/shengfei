package sample.util;

import javafx.scene.control.Tab;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameRecorder;
import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.SAXWriter;
import org.dom4j.io.XMLWriter;
import sample.controller.YBCC.YBCCBean;
import sample.entity.*;

import java.io.*;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

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
        Speaker speaker = null;
        if (!TextUtil.isEmpty(t.speaker)) {
            speaker = DbHelper.getInstance().getSpeakerById(Integer.valueOf(t.speaker));
        }else {
            speaker = new Speaker();
        }
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

        createXML(speaker,filePath,filePath.replace(".eaf",Constant.AUDIO_SUFFIX),records,tableType);


    }



    private void createXML(Speaker speaker, String savePath, String media_url, List<Record> records, int tableType){
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

        String[] tierRefType =null;
        String[] annotation_value_key =null;
        if (tableType == 0//字
            ||tableType == 1){//词
            tierRefType = new String[]{"SN", "CH", "IPA", "MWFY", "EN", "Pinyin", "Note"};
            annotation_value_key = new String[]{"investCode", "content", "IPA", "MWFY", "english", "spell", "note"};
        }else if (tableType == 2){//句
            tierRefType = new String[]{"IPA","PTHtrans","WORD"};
            annotation_value_key = new String[]{"IPA", "free_trans", "content"};
        }



        for (int i=0,max1 = tierRefType.length;i<max1;i++){
            Element tier = rootElement.addElement("TIER");
            tier.addAttribute("TIER_ID",speaker.speakcode+"[SN] (TIE"+i+")");
            tier.addAttribute("PARTICIPANT",speaker.ID+"");
            tier.addAttribute("LINGUISTIC_TYPE_REF",tierRefType[i]);
            tier.addAttribute("DEFAULT_LOCALE","en");
            if (i!=0){
                tier.addAttribute("PARENT_REF",speaker.speakcode+"[SN] (TIE0)");
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


    public List<String> readXmlAttrTitle(String xmlPath){
        List<String> xmlAttr = new ArrayList<>();
        SAXReader reader = new SAXReader();
        try {
            Document doc = reader.read(new File(xmlPath));
            Element rootElement = doc.getRootElement();

            List<Element> tiers = rootElement.elements("TIER");

            for (int i=0,max = tiers.size();i<max;i++){
                String value =tiers.get(i).attribute("LINGUISTIC_TYPE_REF").getValue();
                xmlAttr.add(value);
            }

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return xmlAttr;
    }

    public XmlResult deco(String xmlPath, String audioPath, List<BindResult> bindResults,int tableType){
        XmlResult result = new XmlResult();

        Speaker speaker = new Speaker();
        List<WAVUtil.AudioAttr> audioAttrs = new ArrayList<>();
        List<Record> records = new ArrayList<>();

        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(new File(xmlPath));
            Element root = document.getRootElement();

            Element time_order = root.element("TIME_ORDER");
            for (int i=0,max = time_order.elements().size(); i < max ;i++){
                Element time_slot = (Element) time_order.elements().get(i);
                String startTime = time_slot.attribute("TIME_VALUE").getValue();
                WAVUtil.AudioAttr audioAttr = new WAVUtil.AudioAttr();
                audioAttr.start = startTime;
                audioAttrs.add(audioAttr);
            }

            List<Element> tiers = root.elements("TIER");

            for (int i=0,max1 =tiers.size() ; i < max1 ;i++){
                Element tier = tiers.get(i);
                String xmlAttrTitle = tier.attribute("LINGUISTIC_TYPE_REF").getValue();
                String fieldName = BindResult.getKey2ByKey1(bindResults,xmlAttrTitle);
                if (TextUtil.isEmpty(fieldName)){
                    continue;
                }

                for (int j=0,max2 = tier.elements().size();j < max2;j++){
                    if (i==0){
                        records.add(new Record());
                    }
                    Record record = records.get(j);
                    try {
                        Field field = Record.class.getDeclaredField(fieldName);
                        field.setAccessible(true);
                        field.set(record, ((Element) tier.elements().get(j)).element("ANNOTATION_VALUE").getText());
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }

            Table table = new Table(tableType+"");
            DbHelper.getInstance().insertNewTable(table);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for(int i=0,max= records.size();i<max;i++){
                    records.get(i).uuid = UUID.randomUUID().toString();
                    records.get(i).baseId = table.id;
                    if (!TextUtil.isEmpty(records.get(i).investCode)){
                        records.get(i).baseCode = records.get(i).investCode;

                    }else if (!TextUtil.isEmpty(records.get(i).baseCode)){
                        records.get(i).investCode = records.get(i).baseCode;

                    }else {
                        records.get(i).investCode = "A"+String.format("%0" + 5 + "d", i + 1);
                        records.get(i).baseCode = records.get(i).investCode;
                    }

                    records.get(i).createDate =simpleDateFormat.format(new Date());
                    records.get(i).done = "1";


                audioAttrs.get(i).path = Constant.getAudioPath(records.get(i).baseId+"",records.get(i).uuid);
            }

            DbHelper.getInstance().insertOrUpdateRecord(records);

            WAVUtil.getInstance().deco(audioAttrs,audioPath);


        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return result;
    }
}
