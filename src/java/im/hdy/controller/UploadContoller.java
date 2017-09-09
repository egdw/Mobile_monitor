package im.hdy.controller;

import com.alibaba.fastjson.JSON;
import im.hdy.entity.ContactMessage;
import im.hdy.entity.Message;
import im.hdy.entity.Mobile;
import im.hdy.entity.Reply;
import im.hdy.reposity.ContactMessageReposity;
import im.hdy.reposity.MessageReposity;
import im.hdy.reposity.MobileReposity;
import im.hdy.utils.Constants;
import im.hdy.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by hdy on 2017/9/8.
 */
@Controller
@RequestMapping("/upload")
public class UploadContoller {

    @Autowired
    private MessageReposity messageReposity;
    @Autowired
    private MobileReposity mobileReposity;
    @Autowired
    private ContactMessageReposity contactMessageReposity;

    /**
     * 每次都是重新上传
     * - - 不写太复杂了.反正没几个人用- -
     */
    @RequestMapping(value = "/message", method = RequestMethod.POST)
    public void uploadMessage(@RequestParam(required = true) String json, @RequestParam(required = true) String username, @RequestParam(required = true) String password) {
        Message message = JSON.parseObject(json, Message.class);
        System.out.println(message);
    }

    @RequestMapping(value = "/contact", method = RequestMethod.POST)
    public void uploadContact(@RequestParam(required = true) String json, @RequestParam(required = true) String username, @RequestParam(required = true) String password) {
        ContactMessage contactMessage = JSON.parseObject(json, ContactMessage.class);
        System.out.println(contactMessage);
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public String upload(@RequestParam(required = true) String json, @RequestParam(required = true) String username, @RequestParam(required = true) String password) {

        Mobile old = mobileReposity.findMobileByMobileNumber(username);
        if (old != null) {
            //说明不是新用户
            String oldPassword = old.getPassword();
            String newPassword = MD5Utils.MD5(username + Constants.SAILT + password);
            if (newPassword.equals(oldPassword)) {
                //说明密码相同
                Mobile mobile = JSON.parseObject(json, Mobile.class);
                old.setContactMessage(mobile.getContactMessage());
                old.setMessage(mobile.getMessage());
                Mobile save = mobileReposity.save(old);
                if (save == null) {
                    return JSON.toJSONString(new Reply(500, "同步失败"));
                }
                return JSON.toJSONString(new Reply(200, "同步成功"));
            } else {
                return JSON.toJSONString(new Reply(500, "密码错误~"));
            }
        }
        return JSON.toJSONString(new Reply(500, "同步失败"));
    }
}
