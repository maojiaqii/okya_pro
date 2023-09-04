package top.okya.component.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author: maojiaqi
 * @Date: 2023/6/27 15:57
 * @describe：
 */

@Accessors(chain = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Sequence {
    /**
     * 类型，作为redis-key
     */
    @Builder.Default
    private String key = "";

    /**
     * 业务编号的前缀
     */
    @Builder.Default
    private String prefix = "";

    /**
     * 时间格式
     */
    @Builder.Default
    private String pattern = "";

    /**
     * 序列号长度， 不足补0
     */
    @Builder.Default
    private int length = 0;

    /**
     * 最小序号
     */
    @Builder.Default
    private Long initialValue = 0L;

    /**
     * 最小序号
     */
    @Builder.Default
    private Long minValue = 0L;


    /**
     * 最大序号
     */
    @Builder.Default
    private Long maxValue = 9223372036854775800L;

    /**
     * 步长
     */
    @Builder.Default
    private int step = 1;

    /**
     * 循环
     */
    @Builder.Default
    private boolean circle = true;

    /**
     * 更新周期
     */
    @Builder.Default
    private String updateInterval = "";

}
