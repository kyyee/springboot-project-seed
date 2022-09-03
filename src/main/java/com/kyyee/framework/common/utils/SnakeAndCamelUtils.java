package com.kyyee.framework.common.utils;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import org.springframework.util.StringUtils;

public class SnakeAndCamelUtils {
    public SnakeAndCamelUtils() {
    }

    public static String convertCamelToSnake(String name) {
        if (StringUtils.isEmpty(name)) {
            return "";
        } else {
            StringBuilder result = new StringBuilder();
            result.append(name.substring(0, 1).toLowerCase());

            for (int i = 1; i < name.length(); ++i) {
                String s = name.substring(i, i + 1);
                String slc = s.toLowerCase();
                if (!s.equals(slc)) {
                    result.append("_").append(slc);
                } else {
                    result.append(s);
                }
            }

            return result.toString();
        }
    }

    public static String convertSnakeToCamel(String name) {
        if (StringUtils.isEmpty(name)) {
            return "";
        } else if (!name.contains("_")) {
            return name;
        } else {
            StringBuilder result = new StringBuilder();
            result.append(name.substring(0, 1).toLowerCase());
            boolean underscore = false;

            for (int i = 1; i < name.length(); ++i) {
                String s = name.substring(i, i + 1);
                if ("_".equals(s)) {
                    underscore = true;
                } else {
                    if (underscore) {
                        s = s.toUpperCase();
                    }

                    underscore = false;
                    result.append(s);
                }
            }

            return result.toString();
        }
    }
}
