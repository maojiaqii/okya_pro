package top.okya.workflow.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.okya.component.domain.NextAssignee;
import top.okya.workflow.domain.AsFlow;

import java.util.List;
import java.util.Map;

/**
 * 流程设计表(AsFlow)表数据库访问层
 *
 * @author makejava
 * @since 2025-04-14 23:08:49
 */
@Mapper
public interface AsFlowMapper {
 
    /**
     * 查询所有数据
     *
     * @return 对象列表
     */
    List<AsFlow> queryLasted(String flowCode);

    /**
     * 查询最新版本号
     *
     * @param flowCode 流程编码
     * @return 版本号
     */
    Integer queryLastedVerByCode(String flowCode);

    int insert(AsFlow asFlow);

    AsFlow queryFlowById(String flowId);

    List<NextAssignee> getAssigneesByRole(@Param("roles") List<String> roles, @Param("roleRange") int roleRange, @Param("deptId") Object deptId);

    int published(@Param("flowId") String flowId, @Param("deploymentId") String deploymentId);

    int unPublished(@Param("flowCode") String flowCode, @Param("flowId") String flowId);

    int update(AsFlow asFlow);

    AsFlow queryFlowByDeploymentId(String deploymentId);
}
