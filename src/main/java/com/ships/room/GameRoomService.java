package com.ships.room;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tinylog.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Service
class GameRoomService {
    static final int MAX_PLAYERS_IN_ROOM = 2;
    static final int MAX_INACTIVE_INTERVAL_IN_ROOM = 10;
    @Getter
    private final List<Player> playerListInRoom = new ArrayList<>(MAX_PLAYERS_IN_ROOM);

    private final SessionContainer sessionContainer;

    @Autowired
    GameRoomService(SessionContainer sessionContainer) {
        this.sessionContainer = sessionContainer;
    }

    HttpSession getSession(String jSessionId) {
        return sessionContainer.getSession(jSessionId);
    }

    RoomStatus addPlayer(String name, HttpServletRequest req) {
        RoomStatus result = checkIfPlayerCanBeAddedToRoom(name, req);
        if(result == RoomStatus.SUCCESS) {
            HttpSession session = req.getSession(true);
            Logger.info("BORYS PLAYER: " + name + " WITH ASSIGNED SESSION ID: " + session.getId());
            session.setMaxInactiveInterval(MAX_INACTIVE_INTERVAL_IN_ROOM);
            sessionContainer.putSession(session.getId(), session);
            LoggedPlayer user = new LoggedPlayer(new Player(name, session.getId()), this, sessionContainer);
            session.setAttribute("user", user);
        }
        return result;
    }

    RoomStatus deletePlayer(String name) {
        for(var player : playerListInRoom)
            if(player.getName().equals(name)) {
                playerListInRoom.remove(player);
                Logger.info("{} deleted from the room", name);
                return RoomStatus.SUCCESS;
            }
        Logger.info("There is no {} in the room", name);
        return RoomStatus.NO_SUCH_PLAYER;
    }

    void deleteAllPlayers() {
        playerListInRoom.clear();
    }

    private RoomStatus checkIfPlayerCanBeAddedToRoom(String playersName, HttpServletRequest req) {
        if(playerListInRoom.size() == MAX_PLAYERS_IN_ROOM) {
            Logger.info("A new player is not added because the room is full!");
            return RoomStatus.ROOM_IS_FULL;
        }
        if (playerListInRoom.stream().anyMatch(p -> p.getName().equals(playersName))) {
            Logger.info("There is a player with the same nickname!");
            return RoomStatus.NICKNAME_DUPLICATION;
        }
        if(null != req.getSession(false)) {
            Logger.info("There is a player with same session ");
            return RoomStatus.DUPLICATED_SESSION;
        }
        return RoomStatus.SUCCESS;
    }
}
