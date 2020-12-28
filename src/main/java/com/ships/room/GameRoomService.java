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


    RoomStatus addPlayer(String name) {
        RoomStatus result = checkIfAnotherPlayerCanBeAddedToRoom(name);
        if(result == RoomStatus.SUCCESS) {
            playerListInRoom.add(new Player(name));
        }
        return result;
    }

    RoomStatus deletePlayer(String name) {
        for(var player : playerListInRoom)
            if(player.getName().equals(name)) {
                playerListInRoom.remove(player);
                return RoomStatus.SUCCESS;
            }
        return RoomStatus.NO_SUCH_PLAYER;
    }

    void deleteAllPlayers() {
        playerListInRoom.clear();
    }

    private RoomStatus checkIfAnotherPlayerCanBeAddedToRoom(String playersName) {
        if(playerListInRoom.size() == MAX_PLAYERS_IN_ROOM) {
            Logger.info("New player is not added because room is full!");
            return RoomStatus.ROOM_IS_FULL;
        }
        if(playerListInRoom.stream().anyMatch(p -> p.getName().equals(playersName))) {
            Logger.info("There player with same nickname!");
            return RoomStatus.NICKNAME_DUPLICATION;
        }
        return RoomStatus.SUCCESS;
    }
}
