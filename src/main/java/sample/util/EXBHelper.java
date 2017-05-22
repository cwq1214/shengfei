package sample.util;

import javafx.scene.control.TableView;
import org.apache.poi.ss.formula.functions.T;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameRecorder;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import sample.controller.YBCC.YBCCBean;
import sample.entity.*;

import java.io.*;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by chenweiqi on 2017/5/16.
 */
public class EXBHelper {
    private static EXBHelper exbHelper;
    private EXBHelper(){

    }

    public static synchronized EXBHelper getInstance(){
        synchronized (EXBHelper.class){
            if (exbHelper ==null){
                exbHelper = new EXBHelper();
            }
        }
        return exbHelper;
    }



    public void writeToExb(String filePath, TableView tableView, Table t){
        Speaker speaker;
        if (!TextUtil.isEmpty(t.speaker)){
            speaker = DbHelper.getInstance().getSpeakerById(Integer.valueOf(t.speaker));
        }else {
            speaker = new Speaker();
        }

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
            WAVUtil.getInstance().join(filePath.replace(".exb",Constant.AUDIO_SUFFIX),audioFilPaths);
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
        createXML(speaker,filePath,filePath.replace(".exb",Constant.AUDIO_SUFFIX),records,tableType);
    }


    public void writeToEXB(File savePath, List<Topic> topics) throws DocumentException, NoSuchFieldException, IllegalAccessException {
        Document document = DocumentHelper.createDocument();
        Element basic = document.addElement("basic-transcription");
        Element head = basic.addElement("head");
        head.add(DocumentHelper.parseText("<meta-information>\n" +
                "<ud-meta-information>\n" +
                "</ud-meta-information>\n" +
                "</meta-information>").getRootElement());
        Element speakertable = head.addElement("speakertable");

        for (int i = 0,max = topics.size();i<max;i++){
            Element speaker = speakertable.addElement("speaker");
            speaker.addAttribute("id",topics.get(i).speakerId);
            speaker.addElement("abbreviation");
        }
        Element basic_body = basic.addElement("basic-body");
        Element tb_type = basic_body.addElement("tb-type");
        tb_type.addText("HY");
        Element common_timeLine = basic_body.addElement("common-timeline");
        for (int i = 0,max = topics.size();i<max;i++){
            Element tli = common_timeLine.addElement("tli");
            tli.addAttribute("id","T"+i);
            tli.addAttribute("time","0.0");
        }

        String[] category = {"IPA","MWFY","PYIN","PTHWORD","PTHSENT","NOTE","EN"};
        String[] fieldName = {"ipa","mwfy","spell","word_trans","free_trans","note","english"};
        String[] type = {"t","a","a","a","a","a","a"};

        for (int i = 0,max = topics.size();i<max;i++){

            for (int j=0 , max2 =fieldName.length;j<max2;j++){
                Element tier = basic_body.addElement("tier");
                tier.addAttribute("id","TIE0");
                tier.addAttribute("speaker",topics.get(i).speakerId);
                tier.addAttribute("category",category[j]);
                tier.addAttribute("type",type[j]);
                tier.addAttribute("display-name",topics.get(i).speakerId+"["+category[j]+"]");

                Element event = tier.addElement("event");
                event.addAttribute("start","T0");
                event.addAttribute("end","T1");
                Field field = Topic.class.getDeclaredField(fieldName[j]);
                Object value = field.get(topics.get(i));
                event.addText(value==null?"":value.toString());
            }


        }
        basic.add(DocumentHelper.parseText("<tierformat-table>\n" +
                "<timeline-item-format show-every-nth-numbering=\"1\" show-every-nth-absolute=\"1\" absolute-time-format=\"time\" miliseconds-digits=\"1\"/>\n" +
                "<tier-format tierref=\"EMPTY\">\n" +
                "<property name=\"row-height-calculation\">Generous</property>\n" +
                "<property name=\"fixed-row-height\">10</property>\n" +
                "<property name=\"font-face\">Plain</property>\n" +
                "<property name=\"font-color\">white</property>\n" +
                "<property name=\"chunk-border-style\">solid</property>\n" +
                "<property name=\"bg-color\">white</property>\n" +
                "<property name=\"text-alignment\">Left</property>\n" +
                "<property name=\"chunk-border-color\">#R00G00B00</property>\n" +
                "<property name=\"chunk-border\">\n" +
                "</property>\n" +
                "<property name=\"font-size\">2</property>\n" +
                "<property name=\"font-name\">Times New Roman</property>\n" +
                "</tier-format>\n" +
                "<tier-format tierref=\"COLUMN-LABEL\">\n" +
                "<property name=\"row-height-calculation\">Generous</property>\n" +
                "<property name=\"fixed-row-height\">10</property>\n" +
                "<property name=\"font-face\">Plain</property>\n" +
                "<property name=\"font-color\">black</property>\n" +
                "<property name=\"chunk-border-style\">solid</property>\n" +
                "<property name=\"bg-color\">lightGray</property>\n" +
                "<property name=\"text-alignment\">Left</property>\n" +
                "<property name=\"chunk-border-color\">#R00G00B00</property>\n" +
                "<property name=\"chunk-border\">\n" +
                "</property>\n" +
                "<property name=\"font-size\">7</property>\n" +
                "<property name=\"font-name\">Times New Roman</property>\n" +
                "</tier-format>\n" +
                "<tier-format tierref=\"TIE6\">\n" +
                "<property name=\"row-height-calculation\">Generous</property>\n" +
                "<property name=\"fixed-row-height\">10</property>\n" +
                "<property name=\"font-face\">Plain</property>\n" +
                "<property name=\"font-color\">black</property>\n" +
                "<property name=\"chunk-border-style\">solid</property>\n" +
                "<property name=\"bg-color\">white</property>\n" +
                "<property name=\"text-alignment\">Left</property>\n" +
                "<property name=\"chunk-border-color\">#R00G00B00</property>\n" +
                "<property name=\"chunk-border\">\n" +
                "</property>\n" +
                "<property name=\"font-size\">14</property>\n" +
                "<property name=\"font-name\">Times New Roman</property>\n" +
                "</tier-format>\n" +
                "<tier-format tierref=\"ROW-LABEL\">\n" +
                "<property name=\"row-height-calculation\">Generous</property>\n" +
                "<property name=\"fixed-row-height\">10</property>\n" +
                "<property name=\"font-face\">Bold</property>\n" +
                "<property name=\"font-color\">black</property>\n" +
                "<property name=\"chunk-border-style\">solid</property>\n" +
                "<property name=\"bg-color\">lightGray</property>\n" +
                "<property name=\"text-alignment\">Left</property>\n" +
                "<property name=\"chunk-border-color\">#R00G00B00</property>\n" +
                "<property name=\"chunk-border\">\n" +
                "</property>\n" +
                "<property name=\"font-size\">10</property>\n" +
                "<property name=\"font-name\">Times New Roman</property>\n" +
                "</tier-format>\n" +
                "<tier-format tierref=\"TIE5\">\n" +
                "<property name=\"row-height-calculation\">Generous</property>\n" +
                "<property name=\"fixed-row-height\">10</property>\n" +
                "<property name=\"font-face\">Plain</property>\n" +
                "<property name=\"font-color\">black</property>\n" +
                "<property name=\"chunk-border-style\">solid</property>\n" +
                "<property name=\"bg-color\">white</property>\n" +
                "<property name=\"text-alignment\">Left</property>\n" +
                "<property name=\"chunk-border-color\">#R00G00B00</property>\n" +
                "<property name=\"chunk-border\">\n" +
                "</property>\n" +
                "<property name=\"font-size\">14</property>\n" +
                "<property name=\"font-name\">宋体</property>\n" +
                "</tier-format>\n" +
                "<tier-format tierref=\"TIE4\">\n" +
                "<property name=\"row-height-calculation\">Generous</property>\n" +
                "<property name=\"fixed-row-height\">10</property>\n" +
                "<property name=\"font-face\">Plain</property>\n" +
                "<property name=\"font-color\">black</property>\n" +
                "<property name=\"chunk-border-style\">solid</property>\n" +
                "<property name=\"bg-color\">white</property>\n" +
                "<property name=\"text-alignment\">Left</property>\n" +
                "<property name=\"chunk-border-color\">#R00G00B00</property>\n" +
                "<property name=\"chunk-border\">\n" +
                "</property>\n" +
                "<property name=\"font-size\">14</property>\n" +
                "<property name=\"font-name\">宋体</property>\n" +
                "</tier-format>\n" +
                "<tier-format tierref=\"TIE3\">\n" +
                "<property name=\"row-height-calculation\">Generous</property>\n" +
                "<property name=\"fixed-row-height\">10</property>\n" +
                "<property name=\"font-face\">Plain</property>\n" +
                "<property name=\"font-color\">black</property>\n" +
                "<property name=\"chunk-border-style\">solid</property>\n" +
                "<property name=\"bg-color\">white</property>\n" +
                "<property name=\"text-alignment\">Left</property>\n" +
                "<property name=\"chunk-border-color\">#R00G00B00</property>\n" +
                "<property name=\"chunk-border\">\n" +
                "</property>\n" +
                "<property name=\"font-size\">14</property>\n" +
                "<property name=\"font-name\">Times New Roman</property>\n" +
                "</tier-format>\n" +
                "<tier-format tierref=\"TIE2\">\n" +
                "<property name=\"row-height-calculation\">Generous</property>\n" +
                "<property name=\"fixed-row-height\">10</property>\n" +
                "<property name=\"font-face\">Plain</property>\n" +
                "<property name=\"font-color\">black</property>\n" +
                "<property name=\"chunk-border-style\">solid</property>\n" +
                "<property name=\"bg-color\">white</property>\n" +
                "<property name=\"text-alignment\">Left</property>\n" +
                "<property name=\"chunk-border-color\">#R00G00B00</property>\n" +
                "<property name=\"chunk-border\">\n" +
                "</property>\n" +
                "<property name=\"font-size\">14</property>\n" +
                "<property name=\"font-name\">Times New Roman</property>\n" +
                "</tier-format>\n" +
                "<tier-format tierref=\"TIE1\">\n" +
                "<property name=\"row-height-calculation\">Generous</property>\n" +
                "<property name=\"fixed-row-height\">10</property>\n" +
                "<property name=\"font-face\">Plain</property>\n" +
                "<property name=\"font-color\">black</property>\n" +
                "<property name=\"chunk-border-style\">solid</property>\n" +
                "<property name=\"bg-color\">white</property>\n" +
                "<property name=\"text-alignment\">Left</property>\n" +
                "<property name=\"chunk-border-color\">#R00G00B00</property>\n" +
                "<property name=\"chunk-border\">\n" +
                "</property>\n" +
                "<property name=\"font-size\">14</property>\n" +
                "<property name=\"font-name\">宋体</property>\n" +
                "</tier-format>\n" +
                "<tier-format tierref=\"TIE0\">\n" +
                "<property name=\"row-height-calculation\">Generous</property>\n" +
                "<property name=\"fixed-row-height\">10</property>\n" +
                "<property name=\"font-face\">Plain</property>\n" +
                "<property name=\"font-color\">black</property>\n" +
                "<property name=\"chunk-border-style\">solid</property>\n" +
                "<property name=\"bg-color\">white</property>\n" +
                "<property name=\"text-alignment\">Left</property>\n" +
                "<property name=\"chunk-border-color\">#R00G00B00</property>\n" +
                "<property name=\"chunk-border\">\n" +
                "</property>\n" +
                "<property name=\"font-size\">14</property>\n" +
                "<property name=\"font-name\">Times New Roman</property>\n" +
                "</tier-format>\n" +
                "<tier-format tierref=\"EMPTY-EDITOR\">\n" +
                "<property name=\"row-height-calculation\">Generous</property>\n" +
                "<property name=\"fixed-row-height\">10</property>\n" +
                "<property name=\"font-face\">Plain</property>\n" +
                "<property name=\"font-color\">white</property>\n" +
                "<property name=\"chunk-border-style\">solid</property>\n" +
                "<property name=\"bg-color\">lightGray</property>\n" +
                "<property name=\"text-alignment\">Left</property>\n" +
                "<property name=\"chunk-border-color\">#R00G00B00</property>\n" +
                "<property name=\"chunk-border\">\n" +
                "</property>\n" +
                "<property name=\"font-size\">2</property>\n" +
                "<property name=\"font-name\">Times New Roman</property>\n" +
                "</tier-format>\n" +
                "<tier-format tierref=\"SUB-ROW-LABEL\">\n" +
                "<property name=\"row-height-calculation\">Generous</property>\n" +
                "<property name=\"fixed-row-height\">10</property>\n" +
                "<property name=\"font-face\">Plain</property>\n" +
                "<property name=\"font-color\">black</property>\n" +
                "<property name=\"chunk-border-style\">solid</property>\n" +
                "<property name=\"bg-color\">white</property>\n" +
                "<property name=\"text-alignment\">Right</property>\n" +
                "<property name=\"chunk-border-color\">#R00G00B00</property>\n" +
                "<property name=\"chunk-border\">\n" +
                "</property>\n" +
                "<property name=\"font-size\">8</property>\n" +
                "<property name=\"font-name\">Times New Roman</property>\n" +
                "</tier-format>\n" +
                "</tierformat-table>").getRootElement());


        try {
            XMLWriter xmlWriter = new XMLWriter(new FileOutputStream(savePath,false), OutputFormat.createPrettyPrint());
            xmlWriter.write(document);
            xmlWriter.close();
            ToastUtil.show("导出成功");
        } catch (Exception e) {
            ToastUtil.show("导出失败");
            e.printStackTrace();
        }
    }

    private void createXML(Speaker speakerBean, String savePath, String media_url, List<Record> records, int tableType){
        Document doc = DocumentHelper.createDocument();
        doc.addComment(" (c) http://www.rrz.uni-hamburg.de/exmaralda ");
        Element root = doc.addElement("basic-transcription");
        Element head = root.addElement("head");
        Element meta_information = head.addElement("meta-information");
        meta_information.addElement("project-name");
        meta_information.addElement("transcription-name");
        Element refrenced_file = meta_information.addElement("referenced-file");
        refrenced_file.addAttribute("url",media_url);
        meta_information.addElement("ud-meta-information");
        meta_information.addElement("comment");
        meta_information.addElement("transcription-convention");
        Element speakertable = head.addElement("speakertable");
        Element speaker = speakertable.addElement("speaker");
        speaker.addAttribute("id",speakerBean.ID+"");
        Element abb = speaker.addElement("abbreviation");
        if (!TextUtil.isEmpty(speakerBean.speakcode))
            abb.addText(speakerBean.speakcode);
        Element sex = speaker.addElement("sex");
        if (!TextUtil.isEmpty(speakerBean.sex))
            sex.addAttribute("value",speakerBean.sex);
        Element languages_used = speaker.addElement("languages-used");
        if (!TextUtil.isEmpty(speakerBean.usualLang))
            languages_used.addText(speakerBean.usualLang);
        Element l1 = speaker.addElement("l1");
        if (!TextUtil.isEmpty(speakerBean.motherLang))
            l1.addText(speakerBean.motherLang);
        Element l2 = speaker.addElement("l2");
        if (!TextUtil.isEmpty(speakerBean.secondLang))
            l2.addText(speakerBean.secondLang);
        speaker.addElement("ud-speaker-information");
        Element comment = speaker.addElement("comment");
        if (!TextUtil.isEmpty(speakerBean.notetext))
            comment.addText(speakerBean.notetext);

        Element basic_body = root.addElement("basic-body");
        Element common_timeLine = basic_body.addElement("common-timeline");


        double time = 0.0;
        for (int i=0,max = records.size(); i < max ;i++){
            Element tli = common_timeLine.addElement("tli");
            tli.addAttribute("id","T"+i);
            tli.addAttribute("time",time+"");
            String path = Constant.getAudioPath(records.get(i).baseId+"",records.get(i).uuid);
            time += (WAVUtil.getInstance().getAudioTimeLine(path))/1000;
        }

        String[] category = null;
        String[] type = null;
        String[] field_key = null;

        if (tableType==0
                || tableType==1){

            category = new String[]{"SN","CH","IPA","MWFY","EN","Pinyin","Note"};
            type = new String[]{"t","a","a","a","a","a","a"};
            field_key = new String[]{"investCode","content","IPA","MWFY","english","spell","note"};
        }else if (tableType==2){
            category = new String[]{"IPA","WORD","PTHtrans"};
            type = new String[]{"t","a","a"};
            field_key = new String[]{"IPA","content","free_trans"};
        }

        for (int i=0,max = category.length;i<max;i++){
            Element tier = basic_body.addElement("tier");
            tier.addAttribute("id","TIE"+i);
            tier.addAttribute("speaker",speakerBean.ID+"");
            tier.addAttribute("category",category[i]);
            tier.addAttribute("type",type[i]);
            tier.addAttribute("display-name",speakerBean.speakcode+"["+category[i]+"]");

            for (int j=0,max2 = records.size();j<max2;j++){
                Element event = tier.addElement("event");
                event.addAttribute("start","T"+j);
                event.addAttribute("end","T"+(j+1));

                try {
                    Field field = records.get(j).getClass().getField(field_key[i]);
                    Object value = field.get(records.get(j));
                    event.addText(value==null?"":value.toString());
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

        }

        try {

            Document tierformat_table = DocumentHelper.parseText("<tierformat-table>\n" +
                    "<timeline-item-format show-every-nth-numbering=\"1\" show-every-nth-absolute=\"1\" absolute-time-format=\"time\" miliseconds-digits=\"1\"/>\n" +
                    "<tier-format tierref=\"EMPTY\">\n" +
                    "<property name=\"row-height-calculation\">Generous</property>\n" +
                    "<property name=\"fixed-row-height\">10</property>\n" +
                    "<property name=\"font-face\">Plain</property>\n" +
                    "<property name=\"font-color\">white</property>\n" +
                    "<property name=\"chunk-border-style\">solid</property>\n" +
                    "<property name=\"bg-color\">white</property>\n" +
                    "<property name=\"text-alignment\">Left</property>\n" +
                    "<property name=\"chunk-border-color\">#R00G00B00</property>\n" +
                    "<property name=\"chunk-border\">\n" +
                    "</property>\n" +
                    "<property name=\"font-size\">2</property>\n" +
                    "<property name=\"font-name\">Times New Roman</property>\n" +
                    "</tier-format>\n" +
                    "<tier-format tierref=\"COLUMN-LABEL\">\n" +
                    "<property name=\"row-height-calculation\">Generous</property>\n" +
                    "<property name=\"fixed-row-height\">10</property>\n" +
                    "<property name=\"font-face\">Plain</property>\n" +
                    "<property name=\"font-color\">black</property>\n" +
                    "<property name=\"chunk-border-style\">solid</property>\n" +
                    "<property name=\"bg-color\">lightGray</property>\n" +
                    "<property name=\"text-alignment\">Left</property>\n" +
                    "<property name=\"chunk-border-color\">#R00G00B00</property>\n" +
                    "<property name=\"chunk-border\">\n" +
                    "</property>\n" +
                    "<property name=\"font-size\">7</property>\n" +
                    "<property name=\"font-name\">Times New Roman</property>\n" +
                    "</tier-format>\n" +
                    "<tier-format tierref=\"TIE6\">\n" +
                    "<property name=\"row-height-calculation\">Generous</property>\n" +
                    "<property name=\"fixed-row-height\">10</property>\n" +
                    "<property name=\"font-face\">Plain</property>\n" +
                    "<property name=\"font-color\">black</property>\n" +
                    "<property name=\"chunk-border-style\">solid</property>\n" +
                    "<property name=\"bg-color\">white</property>\n" +
                    "<property name=\"text-alignment\">Left</property>\n" +
                    "<property name=\"chunk-border-color\">#R00G00B00</property>\n" +
                    "<property name=\"chunk-border\">\n" +
                    "</property>\n" +
                    "<property name=\"font-size\">14</property>\n" +
                    "<property name=\"font-name\">Times New Roman</property>\n" +
                    "</tier-format>\n" +
                    "<tier-format tierref=\"ROW-LABEL\">\n" +
                    "<property name=\"row-height-calculation\">Generous</property>\n" +
                    "<property name=\"fixed-row-height\">10</property>\n" +
                    "<property name=\"font-face\">Bold</property>\n" +
                    "<property name=\"font-color\">black</property>\n" +
                    "<property name=\"chunk-border-style\">solid</property>\n" +
                    "<property name=\"bg-color\">lightGray</property>\n" +
                    "<property name=\"text-alignment\">Left</property>\n" +
                    "<property name=\"chunk-border-color\">#R00G00B00</property>\n" +
                    "<property name=\"chunk-border\">\n" +
                    "</property>\n" +
                    "<property name=\"font-size\">10</property>\n" +
                    "<property name=\"font-name\">Times New Roman</property>\n" +
                    "</tier-format>\n" +
                    "<tier-format tierref=\"TIE5\">\n" +
                    "<property name=\"row-height-calculation\">Generous</property>\n" +
                    "<property name=\"fixed-row-height\">10</property>\n" +
                    "<property name=\"font-face\">Plain</property>\n" +
                    "<property name=\"font-color\">black</property>\n" +
                    "<property name=\"chunk-border-style\">solid</property>\n" +
                    "<property name=\"bg-color\">white</property>\n" +
                    "<property name=\"text-alignment\">Left</property>\n" +
                    "<property name=\"chunk-border-color\">#R00G00B00</property>\n" +
                    "<property name=\"chunk-border\">\n" +
                    "</property>\n" +
                    "<property name=\"font-size\">14</property>\n" +
                    "<property name=\"font-name\">宋体</property>\n" +
                    "</tier-format>\n" +
                    "<tier-format tierref=\"TIE4\">\n" +
                    "<property name=\"row-height-calculation\">Generous</property>\n" +
                    "<property name=\"fixed-row-height\">10</property>\n" +
                    "<property name=\"font-face\">Plain</property>\n" +
                    "<property name=\"font-color\">black</property>\n" +
                    "<property name=\"chunk-border-style\">solid</property>\n" +
                    "<property name=\"bg-color\">white</property>\n" +
                    "<property name=\"text-alignment\">Left</property>\n" +
                    "<property name=\"chunk-border-color\">#R00G00B00</property>\n" +
                    "<property name=\"chunk-border\">\n" +
                    "</property>\n" +
                    "<property name=\"font-size\">14</property>\n" +
                    "<property name=\"font-name\">宋体</property>\n" +
                    "</tier-format>\n" +
                    "<tier-format tierref=\"TIE3\">\n" +
                    "<property name=\"row-height-calculation\">Generous</property>\n" +
                    "<property name=\"fixed-row-height\">10</property>\n" +
                    "<property name=\"font-face\">Plain</property>\n" +
                    "<property name=\"font-color\">black</property>\n" +
                    "<property name=\"chunk-border-style\">solid</property>\n" +
                    "<property name=\"bg-color\">white</property>\n" +
                    "<property name=\"text-alignment\">Left</property>\n" +
                    "<property name=\"chunk-border-color\">#R00G00B00</property>\n" +
                    "<property name=\"chunk-border\">\n" +
                    "</property>\n" +
                    "<property name=\"font-size\">14</property>\n" +
                    "<property name=\"font-name\">Times New Roman</property>\n" +
                    "</tier-format>\n" +
                    "<tier-format tierref=\"TIE2\">\n" +
                    "<property name=\"row-height-calculation\">Generous</property>\n" +
                    "<property name=\"fixed-row-height\">10</property>\n" +
                    "<property name=\"font-face\">Plain</property>\n" +
                    "<property name=\"font-color\">black</property>\n" +
                    "<property name=\"chunk-border-style\">solid</property>\n" +
                    "<property name=\"bg-color\">white</property>\n" +
                    "<property name=\"text-alignment\">Left</property>\n" +
                    "<property name=\"chunk-border-color\">#R00G00B00</property>\n" +
                    "<property name=\"chunk-border\">\n" +
                    "</property>\n" +
                    "<property name=\"font-size\">14</property>\n" +
                    "<property name=\"font-name\">Times New Roman</property>\n" +
                    "</tier-format>\n" +
                    "<tier-format tierref=\"TIE1\">\n" +
                    "<property name=\"row-height-calculation\">Generous</property>\n" +
                    "<property name=\"fixed-row-height\">10</property>\n" +
                    "<property name=\"font-face\">Plain</property>\n" +
                    "<property name=\"font-color\">black</property>\n" +
                    "<property name=\"chunk-border-style\">solid</property>\n" +
                    "<property name=\"bg-color\">white</property>\n" +
                    "<property name=\"text-alignment\">Left</property>\n" +
                    "<property name=\"chunk-border-color\">#R00G00B00</property>\n" +
                    "<property name=\"chunk-border\">\n" +
                    "</property>\n" +
                    "<property name=\"font-size\">14</property>\n" +
                    "<property name=\"font-name\">宋体</property>\n" +
                    "</tier-format>\n" +
                    "<tier-format tierref=\"TIE0\">\n" +
                    "<property name=\"row-height-calculation\">Generous</property>\n" +
                    "<property name=\"fixed-row-height\">10</property>\n" +
                    "<property name=\"font-face\">Plain</property>\n" +
                    "<property name=\"font-color\">black</property>\n" +
                    "<property name=\"chunk-border-style\">solid</property>\n" +
                    "<property name=\"bg-color\">white</property>\n" +
                    "<property name=\"text-alignment\">Left</property>\n" +
                    "<property name=\"chunk-border-color\">#R00G00B00</property>\n" +
                    "<property name=\"chunk-border\">\n" +
                    "</property>\n" +
                    "<property name=\"font-size\">14</property>\n" +
                    "<property name=\"font-name\">Times New Roman</property>\n" +
                    "</tier-format>\n" +
                    "<tier-format tierref=\"EMPTY-EDITOR\">\n" +
                    "<property name=\"row-height-calculation\">Generous</property>\n" +
                    "<property name=\"fixed-row-height\">10</property>\n" +
                    "<property name=\"font-face\">Plain</property>\n" +
                    "<property name=\"font-color\">white</property>\n" +
                    "<property name=\"chunk-border-style\">solid</property>\n" +
                    "<property name=\"bg-color\">lightGray</property>\n" +
                    "<property name=\"text-alignment\">Left</property>\n" +
                    "<property name=\"chunk-border-color\">#R00G00B00</property>\n" +
                    "<property name=\"chunk-border\">\n" +
                    "</property>\n" +
                    "<property name=\"font-size\">2</property>\n" +
                    "<property name=\"font-name\">Times New Roman</property>\n" +
                    "</tier-format>\n" +
                    "<tier-format tierref=\"SUB-ROW-LABEL\">\n" +
                    "<property name=\"row-height-calculation\">Generous</property>\n" +
                    "<property name=\"fixed-row-height\">10</property>\n" +
                    "<property name=\"font-face\">Plain</property>\n" +
                    "<property name=\"font-color\">black</property>\n" +
                    "<property name=\"chunk-border-style\">solid</property>\n" +
                    "<property name=\"bg-color\">white</property>\n" +
                    "<property name=\"text-alignment\">Right</property>\n" +
                    "<property name=\"chunk-border-color\">#R00G00B00</property>\n" +
                    "<property name=\"chunk-border\">\n" +
                    "</property>\n" +
                    "<property name=\"font-size\">8</property>\n" +
                    "<property name=\"font-name\">Times New Roman</property>\n" +
                    "</tier-format>\n" +
                    "</tierformat-table>");
            root.add(tierformat_table.getRootElement());
        } catch (DocumentException e) {
            e.printStackTrace();
        }


        try {
            XMLWriter xmlWriter = new XMLWriter(new FileOutputStream(new File(savePath),false), OutputFormat.createPrettyPrint());
            xmlWriter.write(doc);
            xmlWriter.close();
            ToastUtil.show("导出成功");
        } catch (Exception e) {
            ToastUtil.show("导出失败");
            e.printStackTrace();
        }
    }


    public void deco(String xmlPath, String audioPath, List<BindResult> bindResults, int tableType){
        SAXReader reader = new SAXReader();

        List<WAVUtil.AudioAttr> audioAttrs = new ArrayList<>();
        List<Record> records = new ArrayList<>();
        try {
            Document document = reader.read(new File(xmlPath));
            Element rootElement = document.getRootElement();
            Element basic_body = rootElement.element("basic-body");

            Element timeLine = basic_body.element("common-timeline");

            List<Element> timeLines = timeLine.elements();
            for (int i=0,max = timeLines.size();i<max;i++){
                String value = timeLines.get(i).attribute("time").getValue();
                WAVUtil.AudioAttr audioAttr = new WAVUtil.AudioAttr();

                audioAttr.start = (long)(Double.valueOf(value)*1000)+"";
                audioAttrs.add(audioAttr);
            }
            List<Element> tiers = basic_body.elements("tier");
            for (int i=0,max1 = tiers.size(); i < max1;i++){
                Element tier = tiers.get(i);
                String category = tier.attribute("category").getValue();
                String fieldName = BindResult.getKey2ByKey1(bindResults,category);
                if (TextUtil.isEmpty(fieldName)){
                    continue;
                }

                List<Element> events = tier.elements();
                for (int j=0,max2 = events.size(); j <max2;j++){
                    String value = events.get(j).getText();
                    if (i==0){
                        records.add(new Record());
                    }
                    Record record = records.get(j);
                    try {
                        Field field = Record.class.getDeclaredField(fieldName);
                        field.setAccessible(true);
                        field.set(record,value);
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
            for (int i=0,max = records.size(); i<max;i++){
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

            ToastUtil.show("导入成功");
        } catch (DocumentException e) {
            ToastUtil.show("导入失败");
            e.printStackTrace();
        }
    }

    public List<String> readXmlAttrTitle(String path) {
        List<String> strings = new ArrayList<>();

        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(new File(path));
            Element rootElement = document.getRootElement();
            List<Element> tiers = rootElement.element("basic-body").elements("tier");
            for (Element element :
                 tiers) {
                String value = element.attribute("category").getValue();
                strings.add(value);
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return strings;
    }
}
