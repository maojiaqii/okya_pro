package top.okya.component.domain.child;

import com.alibaba.fastjson2.JSONArray;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author: maojiaqi
 * @Date: 2024/7/26 16:05
 * @describe: 流程节点审核情况类
 */

@Accessors(chain = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NodeExecuteInfo {
    /**
    * 节点名称
    */
    private String activityName;

    /**
     * 节点类型
     */
    private String activityType;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 耗时（分钟）
     */
    private Long durationInMin;

    /**
     * 处理人
     */
    private String assignee;

}
