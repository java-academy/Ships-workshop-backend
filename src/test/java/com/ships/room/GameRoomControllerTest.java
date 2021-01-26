package com.ships.room;

import org.hamcrest.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.testng.Assert.assertEquals;

@WebMvcTest(GameRoomController.class)
public class GameRoomControllerTest {
    private static final Player DUMMY_PLAYER_1 = new Player("DUMMY_NAME_1");
    private static final Player DUMMY_PLAYER_2 = new Player("DUMMY_NAME_2");
    private static final String ROOM_API = "/room";
    private static final String ROOM_WITH_PLAYER_NAME_API = ROOM_API + "/{name}";
    private static final String DUMMY_TOKEN = "DUMMY_TOKEN";
    @Mock
    private GameRoomService gameRoomService;

    @Mock
    private HttpServletRequest httpServletRequestMock;

    @Mock
    private HttpSession httpSessionMock;

    private MockMvc mockMvc;

    Map<String, String> createAddPlayerResult(RoomStatus roomStatus) {
        return Map.of("status", roomStatus.name(), "token", DUMMY_TOKEN);
    }

    @BeforeMethod
    void setup() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(new GameRoomController(gameRoomService)).build();
    }

    @Test
    void shouldHttpGetReturnEmptyMessageWhenNobodyIsInRoom() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(ROOM_API))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(mvcResult.getResponse().getContentType(), "application/json");
    }

    @Test
    void shouldHttpGetReturnMessageWithTwoPlayersWhenTwoAreAlreadyInRoom() throws Exception {
        when(gameRoomService.getPlayerListInRoom()).thenReturn(List.of(DUMMY_PLAYER_1, DUMMY_PLAYER_2));
        when(gameRoomService.updateSession(any())).thenReturn(true);
        MvcResult mvcResult = this.mockMvc.perform(get(ROOM_API))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(DUMMY_PLAYER_1.getName()))
                .andExpect(jsonPath("$[1].name").value(DUMMY_PLAYER_2.getName()))
                .andReturn();

        assertEquals(mvcResult.getResponse().getContentType(), "application/json");
    }

    @Test
    void shouldHttpGetReturnEmptyListWhenUpdateSessionReturnsFalse() throws Exception {
        when(gameRoomService.updateSession(any())).thenReturn(false);
        MvcResult mvcResult = this.mockMvc.perform(get(ROOM_API))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty())
                .andReturn();

        assertEquals(mvcResult.getResponse().getContentType(), "application/json");
    }


    @Test
    void shouldAddPlayerReturnSuccessWhenRoomIsEmpty() throws Exception {
        Map<String, String> response = createAddPlayerResult(RoomStatus.SUCCESS);
        when(gameRoomService.addPlayer(eq(DUMMY_PLAYER_2.getName()), any()))
                .thenReturn(createAddPlayerResult(RoomStatus.SUCCESS));
        MvcResult mvcResult = this.mockMvc
                .perform(post(ROOM_WITH_PLAYER_NAME_API, DUMMY_PLAYER_2.getName()))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", Matchers.is(response.get("status"))))
                .andExpect(jsonPath("$.token", Matchers.is(response.get("token"))))
                .andExpect(header().string("Location", "http://localhost/room/DUMMY_NAME_2"))
                .andReturn();

        assertEquals(mvcResult.getResponse().getContentType(), "application/json");
    }

    @DataProvider
    Object[] errorCasesInHttpPostResponse() {
        return new Object[] {
                RoomStatus.NICKNAME_DUPLICATION,
                RoomStatus.ROOM_IS_FULL,
                RoomStatus.DUPLICATED_SESSION
        };
    }


    @Test(dataProvider = "errorCasesInHttpPostResponse")
    void shouldHttpPostReturnMessageWithNicknameDuplicationWhenSuchStatusOccurred(RoomStatus roomStatus) throws Exception {
        when(gameRoomService.addPlayer(eq(DUMMY_PLAYER_1.getName()), any()))
                .thenReturn(createAddPlayerResult(roomStatus));
        MvcResult mvcResult = this.mockMvc.perform(post(ROOM_WITH_PLAYER_NAME_API, DUMMY_PLAYER_1.getName()))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(header().string("Location", "http://localhost/room/DUMMY_NAME_1"))
                .andExpect(jsonPath("$", Matchers.is(roomStatus.name())))
                .andReturn();

        assertEquals(mvcResult.getResponse().getContentType(), "application/json");
    }

    @Test
    void shouldHttpDeleteReturnSuccess() throws Exception {
        when(gameRoomService.removePlayerAndTheirSession(DUMMY_PLAYER_1.getName(), httpServletRequestMock)).thenReturn(RoomStatus.SUCCESS);
        when(httpServletRequestMock.getSession(false)).thenReturn(httpSessionMock);
        MvcResult mvcResult = this.mockMvc
                .perform(delete(ROOM_WITH_PLAYER_NAME_API, DUMMY_PLAYER_1.getName()))
                .andDo(print())
                .andExpect(status().isMultiStatus())
                .andExpect(jsonPath("$").doesNotExist())
                .andReturn();

        assertEquals(mvcResult.getResponse().getContentType(), "application/json");
    }

    @Test
    void shouldHttpDeleteReturnNoSuchPlayerMessage() throws Exception {
        when(gameRoomService.removePlayerAndTheirSession(DUMMY_PLAYER_1.getName(), httpServletRequestMock)).thenReturn(RoomStatus.NO_SUCH_PLAYER);
        when(httpServletRequestMock.getSession(false)).thenReturn(null);
        MvcResult mvcResult = this.mockMvc
                .perform(delete(ROOM_WITH_PLAYER_NAME_API, DUMMY_PLAYER_1.getName()))
                .andDo(print())
                .andExpect(status().isMultiStatus())
                .andExpect(jsonPath("$").doesNotExist())
                .andReturn();

        assertEquals(mvcResult.getResponse().getContentType(), "application/json");
    }
}