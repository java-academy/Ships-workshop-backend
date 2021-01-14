package com.ships.room;

import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.tinylog.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/room", produces = "application/json")
@AllArgsConstructor
class GameRoomController {
    private final GameRoomService gameRoomService;

    @GetMapping
    ResponseEntity<List<Player>> fetchPlayers(HttpServletRequest req) {
        int numberOfPlayers = gameRoomService.getPlayerListInRoom().size();
        Logger.debug("Get list of all players, No of players : {}", numberOfPlayers);

        HttpSession session = req.getSession(false);
        Logger.info("BORYS SESSION: " + session);
        if (null != session) {
            Logger.info("BORYS NULL RESET");
            if (GameRoomService.MAX_PLAYERS_IN_ROOM == numberOfPlayers) {
                Logger.info("BORYS MAMY TUTAJ RESET");
                LoggedPlayer loggedPlayer = (LoggedPlayer) session.getAttribute("user");
                loggedPlayer.removeOnlySession();
            } else
                session.setMaxInactiveInterval(GameRoomService.MAX_INACTIVE_INTERVAL_IN_ROOM);
        }

        return new ResponseEntity<>(gameRoomService.getPlayerListInRoom(), HttpStatus.OK);
    }

    @PostMapping("/{name}")
    ResponseEntity<?> addPlayerToRoom(@PathVariable String name, HttpServletRequest req) {
        Logger.debug("Adding {} to room", name);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().buildAndExpand(name).toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setLocation(location);
        RoomStatus result = gameRoomService.addPlayer(name, req);

        Map<String, String> response = new HashMap<>();
        response.put("status", result.name());
        response.put("token", req.getSession(false).getId());

        if (result != RoomStatus.SUCCESS)
            return new ResponseEntity<>(result, headers, HttpStatus.CONFLICT);
        return new ResponseEntity<>(response, headers, HttpStatus.CREATED);
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
