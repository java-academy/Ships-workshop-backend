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
import java.util.List;

@RestController
@RequestMapping(value = "/room", produces = "application/json")
@AllArgsConstructor
@CrossOrigin
class GameRoomController {
    private final GameRoomService gameRoomService;

    @GetMapping
    ResponseEntity<List<Player>> fetchPlayers(HttpServletRequest req) {
        Logger.debug("Get list of all players, No of players : {}",
                gameRoomService.getPlayerListInRoom().size());

        HttpSession session = req.getSession(false);
        if(null != session)
            session.setMaxInactiveInterval(10);

        return new ResponseEntity<>(gameRoomService.getPlayerListInRoom(), HttpStatus.OK);
    }

    @PostMapping("/{name}")
    ResponseEntity<?> addPlayerToRoom(@PathVariable String name, HttpServletRequest req) {
        Logger.debug("Add {} to room", name);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().buildAndExpand(name).toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setLocation(location);

        RoomStatus result = gameRoomService.checkIfPlayerCanBeAddedToRoom(name);
        if (result != RoomStatus.SUCCESS) {
            return new ResponseEntity<>(result, headers, HttpStatus.CONFLICT);
        }
        HttpSession session = req.getSession(true);
        LoggedPlayer user = new LoggedPlayer(name, gameRoomService);
        session.setAttribute("user", user);
        session.setMaxInactiveInterval(10);
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
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
        Logger.debug("Delete al players in room");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        gameRoomService.deleteAllPlayers();
        return new ResponseEntity<>(headers, HttpStatus.NO_CONTENT);
    }
}
