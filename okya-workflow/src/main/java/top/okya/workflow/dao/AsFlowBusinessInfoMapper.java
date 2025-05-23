package top.okya.workflow.dao;
 
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.okya.workflow.domain.AsFlowBusinessInfo;

import java.util.List;
import java.util.Map;
 
/**
 * 业务流程关联信息表(AsFlowBusinessInfo)表数据库访问层
 *
 * @author makejava
 * @since 2025-05-06 16:05:23
 */
 
@Mapper
public interface AsFlowBusinessInfoMapper {
 
    /**
     * 通过Id查询单条数据
     *
     * @return 实例对象
     */
    AsFlowBusinessInfo queryById(String id);

    /**
     * 通过assignee查询用户代码及名称
     *
     * @return 实例对象
     */
    String queryByAssignee(String id);

    /**
     * 通过流程实例Id查询单条数据
     *
     * @param procInstId 流程实例Id
     * @return 实例对象
     */
    List<Map<String, Object>> queryByProcInstId(String procInstId);
 
    /**
     * 新增数据
     *
     * @param asFlowBusinessInfo 实例对象
     * @return 影响行数
     */
    int insert(AsFlowBusinessInfo asFlowBusinessInfo);

    int finish(@Param("procInstId") String processInstanceId, @Param("endTime") String endTime);

    int updateNodeInfo(@Param("procInstId") String processInstanceId, @Param("taskDefinitionKey") String taskDefinitionKey, @Param("taskDefinitionName") String taskDefinitionName);

    int deleteByProcessInstanceId(String procInstId);
}
