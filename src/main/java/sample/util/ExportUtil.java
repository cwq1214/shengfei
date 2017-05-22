package sample.util;

import com.sun.javafx.binding.StringFormatter;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import jdk.nashorn.internal.ir.IfNode;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bytedeco.javacpp.annotation.Const;
import org.dom4j.DocumentException;
import sample.controller.NewTableView.TempTableView;
import sample.controller.YBCC.YBCCBean;
import sample.controller.ZlyydView.CollectBean;
import sample.controller.ZlyydView.SameIPABean;
import sample.entity.Record;
import sample.entity.Speaker;
import sample.entity.Table;
import sample.entity.Topic;

import java.awt.*;
import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Created by Bee on 2017/5/5.
 */
public class ExportUtil {
    private static String yydHtml = "<html>\n" +
            " <head> \n" +
            "  <title>%s</title> \n" +
            "  <meta http-equiv=\"content-type\" content=\"text/html;charset=utf-8\" /> \n" +
            "  <script src=\"voice/jquery.min.js\"></script>\n" +
            "  <script src=\"voice/playwav.js\"></script>\n" +
            "  <style type=\"text/css\">\n" +
            "table{background-color:#FFFFFF;margin-top:5px; }\n" +
            "table.sm{font-size:12px; }\n" +
            "table  tr{background-color: #FFFFFF;}\n" +
            "table  th{background-color: #5382BB;}\n" +
            "table  tr.title{background-color: #5382BB;color:#FFFFFF;}\n" +
            "table  td.tdtitle{font-weight:700; background-color: #AEEEEE;color:#000;}\n" +
            "a {\ttext-decoration: none;}\n" +
            ".autop { text-indent:2em }\n" +
            "audio {\tdisplay: none;}\n" +
            "div[f]{float:left1;display:inline;overflow:hidden}\n" +
            "#maskdiv{z-index: 1;width:320;height:250px;background: #cccccc;  position:fixed;right: 0px; top: 100px; border-radius:10px; }\n" +
            "#maskdiv #video{ z-index: 2;margin-top:-10px; }\n" +
            "#maskdiv a{text-decoration:none;float:right;margin:5px;}\n" +
            "</style>\n" +
            " </head>\n" +
            " <body>\n" +
            "  <div class=\"wrapper\">\n" +
            "   <h1 align=\"center\">%s</h1> \n" +
            "%s"+
            "       <tr>%s</tr>"+

            "%s"+
            "   </table>\n" +
            "   <p align=\"right\">2017-04-06</p> \n" +
            "  </div>\n" +
            " </body>\n" +
            "</html>";

    private static String tableWordTh = "      <th>编码</th>\n" +
            "      <th>单字</th>\n" +
            "      <th>音标注音</th>\n" +
            "      <th>音韵</th>\n" +
            "      <th>注释</th>\n";

    private static String tableCiTh = "      <th>编码</th>\n" +
            "      <th>词条</th>\n" +
            "      <th>音标注音</th>\n" +
            "      <th>方言字或民族文字</th>\n" +
            "      <th>拼音</th>\n" +
            "      <th>英语</th>\n" +
            "      <th>注释</th>\n" ;

    private static String tableSentenceTh = "      <th>编码</th>\n" +
            "      <th>句子</th>\n" +
            "      <th>音标注音</th>\n" +
            "      <th>方言字或民族文字</th>\n" +
            "      <th>普通话对译</th>\n" +
            "      <th>英语</th>\n" +
            "      <th>注释</th>\n" ;

    private static String tableWordTrTd = "<td>%s</td>" +
            "<td>%s</td>" +
            "<td>%s</td>" +
            "<td>%s</td>" +
            "<td>%s</td>" ;

    private static String tableCiTrTd = "<td>%s</td>" +
            "<td>%s</td>" +
            "<td>%s</td>" +
            "<td>%s</td>" +
            "<td>%s</td>" +
            "<td>%s</td>" +
            "<td>%s</td>" ;

    private static String tableSentenceTrTd = "<td>%s</td>" +
            "<td>%s</td>" +
            "<td>%s</td>" +
            "<td>%s</td>" +
            "<td>%s</td>" +
            "<td>%s</td>" +
            "<td>%s</td>" ;

    private static String horTh = "      <th>声韵调</th>\n" +
            "      <th>出现次数</th>\n" +
            "      <th>例字</th>\n" ;

    private static String vTh = "     <tr class=\"title\">\n" +
            "      <th>声韵调</th>\n" +
            "      <th>条目</th>" +
            "     </tr>\n";

    private static String tbHeader = "   <table align=\"center\" width=\"98%\" border=\"0\" cellspacing=\"1\" cellpadding=\"5\">\n";

    private static String yydtr = "       <tr class=\"title\">\n" +
            "%s"+
            "       </tr>\n";


    private static String yydTrTd = "<td>%s</td>" +
                                    "<td>%s</td>" +
                                    "<td>%s</td>" ;


    private static String yyDemoWordWithVoiceTd = "%s;";
    private static String yydDemoWordTd = "<div f=\"%s\"></div>%s;";


    public static void exportTable (Table t){
        TempTableView temp = new TempTableView(Integer.parseInt(t.getDatatype()),t.getId());
        exportTable(null,temp.getTableView(),t);
    }

    public static void exportTopic(List<Topic> topics){
        File saveFile = DialogUtil.exportFileDialog(new FileChooser.ExtensionFilter[]{new FileChooser.ExtensionFilter("2003Excel文件", "*.xls")
                ,new FileChooser.ExtensionFilter("2007Excel文件", "*.xlsx")
                ,new FileChooser.ExtensionFilter("网页", "*.html")
                ,new FileChooser.ExtensionFilter("手机版网页", "*.htm")
                ,new FileChooser.ExtensionFilter("exmaralda", "*.exb")
                ,new FileChooser.ExtensionFilter("声飞xml", "*.xml")
        });

        if (saveFile==null){
            return;
        }
        if (saveFile.getPath().endsWith(".xlsx")
                ||saveFile.getPath().endsWith(".xls")){
            Workbook workbook ;
            if (saveFile.getPath().endsWith(".xlsx")){
                workbook = new XSSFWorkbook();
            }else {
                workbook = new HSSFWorkbook();
            }
            Sheet sheet = workbook.createSheet();
            String[] title = {"音标注音","民文或方言转写","拼音","普通话词对译","普通话意译","注释","英语"};
            String[] fieldName = {"ipa","mwfy","spell","word_trans","free_trans","note","english"};
            for (int i=0,max = topics.size();i<max;i++){
                if (i == 0){
                    Row row = sheet.createRow(i);
                    for (int j=0;j<title.length;j++){
                        Cell cell = row.createCell(j);
                        cell.setCellValue(title[j]);
                    }
                }


                Row row = sheet.createRow(i+1);


                for (int j=0,max2=fieldName.length;j<max2;j++){
                    Cell cell = row.createCell(j);
                    try {
                        Field field = Topic.class.getDeclaredField(fieldName[j]);
                        Object o = field.get(topics.get(i));
                        String value = o==null?"":o.toString();
                        cell.setCellValue(value);
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }

                }

            }
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(saveFile);
                workbook.write(fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else if (saveFile.getPath().endsWith(".exb")){
            try {
                EXBHelper.getInstance().writeToEXB(saveFile,topics);
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }else if (saveFile.getPath().endsWith(".xml")){

        }else if (saveFile.getPath().endsWith("html")
                ||saveFile.getPath().endsWith("htm")){

        }



        return;

    }

    public static void exportTable(Stage stage, TableView tableView,Table t){


        File saveFile = DialogUtil.exportFileDialog();
        String saveType = saveFile.getName().substring(saveFile.getName().lastIndexOf(".") + 1);

        if (saveType.equals("xls")){
            exportTableExcel(saveFile,true,tableView);
        }else if (saveType.equals("xlsx")){
            exportTableExcel(saveFile,false,tableView);
        }else if (saveType.equals("html")){
            exportTableHtml(saveFile,false,tableView,t);
        }else if (saveType.equals("htmla")){
            exportTableHtml(saveFile,true,tableView,t);
        }else if (saveType.equals("htm")){
            exportTableHtml(saveFile,false,tableView,t);
        }else if (saveType.equals("htma")){
            exportTableHtml(saveFile,true,tableView,t);
        }else if (saveType.equals("exb")){
            exportTableEXB(saveFile.getAbsolutePath(),tableView,t);
        }else if (saveType.equals("eaf")){
            exportTableEAF(saveFile.getAbsolutePath(),tableView,t);
        }else if (saveType.equals("xml")){
            exportTableXML(saveFile.getAbsolutePath(),tableView,t);
        }else if (saveType.equals("txt")){

        }

    }

    private static void exportTableHtml(File saveFile,boolean hasAudio,TableView tableView,Table t){
        StringBuilder allSb = new StringBuilder();
        StringBuilder allTrDatas = new StringBuilder();
        StringBuilder thDatas = new StringBuilder();
        int tableType = Integer.parseInt(t.getDatatype());

        FileUtil.copy(Constant.ROOT_FILE_DIR + "/HtmlData/voice", saveFile.getParentFile().getAbsolutePath() + "/voice");

        for (int i = 0; i < tableView.getItems().size() + 1; i++) {
            if (i == 0){
                if (tableType == 0){
                    thDatas.append(String.format(yydtr,tableWordTh));
                }else if (tableType == 1){
                    thDatas.append(String.format(yydtr,tableCiTh));
                }else if (tableType == 2){
                    thDatas.append(String.format(yydtr,tableSentenceTh));
                }
            }else {
                YBCCBean bean = ((YBCCBean) tableView.getItems().get(i - 1));
                Record r = bean.getRecord();
                if (tableType == 0){
                    allTrDatas.append("<tr>"+String.format(tableWordTrTd,Integer.toString(i),hasAudio?String.format(yydDemoWordTd,r.getUuid(),r.getContent()):r.getContent(),r.getIPA(),r.getYun(),r.getNote())+"</tr>");
                }else if (tableType == 1){
                    allTrDatas.append("<tr>"+String.format(tableCiTrTd,Integer.toString(i),hasAudio?String.format(yydDemoWordTd,r.getUuid(),r.getContent()):r.getContent(),r.getIPA(),r.getMWFY(),r.getSpell(),r.getEnglish(),r.getNote())+"</tr>");
                }else if (tableType == 2){
                    allTrDatas.append("<tr>"+String.format(tableSentenceTrTd,Integer.toString(i),hasAudio?String.format(yydDemoWordTd,r.getUuid(),r.getContent()):r.getContent(),r.getIPA(),r.getMWFY(),r.getFree_trans(),r.getEnglish(),r.getNote())+"</tr>");
                }

                File vFile = new File(Constant.ROOT_FILE_DIR + "/audio/" + t.getId() + "/" + r.getUuid()+".wav");
                if (vFile.exists()){
                    FileUtil.fileCopy(vFile.getAbsolutePath(),saveFile.getParentFile().getAbsolutePath() + "/voice/" + r.getUuid() + ".wav");
                }
            }
        }

        allSb.append(String.format(yydHtml, t.getTitle(), t.getTitle(), tbHeader, String.format(yydtr,thDatas), allTrDatas));

        saveFile = new File(saveFile.getAbsolutePath().substring(0,saveFile.getAbsolutePath().lastIndexOf("."))+".html");

        saveContentToFile(saveFile.getAbsolutePath(), allSb.toString());

    }

    private static void exportTableExcel(File saveFile,boolean isXLS,TableView tableView){
        Workbook workbook;
        if (isXLS){
            workbook = new HSSFWorkbook();
        }else {
            workbook = new XSSFWorkbook();
        }

        Sheet sheet = workbook.createSheet();

        for (int i = 0; i < tableView.getItems().size() + 1; i++) {
            Row row = sheet.createRow(i);
            for (int j = 0; j < tableView.getColumns().size(); j++) {
                TableColumn col = (TableColumn) tableView.getColumns().get(j);
                if (i == 0){
                    row.createCell(j).setCellValue(col.getText());
                }else {
                    YBCCBean bean = ((YBCCBean) tableView.getItems().get(i - 1));
                    row.createCell(j).setCellValue(((StringProperty) col.getCellValueFactory().call(new TableColumn.CellDataFeatures<>(tableView, col, bean))).get());
                }
            }
        }

        try {
            FileOutputStream outputStream = new FileOutputStream(saveFile);
            workbook.write(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void exportTYZHCHHtml(Stage stage,List<SameIPABean> beans,int fileFormat,Table t){
        File choiceDir = DialogUtil.dirChooses(stage);

        StringBuilder allSb = new StringBuilder();

        if (choiceDir != null) {
            File outDir = new File(choiceDir.getAbsolutePath() + "/" + t.getTitle());
            if (!outDir.exists()) {
                outDir.mkdir();
            }

            FileUtil.copy(Constant.ROOT_FILE_DIR + "/HtmlData/voice", outDir.getAbsolutePath() + "/voice");

            StringBuilder allTrDatas = new StringBuilder();
            for (SameIPABean bean: beans){
                allTrDatas.append("<tr>"+String.format(yydTrTd,bean.getIpa(),bean.getCount(),demoWordTYZHCH(bean,fileFormat,outDir.getAbsolutePath() + "/voice"))+"</tr>");
            }
            allSb.append(String.format(yydHtml, "同音字汇词汇", "同音字汇词汇", tbHeader, String.format(yydtr,horTh), allTrDatas));

            saveContentToFile(outDir.getAbsolutePath() + "/同音字汇词汇" + ".html", allSb.toString());
        }
    }

    private static String demoWordTYZHCH(SameIPABean bean,int fileFormat,String voiceUrl){
        StringBuilder sb = new StringBuilder();
        for (Record r:bean.getSameRecords()){
            if (fileFormat == 1){
                sb.append(r.getContent()+";");
            }else{
                sb.append(String.format(yydDemoWordTd,r.getUuid(),r.getContent())+ ";");
            }

            File vFile = new File(Constant.ROOT_FILE_DIR + "/audio/" + r.getBaseId() + "/" + r.getUuid()+".wav");
            if (vFile.exists()){
                FileUtil.fileCopy(vFile.getAbsolutePath(),voiceUrl + "/" + r.getUuid() + ".wav");
            }
        }
        if (sb.length()!=0){
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public static void exportTYZHCHExcel(Stage stage, List<SameIPABean> beans,int fileFormat){
        File saveFile = DialogUtil.saveFileDialog("选择保存路径");

        Workbook workbook = null;
        if (saveFile.getName().contains(".xlsx")) {
            workbook = new XSSFWorkbook();
        } else if (saveFile.getName().contains(".xls")) {
            workbook = new HSSFWorkbook();
        }

        Sheet sheet = workbook.createSheet();
        for (int i = 0; i < beans.size() + 1; i++) {
            Row row = sheet.createRow(i);
            if (i == 0 ){

            }else{
                SameIPABean bean = beans.get(i - 1);
                row.createCell(0).setCellValue(bean.getIpa());
                row.createCell(1).setCellValue(bean.getCount());
                row.createCell(2).setCellValue(demoWordWithSameIpa(bean));
            }
        }

        try {
            FileOutputStream outputStream = new FileOutputStream(saveFile);
            workbook.write(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String demoWordWithSameIpa(SameIPABean bean){
        StringBuilder sb = new StringBuilder();
        for (Record r : bean.getSameRecords()){
            sb.append(r.getContent()+";");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public static void exportYYDExcel(Stage stage,boolean isHor, int demoWordCount, int lineSmCount, int lineYmCount, int lineSdCount, int fileFormat, boolean ipaFirst, List<CollectBean> smDatas,List<CollectBean> ymDatas,List<CollectBean> sdDatas){
        File saveFile = DialogUtil.saveFileDialog("选择保存路径");
        if (saveFile == null) {
            throw new RuntimeException("save File can not be null");
        }
        if (!saveFile.exists()) {
            saveFile.getParentFile().mkdirs();
        }

        Workbook workbook = null;
        String[] titleName = new String[]{"条目","出现次数","例字"};
        if (saveFile.getName().contains(".xlsx")) {
            workbook = new XSSFWorkbook();
        } else if (saveFile.getName().contains(".xls")) {
            workbook = new HSSFWorkbook();
        }


        Sheet sheet = workbook.createSheet();

        if (!isHor) {
            List<CollectBean> data = new ArrayList<>();
            data.addAll(smDatas);
            data.addAll(ymDatas);
            data.addAll(sdDatas);

            for (int i = 0; i < data.size() + 1; i++) {
                Row r = sheet.createRow(i);
                if (i == 0){
                    r.createCell(0).setCellValue(titleName[0]);
                    r.createCell(1).setCellValue(titleName[1]);
                    r.createCell(2).setCellValue(titleName[2]);
                }else {
                    CollectBean bean = data.get(i - 1);
                    r.createCell(0).setCellValue(bean.getKey());
                    r.createCell(1).setCellValue(bean.getCount());
                    r.createCell(2).setCellValue(demoWordStr(bean,demoWordCount,ipaFirst,fileFormat,""));
                }
            }
        }else {
            int maxCount = lineSmCount;
            if (maxCount < lineYmCount) maxCount = lineYmCount;
            if (maxCount < lineSdCount) maxCount = lineSdCount;

            Row titleRow = sheet.createRow(0);
            for (int i = 0; i < maxCount; i++) {
                titleRow.createCell(i * 3 ).setCellValue(titleName[0]);
                titleRow.createCell(i * 3 + 1).setCellValue(titleName[1]);
                titleRow.createCell(i * 3 + 2).setCellValue(titleName[2]);
            }

            int nowRow = putToSheet(lineSmCount,sheet,smDatas,demoWordCount,ipaFirst,fileFormat,1);
            nowRow = putToSheet(lineYmCount,sheet,ymDatas,demoWordCount,ipaFirst,fileFormat,nowRow + 1);
            nowRow = putToSheet(lineSdCount,sheet,sdDatas,demoWordCount,ipaFirst,fileFormat,nowRow + 1);
        }


        try {
            FileOutputStream outputStream = new FileOutputStream(saveFile);
            workbook.write(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int putToSheet(int lineMaxCount,Sheet sheet,List<CollectBean> datas,int demoWordCount,boolean ipaFirst,int fileFormat,int rowIndex){
        int lineNowItemCount = 0;
        int nowRowIndex = rowIndex;
        Row tempRow = sheet.createRow(nowRowIndex);
        for (int i = 0; i < datas.size();i++) {
            if (lineNowItemCount == lineMaxCount){
                tempRow = sheet.createRow(++nowRowIndex);
                lineNowItemCount = 0;
            }
            CollectBean bean = datas.get(i);
            tempRow.createCell(lineNowItemCount * 3).setCellValue(bean.getKey());
            tempRow.createCell(lineNowItemCount * 3 + 1).setCellValue(bean.getCount());
            tempRow.createCell(lineNowItemCount * 3 + 2).setCellValue(demoWordStr(bean,demoWordCount,ipaFirst,fileFormat,""));
            lineNowItemCount++;
        }
        return nowRowIndex;
    }

    public static void exportYYD(Stage stage, boolean isHor, int demoWordCount, int lineSmCount, int lineYmCount, int lineSdCount, int fileFormat, boolean ipaFirst, List<CollectBean> smDatas,List<CollectBean> ymDatas,List<CollectBean> sdDatas, Table t) {
        DirectoryChooser dc = new DirectoryChooser();
        File choiceDir = dc.showDialog(stage);

        StringBuilder allSb = new StringBuilder();

        if (choiceDir != null) {
            File outDir = new File(choiceDir.getAbsolutePath() + "/" + t.getTitle());
            if (!outDir.exists()) {
                outDir.mkdir();
            }
            FileUtil.copy(Constant.ROOT_FILE_DIR + "/HtmlData/voice", outDir.getAbsolutePath() + "/voice");


            if (!isHor) {
                StringBuilder yydAllTr = new StringBuilder();

                List<CollectBean> data = new ArrayList<>();
                data.addAll(smDatas);
                data.addAll(ymDatas);
                data.addAll(sdDatas);

                for (int i = 0; i < data.size(); i++) {
                    CollectBean tempBean = data.get(i);
                    yydAllTr.append(String.format(new String( "<tr>"+yydTrTd+"</tr>"), tempBean.getKey(), tempBean.getCount(), demoWordStr(tempBean, demoWordCount, ipaFirst, fileFormat,outDir.getAbsolutePath() + "/voice")));
                }
                allSb.append(String.format(yydHtml, "声韵调", "声韵调", tbHeader, String.format(yydtr,horTh), yydAllTr));
            } else {
                int maxCount = lineSmCount;
                if (maxCount < lineYmCount) maxCount = lineYmCount;
                if (maxCount < lineSdCount) maxCount = lineSdCount;

                StringBuilder th = new StringBuilder();
                for (int i = 0; i < maxCount; i++) {
                    th.append(horTh);
                }

                StringBuilder allTrData = new StringBuilder();
                putDataToTd(outDir.getAbsolutePath() + "/voice",lineSmCount,maxCount,smDatas,allTrData,demoWordCount,ipaFirst,fileFormat);
                allTrData.append(insertNullTd(new StringBuilder(),maxCount));
                putDataToTd(outDir.getAbsolutePath() + "/voice",lineYmCount,maxCount,ymDatas,allTrData,demoWordCount,ipaFirst,fileFormat);
                allTrData.append(insertNullTd(new StringBuilder(),maxCount));
                putDataToTd(outDir.getAbsolutePath() + "/voice",lineSdCount,maxCount,sdDatas,allTrData,demoWordCount,ipaFirst,fileFormat);

                allSb.append(String.format(yydHtml, "声韵调", "声韵调", tbHeader, String.format(yydtr,th), allTrData));
            }
            saveContentToFile(outDir.getAbsolutePath() + "/声韵调" + ".html", allSb.toString());
        }
    }

    private static void putDataToTd(String vPath,int lineMaxItemCount,int maxCount,List<CollectBean> datas,StringBuilder allTrData,int demoWordCount,boolean ipaFirst,int fileFormat){
        int lineNowItemCount = 0;
        StringBuilder tempTrData = new StringBuilder();
        for (int i = 0;i<datas.size();i++){
            CollectBean bean = datas.get(i);
            tempTrData.append(String.format(yydTrTd,bean.getKey(),bean.getCount(),demoWordStr(bean,demoWordCount,ipaFirst,fileFormat,vPath)));
            lineNowItemCount++;

            if (lineNowItemCount==lineMaxItemCount){
                allTrData.append("<tr>" + insertNullTd(tempTrData,maxCount - lineNowItemCount) + "</tr>");
                tempTrData = new StringBuilder();
                lineNowItemCount = 0;
            }
        }
        if (tempTrData.length() != 0){
            allTrData.append("<tr>" + insertNullTd(tempTrData,maxCount - lineNowItemCount) + "</tr>");
        }
    }

    public static String demoWordStr (CollectBean tempBean,int demoWordCount,boolean ipaFirst,int fileFormat,String vPath){
        StringBuilder yyDemoWordAllTd = new StringBuilder();

        int realDemoSize = tempBean.getDemoRecordList().size();
        for (int j = 0; j < (demoWordCount > realDemoSize ? realDemoSize : demoWordCount); j++) {
            Record r = tempBean.getDemoRecordList().get(j);
            String tempDw = ipaFirst ? r.getIPA() + r.getContent() : r.getContent() + r.getIPA();
            if (fileFormat == 0){
                yyDemoWordAllTd.append(tempDw+";");
            } else if (fileFormat == 1) {
                yyDemoWordAllTd.append(String.format(yyDemoWordWithVoiceTd, tempDw));
            } else if (fileFormat == 2) {
                yyDemoWordAllTd.append(String.format(yydDemoWordTd, r.getUuid(), tempDw));
                File vFile = new File(Constant.ROOT_FILE_DIR + "/audio/" + r.getBaseId() + "/" + r.getUuid()+".wav");
                if (vFile.exists()){
                    FileUtil.fileCopy(vFile.getAbsolutePath(),vPath + "/" + r.getUuid() + ".wav");
                }
            }
        }
        yyDemoWordAllTd.deleteCharAt(yyDemoWordAllTd.length() - 1);

        return yyDemoWordAllTd.toString();
    }

    private static String insertNullTd(StringBuilder tempTrData,int count){
        System.out.println("insert null:"+tempTrData.toString()+"*********"+count);
        for (int i = 0; i < count; i++) {
            tempTrData.append(String.format(yydTrTd,"","",""));
        }
        return tempTrData.toString();
    }

    public static void saveContentToFile(String path,String content){
        File target = new File(path);
        try {
            FileWriter fw = new FileWriter(target);
            fw.write(content);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //语档导出文件
    public static String htmlWithYDBase = "<html>\n" +
            "  \n" +
            "  <head>\n" +
            "    <title>%s</title>\n" +
            "    <meta http-equiv='content-type' content='text/html;charset=utf-8'>\n" +
            "    <script src=\"voice/jquery.min.js\"></script>\n" +
            "    <script src=\"voice/playwav.js\"></script>\n" +
            "    <style type='text/css'>table{background-color:#5382BB;margin-top:5px; } table.sm{font-size:12px; } table tr{background-color: #FFFFFF;} table tr.title{background-color: #5382BB;color:#FFFFFF;} table td.tdtitle{font-weight:700; background-color: #AEEEEE;color:#000;} a { text-decoration: none;} .autop { text-indent:2em } audio { display: none;} div[f]{float:left1;display:inline;overflow:hidden} #maskdiv{z-index: 1;width:320;height:250px;background: #cccccc; position:fixed;right: 0px; top: 100px; border-radius:10px; } #maskdiv #video{ z-index: 2;margin-top:-10px; } #maskdiv a{text-decoration:none;float:right;margin:5px;}</style></head>\n" +
            "  \n" +
            "  <body>\n" +
            "    <div class='wrapper'>\n" +
            "      <h1 align='center'>%s</h1>\n" +
            "      <style type='text/css'>td,th{text-align:left;}</style>\n" +
            "      %s" +
            "      <div id='pagediv'>%s</div>\n" +
            "      <p align='right'>2017-05-19</p></div>\n" +
            "  </body>\n" +
            "\n" +
            "</html>";

    //说话人信息Table
    public static String speakInfoHtml = "<table class='sm' width='100%%' border='0' cellspacing='1' cellpadding='5'>\n" +
            "        <tr class='title showtitle'>\n" +
            "          <th colspan='4'>说话人信息</th></tr>\n" +
            "        <tr>\n" +
            "          <td>\n" +
            "            <table class='sm' width='100%%' border='0' cellspacing='1' cellpadding='5'>\n" +
            "              <tr>\n" +
            "                <td width='25%%' class='tdtitle'>说话人代号</td>\n" +
            "                <td width='25%%'>%s</td>\n" +
            "                <td width='25%%' class='tdtitle'>实名</td>\n" +
            "                <td width='25%%'>%s</td></tr>\n" +
            "              <tr>\n" +
            "                <td width='25%%' class='tdtitle'>出生年月</td>\n" +
            "                <td width='25%%'>%s</td>\n" +
            "                <td width='25%%' class='tdtitle'>性别</td>\n" +
            "                <td width='25%%'>%s</td></tr>\n" +
            "              <tr>\n" +
            "                <td class='tdtitle'>常用语言</td>\n" +
            "                <td colspan='3'>%s</td></tr>\n" +
            "              <tr>\n" +
            "                <td class='tdtitle'>母语</td>\n" +
            "                <td colspan='3'>%s</td></tr>\n" +
            "              <tr>\n" +
            "                <td class='tdtitle'>第二语言</td>\n" +
            "                <td colspan='3'>%s</td></tr>\n" +
            "              <tr>\n" +
            "                <td width='25%%' class='tdtitle'>文化程度</td>\n" +
            "                <td width='25%%'>%s</td>\n" +
            "                <td width='25%%' class='tdtitle'>职业</td>\n" +
            "                <td width='25%%'>%s</td></tr>\n" +
            "              <tr>\n" +
            "                <td class='tdtitle'>工作角色</td>\n" +
            "                <td colspan='3'>%s</td>\n" +
            "              </tr>\n" +
            "              <tr>\n" +
            "                <td class='tdtitle'>籍贯和居住地</td>\n" +
            "                <td colspan='3'>%s</td></tr>\n" +
            "            </table>\n" +
            "          </td>\n" +
            "        </tr>\n" +
            "      </table>";

    public static String tblInfoTable = "<table class='sm' width='100%%' border='0' cellspacing='1' cellpadding='5'>\n" +
            "        <tr class='title showtitle'>\n" +
            "          <th colspan='4'>调查表信息</th></tr>\n" +
            "        <tr>\n" +
            "          <td>\n" +
            "            <table class='sm' width='100%%' border='0' cellspacing='1' cellpadding='5'>\n" +
            "              <tr>\n" +
            "                <td class='tdtitle'>语料类型</td>\n" +
            "                <td colspan='3'>%s</td></tr>\n" +
            "              <tr>\n" +
            "                <td class='tdtitle'>语料描述</td>\n" +
            "                <td colspan='3'>%s</td></tr>\n" +
            "              <tr>\n" +
            "                <td class='tdtitle'>项目名称和编号</td>\n" +
            "                <td colspan='3'>%s</td>\n" +
            "              </tr>\n" +
            "              <tr>\n" +
            "                <td width='25%%' class='tdtitle'>发音人/说话人</td>\n" +
            "                <td width='25%%'>%s</td>\n" +
            "                <td width='25%%' class='tdtitle'>创建人</td>\n" +
            "                <td width='25%%'>%s</td></tr>\n" +
            "              <tr>\n" +
            "                <td class='tdtitle'>贡献人</td>\n" +
            "                <td colspan='3'>%s</td>\n" +
            "              </tr>\n" +
            "              <tr>\n" +
            "                <td class='tdtitle'>采录日期</td>\n" +
            "                <td colspan='3'>%s</td></tr>\n" +
            "              <tr>\n" +
            "                <td class='tdtitle'>采录地点</td>\n" +
            "                <td colspan='3'>%s</td></tr>\n" +
            "              <tr>\n" +
            "                <td width='25%%' class='tdtitle'>语言名称</td>\n" +
            "                <td width='25%%'>%s</td>\n" +
            "                <td width='25%%' class='tdtitle'>语言代码</td>\n" +
            "                <td width='25%%'>%s</td>\n" +
            "              </tr>\n" +
            "              <tr>\n" +
            "                <td class='tdtitle'>语言地点</td>\n" +
            "                <td colspan='3'>%s</td></tr>\n" +
            "              <tr>\n" +
            "                <td class='tdtitle'>录音设备</td>\n" +
            "                <td colspan='3'>%s</td>\n" +
            "              </tr>\n" +
            "              <tr>\n" +
            "                <td class='tdtitle'>软件工具</td>\n" +
            "                <td colspan='3'>%s</td>\n" +
            "              </tr>\n" +
            "              <tr>\n" +
            "                <td class='tdtitle'>使用权限</td>\n" +
            "                <td colspan='3'>%s</td>\n" +
            "              </tr>\n" +
            "              <tr>\n" +
            "                <td class='tdtitle'>备注</td>\n" +
            "                <td colspan='3'>%s</td>\n" +
            "              </tr>\n" +
            "            </table>\n" +
            "          </td>\n" +
            "        </tr>\n" +
            "      </table>";

    public static String contentHtml = "<table width='100%%' border='0' cellspacing='1' cellpadding='5'>\n" +
            "        <tr class='title'>\n" +
            "        %s" +
            "        </tr>\n" +
            "%s"+
            "      </table>";

    public static String tdHtml = "<td>%s</td><td><div f=%s></div>%s</td>";

    public static void exportTableHtml(List<Table> tbls,boolean isShowSpeaker,boolean isShowMeta,boolean isSplit,int line,int lineItemCount,int align){
        File choiceDir = DialogUtil.dirChooses(new Stage());
        FileUtil.copy(Constant.ROOT_FILE_DIR + "/HtmlData/voice", choiceDir.getAbsolutePath() + "/voice");

        for (Table t : tbls) {
            StringBuilder contentSB = new StringBuilder();
            StringBuilder headerSB = new StringBuilder();
            if (isShowSpeaker){
                String speakerID = t.getSpeaker();
                Speaker tempSpeak = null;
                if (speakerID != null && speakerID.length() != 0){
                    tempSpeak = DbHelper.getInstance().getSpeakerById(Integer.parseInt(speakerID));
                }else {
                    tempSpeak = new Speaker();
                }
                headerSB.append(String.format(speakInfoHtml,
                        tempSpeak.speakcode,
                        tempSpeak.realname,
                        tempSpeak.birth,
                        tempSpeak.sex,
                        tempSpeak.usualLang,
                        tempSpeak.motherLang,
                        tempSpeak.secondLang,
                        tempSpeak.education,
                        tempSpeak.job,
                        tempSpeak.workRole,
                        tempSpeak.addr));
            }

            if (isShowMeta){
                headerSB.append(String.format(tblInfoTable,
                        t.datatype,
                        t.datades,
                        t.title,
                        t.speaker,
                        t.creator,
                        t.contributor,
                        t.recordingdate,
                        t.recordingplace,
                        t.language,
                        t.languagecode,
                        t.languageplace,
                        t.equipment,
                        t.software,
                        t.rightl,
                        t.snote));
            }

            ObservableList<Record> tReocrds = DbHelper.getInstance().searchTempRecordKeepAndDone(t.getId());
            //句表
            if (t.datatype.equalsIgnoreCase("2")){

            }else{
            //字表，词表
                StringBuilder thSb = new StringBuilder();
                for (int i = 0; i < lineItemCount; i++) {
                    thSb.append("<th>编码</th><th>条目</th>");
                }

                StringBuilder trSb = new StringBuilder();
                int lineCount = tReocrds.size()%lineItemCount == 0 ? tReocrds.size()/lineItemCount : tReocrds.size()/lineItemCount + 1;

                //每个文件内现在行数
                int nowLine = 0;
                int nowFileSplit = 0;
                int splitCount = lineCount%line == 0 ? lineCount/line : lineCount/line + 1;

                for (int i = 0; i < lineCount; i++) {
                    nowLine++;
                    StringBuilder tdSb = new StringBuilder();
                    for (int j = 0; j < lineItemCount; j++) {
                        int index = i*lineItemCount+j;
                        if (index < tReocrds.size()){
                            Record r = tReocrds.get(index);
                            tdSb.append(String.format(tdHtml,r.getBaseCode(),r.getUuid(),r.getContent()));
                        }
                    }

                    trSb.append("<tr>"+ tdSb +"</tr>");

                    if (isSplit && nowLine == line){
                        nowLine = 0;
                        contentSB.append(headerSB);
                        contentSB.append(String.format(contentHtml,thSb,trSb));
                        saveContentToFile(choiceDir.getAbsolutePath() + "/" + t.getTitle()+ "-" + nowFileSplit + ".html",
                                String.format(htmlWithYDBase,t.getTitle(),t.getTitle(),contentSB,makeHtmlPageDiv(t.getTitle(),nowFileSplit,splitCount)));
                        trSb = new StringBuilder();
                        contentSB = new StringBuilder();
                        nowFileSplit++;
                    }
                }

                if (isSplit){
                    if (tReocrds.size() == 0){
                        contentSB.append(headerSB);
                        contentSB.append(String.format(contentHtml,thSb,trSb));
                        saveContentToFile(choiceDir.getAbsolutePath() + "/" + t.getTitle() + ".html",
                                String.format(htmlWithYDBase,t.getTitle(),t.getTitle(),contentSB,"1"));
                    }
                    if (nowLine != 0){
                        contentSB.append(headerSB);
                        contentSB.append(String.format(contentHtml,thSb,trSb));
                        saveContentToFile(choiceDir.getAbsolutePath() + "/" + t.getTitle()+ "-" + nowFileSplit + ".html",
                                String.format(htmlWithYDBase,t.getTitle(),t.getTitle(),contentSB,makeHtmlPageDiv(t.getTitle(),nowFileSplit,splitCount)));
                    }
                }else{
                    contentSB.append(headerSB);
                    contentSB.append(String.format(contentHtml,thSb,trSb));
                    saveContentToFile(choiceDir.getAbsolutePath() + "/" + t.getTitle() + ".html",
                            String.format(htmlWithYDBase,t.getTitle(),t.getTitle(),contentSB,"1"));
                }
            }
        }

    }

    private static String makeHtmlPageDiv(String prefix,int nowIndex,int allIndex){
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < allIndex; i++) {
            String nIndex = i+ 1 + "";
            if (i == nowIndex){
                sb.append(nIndex+ " ");
            }else{
                sb.append("<a href='"+prefix+"-"+i+".html'>"+nIndex+"</a> ");
            }
        }
        return sb.toString();
    }

    public static void exportTableEXB(String savePath,TableView tableView,Table t){
        EXBHelper.getInstance().writeToExb(savePath,tableView,t);
    }
    public static void exportTableEAF(String savePath,TableView tableView,Table t){
        EAFHelper.getInstance().writeToEaf(savePath,tableView,t);
    }
    public static void exportTableXML(String savePath,TableView tableView,Table t){
        XMLHelper.getInstance().writeToXml(savePath,tableView,t);
    }

    public static void exportSentenceJZCH(Table t,boolean isExportExcel,int excelFormat){
        ObservableList<YBCCBean> tempBeans = DbHelper.getInstance().searchTempRecord2YBCCBean(t.getDatatype(),t.getId());
        tempBeans = tempBeans.filtered(new Predicate<YBCCBean>() {
            @Override
            public boolean test(YBCCBean bean) {
                if (bean.getRecord().getIPA() != null && bean.getRecord().getIPA().length() != 0){
                    return true;
                }
                return false;
            }
        });

        File saveFile = DialogUtil.exportTqjzchDialog(isExportExcel);
        String saveType = saveFile.getName().substring(saveFile.getName().lastIndexOf(".") + 1);


        if (isExportExcel){
            Workbook workbook;
            if (saveType.equals("xls")){
                workbook = new HSSFWorkbook();
            }else{
                workbook = new XSSFWorkbook();
            }

            Sheet sheet = workbook.createSheet();

            if (excelFormat == 0){
                for (int i = 0; i <= tempBeans.size(); i++) {
                    if (i == 0){
                        Row row = sheet.createRow(i);
                        row.createCell(0).setCellValue("编码");
                        row.createCell(1).setCellValue("音标注音");
                        row.createCell(2).setCellValue("民文方言");
                        row.createCell(3).setCellValue("普通话对译");
                        continue;
                    }

                    YBCCBean bean = tempBeans.get(i - 1);
                    String[] ipas = bean.getRecord().getIPA().split(" ");
                    String[] mwfys = bean.getRecord().getMWFY().split(" ");
                    String[] frees = bean.getRecord().getFree_trans().split(" ");
                    for (int j = 0; j < ipas.length; j++) {
                        Row row = sheet.createRow(sheet.getLastRowNum() + 1);
                        row.createCell(0).setCellValue(bean.getRecord().getBaseCode());
                        row.createCell(1).setCellValue(ipas[j]);

                        if (j < mwfys.length) {
                            row.createCell(2).setCellValue(mwfys[j]);
                        }

                        if (j < frees.length) {
                            row.createCell(3).setCellValue(frees[j]);
                        }
                    }
                }
            }else {
                int maxLen = 0;
                for (YBCCBean bean :tempBeans) {
                    String[] ipas = bean.getRecord().getIPA().split(" ");
                    String[] mwfys = bean.getRecord().getMWFY().split(" ");
                    String[] frees = bean.getRecord().getFree_trans().split(" ");

                    Row r1 = sheet.createRow(sheet.getLastRowNum() == 0?1:sheet.getLastRowNum()+1);
                    Row r2 = sheet.createRow(sheet.getLastRowNum()+1);
                    Row r3 = sheet.createRow(sheet.getLastRowNum()+1);

                    r1.createCell(0).setCellValue(bean.getRecord().getBaseCode());
                    r2.createCell(0).setCellValue(bean.getRecord().getBaseCode());
                    r3.createCell(0).setCellValue(bean.getRecord().getBaseCode());

                    if (ipas.length > maxLen ){
                        maxLen = ipas.length;
                    }

                    for (int i = 0; i < ipas.length; i++) {
                        r1.createCell(i + 1).setCellValue(ipas[i]);

                        if (i<mwfys.length){
                            r2.createCell(i + 1).setCellValue(mwfys[i]);
                        }

                        if (i<frees.length){
                            r3.createCell(i + 1).setCellValue(frees[i]);
                        }
                    }
                }
                Row first = sheet.createRow(0);
                for (int i = 0; i <= maxLen; i++) {
                    if (i == 0){
                        first.createCell(0).setCellValue("编码");
                    }else {
                        first.createCell(i).setCellValue("词"+i);
                    }
                }
            }

            try {
                FileOutputStream outputStream = new FileOutputStream(saveFile);
                workbook.write(outputStream);
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }


    private void exportTopic2XML(){

    }
    private void exportTopic2Excel(){

    }
    private void exportTopic2Html(){

    }
}
