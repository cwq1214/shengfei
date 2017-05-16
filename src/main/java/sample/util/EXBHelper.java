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
import org.dom4j.io.XMLWriter;
import sample.controller.YBCC.YBCCBean;
import sample.entity.Record;
import sample.entity.Speaker;
import sample.entity.Table;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

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
            field_key = new String[]{"baseCode","content","IPA","MWFY","english","spell","note"};
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
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
