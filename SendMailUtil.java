package com.gohnstudio.service.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.UnsupportedEncodingException;
import java.security.Security;
import java.util.Date;
import java.util.Properties;

/**
 * Created by tw on 2017/11/2.
 */
public class SendMailUtil {
    private static final Logger logger= LoggerFactory.getLogger(AliyunOssUtil.class);
    public static final String SEND_USER = "scdg@yingougou.com";
    public static final String SEND_PWD = "PJGSI6Gum4uIiIKr";

    /**
     *
     * @param to 收件人
     * @param subject 主题
     * @param content 内容
     * @return
     */
    public static boolean send(String[] to, String subject, String content) {
        Properties prop=new Properties();
        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
        //邮箱的发送服务器地址
        prop.setProperty("mail.host","smtp.mxhichina.com" );
        prop.setProperty("mail.transport.protocol", "smtp");
        prop.setProperty("mail.smtp.socketFactory.fallback", "false");
        //邮箱发送服务器端口,这里设置为465端口
        prop.setProperty("mail.smtp.port", "465");
        prop.setProperty("mail.smtp.socketFactory.port", "465");
        prop.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
//        prop.setProperty("mail.smtp.timeout", "80000")
        prop.put("mail.smtp.auth", "true");
        try{
            //获取到邮箱会话,利用匿名内部类的方式,将发送者邮箱用户名和密码授权给jvm
            Session session=Session.getInstance(prop, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(SEND_USER, SEND_PWD);
                }
            });
//            session.setDebug(true);
//            Transport ts=session.getTransport();
//            ts.connect(SEND_USER, SEND_PWD);
            Message message=new MimeMessage(session);
            //设置自定义发件人昵称
            String nick="";
            try {
                nick= MimeUtility.encodeText("四川省达观科技有限公司");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            message.setFrom(new InternetAddress(nick+" <"+SEND_USER+">"));
            InternetAddress[] sendTo = new InternetAddress[to.length];
            for (int i = 0; i < to.length; i++)
            {
                //System.out.println("发送到:" + to[i]);
                sendTo[i] = new InternetAddress(to[i]);
            }
            message.setRecipients(Message.RecipientType.TO, sendTo);
            message.setSubject(subject);
            message.setSentDate(new Date());
            message.setContent(content+"<br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>[This e-mail is confidential and may also be privileged. If you are not the intended recipient, please notify us immediately by replying to this message and then delete it from your system. You should not copy or use it for any purpose, nor disclose its contents to any other person. Thank you.]", "text/html;charset=utf-8");
            //message.setContent(content+"<br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>[这封电子邮件是保密的，也可能是特权。如果您不是预期的收件人，请立即回复本邮件通知我们，然后从您的系统中删除它。你不应该为了任何目的而复制或使用它，也不要把它的内容透露给任何其他人。谢谢您.]", "text/html;charset=utf-8");
            //调用Transport的send方法去发送邮件
            Transport.send(message);
//            ts.sendMessage(message, message.getAllRecipients());
            return true;
        }catch (Exception e){
            logger.error("邮件发送错误==========="+e.getMessage());
        }
        return false;
    }
}
