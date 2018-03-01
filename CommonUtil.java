package com.gohnstudio.service.core.util;

import com.gohnstudio.service.core.exception.AMapException;

/**
 * 公共方法
 * Created by tw on 2017/8/3.
 */
public class CommonUtil {
    //高德根据两点的经纬度算两点之间的距离AMapUtils.calculateLineDistance(LatLng startLatlng, LatLng endLatlng)
    public static float calculateLineDistance(Double startLat, Double startLng, Double endLat, Double endLng) {
        if (startLng != null && startLat != null && endLng != null && endLat != null) {
            // double var2 = 0.01745329251994329D;
            startLng *= 0.01745329251994329D;
            startLat *= 0.01745329251994329D;
            endLng *= 0.01745329251994329D;
            endLat *= 0.01745329251994329D;
            double var12 = Math.sin(startLng);
            double var14 = Math.sin(startLat);
            double var16 = Math.cos(startLng);
            double var18 = Math.cos(startLat);
            double var20 = Math.sin(endLng);
            double var22 = Math.sin(endLat);
            double var24 = Math.cos(endLng);
            double var26 = Math.cos(endLat);
            double[] var28 = new double[3];
            double[] var29 = new double[3];
            var28[0] = var18 * var16;
            var28[1] = var18 * var12;
            var28[2] = var14;
            var29[0] = var26 * var24;
            var29[1] = var26 * var20;
            var29[2] = var22;
            double var30 = Math.sqrt((var28[0] - var29[0]) * (var28[0] - var29[0]) + (var28[1] - var29[1]) * (var28[1] - var29[1]) + (var28[2] - var29[2]) * (var28[2] - var29[2]));
            return (float) (Math.asin(var30 / 2.0D) * 1.27420015798544E7D);
        } else {
            try {
                throw new AMapException("非法坐标值");
            } catch (AMapException var32) {
                var32.printStackTrace();
                return 0.0F;
            }
        }
    }

    /**
     * 获取全局唯一标识符，它是由网卡上的标识数字(每个网卡都有唯一的标识号)以及 CPU 时钟的唯一数字生成的的一个 16 字节的二进制值
     * @return MD5是32位0-9 a-f组成
     */
    public static String getGUID(){
        return java.util.UUID.randomUUID().toString().replace("-","");
    }
    //邮箱格式验证
    public static boolean isEmail(String email) {
        String regex = "^[A-Za-z0-9]{1,40}@[A-Za-z0-9]{1,40}\\.[A-Za-z]{2,3}$";
        return email.matches(regex);
    }
    public static void main(String[] args) {
        System.out.print( calculateLineDistance(104.055732,30.550799,104.065686,30.6576));
    }
    //1)将长网址md5生成32位签名串,分为4段, 每段8个字节;
    //2)对这四段循环处理, 取8个字节, 将他看成16进制串与0x3fffffff(30位1)与操作, 即超过30位的忽略处理;
    //3)这30位分成6段, 每5位的数字作为字母表的索引取得特定字符, 依次进行获得6位字符串;
    //4)总的md5串可以获得4个6位串; 取里面的任意一个就可作为这个长url的短url地址;
    //    这种算法,虽然会生成4个,但是仍然存在重复几率,下面的算法一和三,就是这种的实现.
    public static String[] shortUrl(String url) {
        // 可以自定义生成 MD5 加密字符传前的混合 KEY
        String key = "ygg" ;
        // 要使用生成 URL 的字符
        String[] chars = new String[] { "a" , "b" , "c" , "d" , "e" , "f" , "g" , "h" ,
                "i" , "j" , "k" , "l" , "m" , "n" , "o" , "p" , "q" , "r" , "s" , "t" ,
                "u" , "v" , "w" , "x" , "y" , "z" , "0" , "1" , "2" , "3" , "4" , "5" ,
                "6" , "7" , "8" , "9" , "A" , "B" , "C" , "D" , "E" , "F" , "G" , "H" ,
                "I" , "J" , "K" , "L" , "M" , "N" , "O" , "P" , "Q" , "R" , "S" , "T" ,
                "U" , "V" , "W" , "X" , "Y" , "Z"
        };
        // 对传入网址进行 MD5 加密
        String sMD5EncryptResult = ( new CMyEncrypt()).md5(key + url);
        String hex = sMD5EncryptResult;
        String[] resUrl = new String[4];
        for ( int i = 0; i < 4; i++) {
            // 把加密字符按照 8 位一组 16 进制与 0x3FFFFFFF 进行位与运算
            String sTempSubString = hex.substring(i * 8, i * 8 +8);
            // 这里需要使用 long 型来转换，因为 Inteper .parseInt() 只能处理 31 位 , 首位为符号位 , 如果不用 long ，则会越界
            long lHexLong = 0x3FFFFFFF & Long.parseLong (sTempSubString, 16);
            String outChars = "" ;
            for ( int j = 0; j < 6; j++) {
                // 把得到的值与 0x0000003D 进行位与运算，取得字符数组 chars 索引
                long index = 0x0000003D & lHexLong;
                // 把取得的字符相加
                outChars += chars[( int ) index];
                // 每次循环按位右移 5 位
                lHexLong = lHexLong >> 5;
            }
            // 把字符串存入对应索引的输出数组
            resUrl[i] = outChars;
        }
        return resUrl;
    }
    public static int StringCharLength(String value) {
        int valueLength = 0;
        String chinese = "[\u4e00-\u9fa5]";
        for (int i = 0; i < value.length(); i++) {
            String temp = value.substring(i, i + 1);
            if (temp.matches(chinese)) {
                valueLength += 2;
            } else {
                valueLength += 1;
            }
        }
        return valueLength;
    }
}
