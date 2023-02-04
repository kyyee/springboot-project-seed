package com.kyyee.sps.common.utils;

import com.kyyee.framework.common.exception.BaseErrorCode;
import com.kyyee.framework.common.exception.BaseException;
import lombok.SneakyThrows;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class BeanCopyUtils {
    private BeanCopyUtils() {
    }

    @SneakyThrows
    public static <S, T> T convert(S source, Class<T> targetClz) {
        if (ObjectUtils.isEmpty(source)) {
            return null;
        }

        final Class<?> sourceClz = source.getClass();
        try {
            T target = targetClz.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, target);
//            BeanCopier beanCopier = BeanCopier.create(sourceClz, targetClz, false);
//            beanCopier.copy(source, target, null);
            return target;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw BaseException.of(BaseErrorCode.PARAM_SWITCH_ERROR.of());
        }
    }

    public static <S, T> List<T> convertCollection(Collection<S> sources, Class<T> targetClz) {
        if (ObjectUtils.isEmpty(sources)) {
            return Collections.emptyList();
        }
        return sources.stream().map(source -> convert(source, targetClz)).collect(Collectors.toList());
    }
}
