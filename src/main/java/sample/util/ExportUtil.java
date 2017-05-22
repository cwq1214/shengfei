package sample.util;

import com.sun.javafx.binding.StringFormatter;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import jdk.nashorn.internal.ir.IfNode;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import sample.controller.YBCC.YBCCBean;
import sample.controller.ZlyydView.CollectBean;
import sample.controller.ZlyydView.SameIPABean;
import sample.entity.Record;
import sample.entity.Table;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
            exportTableAC(saveFile.getAbsolutePath(),tableView,t);
        }

    }

    private static void exportTableHtml(File saveFile,boolean hasAudio,TableView tableView,Table t){
        StringBuilder allSb = new StringBuilder();
        StringBuilder allTrDatas = new StringBuilder();
        StringBuilder thDatas = new StringBuilder();
        int tableType = Integer.parseInt(t.getDatatype());

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
                    allTrDatas.append("<tr>"+String.format(tableWordTrTd,Integer.toString(i),hasAudio?String.format(yydDemoWordTd,r.getBaseCode(),r.getContent()):r.getContent(),r.getIPA(),r.getYun(),r.getNote())+"</tr>");
                }else if (tableType == 1){
                    allTrDatas.append("<tr>"+String.format(tableCiTrTd,Integer.toString(i),hasAudio?String.format(yydDemoWordTd,r.getBaseCode(),r.getContent()):r.getContent(),r.getIPA(),r.getMWFY(),r.getSpell(),r.getEnglish(),r.getNote())+"</tr>");
                }else if (tableType == 2){
                    allTrDatas.append("<tr>"+String.format(tableSentenceTrTd,Integer.toString(i),hasAudio?String.format(yydDemoWordTd,r.getBaseCode(),r.getContent()):r.getContent(),r.getIPA(),r.getMWFY(),r.getFree_trans(),r.getEnglish(),r.getNote())+"</tr>");
                }
            }
        }

        allSb.append(String.format(yydHtml, t.getTitle(), t.getTitle(), tbHeader, String.format(yydtr,thDatas), allTrDatas));

        saveFile = new File(saveFile.getAbsolutePath().substring(0,saveFile.getAbsolutePath().lastIndexOf("."))+".html");

        saveContentToFile(saveFile.getAbsolutePath(), allSb.toString());
        FileUtil.copy(Constant.ROOT_FILE_DIR + "/HtmlData/voice", saveFile.getParentFile().getAbsolutePath() + "/voice");

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
            StringBuilder allTrDatas = new StringBuilder();
            for (SameIPABean bean: beans){
                allTrDatas.append("<tr>"+String.format(yydTrTd,bean.getIpa(),bean.getCount(),demoWordTYZHCH(bean,fileFormat))+"</tr>");
            }
            allSb.append(String.format(yydHtml, "同音字汇词汇", "同音字汇词汇", tbHeader, String.format(yydtr,horTh), allTrDatas));

            File outDir = new File(choiceDir.getAbsolutePath() + "/" + t.getTitle());
            if (!outDir.exists()) {
                outDir.mkdir();
            }
            saveContentToFile(outDir.getAbsolutePath() + "/同音字汇词汇" + ".html", allSb.toString());
            FileUtil.copy(Constant.ROOT_FILE_DIR + "/HtmlData/voice", outDir.getAbsolutePath() + "/voice");
        }
    }

    private static String demoWordTYZHCH(SameIPABean bean,int fileFormat){
        StringBuilder sb = new StringBuilder();
        for (Record r:bean.getSameRecords()){
            if (fileFormat == 1){
                sb.append(r.getContent()+";");
            }else{
                sb.append(String.format(yydDemoWordTd,r.getBaseCode(),r.getContent())+ ";");
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
                    r.createCell(2).setCellValue(demoWordStr(bean,demoWordCount,ipaFirst,fileFormat));
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
            tempRow.createCell(lineNowItemCount * 3 + 2).setCellValue(demoWordStr(bean,demoWordCount,ipaFirst,fileFormat));
            lineNowItemCount++;
        }
        return nowRowIndex;
    }

    public static void exportYYD(Stage stage, boolean isHor, int demoWordCount, int lineSmCount, int lineYmCount, int lineSdCount, int fileFormat, boolean ipaFirst, List<CollectBean> smDatas,List<CollectBean> ymDatas,List<CollectBean> sdDatas, Table t) {
        DirectoryChooser dc = new DirectoryChooser();
        File choiceDir = dc.showDialog(stage);

        StringBuilder allSb = new StringBuilder();

        if (choiceDir != null) {
            if (!isHor) {
                StringBuilder yydAllTr = new StringBuilder();

                List<CollectBean> data = new ArrayList<>();
                data.addAll(smDatas);
                data.addAll(ymDatas);
                data.addAll(sdDatas);

                for (int i = 0; i < data.size(); i++) {
                    CollectBean tempBean = data.get(i);
                    yydAllTr.append(String.format(new String( "<tr>"+yydTrTd+"</tr>"), tempBean.getKey(), tempBean.getCount(), demoWordStr(tempBean, demoWordCount, ipaFirst, fileFormat)));
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
                putDataToTd(lineSmCount,maxCount,smDatas,allTrData,demoWordCount,ipaFirst,fileFormat);
                allTrData.append(insertNullTd(allTrData,maxCount));
                putDataToTd(lineYmCount,maxCount,ymDatas,allTrData,demoWordCount,ipaFirst,fileFormat);
                allTrData.append(insertNullTd(allTrData,maxCount));
                putDataToTd(lineSdCount,maxCount,sdDatas,allTrData,demoWordCount,ipaFirst,fileFormat);
                allTrData.append(insertNullTd(allTrData,maxCount));

                allSb.append(String.format(yydHtml, "声韵调", "声韵调", tbHeader, String.format(yydtr,th), allTrData));
            }

            File outDir = new File(choiceDir.getAbsolutePath() + "/" + t.getTitle());
            if (!outDir.exists()) {
                outDir.mkdir();
            }
            saveContentToFile(outDir.getAbsolutePath() + "/声韵调" + ".html", allSb.toString());
            FileUtil.copy(Constant.ROOT_FILE_DIR + "/HtmlData/voice", outDir.getAbsolutePath() + "/voice");
        }
    }

    private static void putDataToTd(int lineMaxItemCount,int maxCount,List<CollectBean> datas,StringBuilder allTrData,int demoWordCount,boolean ipaFirst,int fileFormat){
        int lineNowItemCount = 0;
        StringBuilder tempTrData = new StringBuilder();
        for (int i = 0;i<datas.size();i++){
            CollectBean bean = datas.get(i);
            tempTrData.append(String.format(yydTrTd,bean.getKey(),bean.getCount(),demoWordStr(bean,demoWordCount,ipaFirst,fileFormat)));
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

    public static String demoWordStr (CollectBean tempBean,int demoWordCount,boolean ipaFirst,int fileFormat){
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
                yyDemoWordAllTd.append(String.format(yydDemoWordTd, r.getBaseCode(), tempDw));
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


    public static void exportTableEXB(String savePath,TableView tableView,Table t){

        EXBHelper.getInstance().writeToExb(savePath,tableView,t);
    }
    public static void exportTableEAF(String savePath,TableView tableView,Table t){

        EAFHelper.getInstance().writeToEaf(savePath,tableView,t);
    }
    public static void exportTableXML(String savePath,TableView tableView,Table t){

        XMLHelper.getInstance().writeToXml(savePath,tableView,t);
    }

    public static void exportTableAC(String savePath,TableView tableView,Table t){
        AudoCityHelper.getInstance().writeToAc(savePath,tableView,t);
    }
}
