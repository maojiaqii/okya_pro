package top.okya.system.dao;

import top.okya.system.domain.AsUploaderFile;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 文件上传表(AsUploaderFile)表数据库访问层
 *
 * @author mjq
 * @since 2023-12-01 16:07:55
 */
@Mapper
public interface AsUploaderFileMapper {

    /**
     * 通过ID查询单条数据
     *
     * @param fileIdentifier 文件标识
     * @return 实例对象
     */
    AsUploaderFile queryByIdentifier(String fileIdentifier);

    /**
     * 新增数据
     *
     * @param asUploaderFile 实例对象
     * @return 影响行数
     */
    int insert(AsUploaderFile asUploaderFile);
}
