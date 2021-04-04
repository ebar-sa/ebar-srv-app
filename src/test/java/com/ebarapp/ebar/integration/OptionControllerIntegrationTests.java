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
class OptionControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VotingService votingService;
    private Voting voting;
    private Voting voting2;
    private Integer votingId;
    private Integer voting2Id;

    @Autowired
    private OptionService optionService;
    private Option option;
    private Option option2;
    private Integer option2Id;
    private Integer optionId;

    @Autowired
    private BarService barService;
    private Bar bar;
    private Integer barId;

    @BeforeEach
    private void setUp(){

        this.bar = new Bar();
        this.bar.setContact("test1@example.com");
        this.bar.setLocation("Right Here");
        this.bar.setDescription("Lorem Ipsum");
        this.bar.setVotings(Collections.emptySet());
        this.bar.setBarTables(null);
        this.bar.setName("Test 1");
        this.bar.setClosingTime(null);
        this.bar.setOpeningTime(null);

        this.option = new Option();
        this.option.setVotes(0);
        this.option.setDescription("Option 1");

        Set<Option> options = new HashSet<>();
        options.add(this.option);

        this.option2 = new Option();
        this.option2.setVotes(0);
        this.option2.setDescription("Option 2");

        Set<Option> options2 = new HashSet<>();
        options2.add(this.option2);

        this.voting = new Voting();
        this.voting.setTitle("Test 1");
        this.voting.setOpeningHour(LocalDateTime.of(2021, 12, 30, 20, 0, 0, 0));
        this.voting.setClosingHour(LocalDateTime.of(2021, 12, 30, 22, 0, 0, 0));
        this.voting.setDescription("Lorem Ipsum");
        this.voting.setTimer(null);
        this.voting.setVotersUsernames(Collections.emptySet());
        this.voting.setOptions(options);

        this.voting2 = new Voting();
        this.voting2.setTitle("Test 2");
        this.voting2.setOpeningHour(LocalDateTime.now().minusHours(1L));
        this.voting2.setClosingHour(LocalDateTime.now().plusHours(1L));
        this.voting2.setDescription("Lorem Ipsum");
        this.voting2.setTimer(null);
        this.voting2.setVotersUsernames(Collections.emptySet());
        this.voting2.setOptions(options2);

        Set<Voting> votings = new HashSet<>();
        votings.add(this.voting);
        votings.add(this.voting2);

        this.bar.setVotings(votings);
        this.barService.createBar(this.bar);

        this.votingService.createOrUpdateVoting(this.voting);
        this.votingService.createOrUpdateVoting(this.voting2);
        this.barId = this.bar.getId();
        this.votingId = this.voting.getId();
        this.voting2Id = this.voting2.getId();
        this.optionService.createOption(this.option);
        this.optionService.createOption(this.option2);
        this.optionId = this.option.getId();
        this.option2Id = this.option2.getId();
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
