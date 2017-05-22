package sample.util;

import javafx.scene.control.TableView;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import sample.controller.YBCC.YBCCBean;
import sample.entity.Record;
import sample.entity.Table;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by chenweiqi on 2017/5/16.
 */
public class XMLHelper {
    private static XMLHelper xmlHelper;

    private XMLHelper(){

    }

    public static synchronized XMLHelper getInstance(){
        synchronized (XMLHelper.class) {
            if (xmlHelper==null){
                xmlHelper = new XMLHelper();
            }
        }
        return xmlHelper;
    }


    public void writeToXml(String filePath,TableView tableView, Table t){

        int tableType = Integer.parseInt(t.getDatatype());
        List<Record> records = new ArrayList<>();
        for (int i=0;i<tableView.getItems().size();i++) {
            Record r;
            Object object = tableView.getItems().get(i);
            if (object instanceof  Record){
                r = (Record) object;
            }else if (object instanceof YBCCBean){
                r = ((YBCCBean) object).getRecord();
            }else {
                return;
            }
            records.add(r);
        }
        createXml(filePath,records,tableType);
    }

    private void createXml(String savePath,List<Record> records, int tableType){
        Document doc = DocumentHelper.createDocument();

        Element basic_body = doc.addElement("basic-body");

        Element tb_type = basic_body.addElement("tb-type");


        String[] category = null;
        String[] field_name = null;
        if (tableType==0
                ||tableType==1){
            tb_type.addText("ZB");
            category = new String[]{"SN","CH","IPA","YYUN","EN","PYIN","NOTE"};
            field_name = new String[]{"investCode","content","IPA","yun","english","spell","note"};

        }else if (tableType==2){
            tb_type.addText("JB");
            category = new String[]{"SN","CH","IPA","MWFY","EN","NOTE","PTHWORD"};
            field_name = new String[]{"investCode","content","IPA","MWFY","english","note","free_trans"};
        }


        for (int i=0,max = category.length; i<max ;i++){
            Element tier = basic_body.addElement("tier");
            tier.addAttribute("category",category[i]);
            for (int j=0,max2 = records.size(); j < max2;j++){
                Element event =tier.addElement("event");
                try {
                    Field field = Record.class.getField(field_name[i]);
                    Object value = field.get(records.get(j));
                    if (value!=null){
                        event.addText(value.toString());
                    }
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
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


    public List<Record> readXml(String path,int tableType){
        SAXReader saxReader = new SAXReader();

        try {
            Document doc = saxReader.read(new File(path));
            Element root = doc.getRootElement();
            String type = root.element("tb-type").getText();


            String[] category = null;
            String[] field_name = null;
            if (tableType==0
                    ||tableType==1){
                category = new String[]{"SN","CH","IPA","YYUN","EN","PYIN","NOTE"};
                field_name = new String[]{"investCode","content","IPA","yun","english","spell","note"};

            }else if (tableType==2){
                category = new String[]{"SN","CH","IPA","MWFY","EN","NOTE","PTHWORD"};
                field_name = new String[]{"investCode","content","IPA","MWFY","english","note","free_trans"};
            }

            List<Record> records = new ArrayList<>();
            for (int i=0,max = category.length;i<max;i++){
                for (int j=0,max2 = ((Element) root.elements("tier").get(i)).elements().size();j<max2;j++){
                    String value = ((Element) ((Element) root.elements("tier").get(i)).elements().get(j)).getText();
                    if (i==0){
                        records.add(new Record());
                    }
                    Record record = records.get(j);
                    Field field = Record.class.getField(field_name[i]);
                    field.set(record,value);

                }
            }
            Table  table = new Table(tableType+"");
            DbHelper.getInstance().insertNewTable(table);

            for (int i=0,max = records.size(); i < max;i++){
                records.get(i).baseId = table.id;
//                records.get(i).baseCode = "A"+String.format("%0" + 5 + "d", i + 1);
                records.get(i).baseCode =records.get(i).investCode;
                records.get(i).uuid = UUID.randomUUID().toString();
            }
            ToastUtil.show("导入成功");
            return records;

        } catch (Exception e) {
            ToastUtil.show("导入失败");
            e.printStackTrace();
        }
        return null;

    }

}
