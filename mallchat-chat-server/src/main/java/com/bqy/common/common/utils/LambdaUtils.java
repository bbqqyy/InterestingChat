package com.bqy.common.common.utils;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;
import lombok.SneakyThrows;
import org.apache.ibatis.reflection.property.PropertyNamer;

import java.lang.reflect.Field;

public class LambdaUtils {
    @SneakyThrows
    public static <T> Class<?> getReturnType(SFunction<T,?> cursorColumn) {
        SerializedLambda lambda = com.baomidou.mybatisplus.core.toolkit.LambdaUtils.resolve(cursorColumn);
        Class<?> aClass = lambda.getInstantiatedType();
        String fieldName = PropertyNamer.methodToProperty(lambda.getImplMethodName());
        Field field = aClass.getField(fieldName);
        field.setAccessible(true);
        return field.getType();

    }
}
