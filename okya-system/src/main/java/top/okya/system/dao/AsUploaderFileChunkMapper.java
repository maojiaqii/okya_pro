package top.okya.system.dao;

import org.apache.ibatis.annotations.Mapper;
import top.okya.system.domain.AsUploaderFileChunk;

import java.util.List;

/**
 * 文件分块上传信息表(AsUploaderFileChunk)表数据库访问层
 *
 * @author mao
 * @since 2023-11-25 21:44:46
 */
@Mapper
public interface AsUploaderFileChunkMapper {

    /**
     * 通过文件唯一码查询数据
     *
     * @param fileIdentifier 主键
     * @return 实例对象
     */
    List<AsUploaderFileChunk> queryByFileIdentifier(String fileIdentifier);

    /**
     * 新增数据
     *
     * @param asUploaderFileChunk 实例对象
     * @return 影响行数
     */
    int insert(AsUploaderFileChunk asUploaderFileChunk);

}
