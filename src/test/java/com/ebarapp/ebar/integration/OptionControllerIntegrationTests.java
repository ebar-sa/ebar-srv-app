package com.ebarapp.ebar.integration;

import com.ebarapp.ebar.model.*;
import com.ebarapp.ebar.repository.BarRepository;
import com.ebarapp.ebar.repository.BarTableRepository;
import com.ebarapp.ebar.repository.OptionRepository;
import com.ebarapp.ebar.repository.VotingRepository;
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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles(profiles = "dev")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class OptionControllerIntegrationTests {

    private static final String TEST_OWNER_FIRST_NAME = "John";
    private static final String TEST_OWNER_LAST_NAME = "Doe";
    private static final String TEST_OWNER_DNI = "11111111K";
    private static final String TEST_OWNER_EMAIL = "johndoe1@email.com";
    private static final String TEST_OWNER_PHONE_NUMBER = "666333999";
    private static final String TEST_OWNER_USERNAME = "admin";
    private static final String TEST_OWNER_PASSWORD = "johndoe1";

    private static final String TEST_CLIENT_FIRST_NAME = "First";
    private static final String TEST_CLIENT_LAST_NAME = "Last";
    private static final String TEST_CLIENT_DNI = "11111111L";
    private static final String TEST_CLIENT_EMAIL = "firstLast@email.com";
    private static final String TEST_CLIENT_PHONE_NUMBER = "666333998";
    private static final String TEST_CLIENT_USERNAME = "user";
    private static final String TEST_CLIENT_PASSWORD = "user";

    private static final int TEST_BAR_ID = 1;
    private static final String TEST_BAR_NAME = "Burger Food Porn";
    private static final String TEST_BAR_DESCRIPTION = "El templo de la hamburguesa.";
    private static final String TEST_BAR_CONTACT = "burgerfoodsevilla@gmail.com";
    private static final String TEST_BAR_LOCATION = "Avenida de Finlandia, 24, Sevilla";
    private static final Date TEST_BAR_OPENING_TIME = Date.from(Instant.parse("1970-01-01T13:00:00.00Z"));
    private static final Date TEST_BAR_CLOSING_TIME = Date.from(Instant.parse("1970-01-01T22:30:00.00Z"));
    private static final Set<Employee> TEST_BAR_EMPLOYEES = new HashSet<>();
    private static final Date TEST_BAR_PAID_UNTIL = Date.from(Instant.parse("2025-01-01T22:30:00.00Z"));

    private static final int TEST_VOTING_ID = 1;
    private static final String TEST_VOTING_TITLE = "¿Qué canción quieres escuchar?";
    private static final String TEST_VOTING_DESCRIPTION = "Elige tu canción favorita";
    private static final LocalDateTime TEST_VOTING_OPENING_TIME = LocalDateTime.of(2021, 12, 30, 20, 0, 0, 0);
    private static final LocalDateTime TEST_VOTING_CLOSING_TIME = LocalDateTime.of(2021, 12, 30, 22, 0, 0, 0);
    private static final Set<String> TEST_VOTING_USERNAMES = new HashSet<>();

    private static final int TEST_OPTION_ID_1 = 1;
    private static final int TEST_OPTION_VOTES_1 = 0;
    private static final String TEST_OPTION_DESCRIPTION_1 = "Holiday";

    private static final int TEST_VOTING_2_ID = 2;
    private static final String TEST_VOTING_2_TITLE = "¿Qué canción quieres escuchar?";
    private static final String TEST_VOTING_2_DESCRIPTION = "Elige tu canción favorita";
       private static final Set<String> TEST_VOTING_2_USERNAMES = new HashSet<>();

    private static final int TEST_OPTION_ID_2 = 2;
    private static final int TEST_OPTION_VOTES_2 = 0;
    private static final String TEST_OPTION_DESCRIPTION_2 = "Radioactive";

    private static  final int TEST_BAR_TABLE_ID = 1;
    private static  final String TEST_BAR_TABLE_NAME = "Mesa 1";
    private static  final String TEST_BAR_TABLE_TOKEN = "aaa-111";
    private static  final int TEST_BAR_TABLE_SEATS = 4;



    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VotingRepository votingRepository;

    @MockBean
    private OptionRepository optionRepository;

    @MockBean
    private BarRepository barRepository;

    @MockBean
    private BarTableRepository barTableRepository;

    @BeforeEach
    private void setUp(){

        Owner owner = new Owner();
        owner.setFirstName(TEST_OWNER_FIRST_NAME);
        owner.setLastName(TEST_OWNER_LAST_NAME);
        owner.setDni(TEST_OWNER_DNI);
        owner.setEmail(TEST_OWNER_EMAIL);
        owner.setPhoneNumber(TEST_OWNER_PHONE_NUMBER);
        owner.setUsername(TEST_OWNER_USERNAME);
        owner.setPassword(TEST_OWNER_PASSWORD);

        Client client = new Client();
        client.setFirstName(TEST_CLIENT_FIRST_NAME);
        client.setLastName(TEST_CLIENT_LAST_NAME);
        client.setDni(TEST_CLIENT_DNI);
        client.setEmail(TEST_CLIENT_EMAIL);
        client.setPhoneNumber(TEST_CLIENT_PHONE_NUMBER);
        client.setUsername(TEST_CLIENT_USERNAME);
        client.setPassword(TEST_CLIENT_PASSWORD);

        List<Client> clients = new ArrayList<>();
        clients.add(client);

        Bar bar = new Bar();
        bar.setId(TEST_BAR_ID);
        bar.setName(TEST_BAR_NAME);
        bar.setDescription(TEST_BAR_DESCRIPTION);
        bar.setContact(TEST_BAR_CONTACT);
        bar.setLocation(TEST_BAR_LOCATION);
        bar.setOpeningTime(TEST_BAR_OPENING_TIME);
        bar.setClosingTime(TEST_BAR_CLOSING_TIME);
        bar.setPaidUntil(TEST_BAR_PAID_UNTIL);
        bar.setEmployees(TEST_BAR_EMPLOYEES);
        bar.setOwner(owner);

        Option option = new Option();
        option.setVotes(TEST_OPTION_VOTES_1);
        option.setDescription(TEST_OPTION_DESCRIPTION_1);

        Set<Option> options = new HashSet<>();
        options.add(option);

        Voting voting = new Voting();
        voting.setId(TEST_VOTING_ID);
        voting.setTitle(TEST_VOTING_TITLE);
        voting.setDescription(TEST_VOTING_DESCRIPTION);
        voting.setDescription(TEST_BAR_DESCRIPTION);
        voting.setOpeningHour(TEST_VOTING_OPENING_TIME);
        voting.setClosingHour(TEST_VOTING_CLOSING_TIME);
        voting.setTimer(null);
        voting.setVotersUsernames(TEST_VOTING_USERNAMES);
        voting.setOptions(options);

        Option option2 = new Option();
        option2.setId(TEST_OPTION_ID_2);
        option2.setVotes(TEST_OPTION_VOTES_2);
        option2.setDescription(TEST_OPTION_DESCRIPTION_2);

        Set<Option> options2 = new HashSet<>();
        options2.add(option2);

        ZonedDateTime serverDefaultTime = ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault());
        ZoneId madridZone = ZoneId.of("Europe/Madrid");
        ZonedDateTime madridZoned = serverDefaultTime.withZoneSameInstant(madridZone);

        Voting voting2 = new Voting();
        voting2.setId(TEST_VOTING_2_ID);
        voting2.setTitle(TEST_VOTING_2_TITLE);
        voting2.setDescription(TEST_VOTING_2_DESCRIPTION);
        voting2.setOpeningHour(madridZoned.toLocalDateTime().minusHours(1L));
        voting2.setClosingHour(madridZoned.toLocalDateTime().plusHours(1L));
        voting2.setTimer(null);
        voting2.setVotersUsernames(TEST_VOTING_2_USERNAMES);
        voting2.setOptions(options2);

        Set<Voting> votings = new HashSet<>();
        votings.add(voting);
        votings.add(voting2);
        bar.setVotings(votings);

        BarTable barTable = new BarTable();
        barTable.setId(TEST_BAR_TABLE_ID);
        barTable.setName(TEST_BAR_TABLE_NAME);
        barTable.setToken(TEST_BAR_TABLE_TOKEN);
        barTable.setSeats(TEST_BAR_TABLE_SEATS);
        barTable.setBar(bar);
        barTable.setClients(clients);

        Set<BarTable> bts = new HashSet<>();
        bts.add(barTable);
        bar.setBarTables(bts);

        List<String> validTokens = new ArrayList<>();
        validTokens.add(TEST_BAR_TABLE_TOKEN);

        BDDMockito.given(this.barRepository.getBarById(TEST_BAR_ID)).willReturn(bar);
        BDDMockito.given(this.votingRepository.findById(TEST_VOTING_ID)).willReturn(java.util.Optional.of(voting));
        BDDMockito.given(this.votingRepository.findById(TEST_VOTING_2_ID)).willReturn(java.util.Optional.of(voting2));
        BDDMockito.given(this.votingRepository.save(Mockito.any(Voting.class))).willReturn(voting);
        BDDMockito.given(this.optionRepository.save(Mockito.any(Option.class))).willReturn(option2);
        BDDMockito.given(this.optionRepository.findById(TEST_OPTION_ID_1)).willReturn(java.util.Optional.of(option));
        BDDMockito.given(this.optionRepository.findById(TEST_OPTION_ID_2)).willReturn(java.util.Optional.of(option2));
        BDDMockito.given(this.barTableRepository.getAllValidTokenByBarId(TEST_BAR_ID)).willReturn(validTokens);
    }

    @WithMockUser(username="admin", roles={"OWNER"})
    @Test
    void successCreateOption() throws Exception{
        String json = "{\n\"description\": \"Radioactive\",\n \"votes\": \"0\"\n}";

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/bar/"+TEST_BAR_ID+"/voting/"+TEST_VOTING_2_ID+"/option")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @WithMockUser(username="admin", roles={"OWNER"})
    @Test
    void failureCreateOption() throws Exception{
        String json = "{\n\"description\": \"Radioactive\",\n \"votes\": \"0\"\n}";

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/bar/"+TEST_BAR_ID+"/voting/9000/option")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @WithMockUser(username="admin", roles={"OWNER"})
    @Test
    void successDeleteOption() throws Exception{
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/bar/"+TEST_BAR_ID+"/voting/"+TEST_VOTING_ID+"/option/"+TEST_OPTION_ID_1))
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

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/bar/"+TEST_BAR_ID+"/voting/"+TEST_VOTING_2_ID+"/option/"+TEST_OPTION_ID_2+"/vote")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @WithMockUser(username="user", roles={"CLIENT"})
    @Test
    void failureVote() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/bar/"+TEST_BAR_ID+"/voting/"+TEST_VOTING_2_ID+"/option/"+TEST_OPTION_ID_2+"/vote")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @WithMockUser(username="user", roles={"CLIENT"})
    @Test
    void successIsValidVoter() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/bar/"+TEST_BAR_ID+"/username/"+TEST_CLIENT_USERNAME)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @WithMockUser(username="user", roles={"CLIENT"})
    @Test
    void failureIsValidVoter() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/bar/2/username/"+TEST_CLIENT_USERNAME)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

}
