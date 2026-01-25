package com.feirui.oss.sdk.exception;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Objects;

public interface CustomAssert<T extends Exception> {

    /**
     * 创建异常
     *
     * @return 异常信息
     */
    default T createException() {
        return null;
    }

    /**
     * 创建异常
     *
     * @param args  参数
     * @param cause 异常
     * @return 异常信息
     */
    default T createException(Object args, Throwable cause) {
        return null;
    }

    /**
     * 创建异常
     *
     * @return 异常信息
     */
    default T createException(Object data) {
        return null;
    }

    /**
     * 抛出异常
     *
     * @throws T 异常信息
     */
    default void assertToMsg() throws T {
        throw createException();
    }

    /**
     * 抛出异常
     *
     * @param obj 需要打印日志的参数对象
     * @param e   捕获异常对象
     * @throws T 抛出异常信息
     */
    default void assertToException(Object obj, Exception e) throws T {
        throw createException(obj, e);
    }

    /**
     * 抛出异常，返回前端data
     *
     * @param data 返回给前端的data字段
     * @throws T 异常信息
     */
    default void assertToData(Object data) throws T {
        throw createException(data);
    }

    /**
     * 断言对象为空，否则抛出异常
     *
     * @param object 判断对象
     * @throws T 异常信息
     */
    default void assertObjectNull(Object object) throws T {
        if (ObjectUtils.isNotEmpty(object)) {
            throw createException();
        }
    }

    /**
     * 断言对象非空，否则抛出异常
     *
     * @param object 判断对象
     * @throws T 异常信息
     */
    default void assertObjectNotNull(Object object) throws T {
        if (ObjectUtils.isEmpty(object)) {
            throw createException();
        }
    }

    /**
     * 断言集合为空，否则抛出异常
     *
     * @param collection 判断集合
     * @throws T 异常信息
     */
    default void assertCollectionEmpty(Collection<?> collection) throws T {
        if (CollectionUtils.isNotEmpty(collection)) {
            throw createException();
        }
    }

    /**
     * 断言集合非空，否则抛出异常
     *
     * @param collection 判断集合
     * @throws T 异常信息
     */
    default void assertCollectionNotEmpty(Collection<?> collection) throws T {
        if (CollectionUtils.isEmpty(collection)) {
            throw createException();
        }
    }

    /**
     * 断言字符串为空，否则抛出异常
     *
     * @param str 判断字符串
     * @throws T 异常信息
     */
    default void assertStringBlank(String str) throws T {
        if (StringUtils.isNotBlank(str)) {
            throw createException();
        }
    }

    /**
     * 断言字符串非空，否则抛出异常
     *
     * @param str 判断字符串
     * @throws T 异常信息
     */
    default void assertStringNotBlank(String str) throws T {
        if (StringUtils.isBlank(str)) {
            throw createException();
        }
    }

    /**
     * 断言表达式为真，否则抛出异常
     *
     * @param expression 布尔表达式
     * @throws T 异常信息
     */
    default void assertFalse(Boolean expression) throws T {
        if (expression) {
            throw createException();
        }
    }

    /**
     * 断言表达式为真，否则抛出异常
     *
     * @param expression 布尔表达式
     * @throws T 异常信息
     */
    default void assertTrue(Boolean expression) throws T {
        if (!expression) {
            throw createException();
        }
    }

    /**
     * 断言两个对象不相等，否则抛出异常
     *
     * @param firstObj  比较对象
     * @param secondObj 比较对象
     * @throws T 异常信息
     */
    default void assertObjectNotEqual(Object firstObj, Object secondObj) throws T {
        if (Objects.equals(firstObj, secondObj)) {
            throw createException();
        }
    }

    /**
     * 断言两个对象相等，否则抛出异常
     *
     * @param firstObj  比较对象
     * @param secondObj 比较对象
     * @throws T 异常信息
     */
    default void assertObjectEqual(Object firstObj, Object secondObj) throws T {
        if (ObjectUtils.notEqual(firstObj, secondObj)) {
            throw createException();
        }
    }

}