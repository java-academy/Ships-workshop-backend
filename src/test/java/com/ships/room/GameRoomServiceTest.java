package com.ships.room;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import static org.testng.Assert.*;

public class GameRoomServiceTest {
    private GameRoomService sut;
    private static final Player DUMMY_PLAYER_1 = new Player("DUMMY_NAME_1");
    private static final Player DUMMY_PLAYER_2 = new Player("DUMMY_NAME_2");
    private static final Player DUMMY_PLAYER_3 = new Player("DUMMY_NAME_3");

    @BeforeMethod
    void init() {
        sut = new GameRoomService();
    }

    @Test
    void shouldAddPlayerReturnSuccessStatusIfThereIsPlaceInRoom() {
        assertEquals(sut.addPlayer(DUMMY_PLAYER_1.getName()), RoomStatus.SUCCESS);
    }

    @Test
    void shouldAddPlayerReturnNicknameDuplicationStatusWhenPersonInRoomHasThisNickname() {
        sut.addPlayer(DUMMY_PLAYER_1.getName());
        assertEquals(sut.addPlayer(DUMMY_PLAYER_1.getName()), RoomStatus.NICKNAME_DUPLICATION);
    }

    @Test
    void shouldAddPlayerReturnRoomIsFullStatusWhenPThereIsNotEnoughSpaceInRoom() {
        sut.addPlayer(DUMMY_PLAYER_1.getName());
        sut.addPlayer(DUMMY_PLAYER_2.getName());
        assertEquals(sut.addPlayer(DUMMY_PLAYER_3.getName()), RoomStatus.ROOM_IS_FULL);
    }

    @Test
    void shouldDeletePlayerReturnNoSuchPlayerStatusWhenTryingToRemovePlayerWithDifferentNicknameThanRoomMembers(){
        sut.addPlayer(DUMMY_PLAYER_1.getName());
        sut.addPlayer(DUMMY_PLAYER_2.getName());
        assertEquals(sut.deletePlayer(DUMMY_PLAYER_3.getName()), RoomStatus.NO_SUCH_PLAYER);
    }

    @Test
    void shouldDeleteAllPlayersInARoom(){
        sut.addPlayer(DUMMY_PLAYER_1.getName());
        sut.addPlayer(DUMMY_PLAYER_2.getName());
        sut.deleteAllPlayers();
        assertTrue(sut.getPlayerListInRoom().isEmpty());
    }

    @Test
    void shouldDeletePlayerReturnSuccessStatusWhenTryingToRemovePlayerWhoAreInRoom(){
        sut.addPlayer(DUMMY_PLAYER_1.getName());
        SoftAssert sa = new SoftAssert();
        sa.assertEquals(sut.deletePlayer(DUMMY_PLAYER_1.getName()), RoomStatus.SUCCESS, "Delete assert");
        sa.assertTrue(sut.getPlayerListInRoom().isEmpty(), "Empty assert");
        sa.assertAll();
    }
}