package com.ebarapp.ebar.integrations;

import com.ebarapp.ebar.model.Bar;
import com.ebarapp.ebar.model.Voting;
import com.ebarapp.ebar.service.BarService;
import com.ebarapp.ebar.service.VotingService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class VotingTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VotingService votingService;
    private Voting voting;
    private Integer votingId;

    @Autowired
    private BarService barService;
    private Bar bar;
    private Integer barId;

    @BeforeEach
    private void setUp() {

        this.bar = new Bar();
        this.bar.setContact("test1@example.com");
        this.bar.setLocation("Right Here");
        this.bar.setDescription("Lorem Ipsum");
        this.bar.setVotings(Collections.emptySet());
        this.bar.setBarTables(null);
        this.bar.setName("Test 1");
        this.bar.setClosingTime(null);
        this.bar.setOpeningTime(null);

        this.voting = new Voting();
        this.voting.setTitle("Test 1");
        this.voting.setOpeningHour(LocalDateTime.of(2021, 12, 30, 20, 0, 0, 0));
        this.voting.setClosingHour(LocalDateTime.of(2021, 12, 30, 22, 0, 0, 0));
        this.voting.setDescription("Lorem Ipsum");
        this.voting.setTimer(null);
        this.voting.setVotersUsernames(Collections.emptySet());
        this.voting.setOptions(Collections.emptySet());

        Set<Voting> votings = new HashSet<>();
        votings.add(voting);

        this.bar.setVotings(votings);
        barService.createBar(this.bar);

        votingService.createOrUpadteVoting(voting);
        barId = this.bar.getId();
        votingId = this.voting.getId();
    }

    @AfterEach
    void tearDown() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/bar/"+barId+"/voting/"+votingId));
        barService.removeBar(barId);
    }

    @WithMockUser(username="admin", roles={"OWNER"})
    @Test
    void successCreateVoting() throws Exception{
        String json = "{ \n \"title\": \"Voting Integration Test\",\n \"description\": \"Lorem Ipsum\",\n \"openingHour\": \"30-12-2021 18:10:00\",\n \"closingHour\": \"30-12-2021 20:00:00\",\n \"timer\": null,\n \"options\": [],\n \"votersUsernames\": [] \n}";

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/bar/"+this.barId+"/voting")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @WithMockUser(username = "user", roles = {"CLIENT"})
    @Test
    void successGetVoting() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/voting/"+this.votingId))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @WithMockUser(username = "admin", roles = {"OWNER"})
    @Test
    void successUpdateVoting() throws Exception {
        String json = "{ \n \"title\": \"Voting Integration Test\",\n \"description\": \"Lorem Ipsum modified\",\n \"openingHour\": \"30-12-2021 18:10:00\",\n \"closingHour\": \"30-12-2021 20:00:00\",\n \"timer\": null,\n \"options\": [],\n \"votersUsernames\": [] \n}";

        this.mockMvc.perform(MockMvcRequestBuilders.put("/api/voting/"+this.votingId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @WithMockUser(username = "admin", roles = {"OWNER"})
    @Test
    void successFinishVoting() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/voting/"+this.votingId+"/finish"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }


    @WithMockUser(username = "admin", roles = {"OWNER"})
    @Test
    void successDeleteVoting() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/bar/"+this.barId+"/voting/"+votingId))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @WithMockUser(username = "admin", roles = {"OWNER"})
    @Test
    void failureFinishVoting() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/voting/9000/finish"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @WithMockUser(username = "user", roles = {"CLIENT"})
    @Test
    void failureGetVoting() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/voting/9000"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
