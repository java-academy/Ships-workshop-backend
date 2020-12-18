package com.ships.room;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/room", produces = "application/json; charset=UTF-8")
@AllArgsConstructor
class GameRoomController {
    private final GameRoomService gameRoomService;

    @GetMapping
    List<Player> fetchPlayers() {
        return gameRoomService.getPlayerListInRoom();
    }

    @PostMapping
    ResponseEntity<?> addPlayerToRoom(Player player) {
        return handleResponse(player, gameRoomService.addPlayer(player), HttpStatus.CONFLICT);
    }

    @DeleteMapping
    ResponseEntity<?> removePlayerFromRoom(Player player) {
        return handleResponse(player, gameRoomService.deletePlayer(player), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    private ResponseEntity<?> handleResponse(Player player, RoomStatus result, HttpStatus httpStatus) {
        if(result == RoomStatus.SUCCESS)
            return new ResponseEntity<>(player, HttpStatus.OK);
        return new ResponseEntity<>(result.val, httpStatus);
    }
}
