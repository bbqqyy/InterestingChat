package com.bqy.common.common.utils;

import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.util.Optional;

public class SpElUtil {
    private static final ExpressionParser EXPRESSION_PARSER = new SpelExpressionParser();
    private static final DefaultParameterNameDiscoverer PARAMETER_NAME_DISCOVERER = new DefaultParameterNameDiscoverer();
    public static String getSpEl(Method method){
        return method.getDeclaringClass()+"#"+method.getName();
    }

    public static String parseSpEl(Method method, Object[] args, String spEl) {
        String[] parameterNames = Optional.ofNullable(PARAMETER_NAME_DISCOVERER.getParameterNames(method)).orElse(new String[]{});
        EvaluationContext context = new StandardEvaluationContext();//el解析需要的上下文对象
        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);//所有参数都作为原材料扔进去
        }
        Expression expression = EXPRESSION_PARSER.parseExpression(spEl);
        return expression.getValue(context, String.class);


    }
}
