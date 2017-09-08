package im.hdy.controller;

import com.alibaba.fastjson.JSON;
import im.hdy.entity.Mobile;
import im.hdy.entity.Reply;
import im.hdy.reposity.MobileReposity;
import im.hdy.utils.Constants;
import im.hdy.utils.MD5Utils;
import im.hdy.utils.RsaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hdy on 2017/9/8.
 */
@Controller
@RequestMapping("/register")
public class RegisterController {

    @Autowired
    private MobileReposity reposity;


    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public String register(@RequestParam(required = true) String phoneNum, @RequestParam(required = true) String password) {
        Pattern compile = Pattern.compile("0?(13|14|15|18|17|)[0-9]{9}");
        Matcher matcher = compile.matcher(phoneNum);
        if (matcher.matches()) {
            if (password.length() < 8) {
                return JSON.toJSONString(new Reply(500, "密码太短!"));
            } else {
                //说明一切准备就绪
                String[] pairs = RsaUtils.createKeyPairs();
                Mobile mobile = new Mobile();
                mobile.setMobileNumber(phoneNum);
                mobile.setPassword(MD5Utils.MD5(phoneNum + Constants.SAILT + password));
                Mobile save = reposity.save(mobile);
                System.out.println(save);
            }
        } else {
            return JSON.toJSONString(new Reply(500, "手机号码格式错误"));
        }
        return phoneNum;
    }
}
