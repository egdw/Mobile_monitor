package im.hdy.controller;

import com.alibaba.fastjson.JSON;
import im.hdy.entity.Mobile;
import im.hdy.entity.Reply;
import im.hdy.reposity.MobileReposity;
import im.hdy.utils.Constants;
import im.hdy.utils.MD5Utils;
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
                Mobile old = reposity.findMobileByMobileNumber(phoneNum);
                String newPassword = MD5Utils.MD5(phoneNum.trim() + Constants.SAILT + password.trim());
                if (old != null) {
                    //说明不是新用户
                    String oldPassword = old.getPassword();
                    if (newPassword.equals(oldPassword)) {
                        //说明密码相同
                        return JSON.toJSONString(new Reply(200, "登录成功~"));
                    }else{
                        return JSON.toJSONString(new Reply(500, "密码错误~"));
                    }
                }
                //说明一是新用户
                Mobile mobile = new Mobile();
                mobile.setMobileNumber(phoneNum);
                System.out.println(phoneNum);
                System.out.println(password);
                System.out.println(newPassword);
                mobile.setPassword(newPassword);
                Mobile save = reposity.save(mobile);
                if (save != null) {
                    //说明注册成功!
                    return JSON.toJSONString(new Reply(200, "已为您注册并登录成功~"));
                }
                return JSON.toJSONString(new Reply(500, "注册失败"));
            }
        } else {
            return JSON.toJSONString(new Reply(500, "手机号码格式错误"));
        }
    }
}
