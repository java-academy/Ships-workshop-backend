package com.ships.room;

import lombok.Setter;
import org.tinylog.Logger;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import java.util.List;

@WebListener
public class LoggedPlayer implements HttpSessionBindingListener {
    private String name;
    private GameRoomService gameRoomService;
    @Setter
    private boolean shouldSessionBeOnlyRemovedDuringUnbound = false;

    LoggedPlayer(String name, GameRoomService gameRoomService) {
        this.name = name;
        this.gameRoomService = gameRoomService;
    }

    public LoggedPlayer() {
    }

    void removeOnlySession() {
        shouldSessionBeOnlyRemovedDuringUnbound = true;
    }

    @Override
    public void valueBound(HttpSessionBindingEvent event) {
        LoggedPlayer loggedPlayer = (LoggedPlayer) event.getValue();
        List<Player> playerListInRoom = loggedPlayer.gameRoomService.getPlayerListInRoom();
        Player player = new Player(loggedPlayer.name);
        playerListInRoom.add(player);
        Logger.debug("Successfully added session with event name: '{}' of player '{}'", event.getName(), player.getName());
    }

    @Override
    public void valueUnbound(HttpSessionBindingEvent event) {
        LoggedPlayer loggedPlayer = (LoggedPlayer) event.getValue();
        if (loggedPlayer.shouldSessionBeOnlyRemovedDuringUnbound) {
            Logger.debug("Session '{}' has been removed", event.getName());
            return;
        }
        List<Player> playerListInRoom = loggedPlayer.gameRoomService.getPlayerListInRoom();
        Player player = new Player(loggedPlayer.name);
        playerListInRoom.remove(player);
        Logger.debug("Successfully removed player: '{}' with bounded session", player.getName());
    }
}
