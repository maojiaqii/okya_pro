package top.okya.component.utils.mybatis;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * @author: maojiaqi
 * @Date: 2022/8/25 9:44
 * @describe：
 */

@Slf4j
public class MybatisUtils {

    private static ApplicationContext applicationContext;

    /**
     * @param clazz Mapper.class
     * @param biConsumer Mapper中方法
     * @param data 待插入数据集
     * @param sqlSessionFactoryName 指定具体数据库的sqlSessionFactory，单一数据源时传递可以为null
     */
    public static <M, T> void batchInsert(Class<M> clazz, BiConsumer<M, T> biConsumer, List<T> data, String sqlSessionFactoryName) {
        SqlSessionFactory sqlSessionFactory = sqlSessionFactoryName == null ? applicationContext.getBean(SqlSessionFactory.class) : applicationContext.getBean(sqlSessionFactoryName, SqlSessionFactory.class);
        SqlSession session = sqlSessionFactory.openSession(ExecutorType.BATCH,false);
        log.info("init session complete...");
        try {
            M mapper = session.getMapper(clazz);
            data.forEach(a -> {
                biConsumer.accept(mapper, a);
            });
            session.commit();
            session.clearCache();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("batchInsert is exception！clazz={}", clazz.getName(), e);
            session.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

}
