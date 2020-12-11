package com.ja.ships_backend.waiting_room;

import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
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
    public void shouldAddPlayerReturnSuccessStatusIfThereIsPlaceInRoom() {
        assertEquals(sut.addPlayer(DUMMY_PLAYER_1), RoomStatus.SUCCESS);
    }

    @Test
    public void shouldAddPlayerReturnNicknameDuplicationStatusWhenPersonInRoomHasThisNickname() {
        sut.addPlayer(DUMMY_PLAYER_1);
        assertEquals(sut.addPlayer(DUMMY_PLAYER_1), RoomStatus.NICKNAME_DUPLICATION);
    }

    @Test
    public void shouldAddPlayerReturnRoomIsFullStatusWhenPThereIsNotEnoughSpaceInRoom() {
        sut.addPlayer(DUMMY_PLAYER_1);
        sut.addPlayer(DUMMY_PLAYER_2);
        assertEquals(sut.addPlayer(DUMMY_PLAYER_3), RoomStatus.ROOM_IS_FULL);
    }

    @Test
    public void shouldDeletePlayerReturnNoSuchPlayerStatusWhenTryingToRemovePlayerWithDifferentNicknameThanRoomMembers(){
        sut.addPlayer(DUMMY_PLAYER_1);
        sut.addPlayer(DUMMY_PLAYER_2);
        assertEquals(sut.deletePlayer(DUMMY_PLAYER_3), RoomStatus.NO_SUCH_PLAYER);
    }

    @Test
    public void shouldDeletePlayerReturnSuccessStatusWhenTryingToRemovePlayerWhoAreInRoom(){
        sut.addPlayer(DUMMY_PLAYER_1);
        SoftAssert sa = new SoftAssert();
        sa.assertEquals(sut.deletePlayer(DUMMY_PLAYER_1), RoomStatus.SUCCESS, "Delete assert");
        sa.assertTrue(sut.getPlayerListInRoom().isEmpty(), "Empty assert");
        sa.assertAll();
    }
}