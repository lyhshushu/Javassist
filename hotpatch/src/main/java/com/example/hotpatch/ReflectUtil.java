package com.example.hotpatch;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;

/**
 * @author 4399lyh
 */
public class ReflectUtil {
    /**
     * @param cl
     * @param filedName
     * @param object
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException 通过反射获取对象属性值
     */
    public static Object getField(Class<?> cl, String filedName, Object object) throws NoSuchFieldException, IllegalAccessException {
        Field field = cl.getDeclaredField(filedName);
        field.setAccessible(true);
        return field.get(object);
    }

    /**
     * @param cl
     * @param fieldName
     * @param object
     * @param value
     * @throws NoSuchFieldException
     * @throws IllegalAccessException 通过反射设置对象属性值
     */
    public static void setField(Class<?> cl, String fieldName, Object object, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = cl.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }

    /**
     *
     * @param firstArr
     * @param secondArr
     * @return newArr
     * 通过反射合并两个数组
     */
    public static Object combineArray(Object firstArr, Object secondArr) {
        int firstLength = Array.getLength(firstArr);
        int secondLength = Array.getLength(secondArr);
        int length = firstLength + secondLength;

        Class<?> componentType = firstArr.getClass().getComponentType();
        assert componentType != null;
        Object newArr = Array.newInstance(componentType, length);
        for (int i = 0; i < length; i++) {
            if (i < firstLength) {
                Array.set(newArr, i, Array.get(firstArr, i));
            } else {
                Array.set(newArr, i, Array.get(secondArr, i - firstLength));
            }
        }
        return newArr;
    }
}
