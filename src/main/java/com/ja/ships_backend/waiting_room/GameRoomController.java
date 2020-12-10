package com.ja.ships_backend.waiting_room;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping(value = "/room", produces = "application/json; charset=UTF-8")
@AllArgsConstructor
public class GameRoomController {
    @Autowired
    private final GameRoomService gameRoomService;

    @GetMapping
    public @ResponseBody
    List<Player> fetchPlayers() {
        return gameRoomService.getPlayerListInRoom();
    }

    @PostMapping
    public @ResponseBody ResponseEntity<?> addPlayerToRoom(Player player) {
        return handleResponse(player, gameRoomService.addPlayer(player), HttpStatus.FORBIDDEN);
    }

    @DeleteMapping
    public @ResponseBody ResponseEntity<?> removePlayerFromRoom(Player player) {
        return handleResponse(player, gameRoomService.deletePlayer(player), HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<?> handleResponse(Player player, RoomStatus result, HttpStatus httpStatus) {
        if(result == RoomStatus.SUCCESS)
            return new ResponseEntity<>(player, HttpStatus.OK);
        return new ResponseEntity<>(result.val, httpStatus);
    }
}
