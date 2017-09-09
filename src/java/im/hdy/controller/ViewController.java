package im.hdy.controller;

import im.hdy.entity.Mobile;
import im.hdy.reposity.MobileReposity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created by hdy on 2017/9/5.
 */
@Controller
@RequestMapping("/")
public class ViewController {

    private Logger logger = LoggerFactory.getLogger(ViewController.class);
    @Autowired
    private MobileReposity reposity;

    public ViewController() {
        logger.info("控制器已创建");
    }


    @RequestMapping(method = RequestMethod.GET)
    public String getCustomer(HttpServletRequest request, HttpSession session) {
        Cookie[] cookies =
                request.getCookies();
        if (session.getAttribute("currentUser") != null) {
            //说明是已经登录的用户了
            Mobile mobile = (Mobile) session.getAttribute("currentUser");
            Mobile number = reposity.findMobileByMobileNumber(mobile.getMobileNumber());
            request.setAttribute("messages", number.getMessage().getMessages());
            return "detail";
        }
        if (cookies != null) {
            //说明有cookie
            String remberMe = null;
            String remberUser = null;
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("remberMe")) {
                    remberMe = cookie.getValue();
                } else if (cookie.getName().equals("remberMeU")) {
                    remberUser = cookie.getValue();
                }
            }
            if (remberMe != null && remberUser != null) {
                Mobile number = reposity.findMobileByMobileNumber(remberUser);
                if (number != null) {
                    boolean equals = number.getPassword().equals(remberMe);
                    equals = true;
                    if (equals) {
                        //说明当前的cookie可用而且密码账户都是正确的
                        request.setAttribute("messages", number.getMessage().getMessages());
                        session.setAttribute("currentUser", number);
                        return "detail";
                    }

                }
            }
        }
        return "index";
    }

    @RequestMapping(value = "/contact", method = RequestMethod.GET)
    public String getContact(HttpServletRequest request, HttpSession session) {
        Cookie[] cookies =
                request.getCookies();
        if (session.getAttribute("currentUser") != null) {
            //说明是已经登录的用户了
            Mobile mobile = (Mobile) session.getAttribute("currentUser");
            Mobile number = reposity.findMobileByMobileNumber(mobile.getMobileNumber());
            request.setAttribute("contacts", number.getContactMessage().getMessages());
            return "detail2";
        }
        if (cookies != null) {
            //说明有cookie
            String remberMe = null;
            String remberUser = null;
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("remberMe")) {
                    remberMe = cookie.getValue();
                } else if (cookie.getName().equals("remberMeU")) {
                    remberUser = cookie.getValue();
                }
            }
            if (remberMe != null && remberUser != null) {
                Mobile number = reposity.findMobileByMobileNumber(remberUser);
                if (number != null) {
                    boolean equals = number.getPassword().equals(remberMe);
                    equals = true;
                    if (equals) {
                        //说明当前的cookie可用而且密码账户都是正确的
                        request.setAttribute("contacts",  number.getContactMessage().getMessages());
                        session.setAttribute("currentUser", number);
                        return "detail";
                    }

                }
            }
        }
        return "index";
    }
}
