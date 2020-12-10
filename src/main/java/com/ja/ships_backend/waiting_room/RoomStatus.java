package com.ja.ships_backend.waiting_room;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum RoomStatus {
    SUCCESS("Success"),
    NICKNAME_DUPLICATION("This nickname is taken!"),
    ROOM_IS_FULL("Room is full!"),
    NO_SUCH_PLAYER("There is no such a player in the room!");

    final String val;
}
