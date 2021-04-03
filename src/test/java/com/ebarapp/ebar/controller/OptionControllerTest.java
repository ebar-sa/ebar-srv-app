package com.ebarapp.ebar.controller;

import com.ebarapp.ebar.model.Bar;
import com.ebarapp.ebar.model.Option;
import com.ebarapp.ebar.model.Voting;
import com.ebarapp.ebar.service.OptionService;
import com.ebarapp.ebar.service.VotingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
 class OptionControllerTest {

    @MockBean
    private OptionService optionService;

    @MockBean
    private VotingService votingService;


    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {

        Option option1 = new Option();
        option1.setId(1);
        option1.setVotes(0);
        option1.setDescription("Option 1");

        Option option2 = new Option();
        option1.setId(2);
        option1.setVotes(0);
        option1.setDescription("Option 2");

        Set<Option> options  = new HashSet<>();
        options.add(option2);

        Voting voting = new Voting();
        voting.setId(1);
        voting.setTitle("Test 1");
        voting.setOpeningHour(LocalDateTime.now().plusHours(1L));
        voting.setClosingHour(LocalDateTime.now().plusHours(2L));
        voting.setDescription("Lorem Ipsum");
        voting.setTimer(null);
        voting.setVotersUsernames(Collections.emptySet());
        voting.setOptions(Collections.emptySet());

        Voting voting2 = new Voting();
        voting2.setId(3);
        voting2.setTitle("Test 2");
        voting2.setOpeningHour(LocalDateTime.now().minusHours(1L));
        voting2.setClosingHour(LocalDateTime.now().plusHours(2L));
        voting2.setDescription("Lorem Ipsum");
        voting2.setTimer(null);
        voting2.setVotersUsernames(Collections.emptySet());
        voting2.setOptions(options);

        Set<Voting> votings = new HashSet<>();
        votings.add(voting);
        votings.add(voting2);

        BDDMockito.given(this.votingService.getVotingById(1)).willReturn(voting);
        BDDMockito.given(this.votingService.getVotingById(2)).willReturn(null);
        BDDMockito.given(this.votingService.getVotingById(3)).willReturn(voting2);
        BDDMockito.given(this.votingService.createOrUpdateVoting(voting)).willReturn(voting);
        BDDMockito.given(this.optionService.getOptionById(1)).willReturn(option1);
        BDDMockito.given(this.optionService.createOption(option1)).willReturn(option1);

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
    }

    @WithMockUser(username="admin", roles={"OWNER"})
    @Disabled
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
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/voting/1/option/1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @WithMockUser(username="admin", roles={"OWNER"})
    @Test
    void failureDeleteOption() throws Exception{
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/voting/2/option/1"))
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
    @Disabled
    @Test
    void successVote() throws Exception{
        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/voting/3/option/1/vote"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
