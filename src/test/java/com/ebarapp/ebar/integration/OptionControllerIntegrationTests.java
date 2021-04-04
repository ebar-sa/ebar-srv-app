package com.ebarapp.ebar.integration;

import com.ebarapp.ebar.model.Bar;
import com.ebarapp.ebar.model.Option;
import com.ebarapp.ebar.model.Voting;
import com.ebarapp.ebar.service.BarService;
import com.ebarapp.ebar.service.OptionService;
import com.ebarapp.ebar.service.VotingService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@ExtendWith(SpringExtension.class)
@ActiveProfiles(profiles = "dev")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class OptionControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VotingService votingService;
    private Integer votingId;
    private Integer voting2Id;

    @Autowired
    private OptionService optionService;
    private Integer option2Id;
    private Integer optionId;

    @Autowired
    private BarService barService;
    private Integer barId;

    @BeforeEach
    private void setUp(){

        Bar bar = new Bar();
        bar.setContact("test1@example.com");
        bar.setLocation("Right Here");
        bar.setDescription("Lorem Ipsum");
        bar.setVotings(Collections.emptySet());
        bar.setBarTables(null);
        bar.setName("Test 1");
        bar.setClosingTime(null);
        bar.setOpeningTime(null);

        Option option = new Option();
        option.setVotes(0);
        option.setDescription("Option 1");

        Set<Option> options = new HashSet<>();
        options.add(option);

        Option option2 = new Option();
        option2.setVotes(0);
        option2.setDescription("Option 2");

        Set<Option> options2 = new HashSet<>();
        options2.add(option2);

        Voting voting = new Voting();
        voting.setTitle("Test 1");
        voting.setOpeningHour(LocalDateTime.of(2021, 12, 30, 20, 0, 0, 0));
        voting.setClosingHour(LocalDateTime.of(2021, 12, 30, 22, 0, 0, 0));
        voting.setDescription("Lorem Ipsum");
        voting.setTimer(null);
        voting.setVotersUsernames(Collections.emptySet());
        voting.setOptions(options);

        Voting voting2 = new Voting();
        voting2.setTitle("Test 2");
        voting2.setOpeningHour(LocalDateTime.now().minusHours(1L));
        voting2.setClosingHour(LocalDateTime.now().plusHours(1L));
        voting2.setDescription("Lorem Ipsum");
        voting2.setTimer(null);
        voting2.setVotersUsernames(Collections.emptySet());
        voting2.setOptions(options2);

        Set<Voting> votings = new HashSet<>();
        votings.add(voting);
        votings.add(voting2);

        bar.setVotings(votings);
        this.barService.createBar(bar);

        this.votingService.createOrUpdateVoting(voting);
        this.votingService.createOrUpdateVoting(voting2);
        this.barId = bar.getId();
        this.votingId = voting.getId();
        this.voting2Id = voting2.getId();
        this.optionService.createOption(option);
        this.optionService.createOption(option2);
        this.optionId = option.getId();
        this.option2Id = option2.getId();
        this.votingService.createOrUpdateVoting(voting);
        this.votingService.createOrUpdateVoting(voting2);
    }

    @AfterEach
    void tearDown() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/bar/"+barId+"/voting/"+votingId));
        this.barService.removeBar(barId);
    }

    @WithMockUser(username="admin", roles={"OWNER"})
    @Test
    void successCreateOption() throws Exception{
        String json = "{\n\"description\": \"Option 2\",\n \"votes\": \"0\"\n}";

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/voting/"+voting2Id+"/option")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @WithMockUser(username="admin", roles={"OWNER"})
    @Test
    void failureCreateOption() throws Exception{
        String json = "{\n\"description\": \"Option 2\",\n \"votes\": \"0\"\n}";

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/voting/9000/option")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @WithMockUser(username="admin", roles={"OWNER"})
    @Test
    void successDeleteOption() throws Exception{
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/voting/"+votingId+"/option/"+optionId))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @WithMockUser(username="admin", roles={"OWNER"})
    @Test
    void failureDeleteOption() throws Exception{
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/voting/9000/option/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @WithMockUser(username="user", roles={"CLIENT"})
    @Test
    void successVote() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/voting/"+voting2Id+"/option/"+option2Id+"/vote"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @WithMockUser(username="user", roles={"CLIENT"})
    @Test
    void failureVote() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/voting/9000/option/1/vote"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

}
