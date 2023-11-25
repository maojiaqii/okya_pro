package top.okya.component.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;

/**
 * @author: maojiaqi
 * @Date: 2023/11/24 10:45
 * @describe: 文件分片上传请求对象
 */

@Accessors(chain = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChunkVo {
    /**
     * 当前文件块，从1开始
     */
    private Integer chunkNumber;
    /**
     * 分块大小
     */
    private Long chunkSize;
    /**
     * 当前分块大小
     */
    private Long currentChunkSize;
    /**
     * 总大小
     */
    private Long totalSize;
    /**
     * 文件标识
     */
    private String identifier;
    /**
     * 文件名
     */
    private String filename;
    /**
     * 相对路径
     */
    private String relativePath;
    /**
     * 总块数
     */
    private Integer totalChunks;
    private MultipartFile file;

    @Override
    public String toString() {
        return "文件分片上传{" +
                "当前文件块：" + chunkNumber +
                ", 分块大小：" + chunkSize +
                ", 当前分块大小：" + currentChunkSize +
                ", 总大小：" + totalSize +
                ", 文件标识（MD5）：'" + identifier + '\'' +
                ", 文件名：'" + filename + '\'' +
                ", 相对路径：'" + relativePath + '\'' +
                ", 总块数：" + totalChunks +
                ", file=" + file +
                "}";
    }
}
