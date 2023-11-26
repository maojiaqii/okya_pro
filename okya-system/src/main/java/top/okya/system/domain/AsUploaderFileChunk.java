package top.okya.system.domain;

import java.util.Date;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 文件分块上传信息表(AsUploaderFileChunk)实体类
 *
 * @author mao
 * @since 2023-11-26 20:53:20
 */
@Accessors(chain = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsUploaderFileChunk implements Serializable {
    private static final long serialVersionUID = 893604636044674215L;
    /**
     * 分块id
     */
    private Long chunkId;
    /**
     * 分块名称
     */
    private String chunkName;
    /**
     * 分块编号
     */
    private Integer chunkNum;
    /**
     * 当前分块大小
     */
    private Long chunkSize;
    /**
     * 总大小
     */
    private Long totalSize;
    /**
     * 总块数
     */
    private Integer totalChunks;
    /**
     * 真实文件名称
     */
    private String fileName;
    /**
     * 文件唯一标志（MD5）
     */
    private String fileIdentifier;
    /**
     * 文件路径
     */
    private String filePath;
    /**
     * 是否合并（0否1是）
     */
    private Integer merged;
    /**
     * 上传人
     */
    private String uploadBy;
    /**
     * 上传时间
     */
    private Date uploadTime;

}
