package com.bestom.stresstest.util;

import java.util.UUID;

public class UUIDUtil {

    public static String getUUID(){

        UUID uuid = UUID.randomUUID();
        System.out.println (uuid);
        String str = uuid.toString();
        // 去掉"-"符号
        String temp = str.substring(0, 8) + str.substring(9, 13) + str.substring(14, 18) + str.substring(19, 23) + str.substring(24);
        return temp;
    }


}
