package com.ships.room;

import org.hamcrest.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.testng.Assert.assertEquals;

@AutoConfigureMockMvc
@WebMvcTest(GameRoomController.class)
public class GameRoomControllerHttpTest {
    private static final Player DUMMY_PLAYER_1 = new Player("DUMMY_NAME_1");
    private static final Player DUMMY_PLAYER_2 = new Player("DUMMY_NAME_2");
    private static final String ROOM_API = "/room";
    private static final String ROOM_WITH_PLAYER_NAME_API = ROOM_API + "/{name}";
    @Mock
    private GameRoomService gameRoomService;

    private MockMvc mockMvc;

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
        MvcResult mvcResult = this.mockMvc.perform(get(ROOM_API))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(DUMMY_PLAYER_1.getName()))
                .andExpect(jsonPath("$[1].name").value(DUMMY_PLAYER_2.getName()))
                .andReturn();

        assertEquals(mvcResult.getResponse().getContentType(), "application/json");
    }


    @Test
    void shouldHttpDeleteReturnSuccess() throws Exception {
        when(gameRoomService.deletePlayer(DUMMY_PLAYER_1.getName())).thenReturn(RoomStatus.SUCCESS);
        MvcResult mvcResult = this.mockMvc
                .perform(delete(ROOM_WITH_PLAYER_NAME_API, DUMMY_PLAYER_1.getName()))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist())
                .andReturn();

        assertEquals(mvcResult.getResponse().getContentType(), "application/json");
    }

    @Test
    void shouldAddPlayerReturnSuccessWhenRoomIsEmpty() throws Exception {
        when(gameRoomService.addPlayer(eq(DUMMY_PLAYER_2.getName()), any())).thenReturn(RoomStatus.SUCCESS);
        MvcResult mvcResult = this.mockMvc
                .perform(post(ROOM_WITH_PLAYER_NAME_API, DUMMY_PLAYER_2.getName()))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").doesNotExist())
                .andExpect(header().string("Location", "http://localhost/room/DUMMY_NAME_2"))
                .andReturn();

        assertEquals(mvcResult.getResponse().getContentType(), "application/json");
    }

    @Test
    void shouldHttpDeleteReturnNoSuchPlayerMessage() throws Exception {
        when(gameRoomService.deletePlayer(DUMMY_PLAYER_1.getName())).thenReturn(RoomStatus.NO_SUCH_PLAYER);
        MvcResult mvcResult = this.mockMvc
                .perform(delete(ROOM_WITH_PLAYER_NAME_API, DUMMY_PLAYER_1.getName()))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$", Matchers.is(RoomStatus.NO_SUCH_PLAYER.name())))
                .andReturn();
        assertEquals(mvcResult.getResponse().getContentType(), "application/json");
    }

    @Test
    public void shouldHttpDeleteAllPlayersReturnSuccess() throws Exception {
        when(gameRoomService.deletePlayer(any())).thenReturn(RoomStatus.NO_SUCH_PLAYER);
        MvcResult mvcResult = this.mockMvc
                .perform(delete("/room"))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist())
                .andReturn();
        assertEquals(mvcResult.getResponse().getContentType(), "application/json");
    }

    @Test
    public void shouldHttpGetReturnListOfAllPlayers() throws Exception {
        when(gameRoomService.getPlayerListInRoom()).thenReturn(List.of(DUMMY_PLAYER_1, DUMMY_PLAYER_2));
        MvcResult mvcResult = this.mockMvc
                .perform(get("/room"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(DUMMY_PLAYER_1.getName()))
                .andExpect(jsonPath("$[1].name").value(DUMMY_PLAYER_2.getName()))
                .andReturn();
        assertEquals(mvcResult.getResponse().getContentType(), "application/json");
    }

    @Test
    void shouldHttpDeleteAllPlayersInARoom() throws Exception {
        MvcResult mvcResult = this.mockMvc
                .perform(delete(ROOM_API))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andReturn();

        assertEquals(mvcResult.getResponse().getContentType(), "application/json");
        verify(gameRoomService, times(1)).deleteAllPlayers();
    }

    @DataProvider
    public Object[][] roomStatusErrors() {
        return new Object[][]{{RoomStatus.NICKNAME_DUPLICATION},
                {RoomStatus.ROOM_IS_FULL}};
    }


    @Test(dataProvider = "roomStatusErrors")
    void shouldHttpPostReturnMessageWithNicknameDuplicationWhenSuchStatusOccurred(RoomStatus roomStatus) throws Exception {
        when(gameRoomService.addPlayer(eq(DUMMY_PLAYER_1.getName()), any())).thenReturn(roomStatus);
        MvcResult mvcResult = this.mockMvc.perform(post(ROOM_WITH_PLAYER_NAME_API, DUMMY_PLAYER_1.getName()))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(header().string("Location", "http://localhost/room/DUMMY_NAME_1"))
                .andExpect(jsonPath("$", Matchers.is(roomStatus.name())))
                .andReturn();
        assertEquals(mvcResult.getResponse().getContentType(), "application/json");
    }

}