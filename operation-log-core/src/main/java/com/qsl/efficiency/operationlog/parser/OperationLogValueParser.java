package com.qsl.efficiency.operationlog.parser;

import com.qsl.efficiency.operationlog.service.IFunctionService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.expression.EvaluationContext;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 解析需要存储的日志里面的SpeEL表达式
 *
 * @author DanielQSL
 * @date 2022/5/25
 */
public class OperationLogValueParser implements BeanFactoryAware {

    protected BeanFactory beanFactory;

    private final OperationLogExpressionEvaluator expressionEvaluator = new OperationLogExpressionEvaluator();

    private IFunctionService functionService;

    /**
     * 正则匹配 e.g. {{#order}}
     */
    private static final Pattern pattern = Pattern.compile("\\{\\s*(\\w*)\\s*\\{(.*?)}}");


    public Map<String, String> processTemplate(Collection<String> templates, Object ret,
                                               Class<?> targetClass, Method method, Object[] args, String errorMsg,
                                               Map<String, String> beforeFunctionNameAndReturnMap) {
        Map<String, String> expressionValues = new HashMap<>();
        EvaluationContext evaluationContext = expressionEvaluator.createEvaluationContext(method, args, targetClass, ret, errorMsg, beanFactory);

        for (String expressionTemplate : templates) {
            if (expressionTemplate.contains("}}")) {
                Matcher matcher = pattern.matcher(expressionTemplate);
                StringBuffer parsedStr = new StringBuffer();
                while (matcher.find()) {
                    String expression = matcher.group(2);
                    AnnotatedElementKey annotatedElementKey = new AnnotatedElementKey(method, targetClass);
                    String value = expressionEvaluator.parseExpression(expression, annotatedElementKey, evaluationContext);
                    // 自定义函数值
                    String functionReturnValue = getFunctionReturnValue(beforeFunctionNameAndReturnMap, value, matcher.group(1));
                    matcher.appendReplacement(parsedStr, (functionReturnValue == null) ? "" : functionReturnValue);
                }
                matcher.appendTail(parsedStr);
                expressionValues.put(expressionTemplate, parsedStr.toString());
            } else {
                expressionValues.put(expressionTemplate, expressionTemplate);
            }

        }
        return expressionValues;
    }

    public Map<String, String> processBeforeExecuteFunctionTemplate(Collection<String> templates, Class<?> targetClass, Method method, Object[] args) {
        Map<String, String> functionNameAndReturnValueMap = new HashMap<>();
        EvaluationContext evaluationContext = expressionEvaluator.createEvaluationContext(method, args, targetClass, null, null, beanFactory);

        for (String expressionTemplate : templates) {
            if (expressionTemplate.contains("{")) {
                Matcher matcher = pattern.matcher(expressionTemplate);
                while (matcher.find()) {
                    String expression = matcher.group(2);
                    if (expression.contains("#_ret") || expression.contains("#_errorMsg")) {
                        continue;
                    }
                    AnnotatedElementKey annotatedElementKey = new AnnotatedElementKey(method, targetClass);
                    String functionName = matcher.group(1);
                    if (functionService.beforeFunction(functionName)) {
                        String value = expressionEvaluator.parseExpression(expression, annotatedElementKey, evaluationContext);
                        String functionReturnValue = getFunctionReturnValue(null, value, functionName);
                        functionNameAndReturnValueMap.put(functionName, functionReturnValue);
                    }
                }
            }
        }
        return functionNameAndReturnValueMap;
    }

    private String getFunctionReturnValue(Map<String, String> beforeFunctionNameAndReturnMap, String value, String functionName) {
        String functionReturnValue = "";
        if (beforeFunctionNameAndReturnMap != null) {
            functionReturnValue = beforeFunctionNameAndReturnMap.get(functionName);
        }
        if (StringUtils.isEmpty(functionReturnValue)) {
            functionReturnValue = functionService.apply(functionName, value);
        }
        return functionReturnValue;
    }

    public void setFunctionService(IFunctionService functionService) {
        this.functionService = functionService;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

}
