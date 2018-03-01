package com.gohnstudio.service.core.util;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.gohnstudio.service.core.consts.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.Patterns;

import javax.servlet.http.HttpServletRequest;


public class StringUtil2 {
    /**
     * Logger for this class
     */
    private static Logger logger = LogManager.getLogger(StringUtil2.class.getName());

    public static boolean isMobile(String mobile) {
        String pMobile = "^1[0-9]{10}$";
        return Pattern.matches(pMobile, mobile);
    }

    public static boolean isMobileOrPlaneNumber(String phone) {
        String pMobile = "^((0\\d{2,3}-\\d{7,8})|(1(([34578][0-9]))\\d{8}))$|^((0\\d{2,3}\\d{7,8}))$";
        return Pattern.matches(pMobile, phone);
    }

    /**
     * 验证请求日期格式是否正确
     */
    public static boolean isDateFormat(String dateTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(dateTime);
            return false;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return true;
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
     * 是否为空
     */
    public static boolean isEmpty(Object str) {
        return (str == null || "".equals(str));
    }

    /**
     * 返回参数按字母顺序排序后的字符串
     *
     * @param params 参数列表
     * @return
     */
    public static String sortParams(Map<String, String> params) {
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);
            if (i == keys.size() - 1) {
                sb.append(key).append("=").append(value);
            } else {
                sb.append(key).append("=").append(value).append("&");
            }
        }
        return sb.toString();
    }

    /**
     * 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址;
     *
     * @param request
     * @return
     * @throws IOException
     */
    public static String getIpAddress(HttpServletRequest request) throws IOException {
        // 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址

        String ip = request.getHeader("X-Forwarded-For");
        if (logger.isInfoEnabled()) {
            logger.info("getIpAddress(HttpServletRequest) - X-Forwarded-For - String ip=" + ip);
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
                if (logger.isInfoEnabled()) {
                    logger.info("getIpAddress(HttpServletRequest) - Proxy-Client-IP - String ip=" + ip);
                }
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
                if (logger.isInfoEnabled()) {
                    logger.info("getIpAddress(HttpServletRequest) - WL-Proxy-Client-IP - String ip=" + ip);
                }
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
                if (logger.isInfoEnabled()) {
                    logger.info("getIpAddress(HttpServletRequest) - HTTP_CLIENT_IP - String ip=" + ip);
                }
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
                if (logger.isInfoEnabled()) {
                    logger.info("getIpAddress(HttpServletRequest) - HTTP_X_FORWARDED_FOR - String ip=" + ip);
                }
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
                if (logger.isInfoEnabled()) {
                    logger.info("getIpAddress(HttpServletRequest) - getRemoteAddr - String ip=" + ip);
                }
            }
        } else if (ip.length() > 15) {
            String[] ips = ip.split(",");
            for (int index = 0; index < ips.length; index++) {
                String strIp = (String) ips[index];
                if (!("unknown".equalsIgnoreCase(strIp))) {
                    ip = strIp;
                    break;
                }
            }
        }
        return ip;
    }

    /**
     * 校验时间是否超过设置时间
     * 例：startTime = 2017-03-10 14:16:29  minute=180  当前时间：2017-03-10 14:16:42  return false
     * 例：startTime = 2017-03-10 14:16:29  minute=180  当前时间：2017-03-10 14:17:34  return true
     *
     * @param startTime 开始时间
     * @param second    设置时间(秒)
     * @return
     */
    public static int verify(String startTime, int second) {
        if (StringUtil2.isEmpty(startTime)) {
            return -1;
        }
        Date start = new Date();
        Date end = formatDate(startTime, Constants.DATE_TIME_FORMAT);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(end);
        calendar.add(Calendar.SECOND, second);
        end = calendar.getTime();
        return (int) ((end.getTime() - start.getTime()) / 1000);
    }

    /**
     * 产生随机的六位数
     *
     * @return
     */
    public static String getSix() {
        Random random = new Random();
        String result = "";
        for (int i = 0; i < 6; i++) {
            result += random.nextInt(10);
        }
        return result;
    }

    /*
         * 将时间戳转换为时间
         */
    public static Date stampToDate(String s) {
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(Long.parseLong(s));
        return date;
    }

    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    public static String getBill(Date now) {
        String dateToString = DateUtil.dateToString(now, "yyyyMMddHHmmss");
        String randomNum = StringUtil.getRandomNum(6);
        return dateToString + randomNum;
    }
}
