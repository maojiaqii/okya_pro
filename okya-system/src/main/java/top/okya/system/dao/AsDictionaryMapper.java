package top.okya.system.dao;

import top.okya.system.domain.AsDictionary;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 字典表(AsDictionary)表数据库访问层
 *
 * @author mjq
 * @since 2023-11-08 10:22:49
 */
@Mapper
public interface AsDictionaryMapper {

    /**
     * 通过ID查询单条数据
     *
     * @param dictCode 字典编码
     * @return 实例对象
     */
    AsDictionary queryByCode(String dictCode);

}
