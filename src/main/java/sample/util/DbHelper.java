package sample.util;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.entity.CodeBase;
import sample.entity.CodeCounty;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bee on 2017/4/11.
 */
public class DbHelper {
    static DbHelper dbHelper = new DbHelper();;
    private ConnectionSource connectionSource;

    public static DbHelper getInstance(){
        return dbHelper;
    }

    /**
     * 打开数据库链接
     */
    public DbHelper() {
        String path = Constant.ROOT_FILE_DIR + "/test.db";
        try {
            connectionSource = new JdbcConnectionSource("jdbc:sqlite:" + path);

//            Dao<CodeCounty, String> accountDao =
//                    DaoManager.createDao(connectionSource, CodeCounty.class);
//
//            CodeCounty account2 = accountDao.queryForEq("code", "110000").get(0);
//            System.out.println("Account: " + account2.name);
//            connectionSource.close();

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



    public List<CodeBase> searchCodeBaseWithCode(int codeType){
        List<CodeBase> resultList = new ArrayList<CodeBase>();

        try {
            Dao<CodeBase,String> codeBaseDao = DaoManager.createDao(connectionSource,CodeBase.class);
            resultList = codeBaseDao.queryForEq("codeType",codeType);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultList;
    }
}
