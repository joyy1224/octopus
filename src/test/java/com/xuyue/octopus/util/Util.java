package com.xuyue.octopus.util;


import cn.hutool.core.util.IdUtil;
import com.xuyue.octopus.infra.util.ConversionUtils;
import com.xuyue.octopus.infra.util.UrlUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class Util {

    @Test
    void contextLoads() {
        boolean validUrl = UrlUtils.checkURL("github.com/zjcscut/octopus");
        System.out.println(validUrl);
    }

    @Test
    void contextLoad1() {
        long num = 1664613928618946560L;
        String binaryStr = Long.toBinaryString(num);
        System.out.println(binaryStr);
//        1011100011001111001011000110001101111011111111111000000000000  雪花
//        1011100011001111001011000110001101111011111111111000000000000
    }

    @Test
    void conversionUtils() {
//        (long workerId, long datacenterId)
        long a = IdUtil.getSnowflake(31,31).nextId();
//        long a = 1664612067170054144L;
        String encode62 = ConversionUtils.X.encode62(a);
        System.out.println("a : " + a);
        System.out.println("a二进制： " + Long.toBinaryString(a));
        System.out.println("a二进制： " + Long.toBinaryString(a).length());
        System.out.println("encode62 :" + encode62);
        System.out.println("encode62 :"  + encode62.substring(encode62.length() - 6));
//        100100001001110011100111001101011110011101111111111100000000000000
//        1011100011001111001011011011111010001101111111111000000000000
    }




}
