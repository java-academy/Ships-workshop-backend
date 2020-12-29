package com.ships.room;

import org.testng.annotations.AfterMethod;
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

    @AfterMethod
    void deleteAllPlayers() {
        sut.deleteAllPlayers();
    }

    @Test
    public void shouldAddPlayerReturnSuccessStatusIfThereIsNeedForAnotherPlayer() {
        assertEquals(sut.addPlayer(DUMMY_PLAYER_1), RoomStatus.SUCCESS);
    }

    @Test
    public void shouldAddPlayerReturnNameDuplicationStatusWhenPersonInRoomHasThisNickname() {
        sut.addPlayer(DUMMY_PLAYER_1);
        assertEquals(sut.addPlayer(DUMMY_PLAYER_1), RoomStatus.NICKNAME_DUPLICATION);
    }

    @Test
    void shouldAddPlayerReturnRoomIsFullStatusWhenPThereIsNotEnoughSpaceInRoom() {
        sut.addPlayer(DUMMY_PLAYER_1);
        sut.addPlayer(DUMMY_PLAYER_2);
        assertEquals(sut.addPlayer(DUMMY_PLAYER_3), RoomStatus.ROOM_IS_FULL);
    }

    @Test
    public void shouldDeletePlayerReturnNoSuchPlayerStatusWhenTryingToRemovePlayerWithDifferentNicknameThanRoomMembers() {
        sut.addPlayer(DUMMY_PLAYER_1);
        sut.addPlayer(DUMMY_PLAYER_2);
        assertEquals(sut.deletePlayer(DUMMY_PLAYER_3), RoomStatus.NO_SUCH_PLAYER);
    }

    @Test
    public void shouldDeletePlayerReturnSuccessStatusWhenTryingToRemovePlayerWhoAreInRoom() {
        sut.addPlayer(DUMMY_PLAYER_1);
        SoftAssert sa = new SoftAssert();
        sa.assertEquals(sut.deletePlayer(DUMMY_PLAYER_1), RoomStatus.SUCCESS, "Delete assert");
        sa.assertTrue(sut.getPlayerListInRoom().isEmpty(), "Empty assert");
        sa.assertAll();
    }

    @Test
    public void shouldDeleteAllPlayersReturnSuccessStatusWhenThereIsNoPlayerInRoom(){
        sut.deleteAllPlayers();
    }

    @Test
    public void shouldDeleteAllPlayersReturnSuccessStatusWhenThereAreNoPlayersInRoom(){
        sut.addPlayer(DUMMY_PLAYER_1);
        sut.addPlayer(DUMMY_PLAYER_3);
        sut.deleteAllPlayers();
    }

    @Test
    public void shouldReturnSuccessWhenAskingIfThePlayerIsInRoom(){
        sut.addPlayer(DUMMY_PLAYER_1);
        assertEquals(sut.isPlayerInRoom(DUMMY_PLAYER_1), RoomStatus.SUCCESS);
    }

    @Test
    public void shouldReturnNoSuchPlayerWhenAskingIfThePlayerIsInRoom(){
        sut.addPlayer(DUMMY_PLAYER_1);
        assertEquals(sut.isPlayerInRoom(DUMMY_PLAYER_2), RoomStatus.NO_SUCH_PLAYER);
    }
}