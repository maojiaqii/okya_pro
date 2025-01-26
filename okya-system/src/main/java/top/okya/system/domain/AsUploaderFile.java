package top.okya.system.domain;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 文件上传表(AsUploaderFile)实体类
 *
 * @author mjq
 * @since 2023-12-01 16:07:38
 */
@Accessors(chain = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsUploaderFile implements Serializable {
    private static final long serialVersionUID = 717142916404817821L;
    /**
    * 文件id
    */
    private String fileId;
    /**
    * 文件名称
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
    * 文件大小
    */
    private Long fileSize;
    /**
     * 是否删除（0否 1是）
     */
    private Integer isDelete;
    /**
    * 上传人
    */
    private String uploadBy;
    /**
    * 上传时间
    */
    private Date uploadTime;
    /**
    * 更新人
    */
    private String updateBy;
    /**
    * 更新时间
    */
    private Date updateTime;

}
