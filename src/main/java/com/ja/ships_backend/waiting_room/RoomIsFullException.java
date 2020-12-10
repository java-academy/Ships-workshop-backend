package com.ja.ships_backend.waiting_room;

public class RoomIsFullException extends Exception {
    public RoomIsFullException(String message) {
        super(message);
    }
}