package com.ta.tawebsocket.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

//@Component
//@ServerEndpoint(value = "/websocket/{userId}")
@Slf4j
public class WebSocketEndpoint_bak {
    private Session session;

//    连接建立成功调用的方法
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId){
//        把会话存入连接池
        this.session = session;
        SessionPoll.sessions.put(userId,session);
        log.info(userId+" 客户端建立了连接！");
    }

    @OnClose
    public  void onClose(Session session) throws IOException {

        SessionPoll.close(session.getId());
        session.close();

    }

    /*
    收到客户端发送过来的消息调用的方法
    message 客户发过来的消息
    */
    @OnMessage
    public void onMessage(String message,Session session) throws InterruptedException {
        //如果是心跳检测的消息，则返回pong 作为心跳回应
        if(message.equalsIgnoreCase("ping")){
            try {
                Map<String, Object> map = new HashMap<>();
                map.put("type","pong");

                ObjectMapper mapper = new ObjectMapper();


                String text = mapper.writeValueAsString(map);

                session.getBasicRemote().sendText(text);
            } catch (JsonProcessingException e) {
                log.error("心跳发生错误！");
                e.printStackTrace();
            } catch (IOException e) {
                log.error("心跳发生错误！");
                e.printStackTrace();
            }
        }else {
            //给所有订阅人发送消息
//            SessionPoll.sendMessage(message);
//            给发来消息的人发回消息

            while (true){
                Thread.sleep(1000);
                SessionPoll.sendMessage(session,message);
            }



        }

    }

}
