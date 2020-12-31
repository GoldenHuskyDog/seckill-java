package com.seckilljava.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * @author husky
 * @version 1.0
 * @date 2020/12/22 15:14
 */
public class RandomUtil {
    /**
     * 生成随机订单号：当前年月日时分秒+五位随机数
     * @return
     */
    public static Long getOrderId() {

        SimpleDateFormat simpleDateFormat;

        simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

        Date date = new Date();

        String str = simpleDateFormat.format(date);

        Random random = new Random();

        int rannum = (int) (random.nextDouble() * (99999 - 10000 + 1)) + 10000;// 获取5位随机数

        return Long.valueOf(rannum + str);// 当前时间
    }

}
