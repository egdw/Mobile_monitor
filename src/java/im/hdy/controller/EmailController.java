package im.hdy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.mail.internet.MimeMessage;

/**
 * Created by hdy on 26/11/2017.
 */
@Controller
@RequestMapping("mail")
public class EmailController {

    @Autowired
    JavaMailSender mailSender;

    @RequestMapping("sendemail")
    public void sendEmail() {
        try {
            final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
            final MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
            message.setFrom("17194110228@163.com");
            message.setTo("378759617@qq.com");
            message.setSubject("测试邮件主题");
            message.setText("发送邮件测试");
            this.mailSender.send(mimeMessage);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
