package com.example.demo.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.util.*;

/**
 * mybatis 拦截器
 * mybatis拦截器可以了解一下：https://blog.csdn.net/moshenglv/article/details/52699976
 * 作用：为发开调试提供更为详细的sql语句打印
 * 注意：1.可同时开启spring logback日志的debug模式(mybatis xml包下执行sql打印)
 *       2.该拦截器需要数据源配置文件中注入后才生生效，如：bean.setPlugins(new Interceptor[]{mybatisInterceptor});
 * @author QuiFar
 * @version V1.0
 **/
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})})
@Slf4j
@Order(-1)
@Component // 注入该拦截器
public class MybatisInterceptor implements Interceptor {

    private boolean enableSqlLogInterceptor = true;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        long start = System.currentTimeMillis();
        log.info("进入mybatis 拦截器");
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        Object parameter = null;
        if (invocation.getArgs().length > 1) {
            parameter = invocation.getArgs()[1];
        }
        BoundSql boundSql = mappedStatement.getBoundSql(parameter);
        Configuration configuration = mappedStatement.getConfiguration();
        String sql = boundSql.getSql().replaceAll("[\\s]+", " ");
        List<String> paramList = getParamList(configuration, boundSql);
        Object proceed = invocation.proceed();
        int result = 0;
        if (proceed instanceof ArrayList) {
            ArrayList resultList = (ArrayList) proceed;
            result = resultList.size();
        }
        if (proceed instanceof Integer) {
            result = (Integer) proceed;
        }
        if (enableSqlLogInterceptor) {
            long end = System.currentTimeMillis();
            long time = end - start;
            log.info("sql={}", sql);
            log.info("result={},time={}ms, params={}", result, time, paramList);
        }
        return proceed;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }

    /**
     * 获取sql参数集合。
     *
     * @param configuration the configuration
     * @param boundSql      the bound sql
     * @return the param list
     */
    private List<String> getParamList(Configuration configuration, BoundSql boundSql) {
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        List<String> params = new ArrayList<>();
        if (parameterMappings.size() > 0 && parameterObject != null) {
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                params.add(getParameterValue(parameterObject));
            } else {
                MetaObject metaObject = configuration.newMetaObject(parameterObject);
                for (ParameterMapping parameterMapping : parameterMappings) {
                    String propertyName = parameterMapping.getProperty();
                    if (metaObject.hasGetter(propertyName)) {
                        Object obj = metaObject.getValue(propertyName);
                        params.add(getParameterValue(obj));
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        Object obj = boundSql.getAdditionalParameter(propertyName);
                        params.add(getParameterValue(obj));
                    }
                }
            }
        }
        return params;
    }

    private String getParameterValue(Object obj) {
        String value;
        if (obj instanceof String) {
            value = "'" + obj.toString() + "'";
        } else if (obj instanceof Date) {
            DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.CHINA);
            value = "'" + formatter.format(obj) + "'";
        } else {
            if (obj != null) {
                value = obj.toString();
            } else {
                value = "";
            }

        }
        return value;
    }

}
