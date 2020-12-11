package com.ja.ships_backend.waiting_room;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/room", produces = "application/json; charset=UTF-8")
@AllArgsConstructor
public class GameRoomController {
    private final GameRoomService gameRoomService;

    @GetMapping
    public List<Player> fetchPlayers() {
        return gameRoomService.getPlayerListInRoom();
    }

    @PostMapping
    public ResponseEntity<?> addPlayerToRoom(Player player) {
        return handleResponse(player, gameRoomService.addPlayer(player), HttpStatus.CONFLICT);
    }

    @DeleteMapping
    public ResponseEntity<?> removePlayerFromRoom(Player player) {
        return handleResponse(player, gameRoomService.deletePlayer(player), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    private ResponseEntity<?> handleResponse(Player player, RoomStatus result, HttpStatus httpStatus) {
        if(result == RoomStatus.SUCCESS)
            return new ResponseEntity<>(player, HttpStatus.OK);
        return new ResponseEntity<>(result.val, httpStatus);
    }
}
