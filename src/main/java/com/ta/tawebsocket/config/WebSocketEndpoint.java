package com.ta.tawebsocket.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ta.tawebsocket.runnable.LightImplement;
import com.ta.tawebsocket.runnable.RsuImplement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@ServerEndpoint(value = "/websocket")
@Slf4j
public class WebSocketEndpoint {
    private Session session;

//    连接建立成功调用的方法
    @OnOpen
    public void onOpen(Session session){
//        把会话存入连接池
        this.session = session;
//        SessionPoll.sessions.put(userId,session);
        log.info(this.session.getId()+" 客户端建立了连接！");
    }

    @OnClose
    public  void onClose(Session session) throws IOException {

//        SessionPoll.close(session.getId());
        log.info(session.getId()+"session.close(); 1...onClose(Session session) session 关闭了！");
        session.close();
        log.info(this.session.getId()+" this.session.close(); 2... onClose(Session session) session 关闭了！");
//        this.session.close();

    }

    /*
    收到客户端发送过来的消息调用的方法
    message 客户发过来的消息
    */
    @OnMessage
    public void onMessage(String message,Session session) throws InterruptedException, IOException {


        log.info("this.session.equals(session):" +this.session.equals(session));
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
            log.info(session.getId()+" :收到了消息..." + message);
//            while (true){

//
                if(message.equalsIgnoreCase("light")){

                    //多线程发送数据

                    LightImplement lightImplement = new LightImplement(session);
                    Thread t = new Thread(lightImplement);
                    t.start();

                    /*while (true){
                        Thread.sleep(1000);
                        synchronized (session) {
                            session.getBasicRemote().sendText(session.getId() + " light");
                        }
                    }*/

                }

                if(message.equalsIgnoreCase("rsu")){

                    /*while (true) {
                        Thread.sleep(1000);
                        synchronized (session) {

                            session.getBasicRemote().sendText(session.getId()+" rsu");

                        }
                    }*/

//
                    //多线程发送数据

                    RsuImplement rs = new RsuImplement(session);
                    Thread t = new Thread(rs);
                    t.start();

//                    session.getAsyncRemote().sendText(message+" rsu");
                }



//            }



        }

    }

}
