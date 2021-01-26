package com.ships.room;

import lombok.Getter;
import org.springframework.stereotype.Service;
import org.tinylog.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
class GameRoomService implements Serializable {
    static final int MAX_PLAYERS_IN_ROOM = 2;
    static final int MAX_INACTIVE_INTERVAL_IN_ROOM = 10;
    static final String EMPTY_TOKEN = "EMPTY";
    private int numberOfSessionsInGame = 0;

    @Getter
    private final List<Player> playerListInRoom = new ArrayList<>(MAX_PLAYERS_IN_ROOM);

    boolean updateSession(HttpServletRequest req) {
        int numberOfPlayers = playerListInRoom.size();
        Logger.debug("No of players in playerListInRoom: {}", numberOfPlayers);
        HttpSession session = req.getSession(false);
        if (null == session) {
            Logger.error("Session is null!");
            return false;
        }
        if (GameRoomService.MAX_PLAYERS_IN_ROOM == numberOfPlayers) {
            session.setMaxInactiveInterval(-1);
        }
        System.out.println("BORYS numberOfSessionsInGame= " + numberOfSessionsInGame);
        return true;
    }

    Map<String, String> addPlayer(String name, HttpServletRequest req) {
        System.out.println("BORYS from add player numberOfSessionsInGame= " + numberOfSessionsInGame);
        RoomStatus result = checkIfPlayerCanBeAddedToRoom(name, req);
        String token = EMPTY_TOKEN;
        if(result == RoomStatus.SUCCESS) {
            numberOfSessionsInGame++;
            HttpSession session = req.getSession(true);
            session.setMaxInactiveInterval(MAX_INACTIVE_INTERVAL_IN_ROOM);
            LoggedPlayer user = new LoggedPlayer(new Player(name), this);
            session.setAttribute("user", user);
            token = session.getId();
        }
        System.out.println("BORYS after adding player numberOfSessionsInGame= " + numberOfSessionsInGame);
        return Map.of("status", result.name(), "token", token);
    }

    RoomStatus removePlayerAndTheirSession(String name, HttpServletRequest req) {
        if(numberOfSessionsInGame <= 0) {
            Logger.info("Try to remove {}, but numberOfSessionsInGame is {}", name, numberOfSessionsInGame);
            return RoomStatus.SUCCESS;
        }
        for(var player : playerListInRoom)
            if(player.getName().equals(name)) {
                Logger.info("{} deleted from the room", name);
                playerListInRoom.remove(player);
                numberOfSessionsInGame--;
                req.getSession().invalidate();
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
        if(null != req.getSession(false) || numberOfSessionsInGame != 0) {
            Logger.info("There is a player with same session ");
            return RoomStatus.DUPLICATED_SESSION;
        }
        return RoomStatus.SUCCESS;
    }
}
