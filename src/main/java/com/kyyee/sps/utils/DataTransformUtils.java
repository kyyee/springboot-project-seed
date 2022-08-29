package com.kyyee.sps.utils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 数据转换工具类
 */
@Slf4j
public final class DataTransformUtils {

    public static final int SEPARATE_CHOOSE_FRONT_PART = 1;

    public static final int SEPARATE_CHOOSE_BACK_PART = 2;

    private DataTransformUtils() {
    }

    /**
     * 分割数组
     *
     * @param sourceDataList 原始数据
     * @param sliceLength    每片长度
     * @param <T>            泛型
     * @return 返回结果
     */
    public static <T> List<List<T>> divideList(List<T> sourceDataList, int sliceLength) {
        if (null == sourceDataList || sourceDataList.isEmpty()) {
            return new ArrayList<>(0);
        }
        List<List<T>> resultList = new ArrayList<>();
        int size = sourceDataList.size();
        if (size <= sliceLength) {
            resultList.add(sourceDataList);
            return resultList;
        }
        int times = (size % sliceLength == 0) ? size / sliceLength : size / sliceLength + 1;
        int start = 0;
        int stop = 0;
        for (int i = 1; i <= times; i++) {
            stop = (stop + sliceLength > size) ? stop + size % sliceLength : stop + sliceLength;
            List<T> subList = new ArrayList<>(sourceDataList.subList(start, stop));
            resultList.add(subList);
            start = stop;
        }
        return resultList;
    }

    /**
     * 数组转字符串（逗号分隔）
     *
     * @param dataList 源数据
     * @param <T>      泛型
     * @return 返回字符串
     */
    public static <T> String collection2String(Collection<T> dataList) {
        if (ObjectUtils.isEmpty(dataList)) {
            return "";
        }
        StringBuilder dataBuilder = new StringBuilder();
        for (T data : dataList) {
            dataBuilder.append(data).append(",");
        }
        dataBuilder.deleteCharAt(dataBuilder.length() - 1);
        return dataBuilder.toString();
    }

    /**
     * 字符串（逗号分隔）转数组
     *
     * @param source 源字符串
     * @param clz    转换的实际类型
     * @param <T>    泛型
     * @return List<T>
     */
    @SneakyThrows
    public static <T> List<T> string2List(String source, Class<T> clz) {
        if (!StringUtils.hasText(source)) {
            return new ArrayList<>(0);
        }
        List<T> resultList = new ArrayList<>();
        String[] stringArr = source.split(",");
        for (String s : stringArr) {
            T data = null;
            if (clz.equals(Integer.class)) {
                Method method = clz.getDeclaredMethod("parseInt", String.class);
                data = clz.cast(method.invoke(clz, s));
            } else if (clz.equals(Long.class)) {
                Method method = clz.getDeclaredMethod("parseLong", String.class);
                data = clz.cast(method.invoke(clz, s));
            } else if (clz.equals(String.class)) {
                data = clz.cast(s);
            } else {
                throw new RuntimeException("Invalid Class Exception");
            }

            resultList.add(data);
        }
        return resultList;
    }

    public static String separate(String source, char separator, int type) {
        try {
            if (type == SEPARATE_CHOOSE_FRONT_PART) {
                return source.substring(0, source.indexOf(separator));
            } else {
                return source.substring(source.indexOf(separator) + 1);
            }
        } catch (Exception e) {
            log.warn("separate String error, the format may be not match, source:{}, separator:{}", source, separator);
            return null;
        }
    }

}
