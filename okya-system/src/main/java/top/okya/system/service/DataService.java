package top.okya.system.service;

import top.okya.component.domain.TableData;
import top.okya.component.domain.vo.DictDataVo;
import top.okya.component.domain.vo.FormDataVo;
import top.okya.component.domain.vo.TableDataVo;

import java.util.Map;

/**
 * @author: maojiaqi
 * @Date: 2024/12/6 22:07
 * @describe: 数据接口Service
 */
public interface DataService {
    /**
     * 获取表格数据
     *
     * @param tableDataVo
     * @return
     */
    TableData getTableData(TableDataVo tableDataVo);
    /**
     * 获取字典数据集
     *
     * @param dictDataVo
     * @return
     */
    Map<String, Object> getDictData(DictDataVo dictDataVo);
    /**
     * 获取表单数据
     *
     * @param formDataVo
     * @return
     */
    Map<String, Object> getFormData(FormDataVo formDataVo);
    /**
     * 保存表单数据
     *
     * @param formDataVo
     * @return
     */
    String saveData(FormDataVo formDataVo);
    /**
     * 多选删除表格选中数据
     *
     * @param formDataVo
     * @return
     */
    String deleteData(FormDataVo formDataVo);
}
