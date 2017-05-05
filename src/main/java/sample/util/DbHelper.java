package sample.util;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import sample.controller.NewTableView.NewTopicKindSelectController;
import sample.controller.NewTableView.NewTopicKindSelectListener;
import sample.controller.NewTableView.TopicBean;
import sample.controller.UnionCode.UnionCodeBean;
import sample.controller.YBCC.YBCCBean;
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
     * 更新编码替换的Record
     * @param records
     */
    public void updateReplaceCode(List<Record> records){
        try {
            Dao<Record,String> recordDao = DaoManager.createDao(connectionSource,Record.class);
            recordDao.callBatchTasks(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    for (Record r : records) {
                        recordDao.update(r);
                    }
                    return null;
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 分析获取编码不统一的数据
     * @param t
     * @return
     */
    public ObservableList<UnionCodeBean> getHasDifferentUnionCodeBean(Table t){
        //TODO
        List<UnionCodeBean> resultList = new ArrayList<>();
        try {
            Dao<Record,String> recordDao = DaoManager.createDao(connectionSource,Record.class);
            Dao<CodeBase,String> baseDao = DaoManager.createDao(connectionSource,CodeBase.class);

            List<Record> rList = recordDao.queryForEq("baseId",t.getId());
            for (Record r : rList) {
                //codeType相同 content相同 code不同的
                List<CodeBase> bList = baseDao.queryBuilder().where().eq("codeType",t.getDatatype()).and().eq("content",r.content).and().ne("code",r.getBaseCode()).query();
                if (bList.size()!=0){
                    resultList.add(new UnionCodeBean(r,bList.get(0)));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return FXCollections.observableArrayList(resultList);
    }

    /**
     * 保存话题表内容
     * @param topics
     */
    public void saveTopics(ObservableList<Topic> topics){
        try {
            Dao<Topic,String> topicDao = DaoManager.createDao(connectionSource,Topic.class);

            topicDao.callBatchTasks(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    List<Topic> temp = topicDao.queryForEq("baseId",topics.get(0).getBaseId());

                    int lastSpeakerId = 0;
                    if (temp.size() != 0){
                        lastSpeakerId = Integer.parseInt(temp.get(temp.size() - 1).getSpeakerId());
                    }

                    for (Topic t: topics) {
                        if (t.getSpeakerId() == null || t.getSpeakerId().length() == 0){
                            t.setSpeakerId(Integer.toString(++lastSpeakerId));
                        }
                        topicDao.createOrUpdate(t);
                    }
                    return null;
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除某条record
     * @param list
     */
    public void delRecord(List<YBCCBean> list){
        try {
            Dao<Record,String> recordDao = DaoManager.createDao(connectionSource,Record.class);
            recordDao.callBatchTasks(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    for (YBCCBean b : list) {
                        recordDao.delete(b.getRecord());
                    }
                    return null;
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除话题表
     * @param t
     */
    public void delTopicTable(Topic t){
        try {
            Dao<Topic,String> topicDao = DaoManager.createDao(connectionSource,Topic.class);
            topicDao.delete(t);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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
     * 查询所有字表、词表、句表、话题信息
     * @return
     */
    public ObservableList searchAllTable(){
        List resultList = new ArrayList<>();
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
    public void insertRecord(ObservableList<YBCCBean> list){
        try {
            System.out.println("insert record in");
            Dao<Record,String> recordDao = DaoManager.createDao(connectionSource,Record.class);
            recordDao.callBatchTasks(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    for (int i = 0; i < list.size(); i++) {
                        recordDao.createOrUpdate(list.get(i).getRecord());
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
    public ObservableList<YBCCBean> searchTempRecord2YBCCBean(String dataType, int baseId){
        List<YBCCBean> resultList = new ArrayList<>();
        List<Record> rList = new ArrayList<>();
        try {
            Dao<Record,String> recordDao = DaoManager.createDao(connectionSource,Record.class);
            rList = recordDao.queryForEq("baseId",baseId);
            if (rList.size() == 0){
                Dao<CodeBase,String> codeBaseDao = DaoManager.createDao(connectionSource,CodeBase.class);
                List<CodeBase> codeBaseList = codeBaseDao.queryForEq("codeType",dataType);
                for (CodeBase cb : codeBaseList) {
                    Record tempRecord = new Record(baseId,cb.code,cb.code,"0","0",cb.IPA,cb.note,cb.spell,cb.english,cb.mwfy,"",cb.content,cb.rank,cb.yun,"");
                    rList.add(tempRecord);
                }
            }
            for (Record r : rList){
                resultList.add(new YBCCBean(r));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return FXCollections.observableArrayList(resultList);
    }

    /**
     * 查询话题表
     * @param baseId
     */
    public ObservableList<Topic> searchTopicData(String kindCode,int baseId){
        List<Topic> result = new ArrayList<>();
        try {
            Dao<Topic,String> topicDao = DaoManager.createDao(connectionSource,Topic.class);
            List<Topic> temp = topicDao.queryForEq("baseId",baseId);
            if (temp.size() == 0){
                result.add(new Topic(kindCode,baseId));
            }else{
                result.addAll(temp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return FXCollections.observableArrayList(result);
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


            if (t.datatype.equalsIgnoreCase("0")){
                titlePre = "字表";
            }else if(t.datatype.equalsIgnoreCase("1")){
                titlePre = "词表";
            }else if(t.datatype.equalsIgnoreCase("2")){
                titlePre = "句表";
            }else if(Integer.parseInt(t.datatype) >= 3){
                int type = Integer.parseInt(t.datatype);
                titlePre = ((TopicBean) NewTopicKindSelectController.topicKind.get(type - 3)).getTopic()+"表";
            }

            List tempTable = tableDao.queryForEq("datatype",t.datatype);
            System.out.println("size:"+tempTable.size());
            if (tempTable.size() == 0){
                lastIndex = 1;
            }else{
                Table lastT = ((Table) tempTable.get(tempTable.size() - 1));
                lastIndex = Integer.parseInt(lastT.getTitle().substring(titlePre.length(),lastT.getTitle().length()))+1;
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

    public List<Speaker> getSpeakers(){
        try {
            return getSpeakerDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Speaker getSpeakerById(Integer id){
        if (id==null){
            return null;
        }
        try {
            return getSpeakerDao().queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int addOrUpdate(Speaker speaker){
        try {
            return getSpeakerDao().createOrUpdate(speaker).getNumLinesChanged();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int deleteSpeaker(Speaker speaker){
        try {
            return getSpeakerDao().delete(speaker);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int deleteSpeakerById(Integer id){
        try {
            return getSpeakerDao().deleteById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }




    public Table getTableById(Integer id){
        try {
            return getTableDao().queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Table> getAllTables(){
        try {
            return getTableDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int addOrUpdateTable(Table table){
        try {
            return getTableDao().createOrUpdate(table).getNumLinesChanged();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Dao<Speaker,Integer> getSpeakerDao(){
        try {
            return DaoManager.createDao(connectionSource,Speaker.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Dao<Table,Integer> getTableDao(){
        try {
            return DaoManager.createDao(connectionSource,Table.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
