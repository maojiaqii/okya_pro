# QLExpress表达式引擎使用说明

## 简介

QLExpress是阿里巴巴开源的一款轻量级、高性能的规则引擎，本工具类封装了基本的表达式计算功能，并扩展了一些常用操作符和函数，适用于工作流条件判断、规则计算等场景。

## 使用方法

### 基本用法

```java
// 准备变量
Map<String, Object> variables = new HashMap<>();
variables.put("amount", 1500);
variables.put("userId", "10001");
variables.put("isVip", true);

// 执行布尔表达式
boolean result = QLExpressUtil.executeBoolean("amount > 1000 && isVip", variables);

// 也支持${变量}格式的表达式
boolean result2 = QLExpressUtil.executeBoolean("${amount} > 1000 && ${isVip}", variables);
```

### 在工作流中使用

在流程服务中使用QLExpress计算条件表达式：

```java
// 在FlowProcessServiceImpl中的evaluateCondition方法
private boolean evaluateCondition(String expression, Map<String, Object> variables) {
    if (StringUtils.isBlank(expression)) {
        return true; // 空表达式默认为真
    }
    
    try {
        return QLExpressUtil.executeBoolean(expression, variables);
    } catch (Exception e) {
        log.error("评估条件表达式失败: {}, 错误: {}", expression, e.getMessage());
        return false;
    }
}
```

## 支持的表达式类型

### 表达式格式

QLExpress支持两种变量引用格式：

1. 直接引用变量名：`amount > 1000`
2. 使用${变量}占位符：`${amount} > 1000`

第二种格式在某些场景下特别有用，尤其是表达式是从外部配置中读取的情况。

### 基本运算符

- 算术运算符: `+`, `-`, `*`, `/`, `%`
- 比较运算符: `>`, `<`, `>=`, `<=`, `==`, `!=`
- 逻辑运算符: `&&`, `||`, `!`
- 三元运算符: `condition ? value1 : value2`

### 自定义运算符

- 日期比较: `dateGt`, `dateLt`, `dateGe`, `dateLe`, `dateEq`, `dateNe`
- 字符串操作: `contains`, `startsWith`, `endsWith`

### 自定义函数

#### 日期函数

- `now()`: 获取当前日期
- `parseDate(dateStr, [format])`: 解析日期字符串
- `formatDate(date, [format])`: 格式化日期

#### 字符串函数

- `isEmpty(str)`: 判断字符串是否为空
- `isNotEmpty(str)`: 判断字符串是否非空
- `length(str)`: 获取字符串长度

#### 数值函数

- `round(num, [scale])`: 四舍五入
- `ceil(num)`: 向上取整
- `floor(num)`: 向下取整

## 表达式示例

### 简单条件

```
// 直接引用变量
amount > 1000
status == 'approved'
isVip == true

// 使用${变量}格式
${amount} > 1000
${status} == 'approved'
${isVip} == true
```

### 复合条件

```
// 直接引用变量
amount > 1000 && (status == 'approved' || isVip)

// 使用${变量}格式
${amount} > 1000 && (${status} == 'approved' || ${isVip})

// 混合格式
${amount} > 1000 && status == 'approved'
```

### 使用自定义操作符

```
// 日期比较
createDate dateGt '2023-01-01'
${createDate} dateGt '2023-01-01'

// 字符串操作
email contains '@' && email endsWith '.com'
${email} contains '@' && ${email} endsWith '.com'
```

### 使用自定义函数

```
// 日期处理
parseDate(createTimeStr) dateGt parseDate('2023-01-01')
parseDate(${createTimeStr}) dateGt parseDate('2023-01-01')
formatDate(now(), 'yyyy-MM-dd') == '2023-05-15'

// 字符串处理
isEmpty(remark) || length(remark) < 10
isEmpty(${remark}) || length(${remark}) < 10

// 数值处理
round(price * 0.8, 2) < 100
round(${price} * 0.8, 2) < 100
```

### 工作流场景示例

```
// 贷款审批
applicantAge >= 18 && applicantSalary > 5000 && ((loanAmount <= 100000 && creditScore >= 650) || hasGuarantor)
${applicantAge} >= 18 && ${applicantSalary} > 5000 && ((${loanAmount} <= 100000 && ${creditScore} >= 650) || ${hasGuarantor})

// VIP客户路径判断
applicantType == 'VIP' && parseDate(applicationTime) dateGt parseDate('2023-01-01')
${applicantType} == 'VIP' && parseDate(${applicationTime}) dateGt parseDate('2023-01-01')
``` 