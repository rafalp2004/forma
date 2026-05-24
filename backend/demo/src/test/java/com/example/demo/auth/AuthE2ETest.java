package com.example.demo.auth;

import com.example.demo.BaseE2ETest;
import com.example.demo.auth.dto.AuthResponse;
import com.example.demo.auth.dto.BiometricsUpdate;
import com.example.demo.auth.dto.GoalUpdate;
import com.example.demo.auth.dto.LoginRequest;
import com.example.demo.auth.dto.RegisterRequest;
import com.example.demo.auth.dto.UserDetailsResponse;
import com.example.demo.auth.entity.Gender;
import com.example.demo.auth.entity.UserGoal;
import com.example.demo.shared.dto.UserDto;
import tools.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testy E2E modułu autoryzacji i profilu użytkownika.
 * Pokrywa rejestrację, logowanie, pobieranie i aktualizację profilu.
 */
class AuthE2ETest extends BaseE2ETest {

    // ==================== REJESTRACJA ====================

    @Test
    void rejestracja_nowyUzytkownik_zwracaNiepustyToken() throws Exception {
        String username = uniqueUser("new_user");
        var req = new RegisterRequest(username, username + "@test.com", "Password1!");

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn();

        String token = objectMapper.readValue(
                result.getResponse().getContentAsString(), AuthResponse.class).token();
        assertThat(token).isNotBlank();
    }

    @Test
    void rejestracja_zduplikowanaUsername_zwraca409() throws Exception {
        String username = uniqueUser("dup_user");
        var req1 = new RegisterRequest(username, username + "@test.com", "Password1!");
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req1)))
                .andExpect(status().isOk());

        // Ta sama nazwa użytkownika, inny e-mail
        var req2 = new RegisterRequest(username, username + "_x@test.com", "Password1!");
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req2)))
                .andExpect(status().isConflict());
    }

    @Test
    void rejestracja_zduplikowanyEmail_zwraca409() throws Exception {
        String username = uniqueUser("dup_email");
        String email = username + "@test.com";
        var req1 = new RegisterRequest(username, email, "Password1!");
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req1)))
                .andExpect(status().isOk());

        // Inny login, ten sam e-mail
        var req2 = new RegisterRequest(username + "_2", email, "Password1!");
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req2)))
                .andExpect(status().isConflict());
    }

    @Test
    void rejestracja_niepoprawnyFormatEmail_zwraca400() throws Exception {
        var req = new RegisterRequest(uniqueUser("bad_email"), "not-an-email", "Password1!");
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejestracja_zaKrotkoHaslo_zwraca400() throws Exception {
        String username = uniqueUser("short_pass");
        var req = new RegisterRequest(username, username + "@test.com", "abc"); // < 6 znaków
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejestracja_zaKrotkaUsername_zwraca400() throws Exception {
        var req = new RegisterRequest("ab", "ab@test.com", "Password1!"); // < 3 znaki
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    // ==================== LOGOWANIE ====================

    @Test
    void login_poprawnePoswiadczenia_zwracaToken() throws Exception {
        String username = uniqueUser("login_ok");
        register(username);

        var loginReq = new LoginRequest(username, "Password1!");
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andReturn();

        String token = objectMapper.readValue(
                result.getResponse().getContentAsString(), AuthResponse.class).token();
        assertThat(token).isNotBlank();
    }

    @Test
    void login_bledneHaslo_zwraca401() throws Exception {
        String username = uniqueUser("bad_pass");
        register(username);

        var loginReq = new LoginRequest(username, "WrongPassword!");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_nieistniejacyUzytkownik_zwraca401() throws Exception {
        var loginReq = new LoginRequest("ghost_" + uniqueUser(""), "Password1!");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isUnauthorized());
    }

    // ==================== PROFIL UŻYTKOWNIKA ====================

    @Test
    void profilUzytkownika_zalogowany_zwracaPoprawneDane() throws Exception {
        String username = uniqueUser("profile");
        String token = register(username);

        MvcResult result = mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        UserDetailsResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserDetailsResponse.class);
        assertThat(response.getUsername()).isEqualTo(username);
        assertThat(response.getEmail()).isEqualTo(username + "@test.com");
    }

    @Test
    void aktualizacjaBiometrii_zapisujeIZwraca200() throws Exception {
        String username = uniqueUser("bio");
        String token = register(username);

        var update = new BiometricsUpdate(82.5, 178.0, 28, Gender.MALE);
        mockMvc.perform(put("/api/users/me/biometrics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk());

        // Weryfikacja przez profil
        MvcResult result = mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        UserDetailsResponse profile = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserDetailsResponse.class);
        assertThat(profile.getWeight()).isEqualTo(82.5);
        assertThat(profile.getHeight()).isEqualTo(178.0);
        assertThat(profile.getAge()).isEqualTo(28);
        assertThat(profile.getGender()).isEqualTo(Gender.MALE);
    }

    @Test
    void aktualizacjaCelu_zapisujeIZwraca200() throws Exception {
        String username = uniqueUser("goal");
        String token = register(username);

        var update = new GoalUpdate(UserGoal.GAIN_WEIGHT);
        mockMvc.perform(put("/api/users/me/goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk());

        MvcResult result = mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        UserDetailsResponse profile = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserDetailsResponse.class);
        assertThat(profile.getGoal()).isEqualTo(UserGoal.GAIN_WEIGHT);
    }

    // ==================== WYSZUKIWANIE UŻYTKOWNIKÓW ====================

    @Test
    void wyszukiwanieUzytkownikow_znajdzePoFragmencieUsername() throws Exception {
        String username = uniqueUser("search_me");
        String token = register(username);

        MvcResult result = mockMvc.perform(get("/api/users/search")
                        .param("query", username)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        List<UserDto> users = objectMapper.readValue(
                result.getResponse().getContentAsString(), new TypeReference<>() {});
        assertThat(users).extracting(UserDto::username).contains(username);
    }

    @Test
    void pobierzUzytkownikaPoId_zwracaPoprawneDane() throws Exception {
        String username = uniqueUser("by_id");
        String token = register(username);
        Long userId = getUserId(token, username);

        MvcResult result = mockMvc.perform(get("/api/users/" + userId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        UserDto user = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserDto.class);
        assertThat(user.id()).isEqualTo(userId);
        assertThat(user.username()).isEqualTo(username);
    }
}
