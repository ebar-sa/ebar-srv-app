package com.ebarapp.ebar.controller;

import com.ebarapp.ebar.model.Bar;
import com.ebarapp.ebar.model.BarTable;
import com.ebarapp.ebar.model.Option;
import com.ebarapp.ebar.model.Voting;
import com.ebarapp.ebar.service.BarTableService;
import com.ebarapp.ebar.service.OptionService;
import com.ebarapp.ebar.service.VotingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "dev")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
 class OptionControllerTests {

    @MockBean
    private OptionService optionService;

    @MockBean
    private VotingService votingService;

    @MockBean
    private BarTableService barTableService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {

        Option option1 = new Option();
        option1.setId(1);
        option1.setVotes(0);
        option1.setDescription("Option 1");

        Option option2 = new Option();
        option2.setId(2);
        option2.setVotes(0);
        option2.setDescription("Option 2");

        Set<Option> options  = new HashSet<>();
        options.add(option2);

        String participant = "user1";
        Set<String> participants = new HashSet<>();
        participants.add(participant);

        ZonedDateTime serverDefaultTime = ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault());
        ZoneId madridZone = ZoneId.of("Europe/Madrid");
        ZonedDateTime madridZoned = serverDefaultTime.withZoneSameInstant(madridZone);

        Voting voting = new Voting();
        voting.setId(1);
        voting.setTitle("Test 1");
        voting.setOpeningHour(madridZoned.toLocalDateTime().plusHours(1L));
        voting.setClosingHour(madridZoned.toLocalDateTime().plusHours(2L));
        voting.setDescription("Lorem Ipsum");
        voting.setTimer(null);
        voting.setVotersUsernames(new HashSet<>());
        voting.setOptions(options);


        Voting voting2 = new Voting();
        voting2.setId(3);
        voting2.setTitle("Test 2");
        voting2.setOpeningHour(madridZoned.toLocalDateTime().minusHours(1L));
        voting2.setClosingHour(madridZoned.toLocalDateTime().plusHours(2L));
        voting2.setDescription("Lorem Ipsum");
        voting2.setTimer(null);
        voting2.setVotersUsernames(participants);
        voting2.setOptions(options);

        Set<Voting> votings = new HashSet<>();
        votings.add(voting);
        votings.add(voting2);

        Bar bar = new Bar();
        bar.setId(1);
        bar.setContact("test1@example.com");
        bar.setLocation("Right Here");
        bar.setDescription("Lorem Ipsum");
        bar.setVotings(votings);
        bar.setBarTables(null);
        bar.setName("Test 1");
        bar.setClosingTime(null);
        bar.setOpeningTime(null);

        BarTable barTable = new BarTable();
        barTable.setId(1);
        barTable.setName("Table 1");
        barTable.setToken("aaa-111");
        barTable.setSeats(4);
        barTable.setBar(bar);

        Set<BarTable> bts = new HashSet<>();
        bts.add(barTable);
        bar.setBarTables(bts);

        List<String> tokens = new ArrayList<>();
        tokens.add("aaa-111");

        BDDMockito.given(this.votingService.getVotingById(1)).willReturn(voting);
        BDDMockito.given(this.votingService.getVotingById(2)).willReturn(null);
        BDDMockito.given(this.votingService.getVotingById(3)).willReturn(voting2);
        BDDMockito.given(this.votingService.createOrUpdateVoting(voting)).willReturn(voting);
        BDDMockito.given(this.optionService.getOptionById(1)).willReturn(option1);
        BDDMockito.given(this.optionService.getOptionById(2)).willReturn(null);
        BDDMockito.given(this.optionService.getOptionById(3)).willReturn(option2);
        BDDMockito.given(this.optionService.createOption(Mockito.any(Option.class))).willReturn(option1);
        BDDMockito.given(this.barTableService.getAllValidTokensByBarId(1)).willReturn(tokens);
    }

    @WithMockUser(username="admin", roles={"OWNER"})
    @Test
    void successCreateOption() throws Exception{
        String json = "{\n\"description\": \"Option 1\",\n \"votes\": \"0\"\n}";

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/voting/1/option")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }


    @WithMockUser(username="admin", roles={"OWNER"})
    @Test
    void failureCreateOption() throws Exception{
        String json = "{\n\"description\": \"Option 1\",\n \"votes\": \"0\"\n}";

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/voting/2/option")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @WithMockUser(username="admin", roles={"OWNER"})
    @Test
    void successDeleteOption() throws Exception{
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/voting/1/option/3"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @WithMockUser(username="admin", roles={"OWNER"})
    @Test
    void failureDeleteOptionNotFound() throws Exception{
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/voting/1/option/2"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @WithMockUser(username="admin", roles={"OWNER"})
    @Test
    void failureDeleteOptionVotingNotFound() throws Exception{
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/voting/2/option/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @WithMockUser(username="admin", roles={"OWNER"})
    @Test
    void failureDeleteOptionVotingHasStarted() throws Exception{
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/voting/3/option/1"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @WithMockUser(username="admin", roles={"OWNER"})
    @Test
    void failureDeleteOptionVotingNotContains() throws Exception{
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/voting/1/option/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @WithMockUser(username="admin", roles={"OWNER"})
    @Test
    void successGetOption() throws Exception{
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/option/1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @WithMockUser(username="admin", roles={"OWNER"})
    @Test
    void failureGetOption() throws Exception{
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/option/2"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @WithMockUser(username="user", roles={"CLIENT"})
    @Test
    void successVote() throws Exception{
        String token = "aaa-111";

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/bar/1/voting/3/option/1/vote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(token))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @WithMockUser(username="user", roles={"CLIENT"})
    @Test
    void failureVoteBadToken() throws Exception{
        String token = "aaa-112";

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/bar/2/voting/3/option/1/vote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(token))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @WithMockUser(username="user", roles={"CLIENT"})
    @Test
    void failureVoteVotingNotFound() throws Exception{
        String token = "aaa-111";

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/bar/1/voting/2/option/1/vote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(token))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @WithMockUser(username="user", roles={"CLIENT"})
    @Test
    void failureVoteNotActive() throws Exception{
        String token = "aaa-111";

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/bar/1/voting/1/option/1/vote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(token))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @WithMockUser(username="user1", roles={"CLIENT"})
    @Test
    void failureVoteCantVoteTwice() throws Exception{
        String token = "aaa-111";

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/bar/1/voting/3/option/1/vote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(token))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
