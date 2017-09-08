package im.hdy.controller;

import com.alibaba.fastjson.JSON;
import im.hdy.entity.ContactMessage;
import im.hdy.entity.Message;
import im.hdy.reposity.ContactMessageReposity;
import im.hdy.reposity.MessageReposity;
import im.hdy.reposity.MobileReposity;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

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
}
