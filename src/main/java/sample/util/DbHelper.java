package sample.util;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

/**
 * Created by Bee on 2017/4/11.
 */
public class DbHelper {
    static DbHelper dbHelper = new DbHelper();;
    private ConnectionSource connectionSource;

    public static DbHelper getInstance(){
        return dbHelper;
    }

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
//
//            changeLanguage.setText(account2.name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }






}
