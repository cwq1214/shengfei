package sample.util;

import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import sample.controller.ZlyydView.CollectBean;
import sample.entity.Record;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
            "table{background-color:#5382BB;margin-top:5px; }\n" +
            "table.sm{font-size:12px; }\n" +
            "table  tr{background-color: #FFFFFF;}\n" +
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
            "    <tbody>\n" +
            "     <tr class=\"title\">\n" +
            "      <th>声韵调</th>\n" +
            "      <th>出现次数</th>\n" +
            "      <th>例字</th>\n" +
            "     </tr>\n" +
            "%s"+
            "    </tbody>\n" +
            "   </table>\n" +
            "   <p align=\"right\">2017-04-06</p> \n" +
            "  </div>\n" +
            " </body>\n" +
            "</html>";

    private static String tbHeader = "   <table align=\"center\" width=\"80%\" border=\"3\" cellspacing=\"1\" cellpadding=\"5\">\n";

    private static String yydTr = "<tr>" +
            "<td>%s</td>" +
            "<td>%s</td>" +
            "<td>%s</td>" +
            "</tr>";

    private static String yydDemoWordTd = "<div f=\"%s\"></div>%s;";

    public static void exportYYD(Stage stage, boolean isHor, int demoWordCount, int lineSmCount, int lineYmCount, int lineSdCount,boolean exportExcel,boolean ipaFirst, List<CollectBean> data){
        DirectoryChooser dc = new DirectoryChooser();
        File choiceDir = dc.showDialog(stage);

        StringBuilder allSb = new StringBuilder();

        if (choiceDir != null) {
            System.out.println(data.size());
            if (!isHor){
                StringBuilder yydAllTr = new StringBuilder();

                for (int i = 0; i < data.size(); i++) {
                    StringBuilder yyDemoWordAllTd = new StringBuilder();

                    CollectBean tempBean = data.get(i);
                    int realDemoSize = tempBean.getDemoRecordList().size();
                    for (int j = 0; j < (demoWordCount > realDemoSize?realDemoSize:demoWordCount); j++) {
                        Record r = tempBean.getDemoRecordList().get(j);
                        String tempDw = ipaFirst?r.getIPA()+r.getContent():r.getContent()+r.getIPA();
                        yyDemoWordAllTd.append(String.format(yydDemoWordTd,"",tempDw));
                    }
                    yyDemoWordAllTd.deleteCharAt(yyDemoWordAllTd.length()-1);
                    yydAllTr.append(String.format(yydTr,tempBean.getKey(),tempBean.getCount(),yyDemoWordAllTd));
                }
                System.out.println(yydAllTr);

                allSb.append(String.format(yydHtml,"声韵调","声韵调",tbHeader,yydAllTr));


                File outDir = new File(choiceDir.getAbsolutePath()+ "/temp");
                if (!outDir.exists()){
                    outDir.mkdir();
                }

                saveContentToFile(outDir.getAbsolutePath()+"/声韵调"+".html",allSb.toString());
                FileUtil.copy(Constant.ROOT_FILE_DIR+"/HtmlData/voice",outDir.getAbsolutePath()+"/voice");
            }else{

            }
        }
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
}
