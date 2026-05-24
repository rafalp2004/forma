package com.example.demo;

import com.example.demo.auth.dto.AuthResponse;
import com.example.demo.auth.dto.RegisterRequest;
import com.example.demo.shared.dto.UserDto;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Wspólna baza dla testów E2E. Zawiera setup MockMvc z Spring Security
 * oraz helpery rejestracji, wyszukiwania i wykonywania żądań HTTP.
 */
@SpringBootTest
public abstract class BaseE2ETest {

    @Autowired
    protected WebApplicationContext webApplicationContext;

    protected MockMvc mockMvc;
    protected final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUpMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    /** Rejestruje użytkownika i zwraca JWT token. */
    protected String register(String username) throws Exception {
        var req = new RegisterRequest(username, username + "@test.com", "Password1!");
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readValue(
                result.getResponse().getContentAsString(), AuthResponse.class).token();
    }

    /** Zwraca ID użytkownika przez wyszukiwanie po nazwie. */
    protected Long getUserId(String token, String username) throws Exception {
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

    /** Unikalny login zapobiegający kolizjom między testami. */
    protected String uniqueUser(String prefix) {
        return prefix + "_" + UUID.randomUUID().toString().substring(0, 6);
    }

    /** POST z tokenem JWT i oczekiwanym statusem HTTP. */
    protected MvcResult authPost(String url, String token, Object body, int expectedStatus) throws Exception {
        return mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().is(expectedStatus))
                .andReturn();
    }

    /** PUT z tokenem JWT i oczekiwanym statusem HTTP. */
    protected MvcResult authPut(String url, String token, Object body, int expectedStatus) throws Exception {
        return mockMvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().is(expectedStatus))
                .andReturn();
    }

    /** DELETE z tokenem JWT i oczekiwanym statusem HTTP. */
    protected MvcResult authDelete(String url, String token, int expectedStatus) throws Exception {
        return mockMvc.perform(delete(url)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().is(expectedStatus))
                .andReturn();
    }

    /** GET z tokenem JWT i oczekiwanym statusem HTTP. */
    protected MvcResult authGet(String url, String token, int expectedStatus) throws Exception {
        return mockMvc.perform(get(url)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().is(expectedStatus))
                .andReturn();
    }
}
