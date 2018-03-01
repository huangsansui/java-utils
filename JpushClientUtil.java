package com.gohnstudio.service.core.util;


import cn.jiguang.common.ClientConfig;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;
import com.gohnstudio.service.core.service.Impl.DpServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

import static org.bouncycastle.asn1.x500.style.RFC4519Style.cn;

public class JpushClientUtil {

    private static final Logger logger = LoggerFactory.getLogger(JpushClientUtil.class);

    private final static String appKey = "0898e3d96a86ec93340a1a51";

    private final static String masterSecret = "36fa22361eb9d3cc9faa2e16";

    // private static JPushClient jPushClient = new JPushClient(masterSecret,appKey);

    private final static String vip_appKey = "9310e6483f762b34f59e6edc";

    private final static String vip_masterSecret = "161f3797040530bd2898133e";

    /**
     * 推送给设备标识参数的用户
     * @param registrationId 设备标识
     * @param notification_title 通知内容标题
     * @param msg_title 消息内容标题
     * @param msg_content 消息内容
     * @param extrasparam 扩展字段
     * @return 0推送失败，1推送成功
     */
    /*public static int sendToRegistrationId( String registrationId,String notification_title, String msg_title, String msg_content, String extrasparam) {
        int result = 0;
        try {
            PushPayload pushPayload= JpushClientUtil.buildPushObject_all_registrationId_alertWithTitle(registrationId,notification_title,msg_title,msg_content,extrasparam);
            System.out.println(pushPayload);
            PushResult pushResult=jPushClient.sendPush(pushPayload);
            System.out.println(pushResult);
            if(pushResult.getResponseCode()==200){
                result=1;
            }
        } catch (APIConnectionException e) {
            e.printStackTrace();

        } catch (APIRequestException e) {
            e.printStackTrace();
        }

        return result;
    }
*/
    /**
     * 发送给所有安卓用户
     * @param notification_title 通知内容标题
     * @param msg_title 消息内容标题
     * @param msg_content 消息内容
     * @param extrasparam 扩展字段
     * @return 0推送失败，1推送成功
     */
    // public static int sendToAllAndroid( String notification_title, String msg_title, String msg_content, String extrasparam) {
    //     int result = 0;
    //     try {
    //         PushPayload pushPayload= JpushClientUtil.buildPushObject_android_all_alertWithTitle(notification_title,msg_title,msg_content,extrasparam);
    //         System.out.println(pushPayload);
    //         PushResult pushResult=jPushClient.sendPush(pushPayload);
    //         System.out.println(pushResult);
    //         if(pushResult.getResponseCode()==200){
    //             result=1;
    //         }
    //     } catch (Exception e) {
    //
    //         e.printStackTrace();
    //     }
    //
    //     return result;
    // }

    /**
     * 发送给所有IOS用户
     * @param notification_title 通知内容标题
     * @param msg_title 消息内容标题
     * @param msg_content 消息内容
     * @param extrasparam 扩展字段
     * @return 0推送失败，1推送成功
     */
    // public static int sendToAllIos(String notification_title, String msg_title, String msg_content, String extrasparam) {
    //     int result = 0;
    //     try {
    //         PushPayload pushPayload= JpushClientUtil.buildPushObject_ios_all_alertWithTitle(notification_title,msg_title,msg_content,extrasparam);
    //         System.out.println("ios推送："+pushPayload);
    //         PushResult pushResult=jPushClient.sendPush(pushPayload);
    //         System.out.println(pushResult);
    //         if(pushResult.getResponseCode()==200){
    //             result=1;
    //         }
    //     } catch (Exception e) {
    //
    //         e.printStackTrace();
    //     }
    //
    //     return result;
    // }

    /**
     * 发送给所有用户
     * @param notification_title 通知内容标题
     * @param noticeType
     * @return 0推送失败，1推送成功
     */
/*    public static int sendToAll( String notification_title, String msg_title, String msg_content, String extrasparam) {
        int result = 0;
        try {
            PushPayload pushPayload= JpushClientUtil.buildPushObject_android_and_ios(notification_title,msg_title,msg_content,extrasparam);
            System.out.println(pushPayload);
            PushResult pushResult=jPushClient.sendPush(pushPayload);
            System.out.println(pushResult);
            if(pushResult.getResponseCode()==200){
                result=1;
            }
        } catch (Exception e) {

            e.printStackTrace();
        }

        return result;
    }*/



    public static PushPayload buildPushObject_android_and_ios(String notification_title, Integer noticeType) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.android_ios())
                .setAudience(Audience.all())
                .setNotification(Notification.newBuilder()
                        .setAlert(notification_title)
                        .addPlatformNotification(AndroidNotification.newBuilder()
                                .setAlert(notification_title)
                                //此字段为透传字段，不会显示在通知栏。用户可以通过此字段来做一些定制需求，如特定的key传要指定跳转的页面（value）
                                .addExtra("noticeType",noticeType)
                                .build()
                        )
                        .addPlatformNotification(IosNotification.newBuilder()
                                //传一个IosAlert对象，指定apns title、title、subtitle等
                                .setAlert(notification_title)
                                //直接传alert
                                //此项是指定此推送的badge自动加1
                                .incrBadge(1)
                                //此字段的值default表示系统默认声音；传sound.caf表示此推送以项目里面打包的sound.caf声音来提醒，
                                // 如果系统没有此音频则以系统默认声音提醒；此字段如果传空字符串，iOS9及以上的系统是无声音提醒，以下的系统是默认声音
                                .setSound("default")
                                //此字段为透传字段，不会显示在通知栏。用户可以通过此字段来做一些定制需求，如特定的key传要指定跳转的页面（value）
                                 //此项说明此推送是一个background推送，想了解background看：http://docs.jpush.io/client/ios_tutorials/#ios-7-background-remote-notification
                                // .setContentAvailable(true)
                                .addExtra("noticeType",noticeType)
                                .build()
                        )
                        .build()
                )
                //Platform指定了哪些平台就会像指定平台中符合推送条件的设备进行推送。 jpush的自定义消息，
                // sdk默认不做任何处理，不会有通知提示。建议看文档http://docs.jpush.io/guideline/faq/的
                // [通知与自定义消息有什么区别？]了解通知和自定义消息的区别

                .setOptions(Options.newBuilder()
                        //此字段的值是用来指定本推送要推送的apns环境，false表示开发，true表示生产；对android和自定义消息无意义
                        .setApnsProduction(true)
                        //此字段是给开发者自己给推送编号，方便推送者分辨推送记录
                        .setSendno(1)
                        //此字段的值是用来指定本推送的离线保存时长，如果不传此字段则默认保存一天，最多指定保留十天，单位为秒
                        .build()
                )
                .build();
    }

    /*private static PushPayload buildPushObject_all_registrationId_alertWithTitle(String notification_title) {

        System.out.println("----------buildPushObject_all_all_alert");
        //创建一个IosAlert对象，可指定APNs的alert、title等字段
        //IosAlert iosAlert =  IosAlert.newBuilder().setTitleAndBody("title", "alert body").build();

        return PushPayload.newBuilder()
                //指定要推送的平台，all代表当前应用配置了的所有平台，也可以传android等具体平台
                .setPlatform(Platform.all())
                //指定推送的接收对象，all代表所有人，也可以指定已经设置成功的tag或alias或该应应用客户端调用接口获取到的registration id
                .setAudience(Audience.all())
                //jpush的通知，android的由jpush直接下发，iOS的由apns服务器下发，Winphone的由mpns下发
                .setNotification(Notification.newBuilder()
                        //指定当前推送的android通知
                        .addPlatformNotification(AndroidNotification.newBuilder()

                                .setAlert(notification_title)
                                //此字段为透传字段，不会显示在通知栏。用户可以通过此字段来做一些定制需求，如特定的key传要指定跳转的页面（value）

                                .build())
                        //指定当前推送的iOS通知
                        .addPlatformNotification(IosNotification.newBuilder()
                                //传一个IosAlert对象，指定apns title、title、subtitle等
                                .setAlert(notification_title)
                                //直接传alert
                                //此项是指定此推送的badge自动加1
                                .incrBadge(1)
                                //此字段的值default表示系统默认声音；传sound.caf表示此推送以项目里面打包的sound.caf声音来提醒，
                                // 如果系统没有此音频则以系统默认声音提醒；此字段如果传空字符串，iOS9及以上的系统是无声音提醒，以下的系统是默认声音
                                .setSound("sound.caf")
                                //此字段为透传字段，不会显示在通知栏。用户可以通过此字段来做一些定制需求，如特定的key传要指定跳转的页面（value）
                                //此项说明此推送是一个background推送，想了解background看：http://docs.jpush.io/client/ios_tutorials/#ios-7-background-remote-notification
                                //取消此注释，消息推送时ios将无法在锁屏情况接收
                                // .setContentAvailable(true)

                                .build())


                        .build())
                //Platform指定了哪些平台就会像指定平台中符合推送条件的设备进行推送。 jpush的自定义消息，
                // sdk默认不做任何处理，不会有通知提示。建议看文档http://docs.jpush.io/guideline/faq/的
                // [通知与自定义消息有什么区别？]了解通知和自定义消息的区别

                .setOptions(Options.newBuilder()
                        //此字段的值是用来指定本推送要推送的apns环境，false表示开发，true表示生产；对android和自定义消息无意义
                        .setApnsProduction(false)
                        //此字段是给开发者自己给推送编号，方便推送者分辨推送记录
                        .setSendno(1)
                        //此字段的值是用来指定本推送的离线保存时长，如果不传此字段则默认保存一天，最多指定保留十天；
                        .setTimeToLive(86400)

                        .build())

                .build();

    }*/

    private static PushPayload buildPushObject_android_all_alertWithTitle(String notification_title, String msg_title, String msg_content, String extrasparam) {
        System.out.println("----------buildPushObject_android_registrationId_alertWithTitle");
        return PushPayload.newBuilder()
                //指定要推送的平台，all代表当前应用配置了的所有平台，也可以传android等具体平台
                .setPlatform(Platform.android())
                //指定推送的接收对象，all代表所有人，也可以指定已经设置成功的tag或alias或该应应用客户端调用接口获取到的registration id
                .setAudience(Audience.all())
                //jpush的通知，android的由jpush直接下发，iOS的由apns服务器下发，Winphone的由mpns下发
                .setNotification(Notification.newBuilder()
                        //指定当前推送的android通知
                        .addPlatformNotification(AndroidNotification.newBuilder()
                                .setAlert(notification_title)
                                .setTitle(notification_title)
                                //此字段为透传字段，不会显示在通知栏。用户可以通过此字段来做一些定制需求，如特定的key传要指定跳转的页面（value）
                                .addExtra("androidNotification extras key",extrasparam)
                                .build())
                        .build()
                )
                //Platform指定了哪些平台就会像指定平台中符合推送条件的设备进行推送。 jpush的自定义消息，
                // sdk默认不做任何处理，不会有通知提示。建议看文档http://docs.jpush.io/guideline/faq/的
                // [通知与自定义消息有什么区别？]了解通知和自定义消息的区别
                .setMessage(Message.newBuilder()
                        .setMsgContent(msg_content)
                        .setTitle(msg_title)
                        .addExtra("message extras key",extrasparam)
                        .build())

                .setOptions(Options.newBuilder()
                        //此字段的值是用来指定本推送要推送的apns环境，false表示开发，true表示生产；对android和自定义消息无意义
                        .setApnsProduction(false)
                        //此字段是给开发者自己给推送编号，方便推送者分辨推送记录
                        .setSendno(1)
                        //此字段的值是用来指定本推送的离线保存时长，如果不传此字段则默认保存一天，最多指定保留十天，单位为秒
                        .setTimeToLive(86400)
                        .build())
                .build();
    }

    private static PushPayload buildPushObject_ios_all_alertWithTitle( String notification_title, String msg_title, String msg_content, String extrasparam) {
        System.out.println("----------buildPushObject_ios_registrationId_alertWithTitle");
        return PushPayload.newBuilder()
                //指定要推送的平台，all代表当前应用配置了的所有平台，也可以传android等具体平台
                .setPlatform(Platform.ios())
                //指定推送的接收对象，all代表所有人，也可以指定已经设置成功的tag或alias或该应应用客户端调用接口获取到的registration id
                .setAudience(Audience.all())
                //jpush的通知，android的由jpush直接下发，iOS的由apns服务器下发，Winphone的由mpns下发
                .setNotification(Notification.newBuilder()
                        //指定当前推送的android通知
                        .addPlatformNotification(IosNotification.newBuilder()
                                //传一个IosAlert对象，指定apns title、title、subtitle等
                                .setAlert(notification_title)
                                //直接传alert
                                //此项是指定此推送的badge自动加1
                                .incrBadge(1)
                                //此字段的值default表示系统默认声音；传sound.caf表示此推送以项目里面打包的sound.caf声音来提醒，
                                // 如果系统没有此音频则以系统默认声音提醒；此字段如果传空字符串，iOS9及以上的系统是无声音提醒，以下的系统是默认声音
                                .setSound("sound.caf")
                                //此字段为透传字段，不会显示在通知栏。用户可以通过此字段来做一些定制需求，如特定的key传要指定跳转的页面（value）
                                .addExtra("iosNotification extras key",extrasparam)
                                //此项说明此推送是一个background推送，想了解background看：http://docs.jpush.io/client/ios_tutorials/#ios-7-background-remote-notification
                                // .setContentAvailable(true)

                                .build())
                        .build()
                )
                //Platform指定了哪些平台就会像指定平台中符合推送条件的设备进行推送。 jpush的自定义消息，
                // sdk默认不做任何处理，不会有通知提示。建议看文档http://docs.jpush.io/guideline/faq/的
                // [通知与自定义消息有什么区别？]了解通知和自定义消息的区别
                .setMessage(Message.newBuilder()
                        .setMsgContent(msg_content)
                        .setTitle(msg_title)
                        .addExtra("message extras key",extrasparam)
                        .build())

                .setOptions(Options.newBuilder()
                        //此字段的值是用来指定本推送要推送的apns环境，false表示开发，true表示生产；对android和自定义消息无意义
                        .setApnsProduction(false)
                        //此字段是给开发者自己给推送编号，方便推送者分辨推送记录
                        .setSendno(1)
                        //此字段的值是用来指定本推送的离线保存时长，如果不传此字段则默认保存一天，最多指定保留十天，单位为秒
                        .setTimeToLive(86400)
                        .build())
                .build();
    }

    /*public static void main(String[] args){
        if(JpushClientUtil.sendToAllIos("testIos","testIos","this is a ios Dev test","")==1){
            System.out.println("success");
        }
    }*/

    /**
     * 构建极光推送所需要的PushPayload对象
     *
     * @param alias
     * @param alert
     * @return
     */
    public static PushPayload buildPushObject_android_ios_alias_alert(String alias, String alert) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.android_ios())
                .setAudience(Audience.alias(alias))
                .setNotification(Notification.newBuilder()
                        .addPlatformNotification(AndroidNotification.newBuilder()
                                .setAlert(alert)

                                .build())
                        .addPlatformNotification(IosNotification.newBuilder()
                                .setAlert(alert)
                                .setSound("XXX")
                                .build())
                        .build())
                .setOptions(Options.newBuilder()
                        .setApnsProduction(false)//true-推送生产环境 false-推送开发环境（测试使用参数）
                        .setTimeToLive(90)//消息在JPush服务器的失效时间（测试使用参数）
                        .build())
                .build();
    }

    public static PushPayload buildPushObject_android_alias_alert(String alias, String alert, Integer noticeType) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.android())
                .setAudience(Audience.alias(alias))
                .setNotification(Notification.newBuilder()
                        .addPlatformNotification(AndroidNotification.newBuilder()
                                .setAlert(alert)
                                .addExtra("noticeType",noticeType)
                                .build())
                        .build())
                .setOptions(Options.newBuilder()
                        .setApnsProduction(true)//true-推送生产环境 false-推送开发环境（测试使用参数）
                        .build())

                .build();
    }

    public static PushPayload buildPushObject_ios_alias_alert(String alias, String alert, Integer noticeType) {
        if (noticeType != 1){
            return PushPayload.newBuilder()
                    .setPlatform(Platform.ios())
                    .setAudience(Audience.alias(alias))
                    .setNotification(Notification.newBuilder()
                            .addPlatformNotification(IosNotification.newBuilder()
                                    .setAlert(alert)
                                    .addExtra("noticeType",noticeType)
                                    .setSound("default")
                                    .build())
                            .build())
                    .setOptions(Options.newBuilder()
                            .setApnsProduction(false)//true-推送生产环境 false-推送开发环境（测试使用参数）
                            .build())
                    .build();
        }else {
            return PushPayload.newBuilder()
                    .setPlatform(Platform.ios())
                    .setAudience(Audience.alias(alias))
                    .setNotification(Notification.newBuilder()
                            .addPlatformNotification(IosNotification.newBuilder()
                                    .setAlert(alert)
                                    .addExtra("noticeType",noticeType)
                                    .setSound("sound.caf")
                                    .build())
                            .build())
                    .setOptions(Options.newBuilder()
                            .setApnsProduction(false)//true-推送生产环境 false-推送开发环境（测试使用参数）
                            .build())
                    .build();
        }

    }

    /**
     * 极光推送方法(采用java SDK)
     *
     *
     * @param type
     * @param alias
     * @param alert
     * @return PushResult
     */
    @Async
    public static PushResult push(String type, String alias, String alert, Integer noticeType,Integer appType) {
        logger.info("推送开始："+alert);
        ClientConfig clientConfig = ClientConfig.getInstance();
        JPushClient jpushClient = null;
        if(appType == 0){
            jpushClient = new JPushClient(vip_masterSecret, vip_appKey, null, clientConfig);
        }else {
            jpushClient = new JPushClient(masterSecret, appKey, null, clientConfig);
        }
        PushPayload payload = null;
        PushPayload payloadAndroid = null;
        PushPayload payloadIos = null;
        try {
            if (type.equals("all")){
                payload = buildPushObject_android_and_ios(alert,noticeType);
                logger.info("推送全部："+payload);
                return jpushClient.sendPush(payload);
            }else {
                payloadAndroid = buildPushObject_android_alias_alert(alias, alert, noticeType);
                jpushClient.sendPush(payloadAndroid);
                logger.info("推送安卓："+payloadAndroid);
                payloadIos = buildPushObject_ios_alias_alert(alias, alert, noticeType);
                logger.info("推送ios："+payloadIos);
                return jpushClient.sendPush(payloadIos);
            }
        } catch (APIConnectionException e) {

            return null;
        } catch (APIRequestException e) {

            return null;
        }
    }
}