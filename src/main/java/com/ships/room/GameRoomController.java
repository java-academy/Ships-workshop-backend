package com.ships.room;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tinylog.Logger;

import java.util.List;

@RestController
//TODO: change origins to URL to frontend when pushing to master
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(value = "/room", produces = "application/json; charset=UTF-8")
@AllArgsConstructor
class GameRoomController {
    private final GameRoomService gameRoomService;

    @GetMapping
    public ResponseEntity<List<Player>> fetchPlayers() {
        Logger.debug("Get list of all players, No of players : {}",
                gameRoomService.getPlayerListInRoom().size());
        return new ResponseEntity<>(gameRoomService.getPlayerListInRoom(), HttpStatus.OK);
    }

    @GetMapping("/{name}")
    public ResponseEntity<?> fetchPlayer(@PathVariable String name) {
        Logger.debug("Get one player of name {}", name);
        Player player = new Player(name);
        return handlePlayerResponse(player, gameRoomService.isPlayerInRoom(player), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @PostMapping("/{name}")
    public ResponseEntity<?> addPlayerToRoom(@PathVariable String name) {
        Logger.debug("Add ", name);
        Player player = new Player(name);
        return handlePlayerResponse(player, gameRoomService.addPlayer(player), HttpStatus.CONFLICT);
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<?> removePlayerFromRoom(@PathVariable String name) {
        Logger.debug("Delete a player of name {}", name);
        Player player = new Player(name);
        return handlePlayerResponse(player, gameRoomService.deletePlayer(player), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @DeleteMapping()
    public ResponseEntity<?> removeAllPlayersFromRoom() {
        Logger.debug("Delete al players in room");
        List<Player> removedPlayers = gameRoomService.getPlayerListInRoom();
        gameRoomService.deleteAllPlayers();
        return new ResponseEntity<>(removedPlayers, HttpStatus.OK);
    }

    private ResponseEntity<?> handlePlayerResponse(Player player, RoomStatus result, HttpStatus errorHttpStatus) {
        if (result == RoomStatus.SUCCESS) {
            return new ResponseEntity<>(player, HttpStatus.OK);
        }
        return new ResponseEntity<>(result.val, errorHttpStatus);
    }
}
