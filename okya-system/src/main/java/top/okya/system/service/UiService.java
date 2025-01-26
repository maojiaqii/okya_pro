package top.okya.system.service;

import top.okya.system.domain.AsJs;

import java.util.Map;

/**
 * @author: maojiaqi
 * @Date: 2024/12/6 22:07
 * @describe: 页面Ui接口Service
 */
public interface UiService {
    /**
     * 获取表格信息
     *
     * @param tableCode
     * @return
     */
    Map<String, Object> getTableInfo(String tableCode);

    /**
     * 获取表单信息
     *
     * @param formCode
     * @return
     */
    Map<String, Object>  getFormInfo(String formCode);

    /**
     * 获取js代码信息
     *
     * @param jsCode
     * @return
     */
    AsJs getJsInfo(String jsCode);
}
