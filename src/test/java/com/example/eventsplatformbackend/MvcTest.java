package com.example.eventsplatformbackend;

import com.example.eventsplatformbackend.domain.dto.response.JwtResponse;
import com.example.eventsplatformbackend.domain.entity.User;
import com.example.eventsplatformbackend.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class MvcTest {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldGetBadRequest_notAuthorized() throws Exception {
        mockMvc.perform(get("/user/username123"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void registerUser_getJwtToken() throws Exception {
        Map<String,Object> body = new HashMap<>();
        body.put("username","dmitry");
        body.put("email","dmitryemail@gmail.com");
        body.put("password","dmitry123");

        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void loginWithWrongEmail_get422() throws Exception {
        Map<String,String> body = new HashMap<>();
        body.put("email","dmitry");
        body.put("password","dmitry123");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(422))
                .andReturn();
    }

    @Test
    void login_getJwt() throws Exception {
        Map<String,String> body = new HashMap<>();
        body.put("email","dmitryemail@gmail.com");
        body.put("password","dmitry123");

        String response = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        JwtResponse jwtResponse = new ObjectMapper().readValue(response, JwtResponse.class);
        assertNotNull("token should return owner's id!", jwtUtil.extractId(jwtResponse.getAccessToken()));
    }

    @Test
    void getUserAfterLogin() throws Exception {
        Map<String,String> body = new HashMap<>();
        body.put("email","dmitryemail@gmail.com");
        body.put("password","dmitry123");

        String response = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        JwtResponse jwtResponse = new ObjectMapper().readValue(response, JwtResponse.class);
        assertNotNull("token should return owner's id", jwtUtil.extractId(jwtResponse.getAccessToken()));

        String userJson = mockMvc.perform(get("/user/dmitry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization","Bearer "+jwtResponse.getAccessToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        User user = new ObjectMapper().readValue(userJson, User.class);
        assertEquals("username should be equals", "dmitry", user.getUsername());
    }
}
