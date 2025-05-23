package top.okya.component.domain.child;

import com.alibaba.fastjson2.JSONArray;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * @author: maojiaqi
 * @Date: 2024/7/26 16:05
 * @describe: 流程流转历史类
 */

@Accessors(chain = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FullPath {
    /**
     * id
     */
    private String id;

    /**
     * 流程节点名称
     */
    private String nodeName;

    /**
     * 流程节点类型
     */
    private String nodeType;

    /**
     * 审批结果
     */
    private String result;

    /**
     * 审核人数
     */
    private int checkerCount;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date finishTime;

    /**
     * 耗时
     */
    private Long duration;

    /**
     * 审批详情
     */
    private List<NodeExecuteInfo> executedInfos;

}
