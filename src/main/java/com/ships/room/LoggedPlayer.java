package com.ships.room;

import lombok.Setter;
import org.tinylog.Logger;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import java.util.List;

@WebListener
public class LoggedPlayer implements HttpSessionBindingListener {
    private Player player;
    private GameRoomService gameRoomService;
    private SessionContainer sessionContainer;
    @Setter
    private boolean shouldSessionBeOnlyRemovedDuringUnbound = false;

    LoggedPlayer(Player player, GameRoomService gameRoomService, SessionContainer sessionContainer) {
        this.player = player;
        this.gameRoomService = gameRoomService;
        this.sessionContainer = sessionContainer;
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
        Player player = loggedPlayer.player;
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
        Player player = loggedPlayer.player;
        playerListInRoom.remove(player);
        Logger.debug("Successfully removed player: '{}' with bounded session", player.getName());
        sessionContainer.removeSession(player.getJSessionId());
    }
}
