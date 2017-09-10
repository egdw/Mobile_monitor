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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by hdy on 2017/9/8.
 */
@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private MobileReposity reposity;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public String login(@RequestParam(required = true) String username, @RequestParam(required = true) String password, @RequestParam(required = true) boolean remberMe, HttpSession session, HttpServletResponse response) {
        Mobile mobile = reposity.findMobileByMobileNumber(username);
        if (mobile != null) {
            System.out.println(username);
            System.out.println(password);
            String md5 = MD5Utils.MD5(username + Constants.SAILT + password);
            System.out.println(md5);
            boolean equals = mobile.getPassword().equals(md5);
            if (equals) {
                session.setAttribute("currentUser", mobile);
                if(remberMe){
                    Cookie cookie = new Cookie("remberMe",md5);
                    cookie.setMaxAge(60 * 60 * 24 * 5);// 设置为五天
                    cookie.setPath("/");
                    response.addCookie(cookie);
                    Cookie cookie2 = new Cookie("remberMeU", username);
                    cookie2.setMaxAge(60 * 60 * 24 * 5);// 设置为五天
                    cookie2.setPath("/");
                    response.addCookie(cookie2);
                }
                return JSON.toJSONString(new Reply(200, "登录成功!"));
            } else {
                return JSON.toJSONString(new Reply(500, "手机不存在或者密码错误!"));
            }
        } else {
            return JSON.toJSONString(new Reply(500, "手机不存在或者密码错误!"));

        }
    }

}
