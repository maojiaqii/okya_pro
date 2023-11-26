package top.okya.system.dao;

import org.apache.ibatis.annotations.Mapper;
import top.okya.system.domain.AsUploaderFileChunk;

/**
 * 文件分块上传信息表(AsUploaderFileChunk)表数据库访问层
 *
 * @author mao
 * @since 2023-11-25 21:44:46
 */
@Mapper
public interface AsUploaderFileChunkMapper {

    /**
     * 通过ID查询单条数据
     *
     * @param chunkId 主键
     * @return 实例对象
     */
    AsUploaderFileChunk queryById(Long chunkId);

    /**
     * 新增数据
     *
     * @param asUploaderFileChunk 实例对象
     * @return 影响行数
     */
    int insert(AsUploaderFileChunk asUploaderFileChunk);

}
