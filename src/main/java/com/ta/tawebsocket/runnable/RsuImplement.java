package com.ta.tawebsocket.runnable;

import javax.websocket.Session;
import java.io.IOException;
import java.util.Date;

public class RsuImplement implements Runnable {

    private final Session session;

    public RsuImplement(Session session) {
        this.session = session;
    }

    @Override
    public void run() {
        try {


            while (true){
//                session.getAsyncRemote().sendText(" rsu");
//                SessionPoll.sendMessage(session," light");
                Thread.sleep(360000);
                synchronized(session){
                    session.getBasicRemote().sendText(session.getId()+ new Date() +": rsu");
                }
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
//
    }

}
