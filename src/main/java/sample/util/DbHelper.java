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
import sample.entity.CodeBase;
import sample.entity.CodeCounty;
import sample.entity.CodeIPABase;
import sample.entity.CodeLangHanYu;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

}
