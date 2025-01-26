package top.okya.api.authenticated;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.okya.component.annotation.ApiLog;
import top.okya.component.domain.HttpResult;
import top.okya.component.domain.vo.DictDataVo;
import top.okya.component.domain.vo.FormDataVo;
import top.okya.component.domain.vo.TableDataVo;
import top.okya.component.enums.OperationType;
import top.okya.system.service.DataService;

/**
 * @author: maojiaqi
 * @Date: 2023/10/25 15:49
 * @describe: 数据请求接口
 */

@RestController
@RequestMapping("/data")
public class DataController {

    @Autowired
    DataService dataService;

    /**
     * 下拉数据源
     */
    @PostMapping(value = "/dictData")
    @ApiLog(title = "字典数据集", operationType = OperationType.SEARCH)
    public HttpResult data(@Validated @RequestBody DictDataVo dictDataVo) {
        return HttpResult.success(dataService.getDictData(dictDataVo));
    }

    /**
     * 获取表格数据
     */
    @PostMapping(value = "/tableData")
    @ApiLog(title = "获取表格数据", operationType = OperationType.SEARCH)
    public HttpResult getTableData(@Validated @RequestBody TableDataVo tableDataVo) {
        return HttpResult.success(dataService.getTableData(tableDataVo));
    }

    /**
     * 获取表单数据
     */
    @PostMapping(value = "/formData")
    @ApiLog(title = "获取表单数据", operationType = OperationType.SEARCH)
    public HttpResult formData(@Validated @RequestBody FormDataVo formDataVo) {
        return HttpResult.success(dataService.getFormData(formDataVo));
    }

    /**
     * 保存表单数据
     */
    @PostMapping(value = "/saveData")
    @ApiLog(title = "保存表单数据", operationType = OperationType.INSERT)
    public HttpResult saveData(@Validated @RequestBody FormDataVo formDataVo) {
        return HttpResult.success(dataService.saveData(formDataVo));
    }

    /**
     * 多选删除表格选中数据
     */
    @PostMapping(value = "/deleteData")
    @ApiLog(title = "多选删除表格选中数据", operationType = OperationType.DELETE)
    public HttpResult deleteData(@Validated @RequestBody FormDataVo formDataVo) {
        return HttpResult.success(dataService.deleteData(formDataVo));
    }

}
