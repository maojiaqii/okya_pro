package top.okya.system.domain;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 操作日志记录(AsOperLog)实体类
 *
 * @author mjq
 * @since 2023-07-25 17:41:38
 */
@Accessors(chain = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsOperLog implements Serializable {
    private static final long serialVersionUID = 411228622990070507L;
    /**
    * 日志主键
    */
    private Long operId;
    /**
    * 模块标题
    */
    private String title;
    /**
    * 业务类型（0其它 1新增 2修改 3删除）
    */
    private String operationType;
    /**
    * 方法名称
    */
    private String method;
    /**
    * 请求方式
    */
    private String requestMethod;
    /**
    * 操作类别（0其它 1后台用户 2手机端用户）
    */
    private Integer operatorType;
    /**
    * 操作人员
    */
    private String operName;
    /**
    * 部门名称
    */
    private String deptName;
    /**
    * 请求URL
    */
    private String operUrl;
    /**
    * 主机地址
    */
    private String operIp;
    /**
    * 操作地点
    */
    private String operLocation;
    /**
    * 请求参数
    */
    private String operParam;
    /**
    * 返回参数
    */
    private String jsonResult;
    /**
    * 操作状态（0正常 1异常）
    */
    private Integer status;
    /**
    * 错误消息
    */
    private String errorMsg;
    /**
    * 操作时间
    */
    private Date operTime;
    /**
    * 消耗时间
    */
    private Long costTime;
    /**
    * 返回时间
    */
    private Date finishTime;

}
