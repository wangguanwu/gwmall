package com.gw.mall.service.snowflake;

public class BitDemo {
    public static void main(String[] args) {

        //第一部分时间戳
        Long a = System.currentTimeMillis();
        System.out.println("当前时间：" + a);
        String aa = Long.toBinaryString(a);
        System.out.println(aa);
        System.out.println("时间戳位数" + aa.length());
        while (aa.length() < 64) {
            aa = "0" + aa;
        }
        System.out.println("//时间戳------------------------------------//");
        System.out.println(aa);


        //第二部分机房区分
        Long b = 5L;
        String bb = Long.toBinaryString(b);
        while (bb.length() < 64) {
            bb = "0" + bb;
        }
        System.out.println("//数据标识------------------------------------//");
        System.out.println(bb);

        //机器区分
        Long c = 6L;
        String cc = Long.toBinaryString(c);
        while (cc.length() < 64) {
            cc = "0" + cc;
        }
        System.out.println("//机器标识------------------------------------//");
        System.out.println(cc);


        //第三部分递增数
        Long d = 1L;
        String dd = Long.toBinaryString(d);
        while (dd.length() < 64) {
            dd = "0" + dd;
        }
        System.out.println("//自增数------------------------------------//");
        System.out.println(dd);

    }
}