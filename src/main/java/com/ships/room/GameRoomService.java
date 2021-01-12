package com.ships.room;

import lombok.Getter;
import org.springframework.stereotype.Service;
import org.tinylog.Logger;

import java.util.ArrayList;
import java.util.List;

@Service
class GameRoomService {
    private static final int MAX_PLAYERS_IN_ROOM = 2;
    @Getter
    private final List<Player> playerListInRoom = new ArrayList<>(MAX_PLAYERS_IN_ROOM);

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

    RoomStatus checkIfPlayerCanBeAddedToRoom(String playersName) {
        if(playerListInRoom.size() == MAX_PLAYERS_IN_ROOM) {
            Logger.info("New player is not added because room is full!");
            return RoomStatus.ROOM_IS_FULL;
        }
        if (playerListInRoom.stream().anyMatch(p -> p.getName().equals(playersName))) {
            Logger.info("There is a player with the same nickname!");
            return RoomStatus.NICKNAME_DUPLICATION;
        }
        return RoomStatus.SUCCESS;
    }
}
