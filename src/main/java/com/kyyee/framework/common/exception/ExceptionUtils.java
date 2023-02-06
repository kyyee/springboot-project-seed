package com.kyyee.framework.common.exception;
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import com.kyyee.framework.common.utils.FastStringWriter;

import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;

public class ExceptionUtils {
    public ExceptionUtils() {
    }

    public static RuntimeException unchecked(Throwable e) {
        if (e instanceof Error error) {
            throw  error;
        } else if (!(e instanceof IllegalAccessException) && !(e instanceof IllegalArgumentException) && !(e instanceof NoSuchMethodException)) {
            if (e instanceof InvocationTargetException ite) {
                return new RuntimeException(ite.getTargetException());
            } else if (e instanceof RuntimeException re) {
                return  re;
            } else {
                if (e instanceof InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
                return runtime(e);
            }
        } else {
            return new IllegalArgumentException(e);
        }
    }

    private static <T extends Throwable> T runtime(Throwable throwable) throws T {
        throw (T) throwable;
    }

    public static Throwable unwrap(Throwable wrapped) {
        Throwable unwrapped = wrapped;

        while (true) {
            while (!(unwrapped instanceof InvocationTargetException ite)) {
                if (unwrapped instanceof UndeclaredThrowableException ute) {
                    unwrapped = ute.getUndeclaredThrowable();
                } else {
                    return unwrapped;
                }
            }

            unwrapped = ite.getTargetException();
        }
    }

    public static String getStackTraceAsString(Throwable ex) {
        FastStringWriter stringWriter = new FastStringWriter();
        ex.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }
}
