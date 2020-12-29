package com.ships.room;

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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.testng.Assert.assertEquals;

@AutoConfigureMockMvc
@WebMvcTest(GameRoomController.class)
public class GameRoomControllerHttpTest {
    private static final Player DUMMY_PLAYER_1 = new Player("DUMMY_NAME_1");
    private static final Player DUMMY_PLAYER_2 = new Player("DUMMY_NAME_@");
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
        MvcResult mvcResult = this.mockMvc.perform(get("/room"))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(mvcResult.getResponse().getContentType(), "application/json;charset=UTF-8");
    }

    @Test
    void shouldHttpGetReturnMessageWithTwoPlayersWhenTwoAreAlreadyInRoom() throws Exception {
        when(gameRoomService.getPlayerListInRoom()).thenReturn(List.of(DUMMY_PLAYER_1, DUMMY_PLAYER_2));
        MvcResult mvcResult = this.mockMvc.perform(get("/room"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(DUMMY_PLAYER_1.getName()))
                .andExpect(jsonPath("$[1].name").value(DUMMY_PLAYER_2.getName()))
                .andReturn();

        assertEquals(mvcResult.getResponse().getContentType(), "application/json;charset=UTF-8");
    }


    @Test
    public void shouldHttpDeleteReturnSuccess() throws Exception {
        when(gameRoomService.deletePlayer(refEq(DUMMY_PLAYER_1))).thenReturn(RoomStatus.SUCCESS);
        MvcResult mvcResult = this.mockMvc
                .perform(delete(String.format("/room/%s", DUMMY_PLAYER_1.getName())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(DUMMY_PLAYER_1.getName()))
                .andReturn();
        assertEquals(mvcResult.getResponse().getContentType(), "application/json;charset=UTF-8");
    }

    @Test
    public void shouldHttpDeleteReturnNoSuchPlayerMessage() throws Exception {
        when(gameRoomService.deletePlayer(any())).thenReturn(RoomStatus.NO_SUCH_PLAYER);
        MvcResult mvcResult = this.mockMvc
                .perform(delete(String.format("/room/%s", DUMMY_PLAYER_1.getName())))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string(RoomStatus.NO_SUCH_PLAYER.val))
                .andReturn();
        assertEquals(mvcResult.getResponse().getContentType(), "application/json;charset=UTF-8");
    }

    @Test
    public void shouldHttpDeleteAllPlayersReturnSuccess() throws Exception {
        when(gameRoomService.deletePlayer(any())).thenReturn(RoomStatus.NO_SUCH_PLAYER);
        MvcResult mvcResult = this.mockMvc
                .perform(delete("/room"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("[]"))
                .andReturn();
        assertEquals(mvcResult.getResponse().getContentType(), "application/json;charset=UTF-8");
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
        assertEquals(mvcResult.getResponse().getContentType(), "application/json;charset=UTF-8");
    }

    @Test
    public void shouldHttpGetReturnOnePlayer() throws Exception {
        when(gameRoomService.isPlayerInRoom(DUMMY_PLAYER_1)).thenReturn(RoomStatus.SUCCESS);
        MvcResult mvcResult = this.mockMvc
                .perform(get(String.format("/room/%s", DUMMY_PLAYER_1.getName())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(DUMMY_PLAYER_1.getName()))
                .andReturn();
        assertEquals(mvcResult.getResponse().getContentType(), "application/json;charset=UTF-8");
    }

    @Test
    public void shouldHttpGetReturnErrorStatus() throws Exception {
        when(gameRoomService.isPlayerInRoom(DUMMY_PLAYER_1)).thenReturn(RoomStatus.NO_SUCH_PLAYER);
        MvcResult mvcResult = this.mockMvc
                .perform(get(String.format("/room/%s", DUMMY_PLAYER_1.getName())))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andReturn();
        assertEquals(mvcResult.getResponse().getContentType(), "application/json;charset=UTF-8");
    }


    @Test
    public void shouldHttpPostReturnOkStatus() throws Exception {
        when(gameRoomService.addPlayer(DUMMY_PLAYER_1)).thenReturn(RoomStatus.SUCCESS);
        MvcResult mvcResult = this.mockMvc
                .perform(post(String.format("/room/%s", DUMMY_PLAYER_1.getName())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(DUMMY_PLAYER_1.getName()))
                .andReturn();
        assertEquals(mvcResult.getResponse().getContentType(), "application/json;charset=UTF-8");
    }

    @DataProvider
    public Object[][] roomStatusErrors() {
        return new Object[][]{{RoomStatus.NICKNAME_DUPLICATION},
                {RoomStatus.ROOM_IS_FULL}};
    }

    @Test(dataProvider = "roomStatusErrors")
    public void shouldHttpPostReturnErrorStatus(RoomStatus roomStatus) throws Exception {
        when(gameRoomService.addPlayer(DUMMY_PLAYER_1)).thenReturn(roomStatus);
        MvcResult mvcResult = this.mockMvc
                .perform(post(String.format("/room/%s", DUMMY_PLAYER_1.getName())))
                .andDo(print())
                .andExpect(status().isConflict())
                .andReturn();
        assertEquals(mvcResult.getResponse().getContentType(), "application/json;charset=UTF-8");
    }


}