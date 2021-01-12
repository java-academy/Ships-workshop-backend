package com.ships.room;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

@WebListener
public class SessionListener implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent hse) {
        System.out.println("-- XXXXXX#sessionCreated invoked --");
        HttpSession session = hse.getSession();
        System.out.println("SessionListener: session id: " + session.getId());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent hse) {
        System.out.println("-- XXXXXX#sessionDestroyed invoked --");
    }
}