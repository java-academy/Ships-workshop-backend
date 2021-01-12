package com.ships.room;

import lombok.Getter;
import org.springframework.stereotype.Component;
import org.tinylog.Logger;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import java.util.List;

@WebListener
public class LoggedPlayer implements HttpSessionBindingListener {
    @Getter
    private String name;
    private GameRoomService gameRoomService;

    public LoggedPlayer(String name, GameRoomService gameRoomService) {
        this.name = name;
        this.gameRoomService = gameRoomService;
    }

    public LoggedPlayer() {}

    @Override
    public void valueBound(HttpSessionBindingEvent event) {
        System.out.println("BORYS BOUND");
        LoggedPlayer loggedPlayer = (LoggedPlayer) event.getValue();
        List<Player> playerListInRoom = gameRoomService.getPlayerListInRoom();
        Player player = new Player(name);
        if (!playerListInRoom.contains(player)) {
            playerListInRoom.add(player);
            Logger.debug(" {} successfully added to the room", player.getName());
        }
    }

    @Override
    public void valueUnbound(HttpSessionBindingEvent event) {
        System.out.println("BORYS UNBOUND");
        List<Player> playerListInRoom = gameRoomService.getPlayerListInRoom();
        LoggedPlayer loggedPlayer = (LoggedPlayer) event.getValue();
        System.out.println(loggedPlayer.getName());
        playerListInRoom.remove(new Player(loggedPlayer.getName()));
    }
}
