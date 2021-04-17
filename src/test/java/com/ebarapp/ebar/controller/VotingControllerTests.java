package com.ebarapp.ebar.controller;

import com.ebarapp.ebar.model.Bar;
import com.ebarapp.ebar.model.Owner;
import com.ebarapp.ebar.model.Voting;
import com.ebarapp.ebar.model.type.RoleType;
import com.ebarapp.ebar.service.BarService;
import com.ebarapp.ebar.service.VotingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.*;
import java.util.*;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles(profiles = "dev")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class VotingControllerTests {

    @MockBean
    private VotingService votingService;

    @MockBean
    private BarService barService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {

        Owner owner = new Owner();
        owner.setFirstName("John");
        owner.setLastName("Doe");
        owner.setEmail("johndoe1@email.com");
        owner.setPassword("johndoe1");
        owner.setDni("11111111K");
        owner.setUsername("admin");
        owner.setPhoneNumber("666333999");

        Set<RoleType> roles = new HashSet<>();
        roles.add(RoleType.ROLE_OWNER);
        owner.setRoles(roles);

        ZonedDateTime serverDefaultTime = ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault());
        ZoneId madridZone = ZoneId.of("Europe/Madrid");
        ZonedDateTime madridZoned = serverDefaultTime.withZoneSameInstant(madridZone);

        Voting voting = new Voting();
        voting.setId(1);
        voting.setTitle("Test 1");
        voting.setOpeningHour(madridZoned.toLocalDateTime().minusHours(1L));
        voting.setClosingHour(madridZoned.toLocalDateTime().plusHours(1L));
        voting.setDescription("Lorem Ipsum");
        voting.setTimer(null);
        voting.setVotersUsernames(Collections.emptySet());
        voting.setOptions(Collections.emptySet());

        Voting voting2 = new Voting();
        voting2.setId(1);
        voting2.setTitle("Test 1");
        voting2.setDescription("Lorem Ipsum");
        voting2.setOpeningHour(madridZoned.toLocalDateTime().plusHours(1L));
        voting2.setClosingHour(null);
        voting2.setTimer(null);
        voting2.setVotersUsernames(new HashSet<>());
        voting2.setOptions(new HashSet<>());

        Set<Voting> votings = new HashSet<>();
        votings.add(voting);
        votings.add(voting2);

        BDDMockito.given(this.votingService.getVotingById(1)).willReturn(voting);
        BDDMockito.given(this.votingService.getVotingById(2)).willReturn(null);
        BDDMockito.given(this.votingService.getVotingById(3)).willReturn(voting2);
        BDDMockito.given(this.votingService.createOrUpdateVoting(voting)).willReturn(voting);
        BDDMockito.given(this.votingService.createOrUpdateVoting(voting2)).willReturn(voting2);
        BDDMockito.given(this.votingService.getVotingsByBarId(1)).willReturn(new ArrayList<>(votings));

        Bar bar = new Bar();
        bar.setId(1);
        bar.setContact("test1@example.com");
        bar.setLocation("Right Here");
        bar.setDescription("Lorem Ipsum");
        bar.setVotings(votings);
        bar.setBarTables(null);
        bar.setName("Test 1");
        bar.setPaidUntil(Date.from(Instant.parse("2025-01-01T22:30:00.00Z")));
        bar.setClosingTime(null);
        bar.setOpeningTime(null);
        bar.setOwner(owner);

        Set<Bar> bars = new HashSet<>();
        bars.add(bar);
        owner.setBar(bars);

        BDDMockito.given(this.barService.findBarById(1)).willReturn(bar);
        BDDMockito.given(this.barService.findBarById(2)).willReturn(null);
        BDDMockito.given(this.barService.isStaff(1, "admin")).willReturn(true);
    }

    @WithMockUser(username="admin", roles={"OWNER"})
    @Test
    void successCreateVoting() throws Exception{
    	String json = "{ \n \"title\": \"Voting Test\",\n \"description\": \"Lorem Ipsum\",\n \"openingHour\": \"30-12-2021 19:00:00\",\n \"closingHour\": \"30-12-2021 20:00:00\",\n \"timer\": null,\n \"options\": [],\n \"votersUsernames\": [] \n}";

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/bar/1/voting")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @WithMockUser(username="admin", roles={"OWNER"})
    @Test
    void failureCreateVoting() throws Exception{
        String json = "{ \n \"title\": \"Voting Test\",\n \"description\": \"Lorem Ipsum\",\n \"openingHour\": \"30-12-2021 18:10:00\",\n \"closingHour\": \"30-12-2021 20:00:00\",\n \"timer\": null,\n \"options\": [],\n \"votersUsernames\": [] \n}";

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/bar/2/voting")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @WithMockUser(username = "user", roles = {"CLIENT"})
    @Test
    void successGetVoting() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/voting/1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @WithMockUser(username = "user", roles = {"CLIENT"})
    @Test
    void failureGetVoting() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/voting/2"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @WithMockUser(username = "admin", roles = {"OWNER"})
    @Test
    void successDeleteVoting() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/bar/1/voting/3"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @WithMockUser(username = "admin", roles = {"OWNER"})
    @Test
    void failureDeleteVotingNotFoundInBar() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/bar/1/voting/2"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @WithMockUser(username = "admin", roles = {"OWNER"})
    @Test
    void failureDeleteVotingBarNotFound() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/bar/2/voting/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @WithMockUser(username = "admin", roles = {"OWNER"})
    @Test
    void successFinishVoting() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/bar/1/voting/3/finish"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @WithMockUser(username = "admin2", roles = {"OWNER"})
    @Test
    void failureFinishVotingNotStaff() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/bar/1/voting/3/finish"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @WithMockUser(username = "admin", roles = {"OWNER"})
    @Test
    void failureFinishVoting() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/bar/1/voting/2/finish"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @WithMockUser(username = "admin", roles = {"OWNER"})
    @Test
    void successUpdateVoting() throws Exception {
        String json = "{ \n \"title\": \"Voting Test\",\n \"description\": \"Lorem Ipsum\",\n \"openingHour\": \"30-12-2021 19:00:00\",\n \"closingHour\": \"30-12-2021 20:00:00\",\n \"timer\": null,\n \"options\": [],\n \"votersUsernames\": [] \n}";

        this.mockMvc.perform(MockMvcRequestBuilders.put("/api/bar/1/voting/3")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @WithMockUser(username = "admin", roles = {"OWNER"})
    @Test
    void failureUpdateVotingIsActive() throws Exception {
        String json = "{ \n \"title\": \"Voting Test\",\n \"description\": \"Lorem Ipsum\",\n \"openingHour\": \"30-12-2021 19:00:00\",\n \"closingHour\": \"30-12-2021 20:00:00\",\n \"timer\": null,\n \"options\": [],\n \"votersUsernames\": [] \n}";

        this.mockMvc.perform(MockMvcRequestBuilders.put("/api/bar/1/voting/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @WithMockUser(username = "admin2", roles = {"OWNER"})
    @Test
    void failureUpdateVotingNotStaff() throws Exception {
        String json = "{ \n \"title\": \"Voting Test\",\n \"description\": \"Lorem Ipsum\",\n \"openingHour\": \"30-12-2021 19:00:00\",\n \"closingHour\": \"30-12-2021 20:00:00\",\n \"timer\": null,\n \"options\": [],\n \"votersUsernames\": [] \n}";

        this.mockMvc.perform(MockMvcRequestBuilders.put("/api/bar/1/voting/3")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @WithMockUser(username = "admin", roles = {"OWNER"})
    @Test
    void failureUpdateVotingVoting() throws Exception {
        String json = "{ \n \"title\": \"Voting Test Modified\",\n \"description\": \"Lorem Ipsum\",\n \"openingHour\": \"30-12-2021 18:10:00\",\n \"closingHour\": \"30-12-2021 20:00:00\",\n \"timer\": null,\n \"options\": [],\n \"votersUsernames\": [] \n}";

        this.mockMvc.perform(MockMvcRequestBuilders.put("/api/bar/1/voting/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @WithMockUser(username = "admin", roles = {"OWNER"})
    @Test
    void successGetVotingsByBarId() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/bar/1/voting")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(2)));
    }

    @Test
    void failureGetVotingsByBarIdUnauthorized() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/bar/1/voting")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @WithMockUser(username = "admin", roles = {"OWNER"})
    @Test
    void failureGetVotingsByBarIdUNotFound() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/bar/2000/voting")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

}
