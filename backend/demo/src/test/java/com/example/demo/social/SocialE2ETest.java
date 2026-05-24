package com.example.demo.social;

import com.example.demo.auth.dto.AuthResponse;
import com.example.demo.auth.dto.RegisterRequest;
import com.example.demo.shared.dto.UserDto;
import com.example.demo.social.dto.ChallengeCreateDto;
import com.example.demo.social.dto.ChallengeDto;
import com.example.demo.social.dto.FeedEntryDto;
import com.example.demo.social.dto.FriendDto;
import com.example.demo.social.dto.FriendRequestDto;
import com.example.demo.social.dto.LeaderboardEntryDto;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class SocialE2ETest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    // ==================== HELPERS ====================

    /** Rejestruje użytkownika i zwraca JWT token. */
    private String register(String username) throws Exception {
        var request = new RegisterRequest(username, username + "@test.com", "Password1!");
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readValue(result.getResponse().getContentAsString(), AuthResponse.class).token();
    }

    /** Zwraca ID użytkownika przez wyszukiwanie po nazwie. */
    private Long getUserId(String token, String username) throws Exception {
        MvcResult result = mockMvc.perform(get("/api/users/search")
                        .param("query", username)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
        List<UserDto> users = objectMapper.readValue(
                result.getResponse().getContentAsString(), new TypeReference<>() {});
        return users.stream()
                .filter(u -> u.username().equals(username))
                .findFirst().orElseThrow().id();
    }

    /** Unikalny login — izolacja między testami. */
    private String uniqueUser(String prefix) {
        return prefix + "_" + UUID.randomUUID().toString().substring(0, 6);
    }

    /** Wykonuje POST z autoryzacją i sprawdza kod HTTP. */
    private MvcResult authPost(String url, String token, Object body, int expectedStatus) throws Exception {
        return mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().is(expectedStatus))
                .andReturn();
    }

    // ==================== TESTY ZNAJOMYCH ====================

    @Test
    void znajomi_pelnyFlow_wyslanieAkceptacjaILista() throws Exception {
        // Arrange
        String userA = uniqueUser("alice");
        String userB = uniqueUser("bob");
        String tokenA = register(userA);
        String tokenB = register(userB);
        Long idB = getUserId(tokenA, userB);

        // A wysyła zaproszenie do B
        MvcResult sendResult = authPost("/api/friends/request", tokenA,
                new FriendRequestDto(idB), 201);
        FriendDto sent = objectMapper.readValue(sendResult.getResponse().getContentAsString(), FriendDto.class);
        Long friendshipId = sent.friendshipId();

        // B widzi zaproszenie w oczekujących
        MvcResult pendingResult = mockMvc.perform(get("/api/friends/pending")
                        .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isOk())
                .andReturn();
        List<FriendDto> pending = objectMapper.readValue(
                pendingResult.getResponse().getContentAsString(), new TypeReference<>() {});

        assertThat(pending).hasSize(1);
        assertThat(pending.get(0).username()).isEqualTo(userA);

        // B akceptuje zaproszenie
        MvcResult acceptResult = mockMvc.perform(post("/api/friends/accept/" + friendshipId)
                        .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isOk())
                .andReturn();
        FriendDto accepted = objectMapper.readValue(acceptResult.getResponse().getContentAsString(), FriendDto.class);
        assertThat(accepted.status()).isEqualTo("ACCEPTED");

        // A widzi B na liście znajomych
        Long idA = getUserId(tokenA, userA);
        MvcResult friendsAResult = mockMvc.perform(get("/api/friends")
                        .param("userId", idA.toString())
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk())
                .andReturn();
        List<FriendDto> friendsA = objectMapper.readValue(
                friendsAResult.getResponse().getContentAsString(), new TypeReference<>() {});

        assertThat(friendsA).hasSize(1);
        assertThat(friendsA.get(0).username()).isEqualTo(userB);
    }

    @Test
    void znajomi_zaproszenieSamegoSiebie_zwraca400() throws Exception {
        String user = uniqueUser("self");
        String token = register(user);
        Long myId = getUserId(token, user);

        authPost("/api/friends/request", token, new FriendRequestDto(myId), 400);
    }

    @Test
    void znajomi_zduplikowaneZaproszenie_zwraca409() throws Exception {
        String userA = uniqueUser("dup_a");
        String userB = uniqueUser("dup_b");
        String tokenA = register(userA);
        register(userB);
        Long idB = getUserId(tokenA, userB);

        // Pierwsze zaproszenie — OK
        authPost("/api/friends/request", tokenA, new FriendRequestDto(idB), 201);

        // Drugie zaproszenie — 409 Conflict
        authPost("/api/friends/request", tokenA, new FriendRequestDto(idB), 409);
    }

    // ==================== TESTY WYZWAŃ ====================

    @Test
    void wyzwanie_utworzenieIDolaczenie_dwochUczestnikowNaRankingu() throws Exception {
        String creator = uniqueUser("creator");
        String joiner  = uniqueUser("joiner");
        String tokenCreator = register(creator);
        String tokenJoiner  = register(joiner);

        // Tworzenie wyzwania
        var createDto = new ChallengeCreateDto(
                "Wyzwanie testowe", "Opis",
                LocalDate.now(), LocalDate.now().plusDays(7),
                "WORKOUT_COUNT");

        MvcResult createResult = authPost("/api/challenges", tokenCreator, createDto, 201);
        ChallengeDto created = objectMapper.readValue(
                createResult.getResponse().getContentAsString(), ChallengeDto.class);

        assertThat(created.participantCount()).isEqualTo(1);
        Long challengeId = created.id();

        // Dołączenie drugiego użytkownika
        mockMvc.perform(post("/api/challenges/" + challengeId + "/join")
                        .header("Authorization", "Bearer " + tokenJoiner))
                .andExpect(status().isOk());

        // Ranking — 2 uczestników
        MvcResult leaderboardResult = mockMvc.perform(get("/api/challenges/" + challengeId + "/leaderboard")
                        .header("Authorization", "Bearer " + tokenCreator))
                .andExpect(status().isOk())
                .andReturn();
        List<LeaderboardEntryDto> leaderboard = objectMapper.readValue(
                leaderboardResult.getResponse().getContentAsString(), new TypeReference<>() {});

        assertThat(leaderboard).hasSize(2);
        assertThat(leaderboard.get(0).rank()).isEqualTo(1);
    }

    @Test
    void wyzwanie_podwojneDolaczenie_zwraca409() throws Exception {
        String user = uniqueUser("double");
        String token = register(user);

        var createDto = new ChallengeCreateDto("Test", null,
                LocalDate.now(), LocalDate.now().plusDays(3), "TOTAL_VOLUME");
        MvcResult createResult = authPost("/api/challenges", token, createDto, 201);
        Long challengeId = objectMapper.readValue(
                createResult.getResponse().getContentAsString(), ChallengeDto.class).id();

        // Twórca jest już uczestnikiem — ponowne dołączenie powinno dać 409
        mockMvc.perform(post("/api/challenges/" + challengeId + "/join")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isConflict());
    }

    @Test
    void wyzwanie_nieprawidlowaMetryka_zwraca400ZListaDozwolonych() throws Exception {
        String user = uniqueUser("badmetric");
        String token = register(user);

        var createDto = new ChallengeCreateDto(
                "Złe wyzwanie", null,
                LocalDate.now(), LocalDate.now().plusDays(3),
                "NIEISTNIEJACA_METRYKA");

        MvcResult result = authPost("/api/challenges", token, createDto, 400);
        assertThat(result.getResponse().getContentAsString()).contains("Dozwolone wartości");
    }

    // ==================== TESTY FEEDA ====================

    @Test
    void feed_poUtworzeniuWyzwania_znajomyWidziWpisTypuCHALLENGE_CREATED() throws Exception {
        String userA = uniqueUser("feed_a");
        String userB = uniqueUser("feed_b");
        String tokenA = register(userA);
        String tokenB = register(userB);
        Long idB = getUserId(tokenA, userB);

        // A i B zostają znajomymi
        MvcResult sendResult = authPost("/api/friends/request", tokenA,
                new FriendRequestDto(idB), 201);
        Long friendshipId = objectMapper.readValue(
                sendResult.getResponse().getContentAsString(), FriendDto.class).friendshipId();

        mockMvc.perform(post("/api/friends/accept/" + friendshipId)
                        .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isOk());

        // A tworzy wyzwanie
        var createDto = new ChallengeCreateDto("Wyzwanie feedowe", null,
                LocalDate.now(), LocalDate.now().plusDays(5), "STREAK_DAYS");
        authPost("/api/challenges", tokenA, createDto, 201);

        // B sprawdza feed — powinien widzieć aktywność A
        MvcResult feedResult = mockMvc.perform(get("/api/feed")
                        .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isOk())
                .andReturn();
        List<FeedEntryDto> feed = objectMapper.readValue(
                feedResult.getResponse().getContentAsString(), new TypeReference<>() {});

        assertThat(feed).isNotEmpty();
        FeedEntryDto entry = feed.get(0);
        assertThat(entry.type()).isEqualTo("CHALLENGE_CREATED");
        assertThat(entry.username()).isEqualTo(userA);
    }
}
