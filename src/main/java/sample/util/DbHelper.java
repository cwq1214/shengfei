package sample.util;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import sample.entity.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by Bee on 2017/4/11.
 */
public class DbHelper {
    static DbHelper dbHelper = new DbHelper();
    private ConnectionSource connectionSource;

    public static DbHelper getInstance(){
        return dbHelper;
    }

    Dao<CodeBase, String> codeBaseDao;
    Dao<CodeLangHanYu, String> hanyuDao;
    Dao<CodeIPABase, String> codeIPADao;


    /**
     * 删除table表以及record表相应的数据
     * @param t
     */
    public void delTableAndRecord(Table t){
        try {
            Dao<Table,String> tableDao = DaoManager.createDao(connectionSource,Table.class);
            Dao<Record,String> recordDao = DaoManager.createDao(connectionSource,Record.class);

            recordDao.delete(recordDao.queryForEq("baseId",t.getId()));
            tableDao.delete(t);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询所有字表、词表、句表信息
     * @return
     */
    public ObservableList<Table> searchAllTable(){
        List<Table> resultList = new ArrayList<>();
        try {
            Dao<Table,String > tableDao = DaoManager.createDao(connectionSource,Table.class);
            resultList = tableDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return FXCollections.observableList(resultList);
    }


    /**
     * 把Record数据插入到数据库中
     * @param list
     */
    public void insertRecord(ObservableList<Record> list){
        try {
            System.out.println("insert record in");
            Dao<Record,String> recordDao = DaoManager.createDao(connectionSource,Record.class);
            recordDao.callBatchTasks(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    for (int i = 0; i < list.size(); i++) {
                        recordDao.createOrUpdate(list.get(i));
                    }
                    return null;
                }
            });

//            recordDao.create(list);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据codeBase获取相应分类的数据，并转换成相应的Record类，组装成ObservableList
     * @param dataType 数据分类：字表，词表，句表
     * @return
     */
    public ObservableList<Record> searchTempRecord(String dataType,int baseId){
        List<Record> resultList = new ArrayList<>();
        try {
            Dao<Record,String> recordDao = DaoManager.createDao(connectionSource,Record.class);
            resultList = recordDao.queryForEq("baseId",baseId);
            if (resultList.size() == 0){
                Dao<CodeBase,String> codeBaseDao = DaoManager.createDao(connectionSource,CodeBase.class);
                List<CodeBase> codeBaseList = codeBaseDao.queryForEq("codeType",dataType);
                for (CodeBase cb : codeBaseList) {
                    Record tempRecord = new Record(baseId,cb.code,cb.code,"0","0",cb.IPA,cb.note,cb.spell,cb.english,cb.mwfy,cb.content,cb.rank,cb.yun,"");
                    resultList.add(tempRecord);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return FXCollections.observableArrayList(resultList);
    }

    /**
     * 创建新字表、词表、句表，并插入数据库
     * @param t
     */
    public void insertNewTable(Table t){
        try {
            Dao<Table,String> tableDao = DaoManager.createDao(connectionSource,Table.class);

            int lastIndex = 0;
            String titlePre = "";

            List tempTable = tableDao.queryForEq("datatype",t.datatype);
            System.out.println("size:"+tempTable.size());
            if (tempTable.size() == 0){
                lastIndex = 1;
            }else{
                Table lastT = ((Table) tempTable.get(tempTable.size() - 1));
                lastIndex = Integer.parseInt(lastT.getTitle().substring(2,lastT.getTitle().length()))+1;
            }

            if (t.datatype.equalsIgnoreCase("0")){
                titlePre = "字表";
            }else if(t.datatype.equalsIgnoreCase("1")){
                titlePre = "词表";
            }else if(t.datatype.equalsIgnoreCase("2")){
                titlePre = "句表";
            }

            tableDao.create(t);
            t.setTitle(titlePre+lastIndex);
            t.setProjectname(titlePre+lastIndex);
            tableDao.update(t);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 打开数据库链接
     */
    public DbHelper() {
        String path = Constant.ROOT_FILE_DIR + "/test.db";
        try {
            connectionSource = new JdbcConnectionSource("jdbc:sqlite:" + path);

            codeBaseDao = DaoManager.createDao(connectionSource, CodeBase.class);
            hanyuDao = DaoManager.createDao(connectionSource, CodeLangHanYu.class);
            codeIPADao = DaoManager.createDao(connectionSource, CodeIPABase.class);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭数据库链接
     */
    public void closeDBHelper(){
        try {
            connectionSource.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 通过codeType查询codeBase表数据
     * @param codeType 数据表里面的codeType字段的值
     * @return
     */
    public ObservableList<CodeBase> searchCodeBaseWithCode(int codeType){
        List<CodeBase> resultList = new ArrayList<CodeBase>();

        try {
            resultList = codeBaseDao.queryForEq("codeType",codeType);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return FXCollections.observableArrayList(resultList);
    }

    /**
     * 查询hanyu表所有数据
     * @return
     */
    public ObservableList<CodeLangHanYu> searchAllCodeLangHanYu(){
        List<CodeLangHanYu> resultList = new ArrayList<CodeLangHanYu>();

        try {
            resultList = hanyuDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return FXCollections.observableArrayList(resultList);
    }

    /**
     * 查询所有IPA数据
     * @return
     */
    public ObservableList<CodeIPABase> searchAllCodeIPABase(){
        List<CodeIPABase> resultList = new ArrayList<CodeIPABase>();

        try {
            codeIPADao = DaoManager.createDao(connectionSource, CodeIPABase.class);
            resultList = codeIPADao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return FXCollections.observableArrayList(resultList);
    }

    public void delete(Object object) {
        if (object instanceof CodeIPABase) {
            delete(((CodeIPABase) object));
        } else if (object instanceof CodeBase) {
            delete(((CodeBase) object));
        } else if (object instanceof CodeLangHanYu) {
            delete(((CodeLangHanYu) object));
        } else {
            throw new RuntimeException("un support class");
        }
    }

    public void delete(CodeBase codeBase) {
        try {
            codeBaseDao.delete(codeBase);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(CodeLangHanYu codeLangHanYu) {
        try {
            hanyuDao.delete(codeLangHanYu);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(CodeIPABase codeIPABase) {
        try {
            codeIPADao.delete(codeIPABase);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void upDate(Object object) {
        if (object instanceof CodeIPABase) {
            upDate(((CodeIPABase) object));
        } else if (object instanceof CodeBase) {
            upDate(((CodeBase) object));
        } else if (object instanceof CodeLangHanYu) {
            upDate(((CodeLangHanYu) object));
        } else {
            throw new RuntimeException("un support class");
        }
    }

    public void upDateAll(List objects) {
        if (objects == null) {
            return;
        }
        for (int i = 0, max = objects.size(); i < max; i++) {
            upDate(objects.get(i));
        }
    }

    public void upDate(CodeIPABase codeIPABase) {
        try {
            codeIPADao.update(codeIPABase);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void upDate(CodeBase codeBase) {
        try {
            codeBaseDao.update(codeBase);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void upDate(CodeLangHanYu codeLangHanYu) {
        try {
            hanyuDao.update(codeLangHanYu);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public CodeBase getCodeBaseByCode(String code) {
        try {
            return codeBaseDao.queryForId(code);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public CodeIPABase getCodeIPABaseByCode(String code) {
        try {
            return codeIPADao.queryForId(code);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public CodeLangHanYu getLangHanYuByCode(String code) {
        try {
            return hanyuDao.queryForId(code);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
