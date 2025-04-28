package top.okya.component.domain;

import com.alibaba.fastjson2.JSONArray;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import top.okya.component.domain.child.NodeExecuteInfo;

import java.util.List;
import java.util.Map;

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
public class WorkflowHistory {
    /**
     * 流程节点
     */
    private JSONArray flowNodes;

    /**
     * 是否结束
     */
    @Builder.Default
    private boolean finished = false;

}
