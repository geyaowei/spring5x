package com.zja.websocket;

import com.zja.constant.Constants;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ZhengJa
 * @description 自定义消息处理类
 * @data 2019/10/31
 */
public class CustomHandler extends TextWebSocketHandler {

    private Map<String, WebSocketSession> nameAndSession = new ConcurrentHashMap<>();

    // 建立连接时候触发
    @Override
    public void afterConnectionEstablished(WebSocketSession session)  {
        String username = getNameFromSession(session);
        nameAndSession.putIfAbsent(username, session);
    }


    // 关闭连接时候触发
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String username = getNameFromSession(session);
        nameAndSession.remove(username);
    }

    // 处理消息
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 防止中文乱码
        String msg = URLDecoder.decode(message.getPayload(), "utf-8");
        String username = getNameFromSession(session);
        // 简单模拟群发消息
        TextMessage reply = new TextMessage(username + " : " + msg);
        nameAndSession.forEach((s, webSocketSession)
                -> {
            try {
                webSocketSession.sendMessage(reply);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 从 WebSocketSession中获取用户名
     * @param session
     * @return java.lang.String 用户名称
     */
    private String getNameFromSession(WebSocketSession session) {
        Map<String, Object> attributes = session.getAttributes();
        return (String) attributes.get(Constants.USER_NAME);
    }

}
