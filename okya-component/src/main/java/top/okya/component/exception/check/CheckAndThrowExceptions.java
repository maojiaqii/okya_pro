package top.okya.component.exception.check;

import org.springframework.stereotype.Service;
import top.okya.component.domain.dto.base.Basic;
import top.okya.component.enums.UseStatus;
import top.okya.component.enums.exception.ServiceExceptionType;
import top.okya.component.exception.ServiceException;

import java.util.Objects;

/**
 * @author: maojiaqi
 * @Date: 2025/4/21 22:46
 * @describe: 检查并抛出异常
 */

@Service
public class CheckAndThrowExceptions {
    public <T extends Basic> void checkDbResult(T result) throws ServiceException {
        if(Objects.isNull(result)){
            throw new ServiceException(ServiceExceptionType.UNKNOWN);
        }
        int status = result.getStatus();
        // 状态异常
        if (Objects.equals(status, UseStatus.DISABLED.getCode())) {
            throw new ServiceException(ServiceExceptionType.DISABLED);
        } else if (!Objects.equals(status, UseStatus.OK.getCode())) {
            throw new ServiceException(ServiceExceptionType.SERVER_EXCEPTION, "操作的对象状态异常！");
        }
    }
}
