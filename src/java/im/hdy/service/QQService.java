/*
 * Copyright (c) 2012-2016, b3log.org & hacpai.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package im.hdy.service;

import im.hdy.callback.MessageCallback;
import im.hdy.client.SmartQQClient;
import im.hdy.model.*;
import im.hdy.util.XiaoVs;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.urlfetch.HTTPRequest;
import org.b3log.latke.urlfetch.HTTPResponse;
import org.b3log.latke.urlfetch.URLFetchService;
import org.b3log.latke.urlfetch.URLFetchServiceFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * QQ service.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.4.3.12, Nov 23, 2017
 * @since 1.0.0
 */
@Service
public class QQService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(QQService.class);

    /**
     * Bot type.
     */
    private static final int QQ_BOT_TYPE = XiaoVs.getInt("qq.bot.type");

    /**
     * Advertisements.
     */
    private static final List<String> ADS = new ArrayList<>();

    /**
     * URL fetch service.
     */
    private static final URLFetchService URL_FETCH_SVC = URLFetchServiceFactory.getURLFetchService();

    /**
     * XiaoV self intro. Built-in advertisement.
     */
    private static final String XIAO_V_INTRO = "关于我的更多资料请看帖 https://hacpai.com/article/1467011936362";

    /**
     * 记录未群推过的群 id 集合.
     */
    private static final Set<Long> UNPUSH_GROUPS = new CopyOnWriteArraySet<>();

    /**
     * 一次群推操作最多只推送 5 个群（为了尽量保证成功率）.
     */
    private static final int PUSH_GROUP_COUNT = 5;

    /**
     * 超过 {@code qq.bot.pushGroupUserCnt} 个成员的群才推送.
     */
    private static int PUSH_GROUP_USER_COUNT = XiaoVs.getInt("qq.bot.pushGroupUserCnt");

    static {
        String adConf = XiaoVs.getString("ads");
        if (StringUtils.isNotBlank(adConf)) {
            final String[] ads = adConf.split("#");
            ADS.addAll(Arrays.asList(ads));
        }

        ADS.add(XIAO_V_INTRO);
        ADS.add(XIAO_V_INTRO);
        ADS.add(XIAO_V_INTRO);
    }

    /**
     * QQ groups.
     * &lt;groupId, group&gt;
     */
    private final Map<Long, Group> QQ_GROUPS = new ConcurrentHashMap<>();
    /**
     * The latest group ad time.
     * &lt;groupId, time&gt;
     */
    private final Map<Long, Long> GROUP_AD_TIME = new ConcurrentHashMap<>();
    /**
     * QQ discusses.
     * &lt;discussId, discuss&gt;
     */
    private final Map<Long, Discuss> QQ_DISCUSSES = new ConcurrentHashMap<>();
    /**
     * The latest discuss ad time.
     * &lt;discussId, time&gt;
     */
    private final Map<Long, Long> DISCUSS_AD_TIME = new ConcurrentHashMap<>();
    /**
     * Group sent messages.
     */
    private final List<String> GROUP_SENT_MSGS = new CopyOnWriteArrayList<>();
    /**
     * Discuss sent messages.
     */
    private final List<String> DISCUSS_SENT_MSGS = new CopyOnWriteArrayList<>();
    /**
     * QQ client.
     */
    private SmartQQClient xiaoV;
    /**
     * QQ client listener.
     */
    private SmartQQClient xiaoVListener;

    /**
     * Initializes QQ client.
     */
    public void initQQClient() {
        LOGGER.info("开始初始化小薇");

//        xiaoV = new SmartQQClient(new MessageCallback() {
//            @Override
//            public void onMessage(final Message message) {
//                new Thread(() -> {
//                    try {
//                        Thread.sleep(500 + RandomUtils.nextInt(1000));
//
//                        final String content = message.getContent();
//                        final String key = XiaoVs.getString("qq.bot.key");
//                        if (!content.startsWith(key)) { // 不是管理命令，只是普通的私聊
//                            // 让小薇进行自我介绍
//                            xiaoV.sendMessageToFriend(message.getUserId(), XIAO_V_INTRO);
//                            System.out.println(message.getUserId());
//                            return;
//                        }
//
//                        final String msg = StringUtils.substringAfter(content, key);
//                        LOGGER.info("Received admin message: " + msg);
//                        sendToPushQQGroups(msg);
//                    } catch (final Exception e) {
//                        LOGGER.log(Level.ERROR, "XiaoV on group message error", e);
//                    }
//                }).start();
//            }
//
//            @Override
//            public void onGroupMessage(final GroupMessage message) {
//                new Thread(() -> {
//                    try {
//                        Thread.sleep(500 + RandomUtils.nextInt(1000));
//
//                        onQQGroupMessage(message);
//                    } catch (final Exception e) {
//                        LOGGER.log(Level.ERROR, "XiaoV on group message error", e);
//                    }
//                }).start();
//            }
//
//        });

        reloadGroups();
        reloadDiscusses();

        LOGGER.info("小薇初始化完毕");
    }

    private void sendToThird(final String msg, final String user) {
        final String thirdAPI = XiaoVs.getString("third.api");
        final String thirdKey = XiaoVs.getString("third.key");
        final HTTPRequest request = new HTTPRequest();
        request.setRequestMethod(HTTPRequestMethod.POST);
        try {
            request.setURL(new URL(thirdAPI));

            final String body = "key=" + URLEncoder.encode(thirdKey, "UTF-8")
                    + "&msg=" + URLEncoder.encode(msg, "UTF-8")
                    + "&user=" + URLEncoder.encode(user, "UTF-8");
            request.setPayload(body.getBytes("UTF-8"));

            final HTTPResponse response = URL_FETCH_SVC.fetch(request);
            final int sc = response.getResponseCode();
            if (HttpServletResponse.SC_OK != sc) {
                LOGGER.warn("Sends message to third system status code is [" + sc + "]");
            }
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Sends message to third system failed: " + e.getMessage());
        }
    }

    /**
     * Closes QQ client.
     */
    public void closeQQClient() {
        if (null == xiaoV) {
            return;
        }

        try {
            xiaoV.close();
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Closes QQ client failed", e);
        }
    }

    /**
     * Sends the specified article to QQ groups.
     *
     * @param msg the specified message
     */
    public void sendToPushQQGroups(final String msg) {
        try {
            final String pushGroupsConf = XiaoVs.getString("qq.bot.pushGroups");
            if (StringUtils.isBlank(pushGroupsConf)) {
                return;
            }

            // Push to all groups
            if (StringUtils.equals(pushGroupsConf, "*")) {
                int totalUserCount = 0;
                int groupCount = 0;

                if (UNPUSH_GROUPS.isEmpty()) { // 如果没有可供推送的群（群都推送过了）
                    reloadGroups();
                }

                for (final Map.Entry<Long, Group> entry : QQ_GROUPS.entrySet()) {
                    long groupId;
                    int userCount;

                    try {
                        final Group group = entry.getValue();
                        groupId = group.getId();

                        final GroupInfo groupInfo = xiaoV.getGroupInfo(group.getCode());
                        userCount = groupInfo.getUsers().size();
                        if (userCount < PUSH_GROUP_USER_COUNT) {
                            // 把人不多的群过滤掉
                            UNPUSH_GROUPS.remove(groupId);

                            continue;
                        }

                        if (!UNPUSH_GROUPS.contains(groupId)) {
                            // 如果该群已经被推送过则跳过本次推送
                            continue;
                        }

                        if (groupCount >= PUSH_GROUP_COUNT) { // 如果本次群推操作已推送群数大于设定的阈值
                            break;
                        }

                        LOGGER.info("群发 [" + msg + "] 到 QQ 群 [" + group.getName() + ", 成员数=" + userCount + "]");
                        xiaoV.sendMessageToGroup(groupId, msg); // Without retry

                        UNPUSH_GROUPS.remove(groupId); // 从未推送中移除（说明已经推送过）

                        totalUserCount += userCount;
                        groupCount++;

                        Thread.sleep(1000 * 10);
                    } catch (final Exception e) {
                        LOGGER.log(Level.ERROR, "群发异常", e);
                    }
                }

                LOGGER.info("一共推送了 [" + groupCount + "] 个群，覆盖 [" + totalUserCount + "] 个 QQ");

                return;
            }

            // Push to the specified groups
//            final String[] groups = pushGroupsConf.split(",");
//            for (final Map.Entry<Long, Group> entry : QQ_GROUPS.entrySet()) {
//                final Group group = entry.getValue();
//                final String name = group.getName();
//
//                if (StringUtils.contains(name, groups)) {
//                    final GroupInfo groupInfo = xiaoV.getGroupInfo(group.getCode());
//                    final int userCount = groupInfo.getUsers().size();
//                    if (userCount < 100) {
//                        continue;
//                    }
//
//                    LOGGER.info("Pushing [msg=" + msg + "] to QQ qun [" + group.getName() + "]");
//                    xiaoV.sendMessageToGroup(group.getId(), msg); // Without retry
//
//                    Thread.sleep(1000 * 10);
//                }
//            }
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Push message [" + msg + "] to groups failed", e);
        }
    }

    private void sendMessageToGroup(final Long groupId, final String msg) {
        Group group = QQ_GROUPS.get(groupId);
        if (null == group) {
            reloadGroups();

            group = QQ_GROUPS.get(groupId);
        }

        if (null == group) {

            return;
        }

        LOGGER.info("Pushing [msg=" + msg + "] to QQ qun [" + group.getName() + "]");
        xiaoV.sendMessageToGroup(groupId, msg);
    }

    private void onQQGroupMessage(final GroupMessage message) {
        final long groupId = message.getGroupId();
        final String content = message.getContent();
        final String userName = Long.toHexString(message.getUserId());
        System.out.println(userName);
        System.out.println(XiaoVs.QQ_BOT_NAME);
        // Push to third system
        String qqMsg = content.replaceAll("\\[\"face\",[0-9]+\\]", "");
        if (StringUtils.isNotBlank(qqMsg)) {
            qqMsg = "<p>" + qqMsg + "</p>";
            sendToThird(qqMsg, userName);
        }

        String msg = "";
//        if (StringUtils.contains(content, XiaoVs.QQ_BOT_NAME)
//                || (StringUtils.length(content) > 6
//                && (StringUtils.contains(content, "?") || StringUtils.contains(content, "？") || StringUtils.contains(content, "问")))) {
//            msg = answer(content, userName);
//        }else{
        if (StringUtils.contains(content, XiaoVs.QQ_BOT_NAME)) {
            System.out.println("条件满足");
            sendMessageToGroup(groupId, UUID.randomUUID().toString());
        } else {
            System.out.println("条件不执行");
        }

//        if (StringUtils.isBlank(msg)) {
//            return;
//        }

//        sendMessageToGroup(groupId, msg);
    }

    private void reloadGroups() {
        final List<Group> groups = xiaoV.getGroupList();
        QQ_GROUPS.clear();
        GROUP_AD_TIME.clear();
        UNPUSH_GROUPS.clear();

        final StringBuilder msgBuilder = new StringBuilder();
        msgBuilder.append("Reloaded groups: \n");
        for (final Group g : groups) {
            QQ_GROUPS.put(g.getId(), g);
            GROUP_AD_TIME.put(g.getId(), 0L);
            UNPUSH_GROUPS.add(g.getId());

            msgBuilder.append("    ").append(g.getName()).append(": ").append(g.getId()).append("\n");
        }

        LOGGER.log(Level.INFO, msgBuilder.toString());
    }

    private void reloadDiscusses() {
        final List<Discuss> discusses = xiaoV.getDiscussList();
        QQ_DISCUSSES.clear();
        DISCUSS_AD_TIME.clear();

        final StringBuilder msgBuilder = new StringBuilder();
        msgBuilder.append("Reloaded discusses: \n");
        for (final Discuss d : discusses) {
            QQ_DISCUSSES.put(d.getId(), d);
            DISCUSS_AD_TIME.put(d.getId(), 0L);

            msgBuilder.append("    ").append(d.getName()).append(": ").append(d.getId()).append("\n");
        }

        LOGGER.log(Level.INFO, msgBuilder.toString());
    }
}
