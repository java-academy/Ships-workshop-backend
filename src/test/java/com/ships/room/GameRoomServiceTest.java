package com.ships.room;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.Map;

import static com.ships.room.GameRoomService.EMPTY_TOKEN;
import static com.ships.room.GameRoomService.MAX_INACTIVE_INTERVAL_IN_ROOM;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@SpringBootTest
public class GameRoomServiceTest {
    private static final Player DUMMY_PLAYER_1 = new Player("DUMMY_NAME_1");
    private static final Player DUMMY_PLAYER_2 = new Player("DUMMY_NAME_2");
    private static final Player DUMMY_PLAYER_3 = new Player("DUMMY_NAME_3");
    private static final String DUMMY_TOKEN_1 = "DUMMY_TOKEN_1";
    private static final String DUMMY_TOKEN_2 = "DUMMY_TOKEN_2";
    private static final String DUMMY_TOKEN_3 = "DUMMY_TOKEN_3";
    private GameRoomService sut;
    private MockHttpServletRequest mockHttpServletRequest;
    private MockHttpSession mockHttpSession;

    SoftAssert sa;

    Map<String, String> createAddPlayerResult(RoomStatus roomStatus, String token) {
        return Map.of("status", roomStatus.name(), "token", token);
    }

    @BeforeMethod
    void init() {
        MockitoAnnotations.openMocks(this);
        sut = new GameRoomService();
        mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpSession = new MockHttpSession();
        sa = new SoftAssert();
    }

    @AfterMethod
    void deleteAllPlayers() {
        sut.deleteAllPlayers();
    }

    @Test
    void shouldAddPlayerReturnSuccessStatusIfThereIsPlaceInRoom() {
        MockHttpServletRequest mockHttpServletRequestMock = mock(MockHttpServletRequest.class);
        MockHttpSession mockHttpSessionMock = mock(MockHttpSession.class);
        when(mockHttpServletRequestMock.getSession(true)).thenReturn(mockHttpSessionMock);
        when(mockHttpSessionMock.getId()).thenReturn(DUMMY_TOKEN_1);

        assertEquals(sut.addPlayer(DUMMY_PLAYER_1.getName(), mockHttpServletRequestMock),
                createAddPlayerResult(RoomStatus.SUCCESS, DUMMY_TOKEN_1));
    }

    @Test
    void shouldAddPlayerReturnNicknameDuplicationStatusWhenPersonInRoomHasThisNickname() {
        sut.addPlayer(DUMMY_PLAYER_1.getName(), mockHttpServletRequest);
        assertEquals(sut.addPlayer(DUMMY_PLAYER_1.getName(), mockHttpServletRequest),
                createAddPlayerResult(RoomStatus.NICKNAME_DUPLICATION, EMPTY_TOKEN));
    }

    @Test
    void shouldAddPlayerReturnRoomIsFullStatusWhenPThereIsNotEnoughSpaceInRoom() {
        MockHttpServletRequest secondServlet = new MockHttpServletRequest();

        sut.addPlayer(DUMMY_PLAYER_1.getName(), mockHttpServletRequest);
        sut.addPlayer(DUMMY_PLAYER_2.getName(), secondServlet);

        assertEquals(sut.addPlayer(DUMMY_PLAYER_3.getName(), mockHttpServletRequest),
                createAddPlayerResult(RoomStatus.ROOM_IS_FULL, EMPTY_TOKEN));
    }

    @Test
    void shouldDeletePlayerReturnNoSuchPlayerStatusWhenTryingToRemovePlayerWithDifferentNicknameThanRoomMembers() {
        sut.addPlayer(DUMMY_PLAYER_1.getName(), mockHttpServletRequest);
        sut.addPlayer(DUMMY_PLAYER_2.getName(), new MockHttpServletRequest());
        assertEquals(sut.deletePlayer(DUMMY_PLAYER_3.getName()), RoomStatus.NO_SUCH_PLAYER);
    }

    @Test
    void shouldDeleteAllPlayersInARoom() {
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
    public void shouldDeleteAllPlayersReturnSuccessStatusWhenThereAreNoPlayersInRoom() {
        sut.addPlayer(DUMMY_PLAYER_1.getName(), mockHttpServletRequest);

        sut.deleteAllPlayers();

        sa.assertTrue(sut.getPlayerListInRoom().isEmpty(), " empty playerListInRoom assertion: ");
        sa.assertEquals(sut.addPlayer(DUMMY_PLAYER_1.getName(), mockHttpServletRequest).get("status"),
                RoomStatus.DUPLICATED_SESSION.name(),
                " duplicated session assertion: ");

        mockHttpServletRequest.getSession().invalidate();

        sa.assertEquals(sut.addPlayer(DUMMY_PLAYER_1.getName(), mockHttpServletRequest).get("status"),
                RoomStatus.SUCCESS.name(), " succes assertion: ");
        sa.assertAll();
    }

    @Test
    void shouldNotLetToEnterSeveralUsersWithSameHttpSession() {
        sut.addPlayer(DUMMY_PLAYER_1.getName(), mockHttpServletRequest);
        sut.addPlayer(DUMMY_PLAYER_3.getName(), mockHttpServletRequest);
        assertEquals(sut.getPlayerListInRoom().size(), 1);
    }

    @Test
    void shouldNotRemovePlayersWhenAmountOfPlayersInRoomEqualsTo2AndSessionsAreDestroyed() {
        MockHttpServletRequest secondServlet = new MockHttpServletRequest();

        sut.addPlayer(DUMMY_PLAYER_1.getName(), mockHttpServletRequest);
        sut.addPlayer(DUMMY_PLAYER_2.getName(), secondServlet);
        sut.updateSession(mockHttpServletRequest);
        sut.updateSession(secondServlet);
        mockHttpServletRequest.getSession().invalidate();
        secondServlet.getSession().invalidate();

        assertEquals(sut.getPlayerListInRoom().size(), 2);
    }

    @Test
    void shouldVerifyIfMaxInactiveIntervalIsRefreshed() {
        MockHttpServletRequest servletMock = mock(MockHttpServletRequest.class);
        MockHttpSession sessionMock = mock(MockHttpSession.class);

        when(servletMock.getSession(false)).thenReturn(sessionMock);
        when(sessionMock.getAttribute(any())).thenReturn(new LoggedPlayer());

        sut.updateSession(servletMock);

        verify(sessionMock).setMaxInactiveInterval(MAX_INACTIVE_INTERVAL_IN_ROOM);
    }

    @Test
    void shouldVerifyThatNeitherRefreshNorSessionRemovalIsExecuted() {
        MockHttpServletRequest servletMock = mock(MockHttpServletRequest.class);
        MockHttpSession sessionMock = mock(MockHttpSession.class);

        when(servletMock.getSession(false)).thenReturn(null);

        sut.updateSession(servletMock);

        verify(sessionMock, times(0)).setMaxInactiveInterval(MAX_INACTIVE_INTERVAL_IN_ROOM);
    }


}