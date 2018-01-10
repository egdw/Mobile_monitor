package im.hdy.controller;

import im.hdy.callback.MessageCallback;
import im.hdy.client.SmartQQClient;
import im.hdy.model.GroupMessage;
import im.hdy.model.Message;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.OutputStream;

/**
 * Created by hdy on 29/11/2017.
 */
@RequestMapping("/qq")
@Controller
public class QQController {

    @Autowired
    private SmartQQClient smartQQClient;

    @RequestMapping("/login")
    @ResponseBody
    public String loginQQ(final HttpServletRequest req, final HttpServletResponse resp) {
        System.out.println("执行");
        smartQQClient.getQRCode();
        String webappRoot = this.getClass().getResource("/").getPath().replaceFirst("/", "").replaceAll("WEB-INF/classes/", "") + "pic/qrcode.png";
        System.out.println(webappRoot);
        resp.addHeader("Cache-Control", "no-store");
        OutputStream output = null;
        try {
            final byte[] data = IOUtils.toByteArray(new FileInputStream(webappRoot));

            output = resp.getOutputStream();
            IOUtils.write(data, output);
            output.flush();
            String code = smartQQClient.verifyQRCode();
            smartQQClient.login(code, new MessageCallback() {
                @Override
                public void onMessage(Message message) {
                    System.out.println(message.getContent());
                    smartQQClient.sendMessageToFriend(message.getUserId(), "我在~");
                }

                @Override
                public void onGroupMessage(GroupMessage message) {

                }

            });
        } catch (final Exception e) {
        } finally {
            IOUtils.closeQuietly(output);
        }
        return "登录成功~";
    }
}
