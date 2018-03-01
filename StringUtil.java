package com.gohnstudio.service.core.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.gohnstudio.service.core.consts.Constants;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * 文本工具类
 *
 * @author Gostoms
 *         2012/07/16
 */
public class StringUtil {
    private static Map<String, Pattern> CUSTOM_FMT_INDEX = new HashMap<String, Pattern>();

    /**
     * 文本格式化接口
     */
    public interface IFormatQueue {
        String format(int pos, Matcher matcher);
    }

    /**
     * 是否为空文本(null或者空文本)
     *
     * @param str
     * @return
     */
    public static boolean isNull(String str) {
        return !(str != null && str.length() > 0);
    }

    /**
     * 是否不为空文本(非null非空文本)
     *
     * @param str
     * @return
     */
    public static boolean isNotNull(String str) {
        return !isNull(str);
    }

    /**
     * 是否不为空文本(非null非空文本)
     *
     * @param str
     * @return
     */
    public static String isNotNullStr(String str) {
        return isNull(str) ? "" : str.trim();
    }

    /**
     * 文本转长整数
     *
     * @param string 文本
     * @return
     */
    public static Long strToLong(String string) {
        Long ret = 0l;
        try {
            ret = Long.valueOf(string);
        } catch (NumberFormatException e) {
        }
        return ret;
    }

    /**
     * 文本转浮点数
     *
     * @param string 文本
     * @return
     */
    public static Float strToFloat(String string) {
        Float ret = 0.0f;
        try {
            ret = Float.valueOf(string);
        } catch (NumberFormatException e) {
        }
        return ret;
    }

    /**
     * 文本转整数
     *
     * @param string 文本
     * @return
     */
    public static int strToInt(String string) {
        return strToLong(string).intValue();
    }

    /**
     * 文本转长短整数
     *
     * @param string 文本
     * @return
     */
    public static Short strToShort(String string) {
        return strToLong(string).shortValue();
    }

    /**
     * 自定义格式化(通过正则表达式)
     *
     * @param content     内容
     * @param searchStr   搜索用的正则表达式
     * @param formatQueue 文本格式化对象
     * @return String string = "ABCdef ggg <img id=\"1\"> HelloW<img id=\"2\">Orld";
     * String out_str = formatCustom(string, "(<img[ ]+id\\=\")([0-9]+)([^>]+>)", new IFormatQueue() {
     * public String format(int pos, Matcher matcher) {
     * return matcher.group(1) + (Integer.valueOf(matcher.group(2)) + 10) + matcher.group(3);
     * }
     * });
     * <p>
     * // >> ABCdef ggg <img id="11"> HelloW<img id="12">Orld
     */
    public static String formatCustom(String content, String searchStr, IFormatQueue formatQueue) {
        Pattern p = CUSTOM_FMT_INDEX.get(searchStr);
        if (null == p) {
            p = Pattern.compile(searchStr);
            CUSTOM_FMT_INDEX.put(searchStr, p);
        }

        int i = 0;
        StringBuffer sb = new StringBuffer();
        Matcher mc = p.matcher(content);
        while (mc.find()) {
            i++;
            mc.appendReplacement(sb, formatQueue.format(i, mc));
        }

        mc.appendTail(sb);
        mc = null;

        String ret = sb.toString();

        sb.delete(0, sb.length());
        sb = null;

        return ret;
    }

    /**
     * 格式化文本通过参数
     *
     * @param string 要格式化的文本。参数格式为 {n}，n为参数位置信息，从1开始。示例：StringUtil2.formatWithParams("{1}、Love{2} Not {3}", 5, "Boy", "Girl"); 返回内容为“5、LoveBoy Not Girl”
     * @param params 自定义数量参数或者对象数组
     * @return
     */
    public static String formatWithParams(String string, final Object... params) {
        final int count = params.length;
        return formatCustom(string, "\\{([0-9]+)\\}", new IFormatQueue() {
            public String format(int pos, Matcher matcher) {
                int cur_pos = Integer.valueOf(matcher.group(1));
                if (cur_pos > 0 && cur_pos <= count) {
                    if (null != params[cur_pos - 1]) {
                        return params[cur_pos - 1].toString();
                    } else {
                        return "";
                    }
                }
                return matcher.group();
            }
        });
    }

    /**
     * 数组转换为文本
     *
     * @param array
     * @param splitString 中间的文本分割符
     * @return
     */
    public static <T> String arrayToString(T[] array, String splitString) {
        if (null == array || array.length == 0) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        for (T item : array) {
            if (item == null) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append(splitString);
            }
            sb.append(item.toString());
        }
        return sb.toString();
    }

    /**
     * 文本转数组(目前支持 文本，长整数，浮点，双精度浮点 数)
     *
     * @param str      文本
     * @param splitStr 分割用文本
     * @param cls      类型(如 Long.class)
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] strToArray(String str, String splitStr, Class<T> cls) {
        if (null == str) {
            return null;
        }

        int type = 0;
        if (cls.equals(Integer.class)) {
            type = 1;
        } else if (cls.equals(Long.class)) {
            type = 2;
        } else if (cls.equals(Float.class)) {
            type = 3;
        } else if (cls.equals(Double.class)) {
            type = 4;
        }

        List<T> rtn_list = new ArrayList<T>();
        String[] sids;
        if (str.indexOf(splitStr) != -1) {
            List<String> l = new ArrayList<String>();
            StringTokenizer st = new StringTokenizer(str, splitStr);
            if (st.countTokens() == 0) {
                return null;
            }
            while (st.hasMoreTokens()) {
                l.add(st.nextToken());
            }
            sids = new String[l.size()];
            sids = l.toArray(sids);
            st = null;
        } else {
            sids = new String[]{str.trim()};
        }
        T data = null;
        for (String s : sids) {
            try {
                s = s.trim();
                switch (type) {
                    case 0:
                        data = (T) s;
                        break;
                    case 1:
                        data = (T) Integer.valueOf(s);
                        break;
                    case 2:
                        data = (T) Long.valueOf(s);
                        break;
                    case 3:
                        data = (T) Float.valueOf(s);
                        break;
                    case 4:
                        data = (T) Double.valueOf(s);
                        break;
                    default:
                        data = (T) s;
                        break;
                }
                rtn_list.add(data);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        if (null == rtn_list || rtn_list.size() == 0) {
            return null;
        }
        rtn_list.toArray();
        Object a = Array.newInstance(cls, rtn_list.size());
        return rtn_list.toArray((T[]) a);
    }

    /**
     * 数字到大写汉字数字映射索引
     **/
    public static Map<Integer, String> UPPER_NUMBER_INDEX = new HashMap<Integer, String>();

    private static String _upper_num_to_upper(int num) {
        String str_num = num + "";
        String ret = "";
        int len = str_num.length();
        String[] s = new String[len];
        for (int i = 0; i < len; i++) {
            s[i] = str_num.substring(i, i + 1);
        }
        if (len == 2) {
            ret = (s[0].equals("1") ? "" : _upper_num_one(strToInt(s[0]))) + _upper_num_one(0) + (s[1].equals("0") ? "" : _upper_num_one(strToInt(s[1])));
        } else if (len == 1) {
            ret = _upper_num_one(strToInt(s[0]));
        } else if (len == 3) {
            ret = _upper_num_one(strToInt(s[0])) + "百"
                    + (!s[1].equals("0") ? (_upper_num_one(strToInt(s[1])) + "十") : "")
                    + (!s[2].equals("0") ? (
                    (s[1].equals("0") ? "零" : "") + _upper_num_one(strToInt(s[2]))
            ) : "");
        }
        return ret;
    }

    private static String _upper_num_one(int num) {
        switch (num) {
            case 1:
                return "一";
            case 2:
                return "二";
            case 3:
                return "三";
            case 4:
                return "四";
            case 5:
                return "五";
            case 6:
                return "六";
            case 7:
                return "七";
            case 8:
                return "八";
            case 9:
                return "九";
            default:
                return "十";
        }
    }

    static {
        for (int i = 1; i < 999; i++) {
            UPPER_NUMBER_INDEX.put(i, _upper_num_to_upper(i));
        }
    }

    /**
     * 数字转换到大写汉字
     *
     * @param num
     * @return
     */
    public static String numToUpperNumStr(int num) {
        return UPPER_NUMBER_INDEX.get(num);
    }

    /**
     * 返回字符长度(中文长度计算)
     *
     * @param value
     * @return
     */
    public static int getChineseLength(String value) {
        if (StringUtil.isNull(value)) {
            return 0;
        }
        value = value.trim();

        float len = 0f;
        String chinese = "[\u0391-\uFFE5]";//\u4E00-\u9FBF
        //获取字段值的长度，如果含中文字符，则每个中文字符长度为1，否则为0.5
        for (int i = 0; i < value.length(); i++) {
            String temp = value.substring(i, i + 1);
            if (temp.matches(chinese)) {
                len += 1f;
            } else {
                len += 0.5;
            }
        }
        return Math.round(len);
    }

    /**
     * 压缩文本
     *
     * @param str
     * @return
     */
    public static String compress(String str) {
        if (isNull(str)) {
            return str;
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            GZIPOutputStream gzip = new GZIPOutputStream(out);
            gzip.write(str.getBytes());
            gzip.close();
            return out.toString("ISO-8859-1");
        } catch (Exception e) {
            e.printStackTrace();
            return str;
        }
    }

    /**
     * 解压缩文本
     *
     * @param str
     * @param charsetName 编码名称。如：UTF8
     * @return
     */
    public static String uncompress(String str, String charsetName) {
        if (isNull(str)) {
            return str;
        }

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ByteArrayInputStream in = new ByteArrayInputStream(str.getBytes("ISO-8859-1"));
            GZIPInputStream gunzip = new GZIPInputStream(in);
            byte[] buffer = new byte[256];
            int n;
            while ((n = gunzip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
            return out.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return str;
        }
    }

    /**
     * 解压缩文本(解压缩后的编码默认为UTF-8)
     *
     * @param str
     * @return
     */
    public static String uncompress(String str) {
        return uncompress(str, "UTF-8");
    }

    public static void main(String[] args) {
        String str = "<!--[if lte IE 8]>\n  <link rel=\"stylesheet\" href=\"../../../730-SVN/asset/style/base/kometui-ie8.css\">\n  <script src=\"../../../730-SVN/asset/script/base/respond.min.js\"></script>\n<![endif]-->\n<!--[if lte IE 7]>\n  <link rel=\"stylesheet\" href=\"../../../730-SVN/asset/style/base/kometui-ie7.css\">\n  <link rel=\"stylesheet\" href=\"../../../730-SVN/asset/style/base/font-awesome-ie7.css\">\n<![endif]-->\n";
        System.out.println("IN =========== " + str.length());
        System.out.println(str);

        str = compress(str);
        System.out.println("OUT =========== " + str.length());
        System.out.println(str);

        str = uncompress(str);
        System.out.println("UNCOMP =========== " + str.length());
        System.out.println(str);

    }

    /**
     * 日期根据格式转换
     */
    public static String dateFormat(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * 根据格式转换日期
     */
    public static Date formatDate(String date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 自动生产政策ID
     */
    public static String autoPolicyID(String name) {
        long time = new Date().getTime();
        return name + time;
    }

    /**
     * 获取不重复的政策ID
     */
    public static List<String> getNoRepeat(String name, List<String> list) {
        if (list == null) {
            list = new ArrayList<String>();
        }
        String pid = autoPolicyID(name);
        if (list.contains(pid)) {
            return getNoRepeat(name, list);
        } else {
            list.add(pid);
        }
        return list;
    }

    /**
     * 对一个字符串截取到小数点后两位
     */
    public static String formateRate(String rateStr) {
        if (rateStr.indexOf(".") != -1) {
            //获取小数点的位置
            int num = 0;
            num = rateStr.indexOf(".");
            //获取小数点后面的数字 是否有两位 不足两位补足两位
            String dianAfter = rateStr.substring(0, num + 1);
            String afterData = rateStr.replace(dianAfter, "");
            if (afterData.length() < 2) {
                afterData = afterData + "0";
            } else {
                afterData = afterData;
            }
            return rateStr.substring(0, num) + "." + afterData.substring(0, 2);
        } else {
            if (rateStr == "1") {
                return "100";
            } else {
                return rateStr;
            }
        }
    }

    /**
     * 对JSONArray分割加逗号
     *
     * @param list
     * @return
     */
    public static String converStringListToImg( List<String> list) {
       String img = null;
       for (int i = 0; i < list.size(); i++) {
           Object object = list.get(i);
           if (object != null) {
               if (i == 0) {
                   img = object.toString();
               } else {
                   img = img + "," + object.toString();
               }
           }
       }
       return img;
    }

    /**
     * 对逗号字符串转JSONArray
     *
     * @param imgs
     * @return
     */
    public static JSONArray converImgToJSONArray(String imgs) {
        JSONArray jsonArray = new JSONArray();
        if (isNotNull(imgs)) {
            String[] strings = imgs.split(",");
            for (String str : strings) {
                jsonArray.add(Constants.ALIYUNOSS + str);
            }
        }
        return jsonArray;
    }

    private static double EARTH_RADIUS = 6378.137;

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    public static String getRandomName() {
        //四位字母
        String randomCode1 = new String();
        String model = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        char[] m = model.toCharArray();
        for (int j = 0; j < 4; j++) {
            char c = m[(int) (Math.random() * 52)];
            randomCode1 = randomCode1 + c;
        }
        //四位数字
        Random random = new Random();
        String randomCode2 = new String();
        for (int i = 0; i < 4; i++) {
            randomCode2 += random.nextInt(10);
        }
        //打乱顺序
        List<String> list = Arrays.asList((randomCode1 + randomCode2).split(""));
        Collections.shuffle(list);
        String out = new String();
        for (String s : list) {
            out += s;
        }
        return out;
    }

    public static boolean validateNickName(String str){
        if(validateNoSpecialCharacters(str)){
            return validateStartAndLength(str);
        }else {
            return false;
        }
    }


    public static boolean validateNoSpecialCharacters(String str){
        String regEx="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return !m.find();
    }

    /**
     * 以英文字母或汉字开头，限4-16字符，一个汉字为2个字符
     * @param str
     * @return
     */
    public static boolean validateStartAndLength(String str) {
        char[] ta = str.toCharArray();
        int str_l = 0;
        int str_fa = ta[0];
        if ((str_fa >= 65 && str_fa <= 90) || (str_fa >= 97 && str_fa <= 122) || (str_fa > 255)) {
            for (int i = 0; i <= ta.length - 1; i++) {
                str_l++;
                if (ta[i] > 255) {
                    str_l++;
                }
            }
            if (str_l >= 4 && str_l <= 16) {
                return true;
            }
        }
        return false;
    }

    public static String getRandomNum(int length) {
        StringBuffer sb = new StringBuffer();
        Random r = new Random();
        char[] ch = "0123456789".toCharArray();
        int index, len = ch.length;
        for (int i = 0; i < length; i++) {
            index = r.nextInt(len);
            sb.append(ch[index]);
        }
        return sb.toString();
    }

    public static String getRandomNC(int length) {
        StringBuffer sb = new StringBuffer();
        Random r = new Random();
        char[] ch = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
        int index, len = ch.length;
        for (int i = 0; i < length; i++) {
            index = r.nextInt(len);
            sb.append(ch[index]);
        }
        return sb.toString();
    }

    public static String getRandomChar(int length) {
        StringBuffer sb = new StringBuffer();
        Random r = new Random();
        char[] ch = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
        int index, len = ch.length;
        for (int i = 0; i < length; i++) {
            index = r.nextInt(len);
            sb.append(ch[index]);
        }
        return sb.toString();
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
}
