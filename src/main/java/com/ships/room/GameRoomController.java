package com.ships.room;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/room", produces = "application/json")
@AllArgsConstructor
@CrossOrigin
class GameRoomController {
    private final GameRoomService gameRoomService;

    @GetMapping
    List<Player> fetchPlayers() {
        return gameRoomService.getPlayerListInRoom();
    }

    @PostMapping("/{name}")
    ResponseEntity<?> addPlayerToRoom(@PathVariable String name) {
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().buildAndExpand(name).toUri();
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);
        header.setLocation(location);

        RoomStatus result = gameRoomService.addPlayer(name);

        if(result == RoomStatus.SUCCESS)
            return new ResponseEntity<>(header, HttpStatus.CREATED);
        return new ResponseEntity<>(result.val, header, HttpStatus.CONFLICT);
    }

    @DeleteMapping("/{name}")
    ResponseEntity<?> removePlayerFromRoom(@PathVariable String name) {
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);
        RoomStatus result = gameRoomService.deletePlayer(name);
        if(result == RoomStatus.SUCCESS)
            return new ResponseEntity<>(header, HttpStatus.OK);
        return new ResponseEntity<>(result.val, header, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @DeleteMapping
    ResponseEntity<?> removeAllPlayersFromRoom() {
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);
        gameRoomService.deleteAllPlayers();
        return new ResponseEntity<>(header, HttpStatus.OK);
    }
}
