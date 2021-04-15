package com.ebarapp.ebar.integration;

import com.ebarapp.ebar.model.*;
import com.ebarapp.ebar.repository.BarRepository;

import com.ebarapp.ebar.repository.VotingRepository;
import org.junit.jupiter.api.*;
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

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles(profiles = "dev")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class VotingControllerIntegrationTests {

    private static final int TEST_VOTING_ID = 1;
    private static final String TEST_VOTING_NAME = "¿Qué partido quieres ver?";
    private static final String TEST_VOTING_DESCRIPTION = "¿Qué partido quieres ver?";
    private static final LocalDateTime TEST_VOTING_OPENING_TIME = LocalDateTime.of(2021, 12, 30, 20, 0, 0, 0);
    private static final LocalDateTime TEST_VOTING_CLOSING_TIME = LocalDateTime.of(2021, 12, 30, 20, 0, 0, 0);
    private static final Set<String> TEST_VOTING_USERNAMES = new HashSet<>();
    private static final Set<Option> TEST_VOTING_OPTIONS = new HashSet<>();

    private static final int TEST_BAR_ID = 1;
    private static final String TEST_BAR_NAME = "Burger Food Porn";
    private static final String TEST_BAR_DESCRIPTION = "El templo de la hamburguesa.";
    private static final String TEST_BAR_CONTACT = "burgerfoodsevilla@gmail.com";
    private static final String TEST_BAR_LOCATION = "Avenida de Finlandia, 24, Sevilla";
    private static final Date TEST_BAR_OPENING_TIME = Date.from(Instant.parse("1970-01-01T13:00:00.00Z"));
    private static final Date TEST_BAR_CLOSING_TIME = Date.from(Instant.parse("1970-01-01T22:30:00.00Z"));
    private static final Set<Employee> TEST_BAR_EMPLOYEES = new HashSet<>();

    private static final String TEST_OWNER_FIRST_NAME = "John";
    private static final String TEST_OWNER_LAST_NAME = "Doe";
    private static final String TEST_OWNER_DNI = "11111111K";
    private static final String TEST_OWNER_EMAIL = "johndoe1@email.com";
    private static final String TEST_OWNER_PHONE_NUMBER = "666333999";
    private static final String TEST_OWNER_USERNAME = "admin";
    private static final String TEST_OWNER_PASSWORD = "johndoe1";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BarRepository barRepository;

    @MockBean
    private VotingRepository votingRepository;

    @BeforeEach
    void setUp() {

        Owner owner = new Owner();
        owner.setFirstName(TEST_OWNER_FIRST_NAME);
        owner.setLastName(TEST_OWNER_LAST_NAME);
        owner.setDni(TEST_OWNER_DNI);
        owner.setEmail(TEST_OWNER_EMAIL);
        owner.setPhoneNumber(TEST_OWNER_PHONE_NUMBER);
        owner.setUsername(TEST_OWNER_USERNAME);
        owner.setPassword(TEST_OWNER_PASSWORD);

        Bar bar = new Bar();
        bar.setId(TEST_BAR_ID);
        bar.setName(TEST_BAR_NAME);
        bar.setDescription(TEST_BAR_DESCRIPTION);
        bar.setContact(TEST_BAR_CONTACT);
        bar.setLocation(TEST_BAR_LOCATION);
        bar.setOpeningTime(TEST_BAR_OPENING_TIME);
        bar.setClosingTime(TEST_BAR_CLOSING_TIME);
        bar.setEmployees(TEST_BAR_EMPLOYEES);
        bar.setOwner(owner);

        Voting voting = new Voting();
        voting.setId(TEST_VOTING_ID);
        voting.setTitle(TEST_VOTING_NAME);
        voting.setDescription(TEST_VOTING_DESCRIPTION);
        voting.setOpeningHour(TEST_VOTING_OPENING_TIME);
        voting.setClosingHour(TEST_VOTING_CLOSING_TIME);
        voting.setTimer(null);
        voting.setVotersUsernames(TEST_VOTING_USERNAMES);
        voting.setOptions(TEST_VOTING_OPTIONS);

        Set<Voting> votings = new HashSet<>();
        List<Voting> votingList = new ArrayList<>();
        votings.add(voting);
        votingList.add(voting);
        bar.setVotings(votings);

        BDDMockito.given(this.barRepository.getBarById(TEST_BAR_ID)).willReturn(bar);
        BDDMockito.given(this.votingRepository.save(Mockito.any(Voting.class))).willReturn(voting);
        BDDMockito.given(this.votingRepository.findById(TEST_VOTING_ID)).willReturn(java.util.Optional.of(voting));
        BDDMockito.given(this.votingRepository.getVotingsByBarId(1)).willReturn(votingList);

    }

    @WithMockUser(username="admin", roles={"OWNER"})
    @Test
    void successCreateVoting() throws Exception{
        String json = "{ \n \"title\": \"Voting Integration Test\",\n \"description\": \"Lorem Ipsum\",\n \"openingHour\": \"30-12-2021 18:10:00\",\n \"closingHour\": \"30-12-2021 20:00:00\",\n \"timer\": null,\n \"options\": [],\n \"votersUsernames\": [] \n}";

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/bar/"+TEST_BAR_ID+"/voting")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @WithMockUser(username = "user", roles = {"CLIENT"})
    @Test
    void successGetVoting() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/voting/"+TEST_VOTING_ID))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @WithMockUser(username = "user", roles = {"CLIENT"})
    @Test
    void failureGetVoting() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/voting/9000"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @WithMockUser(username = "admin", roles = {"OWNER"})
    @Test
    void successGetVotingsByBarId() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/bar/" + TEST_BAR_ID + "/voting")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)));
    }

    @WithMockUser(username = "admin", roles = {"OWNER"})
    @Test
    void successDeleteVoting() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/bar/"+TEST_BAR_ID+"/voting/"+TEST_VOTING_ID))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @WithMockUser(username = "admin", roles = {"OWNER"})
    @Test
    void successUpdateVoting() throws Exception {
        String json = "{ \n \"title\": \"Voting Integration Test\",\n \"description\": \"Lorem Ipsum modified\",\n \"openingHour\": \"30-12-2021 18:10:00\",\n \"closingHour\": \"30-12-2021 20:00:00\",\n \"timer\": null,\n \"options\": [],\n \"votersUsernames\": [] \n}";

        this.mockMvc.perform(MockMvcRequestBuilders.put("/api/bar/"+TEST_BAR_ID+"/voting/"+TEST_VOTING_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @WithMockUser(username = "admin", roles = {"OWNER"})
    @Test
    void successFinishVoting() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/bar/"+TEST_BAR_ID+"/voting/"+TEST_VOTING_ID+"/finish"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @WithMockUser(username = "admin", roles = {"OWNER"})
    @Test
    void failureFinishVoting() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/bar/"+TEST_BAR_ID+"voting/9000/finish"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void failureGetVotingsByBarIdUnauthorized() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/bar/" + TEST_BAR_ID + "/voting")
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
