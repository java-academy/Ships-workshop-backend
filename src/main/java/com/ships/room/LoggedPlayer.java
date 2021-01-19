package com.ships.room;

import org.tinylog.Logger;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import java.io.Serializable;
import java.util.List;

@WebListener
public class LoggedPlayer implements HttpSessionBindingListener, Serializable {
    private Player player;
    private GameRoomService gameRoomService;

    LoggedPlayer(Player player, GameRoomService gameRoomService) {
        this.player = player;
        this.gameRoomService = gameRoomService;
    }

    public LoggedPlayer() {
    }

    @Override
    public void valueBound(HttpSessionBindingEvent event) {
        LoggedPlayer loggedPlayer = (LoggedPlayer) event.getValue();
        List<Player> playerListInRoom = loggedPlayer.gameRoomService.getPlayerListInRoom();
        Player player = loggedPlayer.player;
        playerListInRoom.add(player);
        Logger.debug("Successfully added session with event name: '{}' of player '{}'", event.getName(), player.getName());
    }

    @Override
    public void valueUnbound(HttpSessionBindingEvent event) {
        LoggedPlayer loggedPlayer = (LoggedPlayer) event.getValue();
        List<Player> playerListInRoom = loggedPlayer.gameRoomService.getPlayerListInRoom();
        Player player = loggedPlayer.player;
        playerListInRoom.remove(player);
        Logger.debug("Successfully removed player: '{}' with bounded session", player.getName());
    }
}
