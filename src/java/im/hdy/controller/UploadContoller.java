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

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public String upload(@RequestParam(required = true) String json, @RequestParam(required = true) String username, @RequestParam(required = true) String password) {

        Mobile old = mobileReposity.findMobileByMobileNumber(username);
        if (old != null) {
            //说明不是新用户
            String oldPassword = old.getPassword();
            String newPassword = MD5Utils.MD5(username.trim() + Constants.SAILT + password.trim());
            if (newPassword.equals(oldPassword)) {
                //说明密码相同
                Mobile mobile = JSON.parseObject(json, Mobile.class);
                if (JSON.toJSONString(old).equals(json)) {
                    //如果两个相同.说明数据没有发生变化
                } else {
                    old.setContactMessage(mobile.getContactMessage());
                    old.setMessage(mobile.getMessage());
                    Mobile save = mobileReposity.save(old);
                    if (save == null) {
                        return JSON.toJSONString(new Reply(500, "同步失败"));
                    }
                    //存储之后.抽取前五个数据作为通知.发送给邮箱.这里先写死
                    try {
                        List<Friend> list =
                                smartQQClient.getFriendList();
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).getNickname().equals("恶搞大王")) {
                                //说明是我
                                MessageDetail detail = mobile.getMessage().getMessages().getFirst();
                                String messageName = detail.getMessageName();
                                if (messageName == null) {
                                    messageName = "";
                                }
                                String messageNum = detail.getMessageNum();
                                String messageTime = detail.getMessageTime();
                                String text = detail.getMessgaeText();
                                if (!messageTime.equals(Constant.oldSMSDate)) {
                                    smartQQClient.sendMessageToFriend(list.get(i).getUserId(), messageName + "(" + messageNum + ")于" + messageTime + ":" + text);
                                    Constant.oldSMSDate = messageTime;
                                }
                                ContactMessageDetail contactMessageDetail = mobile.getContactMessage().getMessages().getFirst();
                                String date = contactMessageDetail.getDate();
                                String duration = contactMessageDetail.getDuration();
                                String contactMessageDetailName = contactMessageDetail.getName();
                                if (contactMessageDetailName == null) {
                                    contactMessageDetailName = "";
                                }
                                String phoneNumber = contactMessageDetail.getPhoneNumber();
                                Integer type = contactMessageDetail.getType();
                                if (type != null && type == 3 && !date.equals(Constant.oldCallDate)) {
                                    smartQQClient.sendMessageToFriend(list.get(i).getUserId(), contactMessageDetailName + "(" + phoneNumber + ")于" + date + "给您来电了~");
                                    Constant.oldCallDate = date;
                                }
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
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
