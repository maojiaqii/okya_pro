package top.okya.component.utils.common;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * QLExpress表达式引擎测试类
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class QLExpressUtilTest {

    @Resource
    private QLExpressUtil qlExpressUtil;

    private Map<String, Object> variables;

    @Before
    public void setUp() {
        variables = new HashMap<>();
        // 数值变量
        variables.put("amount", 1500);
        variables.put("age", 25);
        variables.put("price", 99.99);
        
        // 字符串变量
        variables.put("name", "张三");
        variables.put("status", "approved");
        variables.put("email", "zhangsan@example.com");
        
        // 布尔变量
        variables.put("isVip", true);
        variables.put("hasDiscount", false);
        
        // 日期变量
        variables.put("createTime", new Date());
    }

    @Test
    public void testSimpleExpressions() {
        // 简单数值比较
        assertTrue(QLExpressUtil.executeBoolean("amount > 1000", variables));
        assertFalse(QLExpressUtil.executeBoolean("amount < 1000", variables));
        
        // 字符串比较
        assertTrue(QLExpressUtil.executeBoolean("name == '张三'", variables));
        assertFalse(QLExpressUtil.executeBoolean("name == '李四'", variables));
        
        // 布尔值
        assertTrue(QLExpressUtil.executeBoolean("isVip", variables));
        assertFalse(QLExpressUtil.executeBoolean("hasDiscount", variables));
        
        // 多条件组合
        assertTrue(QLExpressUtil.executeBoolean("amount > 1000 && isVip", variables));
        assertFalse(QLExpressUtil.executeBoolean("amount < 1000 || (hasDiscount && price > 100)", variables));
    }
    
    @Test
    public void testPlaceholderExpressions() {
        // 测试${变量}格式的表达式
        assertTrue(QLExpressUtil.executeBoolean("${age} > 18", variables));
        assertFalse(QLExpressUtil.executeBoolean("${age} < 18", variables));
        
        // 测试字符串变量
        assertTrue(QLExpressUtil.executeBoolean("${name} == '张三'", variables));
        assertFalse(QLExpressUtil.executeBoolean("${name} == '李四'", variables));
        
        // 测试布尔变量
        assertTrue(QLExpressUtil.executeBoolean("${isVip}", variables));
        assertFalse(QLExpressUtil.executeBoolean("${hasDiscount}", variables));
        
        // 测试复合条件
        assertTrue(QLExpressUtil.executeBoolean("${amount} > 1000 && ${isVip}", variables));
        assertFalse(QLExpressUtil.executeBoolean("${amount} < 1000 || (${hasDiscount} && ${price} > 100)", variables));
        
        // 测试混合格式
        assertTrue(QLExpressUtil.executeBoolean("${age} > 18 && amount > 1000", variables));
        assertFalse(QLExpressUtil.executeBoolean("${age} < 18 || amount < 1000", variables));
    }
    
    @Test
    public void testCustomOperatorsWithPlaceholders() {
        // 测试字符串包含操作符
        assertTrue(QLExpressUtil.executeBoolean("${email} contains '@'", variables));
        assertFalse(QLExpressUtil.executeBoolean("${name} contains 'Lee'", variables));
        
        // 测试字符串开始、结束操作符
        assertTrue(QLExpressUtil.executeBoolean("${email} endsWith 'example.com'", variables));
        assertTrue(QLExpressUtil.executeBoolean("${name} startsWith '张'", variables));
    }
    
    @Test
    public void testNullSafetyWithPlaceholders() {
        // 测试空值安全性
        variables.put("nullValue", null);
        
        assertTrue(QLExpressUtil.executeBoolean("isEmpty(${nullValue})", variables));
        assertFalse(QLExpressUtil.executeBoolean("isNotEmpty(${nullValue})", variables));
        
        // 测试不存在的变量
        assertTrue(QLExpressUtil.executeBoolean("isEmpty(${nonExistingVar})", variables));
    }
    
    @Test
    public void testComplexExpressionsWithPlaceholders() {
        // 复杂表达式测试
        String expr1 = "${amount} > 1000 && (${status} == 'approved' || ${isVip}) && !${hasDiscount}";
        assertTrue(QLExpressUtil.executeBoolean(expr1, variables));
        
        String expr2 = "round(${price} * 0.8, 2) < 100 && length(${name}) <= 5";
        assertTrue(QLExpressUtil.executeBoolean(expr2, variables));
    }
    
    @Test
    public void testWorkflowScenariosWithPlaceholders() {
        // 模拟工作流场景
        Map<String, Object> flowVars = new HashMap<>();
        flowVars.put("applicantAge", 35);
        flowVars.put("applicantSalary", 12000);
        flowVars.put("loanAmount", 50000);
        flowVars.put("creditScore", 720);
        flowVars.put("hasGuarantor", true);
        
        // 场景1：贷款审批
        String loanApprovalExpr = "${applicantAge} >= 18 && ${applicantSalary} > 5000 && " +
                "((${loanAmount} <= 100000 && ${creditScore} >= 650) || ${hasGuarantor})";
        assertTrue(QLExpressUtil.executeBoolean(loanApprovalExpr, flowVars));
        
        // 场景2：贷款拒绝
        flowVars.put("creditScore", 620);
        flowVars.put("hasGuarantor", false);
        assertFalse(QLExpressUtil.executeBoolean(loanApprovalExpr, flowVars));
    }
} 