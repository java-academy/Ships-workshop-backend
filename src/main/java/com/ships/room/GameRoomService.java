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


    RoomStatus addPlayer(Player player) {
        RoomStatus result = checkIfPlayerCanBeAddedToRoom(player);
        if (result == RoomStatus.SUCCESS) {
            Logger.debug(" {} successfully added to the room", player.toString());
            playerListInRoom.add(player);
        }
        return result;
    }

    RoomStatus deletePlayer(Player playerToDelete) {
        for (Player player : playerListInRoom)
            if (player.equals(playerToDelete)) {
                playerListInRoom.remove(player);
                Logger.info("{} deleted from the room", playerToDelete.toString());
                return RoomStatus.SUCCESS;
            }
        Logger.info("There is no {} in the room", playerToDelete.toString());
        return RoomStatus.NO_SUCH_PLAYER;
    }

    RoomStatus isPlayerInRoom(Player player){
        return playerListInRoom.contains(player) ? RoomStatus.SUCCESS : RoomStatus.NO_SUCH_PLAYER;
    }

    RoomStatus deleteAllPlayers() {
        playerListInRoom.clear();
        return RoomStatus.SUCCESS;
    }

    private RoomStatus checkIfPlayerCanBeAddedToRoom(Player player) {
        if (playerListInRoom.size() == MAX_PLAYERS_IN_ROOM) {
            Logger.info("New player is not added because room is full!");
            return RoomStatus.ROOM_IS_FULL;
        }
        if (playerListInRoom.contains(player)) {
            Logger.info("There is a player with the same nickname!");
            return RoomStatus.NICKNAME_DUPLICATION;
        }
        return RoomStatus.SUCCESS;
    }
}
