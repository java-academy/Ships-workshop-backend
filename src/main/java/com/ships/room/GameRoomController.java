package com.ships.room;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.tinylog.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;

@RestController
@RequestMapping(value = "/room", produces = "application/json")
@AllArgsConstructor
class GameRoomController {
    private final GameRoomService gameRoomService;

    @GetMapping
    ResponseEntity<?> fetchPlayers(HttpServletRequest req) {
        List<Player> playerList = gameRoomService.getPlayerListInRoom();
        Logger.debug("Get list of all players, No of players : {}", playerList.size());
        return new ResponseEntity<>(gameRoomService.updateSession(req) ? playerList : emptyList(), HttpStatus.OK);
    }

    @PostMapping("/{name}")
    ResponseEntity<?> addPlayerToRoom(@PathVariable String name, HttpServletRequest req) {
        Logger.debug("Adding {} to room", name);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().buildAndExpand(name).toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setLocation(location);

        Map<String,String> result = gameRoomService.addPlayer(name, req);
        String status = result.get("status");
        if (status.equals(RoomStatus.SUCCESS.name()))
            return new ResponseEntity<>(result, headers, HttpStatus.CREATED);
        return new ResponseEntity<>(status, headers, HttpStatus.CONFLICT);
    }

    @DeleteMapping("/{name}")
    ResponseEntity<?> removePlayerFromRoom(@PathVariable String name) {
        Logger.debug("Delete a player of name {}", name);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        RoomStatus result = gameRoomService.deletePlayer(name);
        if (result == RoomStatus.SUCCESS)
            return new ResponseEntity<>(headers, HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(result, headers, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @DeleteMapping
    ResponseEntity<?> removeAllPlayersFromRoom() {
        Logger.debug("Delete all the players in the room");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        gameRoomService.deleteAllPlayers();
        return new ResponseEntity<>(headers, HttpStatus.NO_CONTENT);
    }
}
