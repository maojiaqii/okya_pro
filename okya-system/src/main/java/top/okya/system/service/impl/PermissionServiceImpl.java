package top.okya.system.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.okya.component.constants.CharacterConstants;
import top.okya.component.domain.LoginUser;
import top.okya.component.domain.Permission;
import top.okya.component.domain.child.PermissionMetaPermis;
import top.okya.component.domain.dto.AsPermission;
import top.okya.component.domain.dto.AsUser;
import top.okya.component.domain.vo.RolePermissionVo;
import top.okya.component.domain.vo.UserPwdVo;
import top.okya.component.global.Global;
import top.okya.component.utils.security.SecureUtil;
import top.okya.component.utils.ui.UiUtil;
import top.okya.system.dao.AsFormMapper;
import top.okya.system.dao.AsPermissionMapper;
import top.okya.system.dao.AsTableMapper;
import top.okya.system.dao.AsUserMapper;
import top.okya.system.domain.AsForm;
import top.okya.system.domain.AsTable;
import top.okya.system.service.PermissionService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author: maojiaqi
 * @Date: 2023/9/20 15:07
 * @describe: 权限相关service实现
 */

@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    AsPermissionMapper asPermissionMapper;

    @Autowired
    AsUserMapper asUserMapper;

    @Autowired
    AsTableMapper asTableMapper;

    @Autowired
    AsFormMapper asFormMapper;

    @Override
    public List<Map<String, Object>> myTenancys() {
        AsUser asUser = Objects.requireNonNull(Global.getLoginUser()).getAsUser();
        String userId = asUser.isAdmin() ? null : asUser.getUserId();
        return asPermissionMapper.queryTenancysByUserId(userId);
    }

    @Override
    public void resetPwd(UserPwdVo userPwdVo) {
        String userId = userPwdVo.getUserId();
        if (Objects.isNull(userId)) {
            AsUser asUser = Objects.requireNonNull(Global.getLoginUser()).getAsUser();
            userId = asUser.getUserId();
        }
        asUserMapper.updatePwdByUserId(userId, SecureUtil.encode(userPwdVo.getPassword()));
    }

    @Override
    public void resetStatus(String userId) {
        asUserMapper.updateStatusByUserId(userId);
    }

    @Override
    public List<Map<String, Object>> getTablePermiButtons() {
        List<AsTable> asTables = asTableMapper.queryAll();
        if (asTables.isEmpty()) {
            return null;
        }
        List<Map<String, Object>> buttons = Lists.newArrayList();
        for (AsTable asTable : asTables) {
            List<Map<String, Object>> buttonsRef = Lists.newArrayList();
            // 表格上方按钮
            JSONArray buttons1 = asTable.getButtons();
            if (buttons1 != null) {
                for (int i = 0; i < buttons1.size(); i++) {
                    JSONObject button2 = buttons1.getJSONObject(i);
                    if (button2.containsKey("permi")) {
                        Map<String, Object> button = new HashMap<>();
                        button.put("id", button2.get("permi"));
                        button.put("label", button2.get("staticText"));
                        button.put("pid", asTable.getTableId());
                        buttonsRef.add(button);
                    }
                }
            }

            // 操作列按钮
            JSONArray columns = asTable.getColumns();
            if (columns != null) {
                for (int i = 0; i < columns.size(); i++) {
                    JSONObject column = columns.getJSONObject(i);
                    if (column.containsKey("type") && column.get("type").equals("operation") && column.containsKey("buttons")) {
                        JSONArray aDefault = column.getJSONArray("buttons");
                        for (int j = 0; j < aDefault.size(); j++) {
                            JSONObject button2 = aDefault.getJSONObject(j);
                            if (button2.containsKey("permi")) {
                                Map<String, Object> button = new HashMap<>();
                                button.put("id", button2.get("permi"));
                                button.put("label", button2.get("staticText"));
                                button.put("pid", asTable.getTableId());
                                buttonsRef.add(button);
                            }
                        }
                    }
                }
            }

            if (!buttonsRef.isEmpty()) {
                // 形成表格-按钮的树形结构
                Map<String, Object> table = new HashMap<>();
                table.put("id", asTable.getTableId());
                table.put("label", asTable.getTableName());
                table.put("pid", "-1");
                buttons.add(table);
                buttons.addAll(buttonsRef);
            }
        }
        return buttons;
    }

    @Override
    public List<Map<String, Object>> getTablePermiColumns() {
        List<AsTable> asTables = asTableMapper.queryAll();
        if (asTables.isEmpty()) {
            return null;
        }
        List<Map<String, Object>> columns = Lists.newArrayList();
        for (AsTable asTable : asTables) {
            List<Map<String, Object>> columnsRef = Lists.newArrayList();

            JSONArray columnsSelect = asTable.getColumns();
            columnsRef.addAll(renderPermiColumns(columnsSelect, asTable.getTableId()));

            if (!columnsRef.isEmpty()) {
                // 形成表格-列的树形结构
                Map<String, Object> table = new HashMap<>();
                table.put("id", asTable.getTableId());
                table.put("label", asTable.getTableName());
                table.put("pid", "-1");
                columns.add(table);
                columns.addAll(columnsRef);
            }
        }
        return columns;
    }

    @Override
    public List<Map<String, Object>> getFormPermiFields() {
        List<AsForm> asForms = asFormMapper.queryAll();
        if (asForms.isEmpty()) {
            return null;
        }
        List<Map<String, Object>> fields = Lists.newArrayList();
        for (AsForm asForm : asForms) {
            List<Map<String, Object>> fieldsRef = Lists.newArrayList();

            JSONArray fieldsSelect = asForm.getFormItems();
            if (fieldsSelect != null) {
                for (int i = 0; i < fieldsSelect.size(); i++) {
                    JSONObject fieldSelect = fieldsSelect.getJSONObject(i);
                    if (fieldSelect.containsKey("permi")) {
                        Map<String, Object> column = new HashMap<>();
                        column.put("id", fieldSelect.get("permi"));
                        column.put("label", fieldSelect.getJSONObject("itemProps").get("label"));
                        column.put("pid", asForm.getFormId());
                        fieldsRef.add(column);
                    }
                }
            }

            if (!fieldsRef.isEmpty()) {
                // 形成表单-字段的树形结构
                Map<String, Object> table = new HashMap<>();
                table.put("id", asForm.getFormId());
                table.put("label", asForm.getFormName());
                table.put("pid", "-1");
                fields.add(table);
                fields.addAll(fieldsRef);
            }
        }
        return fields;
    }

    @Override
    public RolePermissionVo getRolePermissions(String roleId) {
        return new RolePermissionVo().setRoleTenancy(asPermissionMapper.queryTenancysByRoleId(roleId))
                .setRolePermission(asPermissionMapper.queryPermissionsByRoleId(roleId))
                .setRoleButton(asPermissionMapper.queryButtonsByRoleId(roleId))
                .setRoleTable(asPermissionMapper.queryColumnsByRoleId(roleId))
                .setRoleForm(asPermissionMapper.queryFieldsByRoleId(roleId));
    }

    @Override
    @Transactional
    public void saveRolePermissions(RolePermissionVo rolePermissionVo) {
        String roleId = rolePermissionVo.getRoleId();
        asPermissionMapper.deleteTenancyByRoleId(roleId);
        asPermissionMapper.deletePermissionByRoleId(roleId);
        asPermissionMapper.deleteTableByRoleId(roleId);
        asPermissionMapper.deleteButtonByRoleId(roleId);
        asPermissionMapper.deleteFormByRoleId(roleId);
        List<String> roleTenancy = rolePermissionVo.getRoleTenancy();
        List<String> rolePermission = rolePermissionVo.getRolePermission();
        List<String> roleTable = rolePermissionVo.getRoleTable();
        List<String> roleButton = rolePermissionVo.getRoleButton();
        List<String> roleForm = rolePermissionVo.getRoleForm();
        if(!Objects.isNull(roleTenancy) && !roleTenancy.isEmpty()){
            for(String tenancy : roleTenancy){
                asPermissionMapper.saveRoleTenancy(roleId, tenancy);
            }
        }
        if(!Objects.isNull(rolePermission) && !rolePermission.isEmpty()){
            for(String permission : rolePermission){
                asPermissionMapper.saveRolePermission(roleId, permission);
            }
        }
        if(!Objects.isNull(roleTable) && !roleTable.isEmpty()){
            for(String table : roleTable){
                asPermissionMapper.saveRoleTable(roleId, table);
            }
        }
        if(!Objects.isNull(roleButton) && !roleButton.isEmpty()){
            for(String button : roleButton){
                asPermissionMapper.saveRoleButton(roleId, button);
            }
        }
        if(!Objects.isNull(roleForm) && !roleForm.isEmpty()){
            for(String form : roleForm){
                asPermissionMapper.saveRoleForm(roleId, form);
            }
        }
    }

    @Override
    public List<Permission> myPermissions(String currentTenancy) {
        AsUser asUser = Objects.requireNonNull(Global.getLoginUser()).getAsUser();
        String userId = asUser.isAdmin() ? null : asUser.getUserId();
        List<AsPermission> asPermissions = asPermissionMapper.queryPermissionsByUserId(userId, currentTenancy).stream().distinct().collect(Collectors.toList());
        // 新增加按钮、表格列、表单字段权限
        PermissionMetaPermis permissionMetaPermis = new PermissionMetaPermis();
        if (asUser.isAdmin()) {
            // admin角色的用户默认全部权限
            ArrayList<String> allPermis = Lists.newArrayList(CharacterConstants.STAR);
            permissionMetaPermis.setButtons(allPermis)
                    .setColumns(allPermis)
                    .setFields(allPermis);
        } else {
            permissionMetaPermis.setButtons(asPermissionMapper.queryButtonsByUserId(userId))
                    .setColumns(asPermissionMapper.queryColumnsByUserId(userId))
                    .setFields(asPermissionMapper.queryFieldsByUserId(userId));
        }
        asPermissions.forEach(obj -> {
            obj.setPermission(permissionMetaPermis);
        });
        return UiUtil.formatPermissions(asPermissions, "0");
    }

    private List<Map<String, Object>> renderPermiColumns(JSONArray columnsSelect, String pid) {
        List<Map<String, Object>> columnsRef = Lists.newArrayList();
        if (columnsSelect != null) {
            for (int i = 0; i < columnsSelect.size(); i++) {
                JSONObject columnSelect = columnsSelect.getJSONObject(i);
                if(columnSelect.containsKey("children")){
                    JSONArray children = columnSelect.getJSONArray("children");
                    columnsRef.addAll(renderPermiColumns(children, pid));
                }
                if (columnSelect.containsKey("permi")) {
                    Map<String, Object> column = new HashMap<>();
                    column.put("id", columnSelect.get("permi"));
                    column.put("label", columnSelect.get("label"));
                    column.put("pid", pid);
                    columnsRef.add(column);
                }
            }
        }
        return columnsRef;
    }

}
