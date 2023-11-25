package top.okya.api.authenticated;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.okya.component.annotation.ApiLog;
import top.okya.component.domain.HttpResult;
import top.okya.component.domain.vo.DictDataVo;
import top.okya.component.enums.OperationType;
import top.okya.system.service.DictService;

/**
 * @author: maojiaqi
 * @Date: 2023/10/25 14:31
 * @describe: 字典接口
 */

@RestController
@RequestMapping("/dict")
public class DictionaryController {


    @Autowired
    DictService dictService;

    /**
     * 下拉数据源
     */
    @GetMapping(value = "/data")
    @ApiLog(title = "字典数据集", operationType = OperationType.SEARCH)
    public HttpResult data(@Validated DictDataVo dictDataVo) {
        return HttpResult.success(dictService.getDictData(dictDataVo));
    }
}
