package com.ta.tawebsocket.config;

import javax.websocket.Session;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionPoll {

    public static Map<String, Session> sessions = new ConcurrentHashMap<>();

    public static void close(String sessionId) throws IOException {

        String id = getId(sessionId);
        if(id!=null){
            sessions.remove(id);
        }
        /*

        for (String userId : SessionPoll.sessions.keySet()) {
            Session session = SessionPoll.sessions.get(userId);

            if(session.getId().equals(sessionId)){
                sessions.remove(userId);
                break;
            }
        }
        */
    }

    public static String getId(String sessionId){
        for (String userId : SessionPoll.sessions.keySet()) {
            Session session = SessionPoll.sessions.get(userId);

            if(session.getId().equals(sessionId)){
                return userId;
            }
        }
        return null;
    }

    public static  void sendMessage(Session session, String message){

//        String id = getId(sessionId);
//        给发来消息的人回消息，不给其他人回消息
        session.getAsyncRemote().sendText(message);
    }

    public static void sendMessage(String message){
        for (String sessionId : SessionPoll.sessions.keySet()) {
            SessionPoll.sessions.get(sessionId).getAsyncRemote().sendText(message);
        }

    }
}
