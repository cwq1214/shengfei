package sample.util;

import javafx.scene.control.TableView;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import sample.controller.YBCC.YBCCBean;
import sample.entity.Record;
import sample.entity.Table;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

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
        for (Object bean:
                tableView.getItems()) {
            records.add(((YBCCBean) bean).getRecord());
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
            field_name = new String[]{"baseCode","content","IPA","yun","english","spell","note"};

        }else if (tableType==2){
            tb_type.addText("JB");
            category = new String[]{"SN","CH","IPA","MWFY","EN","NOTE","PTHWORD"};
            field_name = new String[]{"baseCode","content","IPA","MWFY","english","note","free_trans"};
        }


        for (int i=0,max = category.length; i<max ;i++){
            Element tier = basic_body.addElement("tier");

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
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
