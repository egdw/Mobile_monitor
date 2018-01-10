package im.hdy.controller;

import com.alibaba.fastjson.JSON;
import im.hdy.client.SmartQQClient;
import im.hdy.config.Constant;
import im.hdy.entity.*;
import im.hdy.model.Friend;
import im.hdy.reposity.MobileReposity;
import im.hdy.utils.Constants;
import im.hdy.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.List;

/**
 * Created by hdy on 2017/9/8.
 */
@Controller
@RequestMapping("/upload")
public class UploadContoller {

    @Autowired
    private MobileReposity mobileReposity;
    @Autowired
    JavaMailSender mailSender;
    @Autowired
    private SmartQQClient smartQQClient;
    @Value("spring.mail.username")
    private String email;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public String upload(@RequestParam(required = true) String json, @RequestParam(required = true) String username, @RequestParam(required = true) String password, String qq_name, String email_input) {

        Mobile old = mobileReposity.findMobileByMobileNumber(username);
        if (old != null) {
            if (MD5Utils.MD5(username.trim() + Constants.SAILT + password.trim()).equals(old.getPassword())) {
                //说明密码相同
                Mobile mobile = JSON.parseObject(json, Mobile.class);
                old.setContactMessage(mobile.getContactMessage());
                old.setMessage(mobile.getMessage());
                Mobile save = mobileReposity.save(old);
                if (JSON.toJSONString(old).equals(json)) {
                    //如果两个相同.说明数据没有发生变化
                } else {
                    //抽取首个短信内容
                    MessageDetail detail = null;
                    String messageName = null;
                    String messageNum = null;
                    String messageTime = null;
                    String text = null;
                    if (mobile.getMessage() != null) {
                        detail = mobile.getMessage().getMessages().getFirst();
                    }
                    if (detail != null) {
                        messageName = detail.getMessageName();
                        if (messageName == null) {
                            messageName = "";
                        }
                        messageNum = detail.getMessageNum();
                        messageTime = detail.getMessageTime();
                        text = detail.getMessgaeText();
                    }

                    //抽取首个通话内容
                    ContactMessageDetail contactMessageDetail = null;
                    String date = null;
                    String duration = null;
                    String contactMessageDetailName = null;
                    String phoneNumber = null;
                    Integer type = null;
                    if (mobile.getContactMessage() != null) {
                        contactMessageDetail = mobile.getContactMessage().getMessages().getFirst();
                        date = contactMessageDetail.getDate();
                        duration = contactMessageDetail.getDuration();
                        contactMessageDetailName = contactMessageDetail.getName();
                        if (contactMessageDetailName == null) {
                            contactMessageDetailName = "";
                        }
                        phoneNumber = contactMessageDetail.getPhoneNumber();
                        type = contactMessageDetail.getType();
                    }
                    //抽取数据作为通知.发送给邮箱.这里先写死
                    if (qq_name != null && !qq_name.isEmpty()) {
                        //判断用户是否需要使用QQ推送功能.
                        try {
                            List<Friend> list =
                                    smartQQClient.getFriendList();
                            for (int i = 0; i < list.size(); i++) {
                                if (list.get(i).getNickname().equals(qq_name)) {
                                    //说明是我
                                    if (!Constant.oldSMSDate.equals(messageTime)) {
                                        smartQQClient.sendMessageToFriend(list.get(i).getUserId(), messageName + "(" + messageNum + ")于" + messageTime + ":" + text);
                                        Constant.oldSMSDate = messageTime;
                                    }
                                    if (type != null && type == 3 && !Constant.oldCallDate.equals(date)) {
                                        smartQQClient.sendMessageToFriend(list.get(i).getUserId(), contactMessageDetailName + "(" + phoneNumber + ")于" + date + "给您来电了~");
                                        Constant.oldCallDate = date;
                                    }
                                }
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    if (email_input != null && !email_input.isEmpty()) {
                        //判断用户是否需要邮箱推送功能.
                        try {
                            final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
                            final MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
                            message.setFrom(email);
                            message.setTo(email_input);


                            message.setSubject("监听器通知:");
                            if (!messageTime.equals(Constant.oldSMSDate)) {
                                message.setText(messageName + "(" + messageNum + ")于" + messageTime + ":" + text);
                                Constant.oldSMSDate = messageTime;
                            }
                            if (type != null && type == 3 && !date.equals(Constant.oldCallDate)) {
                                Constant.oldCallDate = date;
                                message.setText(contactMessageDetailName + "(" + phoneNumber + ")于" + date + "给您来电了~");
                            }
                            this.mailSender.send(mimeMessage);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                }
                return JSON.toJSONString(new Reply(200, "同步成功"));
            } else {
                return JSON.toJSONString(new Reply(500, "密码错误~"));
            }
        }
        return JSON.toJSONString(new Reply(500, "同步失败"));
    }
}
