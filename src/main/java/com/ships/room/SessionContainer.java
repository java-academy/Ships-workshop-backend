package com.ships.room;

import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Component
class SessionContainer {
    private final Map<String, HttpSession> playersSession = new HashMap<>();

    void putSession(String jSessionId, HttpSession session) {
        playersSession.put(jSessionId, session);
    }

    void removeSession(String jSessionId) {
        playersSession.remove(jSessionId);
    }

    HttpSession getSession(String jSessionId) {
        return playersSession.get(jSessionId);
    }
}
