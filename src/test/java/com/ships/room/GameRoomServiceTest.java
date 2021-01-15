package com.ships.room;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@SpringBootTest
public class GameRoomServiceTest {
    private static final Player DUMMY_PLAYER_1 = new Player("DUMMY_NAME_1", "DUMMY_ID_1");
    private static final Player DUMMY_PLAYER_2 = new Player("DUMMY_NAME_2", "DUMMY_ID_2");
    private static final Player DUMMY_PLAYER_3 = new Player("DUMMY_NAME_3", "DUMMY_ID_3");
    private GameRoomService sut;
    @Mock
    private MockHttpServletRequest mockHttpServletRequest;

    @Mock
    SessionContainer sessionContainerMock;
    SoftAssert sa;


    @BeforeMethod
    void init() {
        MockitoAnnotations.openMocks(this);
        mockHttpServletRequest = new MockHttpServletRequest();
        sut = new GameRoomService(sessionContainerMock);
        sa = new SoftAssert();
    }

    @AfterMethod
    void deleteAllPlayers() {
        sut.deleteAllPlayers();
    }

    @Test
    void shouldAddPlayerReturnSuccessStatusIfThereIsPlaceInRoom() {
        assertEquals(sut.addPlayer(DUMMY_PLAYER_1.getName(), mockHttpServletRequest), RoomStatus.SUCCESS);
    }

    @Test
    void shouldAddPlayerReturnNicknameDuplicationStatusWhenPersonInRoomHasThisNickname() {
        sut.addPlayer(DUMMY_PLAYER_1.getName(), mockHttpServletRequest);
        assertEquals(sut.addPlayer(DUMMY_PLAYER_1.getName(), mockHttpServletRequest), RoomStatus.NICKNAME_DUPLICATION);
    }

    @Test
    void shouldAddPlayerReturnRoomIsFullStatusWhenPThereIsNotEnoughSpaceInRoom() {
        sut.addPlayer(DUMMY_PLAYER_1.getName(), mockHttpServletRequest);
        sut.addPlayer(DUMMY_PLAYER_2.getName(), new MockHttpServletRequest());
        assertEquals(sut.addPlayer(DUMMY_PLAYER_3.getName(), mockHttpServletRequest), RoomStatus.ROOM_IS_FULL);
    }

    @Test
    void shouldDeletePlayerReturnNoSuchPlayerStatusWhenTryingToRemovePlayerWithDifferentNicknameThanRoomMembers() {
        sut.addPlayer(DUMMY_PLAYER_1.getName(), mockHttpServletRequest);
        sut.addPlayer(DUMMY_PLAYER_2.getName(), new MockHttpServletRequest());
        assertEquals(sut.deletePlayer(DUMMY_PLAYER_3.getName()), RoomStatus.NO_SUCH_PLAYER);
    }

    @Test
    void shouldDeleteAllPlayersInARoom(){
        sut.addPlayer(DUMMY_PLAYER_1.getName(), mockHttpServletRequest);
        sut.addPlayer(DUMMY_PLAYER_2.getName(), new MockHttpServletRequest());
        sut.deleteAllPlayers();
        assertTrue(sut.getPlayerListInRoom().isEmpty());
    }

    @Test
    void shouldDeletePlayerReturnSuccessStatusWhenTryingToRemovePlayerWhoAreInRoom() {
        sut.addPlayer(DUMMY_PLAYER_1.getName(), mockHttpServletRequest);
        sa.assertEquals(sut.deletePlayer(DUMMY_PLAYER_1.getName()), RoomStatus.SUCCESS, "Delete assert");
        sa.assertTrue(sut.getPlayerListInRoom().isEmpty(), "Empty assert");
        sa.assertAll();
    }

    @Test
    public void shouldDeleteAllPlayersReturnSuccessStatusWhenThereAreNoPlayersInRoom(){
        sut.addPlayer(DUMMY_PLAYER_1.getName(), mockHttpServletRequest);
        sut.deleteAllPlayers();
        sa.assertTrue(sut.getPlayerListInRoom().isEmpty(), " empty playerListInRoom assertion: ");
        sa.assertEquals(sut.addPlayer(DUMMY_PLAYER_1.getName(), mockHttpServletRequest),
                RoomStatus.DUPLICATED_SESSION,
                " duplicated session assertion: ");
        LoggedPlayer loggedPlayer = (LoggedPlayer) mockHttpServletRequest.getSession().getAttribute("user");
        loggedPlayer.removeOnlySession();
        mockHttpServletRequest.getSession().setAttribute("user", loggedPlayer);
        sa.assertEquals(sut.addPlayer(DUMMY_PLAYER_1.getName(), mockHttpServletRequest),
                RoomStatus.SUCCESS,
                " succes assertion: ");
        sa.assertAll();
    }

    @Test
    void shouldNotLetToEnterSeveralUsersWithSameHttpSession() {
        sut.addPlayer(DUMMY_PLAYER_1.getName(), mockHttpServletRequest);
        sut.addPlayer(DUMMY_PLAYER_3.getName(), mockHttpServletRequest);
        assertEquals(sut.getPlayerListInRoom().size(), 1);
    }
}