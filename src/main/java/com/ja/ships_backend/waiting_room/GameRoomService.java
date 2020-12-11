package com.ja.ships_backend.waiting_room;

import lombok.Getter;
import org.springframework.stereotype.Service;
import org.tinylog.Logger;

import java.util.ArrayList;
import java.util.List;

@Service
public class GameRoomService {
    @Getter
    private final List<Player> playerListInRoom = new ArrayList<>(2);
    private static final int MAX_PLAYERS_IN_ROOM = 2;

    RoomStatus addPlayer(Player player) {
        RoomStatus result = checkIfAnotherPlayerCanBeAddedToRoom(player.getName());
        if(result == RoomStatus.SUCCESS) {
            playerListInRoom.add(player);
        }
        return result;
    }

    RoomStatus deletePlayer(Player playerToDelete) {
        for(var player : playerListInRoom)
            if(player.getName().equals(playerToDelete.getName())) {
                playerListInRoom.remove(player);
                return RoomStatus.SUCCESS;
            }
        return RoomStatus.NO_SUCH_PLAYER;
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
